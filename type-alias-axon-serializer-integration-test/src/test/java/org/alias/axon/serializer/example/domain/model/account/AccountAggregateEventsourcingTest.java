package org.alias.axon.serializer.example.domain.model.account;

import org.alias.axon.serializer.example.messages.command.account.ChangeNicknameCommand;
import org.alias.axon.serializer.example.messages.command.account.CreateAccountCommand;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.Before;
import org.junit.Test;

public class AccountAggregateEventsourcingTest {

	private static final String ID = "1234";

	private AggregateTestFixture<AccountAggregate> accountFixture;

	@Before
	public void setUp() throws Exception {
		accountFixture = new AggregateTestFixture<>(AccountAggregate.class);
	}

	@Test
	public void createdAccountPresetsNickname() {
		accountFixture.givenNoPriorActivity()
				.when(new CreateAccountCommand(ID))
				.expectEvents(new AccountCreatedEvent(ID), new NicknamePresetEvent(ID, ""));
	}

	@Test
	public void nicknameShouldBeChangeable() {
		String nickname = "TestNickname";
		accountFixture.givenCommands(new CreateAccountCommand(ID))
				.when(new ChangeNicknameCommand(ID, nickname))
				.expectEvents(new NicknameChangedEvent(ID, nickname));
	}
}
