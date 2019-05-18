package org.alias.axon.serializer.example.internal;

import org.alias.axon.serializer.example.messages.CommandTargetAggregateIdentifier;
import org.alias.axon.serializer.example.messages.CommandTargetAggregateVersion;
import org.axonframework.modelling.command.AnnotationCommandTargetResolver;
import org.axonframework.test.aggregate.AggregateTestFixture;

/**
 * Internal class that prepares and configured the {@link AggregateTestFixture}.
 * 
 * @author JohT
 *
 * @param <T> The type of Aggregate tested in this Fixture
 */
public class PreconfiguredAggregateTestFixture<T> {

	private final AggregateTestFixture<T> aggregateFixture;

	public PreconfiguredAggregateTestFixture(Class<T> aggregateType) {
		this.aggregateFixture = new AggregateTestFixture<>(aggregateType);
		setUp();
	}

	private void setUp() {
		getFixture().registerCommandTargetResolver(commandTargetResolver());
		getFixture().registerInjectableResource(new AggregateEventServiceStub());
	}

	private static AnnotationCommandTargetResolver commandTargetResolver() {
		return AnnotationCommandTargetResolver.builder()
				.targetAggregateIdentifierAnnotation(CommandTargetAggregateIdentifier.class)
				.targetAggregateVersionAnnotation(CommandTargetAggregateVersion.class)
				.build();
	}

	public AggregateTestFixture<T> getFixture() {
		return aggregateFixture;
	}
}