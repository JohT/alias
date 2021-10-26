package org.alias.axon.serializer.integration;

import static org.alias.axon.serializer.example.query.model.member.nickname.NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.alias.axon.serializer.example.domain.model.account.AccountAggregate;
import org.alias.axon.serializer.example.domain.model.account.AccountService;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.alias.axon.serializer.example.messaging.axon.configuration.injection.CdiParameterResolverFactoryTest;
import org.alias.axon.serializer.example.messaging.boundary.query.EventProcessorService;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelProjection;
import org.alias.axon.serializer.example.query.model.member.nickname.NicknameProjection;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(ArquillianExtension.class)
public class AccountEventsourcingIntegrationIT {

	@Inject
	private AccountService accountService;

	@Inject
	@QueryModelProjection(processingGroup = NicknameProjection.PROCESSING_GROUP_FOR_NICKNAMES)
	private EventProcessorService eventProcessor;

	@Resource(name = "jdbc/eventsourcing")
	private DataSource eventsourcingDatasource;

	private EventsourcingTestDatabaseRepository eventsourcingRepository;

	@PersistenceContext(unitName = "query.member.model")
	private EntityManager queryMember;

	@Deployment
	public static WebArchive createDeployment() throws Exception {
		File[] files = Maven.resolver()
				.loadPomFromFile("pom.xml")
				.importCompileAndRuntimeDependencies()
				.resolve()
				.withTransitivity().asFile();

		WebArchive archive = ShrinkWrap.create(WebArchive.class)
				.addPackages(true, "org.alias.axon.serializer.example")
				.deleteClass(CdiParameterResolverFactoryTest.CdiDummy.class)
				.addAsResource("META-INF/services/org.axonframework.messaging.annotation.ParameterResolverFactory")
				.addAsResource("META-INF/persistence.xml").addAsLibraries(files)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

		// System.out.println(archive.toString(true)); // Console log the deploy structure
		
		return archive;
	}

	@BeforeEach
	public void setUp() {
		eventsourcingRepository = new EventsourcingTestDatabaseRepository(eventsourcingDatasource);
	}

	@Test
	@Order(1)
	public void createdAccountHasEmptyPresetNickname() throws InterruptedException {
		String accountId = accountService.createAccount();
		String queriedNickname = accountService.queryNickname(accountId);
		assertEquals("", queriedNickname);
	}

	@Test
	@Order(2)
	public void aliasNamesShouldBeTakenAsEventPayloadType() {
		String accountId = accountService.createAccount();
		List<String> eventTypes = eventTypesForAggregateId(accountId);
		assertThat(eventTypes, CoreMatchers.hasItem("AccountCreated"));
		assertThat(eventTypes, CoreMatchers.hasItem("NicknamePreset"));
	}

	@Test
	@Order(3)
	public void fullQualifiedEventPayloadTypesShouldBeSupportedAsFallback() throws InterruptedException {
		String accountId = accountService.createAccount();
		String eventsPackage = AccountCreatedEvent.class.getPackage().getName();
		EventsourcingTestDatabaseRepository.printQueryResults(eventsourcingRepository.queryEvents(), System.out);
		// changing the same aggregate triggers reading all events (without snapshots).
		// if there is a problem while reading and reconstructing, the test will and
		// should fail.
		eventsourcingRepository.prefixEventPayloadTypesBy(eventsPackage);
		EventsourcingTestDatabaseRepository.printQueryResults(eventsourcingRepository.queryEvents(), System.out);
		String nickname = "OtherTestNickname";
		accountService.changeNickname(accountId, nickname);
		assertEquals("OtherTestNickname", accountService.queryNickname(accountId));
	}

	@Test
	@Order(4)
	public void changedNicknameMatchesQueriedNickname() {
		String nickname = "TestNickname";
		String accountId = accountService.createAccount();
		accountService.changeNickname(accountId, nickname);
	}

	@Test
	@Order(5)
	public void tokensShouldBeUpToDateAfterProcessing() throws InterruptedException {
		String nickname = "TestNickname";
		String accountId = accountService.createAccount();
		// Check, if account creation was tracked
		waitForMessageContaining(AccountCreatedEvent.class, accountId);
		waitForMessageContaining(NicknamePresetEvent.class, "");

		accountService.changeNickname(accountId, nickname);

		// Check, if nickname change was tracked
		waitForMessageContaining(NicknameChangedEvent.class, nickname);
	}

	@Test
	@Order(6)
	public void nicknameChangePublishedBySubscriptionQuery() throws InterruptedException {
		BlockingQueue<String> publishedNicknames = new ArrayBlockingQueue<String>(1);
		String nickname = "SubscribedNickname";
		String accountId = accountService.createAccount();

		// Query all nicknames, that contain the name above.
		// Ignore already available nicknames.
		// Add every newly added nickname in future, that matches the query.
		accountService.queryNicknamesContaining(nickname, publishedNicknames::add);

		// Change the nickname, which should be published to the subscripted query.
		accountService.changeNickname(accountId, nickname);

		// Wait until the changed nickname gets published, or time out and fail.
		assertEquals(nickname, publishedNicknames.poll(5, TimeUnit.SECONDS));
	}

	@Test
	@Order(7)
	public void enoughChangesToTriggerSnapshot() {
		String accountId = accountService.createAccount();
		for (int i = 0; i < 20; i++) {
			accountService.changeNickname(accountId, String.format("Nickname %05d", i));
		}
		List<String> snapshots = snapshotTypesForAggregateId(accountId);
		assertThat(snapshots, CoreMatchers.hasItem(AccountAggregate.class.getName()));
	}

	private List<String> eventTypesForAggregateId(String aggregateId) {
		return payloadtypeForAggregateId(eventsourcingRepository.queryEvents(), aggregateId);
	}

	private List<String> snapshotTypesForAggregateId(String aggregateId) {
		return payloadtypeForAggregateId(eventsourcingRepository.querySnapshots(), aggregateId);
	}

	private static List<String> payloadtypeForAggregateId(List<Map<String, Object>> queryResults, String aggregateId) {
		return queryResults.stream().filter(columns -> columns.get("AGGREGATEIDENTIFIER").equals(aggregateId))
				.map(columns -> (String) columns.get("PAYLOADTYPE")).collect(Collectors.toList());
	}

	private void waitForMessageContaining(Class<?> messageType, String messageContent) throws InterruptedException {
		assertTrue(eventProcessor.waitForMessage(PROCESSING_GROUP_FOR_NICKNAMES, messageType, messageContent));
	}
}