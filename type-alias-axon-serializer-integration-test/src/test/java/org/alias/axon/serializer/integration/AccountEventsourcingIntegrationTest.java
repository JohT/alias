package org.alias.axon.serializer.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.alias.axon.serializer.example.domain.model.account.AccountService;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(Arquillian.class)
public class AccountEventsourcingIntegrationTest {

	@Inject
	private AccountService accountService;

	@Resource(name = "jdbc/eventsourcing")
	private DataSource eventsourcingDatasource;

	private EventsourcingTestDatabaseRepository eventsourcingRepository;

	@PersistenceContext(unitName = "query.member.model")
	private EntityManager queryMember;

	@Deployment
	public static JavaArchive createDeployment() throws Exception {
		return ShrinkWrap.create(JavaArchive.class)
				.addPackages(true, "org.alias.axon.serializer.example")
				.addAsResource("META-INF/persistence.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Before
	public void setUp() {
		eventsourcingRepository = new EventsourcingTestDatabaseRepository(eventsourcingDatasource);
	}

	@Test
	public void createdAccountHasEmptyPresetNickname() {
		String accountId = accountService.createAccount();
		assertEquals("", accountService.queryNickname(accountId));
	}

	@Test
	public void aliasNamesShouldBeTakenAsEventPayloadType() {
		String accountId = accountService.createAccount();
		List<String> eventTypes = eventTypesForAggregateId(accountId);
		assertThat(eventTypes, CoreMatchers.hasItem("AccountCreated"));
		assertThat(eventTypes, CoreMatchers.hasItem("NicknamePreset"));
	}

	@Test
	public void fullQualifiedEventPayloadTypesShouldBeSupportedAsFallback() {
		String accountId = accountService.createAccount();
		String eventsPackage = AccountCreatedEvent.class.getPackage().getName();
		EventsourcingTestDatabaseRepository.printQueryResults(eventsourcingRepository.queryEvents(), System.out);
		// changing the same aggregate triggers reading all events (without snapshots).
		// if there is a problem while reading and reconstructing, the test will and should fail.
		eventsourcingRepository.prefixEventPayloadTypesBy(eventsPackage);
		EventsourcingTestDatabaseRepository.printQueryResults(eventsourcingRepository.queryEvents(), System.out);
		String nickname = "OtherTestNickname";
		accountService.changeNickname(accountId, nickname);
		assertEquals("OtherTestNickname", accountService.queryNickname(accountId));
	}

	@Test
	public void changedNicknameMatchesQueriedNickname() {
		String nickname = "TestNickname";
		String accountId = accountService.createAccount();
		accountService.changeNickname(accountId, nickname);
	}

	@Test
	public void enoughChangesToTriggerSnapshot() {
		String accountId = accountService.createAccount();
		for (int i = 0; i < 20; i++) {
			accountService.changeNickname(accountId, String.format("Nickname %05d", i));
		}
		List<String> snapshots = snapshotTypesForAggregateId(accountId);
		assertThat(snapshots, CoreMatchers.hasItem("AccountAggregate"));
	}

	private List<String> eventTypesForAggregateId(String aggregateId) {
		return payloadtypeForAggregateId(eventsourcingRepository.queryEvents(), aggregateId);
	}

	private List<String> snapshotTypesForAggregateId(String aggregateId) {
		return payloadtypeForAggregateId(eventsourcingRepository.querySnapshots(), aggregateId);
	}

	private static List<String> payloadtypeForAggregateId(List<Map<String, Object>> queryResults, String aggregateId) {
		return queryResults
				.stream()
				.filter(columns -> columns.get("AGGREGATEIDENTIFIER").equals(aggregateId))
				.map(columns -> (String) columns.get("PAYLOADTYPE"))
				.collect(Collectors.toList());
	}
}