package org.alias.axon.serializer.example.configuration.eventsourcing;

import static org.alias.axon.serializer.example.configuration.eventsourcing.CdiLookupFunctions.directLookup;
import static org.alias.axon.serializer.example.configuration.eventsourcing.CdiLookupFunctions.lookup;

import java.util.Collection;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.TypeAliases;
import org.alias.axon.serializer.TypeDecoratableSerializer;
import org.alias.axon.serializer.example.configuration.eventsourcing.database.Eventsourcing;
import org.alias.axon.serializer.example.configuration.eventsourcing.serializer.JacksonObjectMapperConfiguration;
import org.alias.axon.serializer.example.configuration.eventsourcing.transaction.JtaTransactionManager;
import org.alias.axon.serializer.experimental.AliasableResourceBundleClassloader;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.config.AggregateConfigurer;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.modelling.command.AggregateRoot;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Axon Configuration.
 * 
 * @author JohT
 */
@Typed()
@ApplicationScoped
@TypeAliases({
		@TypeAlias(value = "MessagingMetaData", type = "org.axonframework.messaging.MetaData")
})
public class AxonConfiguration {

	/**
	 * For demonstration purpose only.
	 * 
	 * This is an example on how @{@link TypeAlias} default bundle can also be used as {@link Class} register.<br>
	 * Normally, this is done by using CDI and meta-annotations, which is the recommended way.<br>
	 */
	private static final RegisteredAnnotatedTypes DEFAULT_TYPES = RegisteredAnnotatedTypes.fromResourceBundleName("TypeAlias");

	/**
	 * For demonstration purpose only.
	 * 
	 * This is an example on how @{@link TypeAlias} can be configured for (sub-)packages.<br>
	 * Be aware, that this is good for modularization, but may break, if the query model package gets e.g. renamed.
	 */
	private static final RegisteredAnnotatedTypes QUERY_TYPES = RegisteredAnnotatedTypes
			.fromResourceBundleName("org.alias.axon.serializer.example.query.model.member.QueryMemberTypeAliasBundle");

	@Inject
	@Eventsourcing
	private ConnectionProvider connectionProvider;

	private Configuration configuration;

	@PostConstruct
	public void postConstruct() {
		configuration = addDiscoveredComponentsTo(DefaultConfigurer.defaultConfiguration())//
				.configureSerializer(jacksonSerializer(JacksonObjectMapperConfiguration.DEFAULT.getObjectMapper()))
				.configureEmbeddedEventStore(config -> eventStorageEngine(config))
				.configureTransactionManager(lookup(JtaTransactionManager.class))
				.start();
		enableBeanValidationForCommandMessages();
	}

	/**
	 * This is the most important part of this example and test project: <br>
	 * It shows how to configure a {@link JacksonSerializer} to be capable to use aliases and to resolve types without
	 * {@link Class#forName(String)}.
	 * 
	 * @param objectMapper - {@link ObjectMapper}
	 * @return {@link Function} to get the {@link Serializer} for the given {@link Configuration}
	 */
	private static final Function<Configuration, Serializer> jacksonSerializer(ObjectMapper objectMapper) {
		// The classLoader is optional.
		// It is a workaround to resolve all types through the ResourceBundle.
		// Without this workaround, deserialization uses the Classloader to resolve class names.
		// TODO Test with
		ClassLoader classLoader = AliasableResourceBundleClassloader.standard();
		JacksonSerializer serializer = JacksonSerializer.builder().objectMapper(objectMapper).classLoader(classLoader).build();
		return c -> TypeDecoratableSerializer.aliasThroughDefaultResourceBundle(serializer);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	@Produces
	public EventBus getEventBus() {
		return configuration.eventBus();
	}

	@Produces
	public CommandGateway getCommandGateway() {
		return configuration.commandGateway();
	}

	@Produces
	public QueryGateway getQueryGateway() {
		return configuration.queryGateway();
	}

	private EventStorageEngine eventStorageEngine(Configuration config) {
		return JdbcEventStorageEngine.builder()
				.eventSerializer(config.eventSerializer())
				.snapshotSerializer(config.serializer())
				.connectionProvider(connectionProvider)
				.transactionManager(directLookup(JtaTransactionManager.class))
				.build();
	}

	private Configurer addDiscoveredComponentsTo(Configurer configurer) {
		registerAggregates(configurer);
		registerEventHandlers(configurer);
		registerCommandHandlers(configurer);
		registerQueryHandlers(configurer);
		return configurer;
	}

	private Configurer registerAggregates(Configurer configurer) {
		for (Class<?> aggregate : DEFAULT_TYPES.annotatedWith(AggregateRoot.class)) {
			AggregateConfigurer<?> aggregateConfigurer = AggregateConfigurer.defaultConfiguration(aggregate);
			aggregateConfigurer.configureSnapshotTrigger(
					config -> new EventCountSnapshotTriggerDefinition(snapshotter(config, aggregateConfigurer), 5));
			configurer.configureAggregate(aggregateConfigurer);
		}
		return configurer;
	}

	private <T> Snapshotter snapshotter(Configuration configuration, AggregateConfigurer<T> aggregate) {
		return AggregateSnapshotter.builder()
				.eventStore(configuration.eventStore())
				.aggregateFactories(new GenericAggregateFactory<T>(aggregate.aggregateType()))
				.build();
	}

	private Configurer registerEventHandlers(Configurer configurer) {
		EventProcessingConfigurer eventProcessing = configurer.eventProcessing();
		// Tracking would need to use a @Resource injected ManagedThreadFactory
		// Since this is only an example, it is kept as simple as possible using a synchronous subscriber.
		// eventProcessing.registerTokenStore(config ->
		// JdbcTokenStore.builder().connectionProvider(connectionProvider).serializer(config.serializer()).build());
		// configurer.registerComponent(ExecutorService.class, config -> executerService)
		//
		// Further more, the Projection needs to be annotated by @ProcessingGroup("query.model.member.account")
		// Otherwise there will be a NullPointerException, because the fallback (packagename)
		// fails on derived cdi proxy classes, since o.getClass().getPackage() is null (.getName fails).
		eventProcessing.usingSubscribingEventProcessors();
		for (Class<?> eventHandler : QUERY_TYPES.annotatedWith(EventHandler.class)) {
			eventProcessing.registerEventHandler(lookup(eventHandler));
		}
		return configurer;
	}

	private Configurer registerCommandHandlers(Configurer configurer) {
		Collection<Class<?>> aggregates = DEFAULT_TYPES.annotatedWith(AggregateRoot.class);
		for (Class<?> commandHandler : DEFAULT_TYPES.annotatedWith(CommandHandler.class)) {
			if (!aggregates.contains(commandHandler)) {
				configurer.registerCommandHandler(lookup(commandHandler));
			}
		}
		return configurer;
	}

	private Configurer registerQueryHandlers(Configurer configurer) {
		for (Class<?> queryHandler : QUERY_TYPES.annotatedWith(QueryHandler.class)) {
			configurer.registerQueryHandler(lookup(queryHandler));
		}
		return configurer;
	}

	private void enableBeanValidationForCommandMessages() {
		configuration.commandBus().registerDispatchInterceptor(new BeanValidationInterceptor<>());
	}

	@Override
	public String toString() {
		return "AxonConfiguration [connectionProvider=" + connectionProvider + ", configuration=" + configuration + "]";
	}
}