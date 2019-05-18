package org.alias.axon.serializer.example.messaging.axon;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.alias.axon.serializer.example.messaging.boundary.query.EventProcessorService;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackedEventMessage;
import org.axonframework.eventhandling.TrackingEventProcessor;

public class EventProcessingServiceAdapter implements EventProcessorService {

	private final EventProcessingConfiguration eventProcessing;
	private final ThreadFactory threadFactory;

	public EventProcessingServiceAdapter(EventProcessingConfiguration eventProcessing, ThreadFactory threadFactory) {
		this.eventProcessing = eventProcessing;
		this.threadFactory = threadFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean waitForMessage(String processingGroup, Class<?> messageType, String messageContent) {
		TrackingEventProcessor trackingEventProcessor = getTrackingEventProcessor(processingGroup);
		Stream<? extends TrackedEventMessage<?>> stream = trackingEventProcessor.getMessageSource()
				.openStream(null)
				.asStream();
		ExecutorService executor = Executors.newSingleThreadExecutor(threadFactory);
		Future<Boolean> result = executor.submit(() -> stream.anyMatch(containsMessageWithContent(messageType, messageContent)));
		try {
			return result.get(3, TimeUnit.SECONDS).booleanValue();
		} catch (InterruptedException | ExecutionException e) {
			throw new IllegalStateException(e);
		} catch (TimeoutException e) {
			return false;
		}
	}

	private Predicate<TrackedEventMessage<?>> containsMessageWithContent(Class<?> messageType, String messageContent) {
		return message -> message.getPayload().toString().contains(messageContent)
				&& message.getPayloadType().equals(messageType);
	}

	private TrackingEventProcessor getTrackingEventProcessor(String processingGroup) {
		return eventProcessing.eventProcessor(processingGroup, TrackingEventProcessor.class).get();
	}

	@Override
	public String toString() {
		return "EventProcessorServiceAdapter [eventProcessing=" + eventProcessing + "]";
	}
}