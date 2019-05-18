package org.alias.axon.serializer.example.messaging.axon.configuration;

import org.alias.axon.serializer.example.messages.CommandTargetAggregateIdentifier;
import org.alias.axon.serializer.example.messages.CommandTargetAggregateVersion;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelAggregate;
import org.axonframework.config.AggregateConfigurer;
import org.axonframework.config.Configuration;
import org.axonframework.config.Configurer;
import org.axonframework.eventsourcing.AggregateSnapshotter;
import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.GenericAggregateFactory;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.modelling.command.AnnotationCommandTargetResolver;

/**
 * Configures all aggregates using {@link AggregateConfigurer}.
 * 
 * @author JohT
 */
class AxonAggregateConfiguration {

	private final RegisteredAnnotatedTypes beanTypes;

	public AxonAggregateConfiguration(RegisteredAnnotatedTypes beanTypes) {
		this.beanTypes = beanTypes;
	}

	public Configurer registerAggregates(Configurer configurer) {
		for (Class<?> aggregate : beanTypes.annotatedWith(CommandModelAggregate.class)) {
			configurer.configureAggregate(aggregateConfigurationFor(aggregate));
		}
		return configurer;
	}

	private AggregateConfigurer<?> aggregateConfigurationFor(Class<?> aggregate) {
		AggregateConfigurer<?> aggregateConfigurer = AggregateConfigurer.defaultConfiguration(aggregate);
		aggregateConfigurer.configureSnapshotTrigger(config -> snapshotConfiguration(aggregateConfigurer, config));
		// Note: Using self defined annotations for "AggregateIdentifier" and "AggregateVersion" located nearby the command
		// value objects and connecting them inside the axon configuration, removes the dependency between the API (containing
		// the commands) and "axon-modelling". The goal isn't, to share the API module (better avoid that),
		// but to make the API as independent as possible.
		//
		// Note: Ideally, there would also be no dependency between from the API to "type-alias".
		// Maybe there will be a way to do that in future (generate TypeAlias for another jar).
		// In the meanwhile, great care had been taken to make "type-alias" small, stable and compile-time-only.
		aggregateConfigurer.configureCommandTargetResolver(config -> annotationCommandTargetResolver());
		return aggregateConfigurer;
	}

	private static AnnotationCommandTargetResolver annotationCommandTargetResolver() {
		return AnnotationCommandTargetResolver.builder()
				.targetAggregateIdentifierAnnotation(CommandTargetAggregateIdentifier.class)
				.targetAggregateVersionAnnotation(CommandTargetAggregateVersion.class)
				.build();
	}

	private EventCountSnapshotTriggerDefinition snapshotConfiguration(AggregateConfigurer<?> aggregateConfigurer, Configuration config) {
		return new EventCountSnapshotTriggerDefinition(snapshotter(config, aggregateConfigurer), 5);
	}

	private <T> Snapshotter snapshotter(Configuration configuration, AggregateConfigurer<T> aggregate) {
		return AggregateSnapshotter.builder()
				.eventStore(configuration.eventStore())
				.aggregateFactories(new GenericAggregateFactory<T>(aggregate.aggregateType()))
				.build();
	}

	@Override
	public String toString() {
		return "AxonAggregateConfiguration [beanTypes=" + beanTypes + "]";
	}
}