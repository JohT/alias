package org.alias.axon.serializer.example.messages;

import java.beans.ConstructorProperties;

import org.alias.annotation.TypeAlias;

/**
 * Example domain event annotated with {@link TypeAlias} to demonstrate alias-based message type resolution.
 */
@TypeAlias("SomethingHappened")
public class SomethingHappenedEvent {

	private final String aggregateId;

	@ConstructorProperties({ "aggregateId" })
	public SomethingHappenedEvent(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	@Override
	public String toString() {
		return "SomethingHappenedEvent [aggregateId=" + aggregateId + "]";
	}
}
