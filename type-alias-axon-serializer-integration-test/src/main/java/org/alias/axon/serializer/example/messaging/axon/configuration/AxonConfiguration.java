package org.alias.axon.serializer.example.messaging.axon.configuration;

import static org.alias.axon.serializer.example.messaging.axon.configuration.CdiLookupFunctions.directLookup;
import static org.alias.axon.serializer.example.messaging.axon.configuration.CdiLookupFunctions.lookup;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import org.alias.axon.serializer.TypeDecoratableSerializer;
import org.alias.axon.serializer.example.messages.MessageAliases;
import org.alias.axon.serializer.example.messaging.axon.AggregateEventServiceAdapter;
import org.alias.axon.serializer.example.messaging.axon.CommandGatewayBoundaryServiceAdapter;
import org.alias.axon.serializer.example.messaging.axon.EventProcessingServiceAdapter;
import org.alias.axon.serializer.example.messaging.axon.QueryGatewayBoundaryServiceAdapter;
import org.alias.axon.serializer.example.messaging.axon.QueryUpdateEmitterBoundaryServiceAdapter;
import org.alias.axon.serializer.example.messaging.axon.configuration.database.Eventsourcing;
import org.alias.axon.serializer.example.messaging.axon.configuration.serializer.JacksonObjectMapperConfiguration;
import org.alias.axon.serializer.example.messaging.axon.configuration.transaction.JtaTransactionManager;
import org.alias.axon.serializer.example.messaging.boundary.command.AggregateEventService;
import org.alias.axon.serializer.example.messaging.boundary.command.CommandGatewayBoundaryService;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelAggregate;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelCommandHandler;
import org.alias.axon.serializer.example.messaging.boundary.query.EventProcessorService;
import org.alias.axon.serializer.example.messaging.boundary.query.QueryGatewayBoundaryService;
import org.alias.axon.serializer.example.messaging.boundary.query.QueryUpdateEmitterBoundaryService;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelEventHandler;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelQueryHandler;
import org.alias.axon.serializer.experimental.AliasableResourceBundleClassloader;
import org.axonframework.common.jdbc.ConnectionProvider;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.TrackingEventProcessorConfiguration;
import org.axonframework.eventhandling.tokenstore.jdbc.JdbcTokenStore;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.jdbc.JdbcEventStorageEngine;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * Axon Configuration.
 * 
 * @author JohT
 */
@Typed()
@ApplicationScoped
public class AxonConfiguration {

	@Inject
	@Eventsourcing
	private ConnectionProvider connectionProvider;

	@Inject
	private BeanManager beanManager;

	@Resource
	private ManagedThreadFactory threadFactory;

	private RegisteredAnnotatedTypes beanTypes;
	private AxonAggregateConfiguration aggregateConfiguration;
	private Configuration configuration;

	@PostConstruct
	protected void startUp() {
		beanTypes = RegisteredAnnotatedTypes.forBeans(beanManager.getBeans(Object.class, AnnotationLiteralAny.ANY));
		aggregateConfiguration = new AxonAggregateConfiguration(beanTypes);
		configuration = addDiscoveredComponentsTo(DefaultConfigurer.defaultConfiguration())//
				.configureSerializer(jacksonSerializer(JacksonObjectMapperConfiguration.DEFAULT.getObjectMapper()))
				.configureEventStore(eventStore())
				.configureTransactionManager(lookup(JtaTransactionManager.class))
				.start();
		enableBeanValidationForCommandMessages();
	}

	@PreDestroy
	protected void shutdown() {
		configuration.shutdown();
	}

	@Produces
	@ApplicationScoped
	public CommandGatewayBoundaryService getCommandGatewayBoundaryService() {
		return new CommandGatewayBoundaryServiceAdapter(configuration.commandGateway());
	}

	@Produces
	@ApplicationScoped
	public QueryGatewayBoundaryService getQueryGatewayBoundryService() {
		return new QueryGatewayBoundaryServiceAdapter(configuration.queryGateway());
	}

	@Produces
	@ApplicationScoped
	public QueryUpdateEmitterBoundaryService getQueryUpdateEmitterBoundaryService() {
		return new QueryUpdateEmitterBoundaryServiceAdapter(configuration.queryUpdateEmitter());
	}

	@Produces
	@ApplicationScoped
	public EventProcessorService getEventProcessorService() {
		return new EventProcessingServiceAdapter(configuration.eventProcessingConfiguration(), threadFactory);
	}

	@Produces
	@ApplicationScoped
	public AggregateEventService getAggregateEventService() {
		return new AggregateEventServiceAdapter();
	}

	/**
	 * This is the most important part of this example and test project: <br>
	 * It shows how to configure a {@link JacksonSerializer} to be capable to use aliases and to resolve types without
	 * default class loading.
	 * 
	 * @param objectMapper - {@link ObjectMapper}
	 * @return {@link Function} to get the {@link Serializer} for the given {@link Configuration}
	 */
	private static final Function<Configuration, Serializer> jacksonSerializer(ObjectMapper objectMapper) {
		// Note: AliasableResourceBundleClassloader is optional.
		// It is a workaround to ensure, that all types are resolved through the ResourceBundle.
		// Without this workaround, deserialization loads classes using the default classloader.
		// Alias names do not depend on this workaround. They work either way.
		ResourceBundle resourceBundle = MessageAliases.getResourceBundle();
		attachClassloaderToObjectMapper(objectMapper, AliasableResourceBundleClassloader.forResourceBundle(resourceBundle));
		JacksonSerializer serializer = JacksonSerializer.builder().objectMapper(objectMapper).build();
		return c -> TypeDecoratableSerializer.aliasThroughResourceBundle(serializer, resourceBundle);
	}

	private static void attachClassloaderToObjectMapper(ObjectMapper objectMapper, ClassLoader classLoader) {
		TypeFactory typeFactory = objectMapper.getTypeFactory();
		objectMapper.setTypeFactory(typeFactory.withClassLoader(classLoader));
	}

	private Function<Configuration, EventStore> eventStore() {
		return config -> {
			EmbeddedEventStore eventStore = EmbeddedEventStore.builder()
					.storageEngine(eventStorageEngine(config))
					.messageMonitor(config.messageMonitor(EmbeddedEventStore.class, "eventStore"))
					.threadFactory(threadFactory)
					.build();
			config.onShutdown(eventStore::shutDown);
			return eventStore;
		};
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
		aggregateConfiguration.registerAggregates(configurer);
		registerEventHandlers(configurer);
		registerCommandHandlers(configurer);
		registerQueryHandlers(configurer);
		return configurer;
	}

	private Configurer registerEventHandlers(Configurer configurer) {
		EventProcessingConfigurer eventProcessing = configurer.eventProcessing();
		// Note: The main projection (event handler) is notified synchronously within the publishing thread
		// and within the publishing transaction. If something goes wrong, everything inside the
		// transaction including the event is rolled back.
		eventProcessing.registerSubscribingEventProcessor("synchron");
		eventProcessing.assignProcessingGroup("query.model.member.account", "synchron");

		// Note: Other projections (e.g. for materialized reports) are notified asynchronously.
		// They shouldn't influence the main thread and aren't needed to react immediate ("eventual consistency").
		eventProcessing.registerTokenStore(this::jdbcTokenStore);
		eventProcessing.registerTrackingEventProcessorConfiguration(this::trackingEventProcessorConfig);

		for (Class<?> eventHandler : beanTypes.annotatedWith(QueryModelEventHandler.class)) {
			eventProcessing.registerEventHandler(lookup(eventHandler));
		}
		return configurer;
	}

	private JdbcTokenStore jdbcTokenStore(Configuration config) {
		return JdbcTokenStore.builder().connectionProvider(connectionProvider).serializer(config.serializer()).build();
	}

	private TrackingEventProcessorConfiguration trackingEventProcessorConfig(Configuration config) {
		// Note: CDI @RequestScoped and other scopes are not guaranteed to be available for threads started by a
		// ManagedThreadFactory. Only @ApplicationScoped and @Dependent can be used reliably.
		// If any other scope is needed, "deltaspike-cdictrl-api" could be a possible solution.
		return TrackingEventProcessorConfiguration.forSingleThreadedProcessing().andThreadFactory((processorGroup) -> threadFactory);
	}

	private Configurer registerCommandHandlers(Configurer configurer) {
		Collection<Class<?>> aggregates = beanTypes.annotatedWith(CommandModelAggregate.class);
		for (Class<?> commandHandler : beanTypes.annotatedWith(CommandModelCommandHandler.class)) {
			if (!aggregates.contains(commandHandler)) {
				configurer.registerCommandHandler(lookup(commandHandler));
			}
		}
		return configurer;
	}

	private Configurer registerQueryHandlers(Configurer configurer) {
		for (Class<?> queryHandler : beanTypes.annotatedWith(QueryModelQueryHandler.class)) {
			configurer.registerQueryHandler(lookup(queryHandler));
		}
		return configurer;
	}

	private void enableBeanValidationForCommandMessages() {
		configuration.commandBus().registerDispatchInterceptor(new BeanValidationInterceptor<>());
	}

	@Override
	public String toString() {
		return "AxonConfiguration [connectionProvider=" + connectionProvider + ", threadFactory=" + threadFactory + ", configuration="
				+ configuration + "]";
	}

	private static class AnnotationLiteralAny extends AnnotationLiteral<Any> {

		private static final long serialVersionUID = 1L;
		public static final AnnotationLiteral<Any> ANY = new AnnotationLiteralAny();
	}
}