package org.alias.axon.serializer.example.domain.model.account;

import java.beans.ConstructorProperties;
import java.util.Objects;

import org.alias.axon.serializer.example.messages.command.account.ChangeNicknameCommand;
import org.alias.axon.serializer.example.messages.command.account.CreateAccountCommand;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.alias.axon.serializer.example.messaging.boundary.command.AggregateEventService;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelAggregate;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelAggregateIdentifier;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelCommandHandler;
import org.alias.axon.serializer.example.messaging.boundary.command.model.CommandModelEventSourcingHandler;

@CommandModelAggregate(type = "Account")
public class AccountAggregate {

	@CommandModelAggregateIdentifier
	private String accountId;

	private String nickname;

	/**
	 * @deprecated Only for frameworks. Not meant to be called directly.
	 */
	@Deprecated
	protected AccountAggregate() {
		super();
	}

	@ConstructorProperties({ "accountId" })
	public AccountAggregate(String accountId) {
		this.accountId = accountId;
	}

	@CommandModelCommandHandler
	public static final AccountAggregate createWith(CreateAccountCommand command, AggregateEventService eventService) {
		AccountAggregate newAggregate = new AccountAggregate(command.getAccountId());
		eventService.apply(new AccountCreatedEvent(command.getAccountId()));
		eventService.apply(NicknamePresetEvent.noNicknameFor(command.getAccountId()));
		return newAggregate;
	}

	@CommandModelCommandHandler
	public void changeNickname(ChangeNicknameCommand command, AggregateEventService eventService) {
		if (!Objects.equals(getNickname(), command.getNickname())) {
			eventService.apply(new NicknameChangedEvent(accountId, command.getNickname()));
		}
	}

	@CommandModelEventSourcingHandler
	private void on(AccountCreatedEvent event) {
		this.accountId = event.getAccountId();
	}

	@CommandModelEventSourcingHandler
	private void on(NicknameChangedEvent event) {
		this.nickname = event.getNickname();
	}

	public String getAccountId() {
		return accountId;
	}

	public String getNickname() {
		return nickname;
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountAggregate castOther = (AccountAggregate) other;
		return Objects.equals(accountId, castOther.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}

	@Override
	public String toString() {
		return "AccountAggregate [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}