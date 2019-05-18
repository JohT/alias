package org.alias.axon.serializer.example.messaging.boundary.query;

import java.util.NoSuchElementException;

public interface EventProcessorService {

	/**
	 * Waits, until the given message type with the given message content shows up within the given processing group.
	 * <p>
	 * Returns <code>false</code>, if there is no matching message after a reasonable amount of time.
	 * 
	 * @param processingGroup {@link String} name of the processing group
	 * @param messageType     {@link Class} of the message (event)
	 * @param messageContent  {@link String} part of the message content
	 * @return <code>true</code>, if successful. <code>false</code>, if a timeout occurred.
	 * @throws NoSuchElementException if the processingGroup does not exist or is not working asynchronously.
	 */
	boolean waitForMessage(String processingGroup, Class<?> messageType, String messageContent);

}
