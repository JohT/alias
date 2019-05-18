package org.alias.axon.serializer.example.messaging.axon;

import javax.enterprise.inject.Typed;

import org.alias.axon.serializer.example.messaging.boundary.command.AggregateEventService;
import org.axonframework.modelling.command.AggregateLifecycle;

@Typed()
public class AggregateEventServiceAdapter implements AggregateEventService {

	@Override
	public void apply(Object payload) {
		AggregateLifecycle.apply(payload);
	}

}
