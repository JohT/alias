package org.alias.axon.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.Optional;

import org.alias.axon.serializer.example.messages.DoSomethingCommand;
import org.alias.axon.serializer.example.messages.SomethingHappenedEvent;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.commandhandling.GenericCommandResultMessage;
import org.axonframework.messaging.commandhandling.configuration.CommandHandlingModule;
import org.axonframework.messaging.core.ClassBasedMessageTypeResolver;
import org.axonframework.messaging.core.MessageStream;
import org.axonframework.messaging.core.MessageType;
import org.axonframework.messaging.core.MessageTypeResolver;
import org.axonframework.messaging.core.QualifiedName;
import org.axonframework.messaging.eventhandling.EventSink;
import org.axonframework.messaging.eventhandling.GenericEventMessage;
import org.axonframework.test.fixture.AxonTestFixture;
import org.junit.jupiter.api.Test;

/**
 * Integration test that verifies {@link AliasableMessageTypeResolver} correctly resolves type aliases
 * from the annotation-processor-generated {@code TypeAlias} resource bundle, and that these aliases
 * are applied as the {@link MessageType} name when events are published within the Axon framework.
 */
public class TypeAliasMessageTypeResolverIT {

	/**
	 * Verifies that the {@link AliasableMessageTypeResolver} resolves the alias defined via
	 * {@code @TypeAlias} on {@link SomethingHappenedEvent} using the annotation-processor-generated
	 * {@code TypeAlias} resource bundle.
	 */
	@Test
	void shouldResolveAliasFromAnnotation() {
		// GIVEN: resolver backed by the generated TypeAlias resource bundle
		MessageTypeResolver resolver = AliasableMessageTypeResolver
				.aliasThroughDefaultResourceBundle(new ClassBasedMessageTypeResolver());

		// WHEN: resolving the type of SomethingHappenedEvent
		Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

		// THEN: the alias from @TypeAlias("SomethingHappened") is returned
		assertThat(result.isPresent(), is(true));
		assertThat(result.get().name(), is("SomethingHappened"));
		assertThat(result.get().version(), is(MessageType.DEFAULT_VERSION));
	}

	/**
	 * Verifies that when the {@link AliasableMessageTypeResolver} is configured in the Axon framework,
	 * events published by a command handler carry the alias name as their {@link MessageType#name()}.
	 */
	@Test
	void shouldPublishEventWithAliasedMessageType() {
		// GIVEN: Axon framework configured with AliasableMessageTypeResolver
		QualifiedName commandName = new QualifiedName(DoSomethingCommand.class);

		AxonTestFixture fixture = AxonTestFixture.with(
				EventSourcingConfigurer.create()
						.registerCommandHandlingModule(
								CommandHandlingModule.named("test").commandHandlers(
										phase -> phase.commandHandler(
												commandName,
												config -> (cmd, ctx) -> {
													MessageTypeResolver resolver = config.getComponent(MessageTypeResolver.class);
													MessageType eventType = resolver.resolve(SomethingHappenedEvent.class).orElseThrow();
													EventSink eventSink = config.getComponent(EventSink.class);
													eventSink.publish(ctx, new GenericEventMessage(eventType, new SomethingHappenedEvent("id1")));
													return MessageStream.just(new GenericCommandResultMessage(
															new MessageType("result", MessageType.DEFAULT_VERSION), null));
												}
										)
								)
						)
						.messaging(m -> m.registerMessageTypeResolver(
								config -> AliasableMessageTypeResolver.aliasThroughDefaultResourceBundle(
										new ClassBasedMessageTypeResolver())))
		);

		// WHEN: DoSomethingCommand is dispatched
		// THEN: the published event carries the alias "SomethingHappened" as its message type name
		fixture.given()
				.noPriorActivity()
				.when()
				.command(new DoSomethingCommand("id1"))
				.then()
				.success()
				.eventsSatisfy(events -> {
					assertThat(events, hasSize(1));
					assertThat(events.get(0).type().name(), is("SomethingHappened"));
				});
	}
}
