package org.alias.axon.serializer.example.messages;

import java.beans.ConstructorProperties;

/**
 * Example command used in the integration test to trigger publication of a {@link SomethingHappenedEvent}.
 */
public class DoSomethingCommand {

	private final String aggregateId;

	@ConstructorProperties({ "aggregateId" })
	public DoSomethingCommand(String aggregateId) {
		this.aggregateId = aggregateId;
	}

	public String getAggregateId() {
		return aggregateId;
	}

	@Override
	public String toString() {
		return "DoSomethingCommand [aggregateId=" + aggregateId + "]";
	}
}
