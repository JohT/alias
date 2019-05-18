package org.alias.axon.serializer.example.messaging.boundary.command;

/**
 * Provides methods to send events from aggregates.
 * <p>
 * This service is not meant to replace direct static calls to "apply" inside aggregate command handlers. This service
 * is not mean't to be used to send events outside an aggregate.<br>
 * 
 * @author JohT
 */
public interface AggregateEventService {

	/**
	 * Immediately applies (publishes) the event with the given payload on all entities part of this aggregate.
	 *
	 * @param payload the payload of the event to apply
	 */
	void apply(Object payload);
}
