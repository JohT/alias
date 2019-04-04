package org.alias.axon.serializer.example.domain.model.account;

import java.beans.ConstructorProperties;
import java.util.Objects;

import org.alias.annotation.TypeAlias;
import org.alias.axon.serializer.example.messages.command.account.ChangeNicknameCommand;
import org.alias.axon.serializer.example.messages.command.account.CreateAccountCommand;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.modelling.command.AggregateRoot;

@AggregateRoot(type = "Account")
@TypeAlias("AccountAggregate")
public class AccountAggregate {

	@AggregateIdentifier
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

	@CommandHandler
	public static final AccountAggregate createWith(CreateAccountCommand command) {
		AccountAggregate newAggregate = new AccountAggregate(command.getAccountId());
		AggregateLifecycle.apply(new AccountCreatedEvent(command.getAccountId()));
		AggregateLifecycle.apply(NicknamePresetEvent.noNicknameFor(command.getAccountId()));
		return newAggregate;
	}

	@CommandHandler
	public void changeNickname(ChangeNicknameCommand command) {
		if (!Objects.equals(getNickname(), command.getNickname())) {
			AggregateLifecycle.apply(new NicknameChangedEvent(accountId, command.getNickname()));
		}
	}

	@EventSourcingHandler
	private void on(AccountCreatedEvent event) {
		this.accountId = event.getAccountId();
	}

	@EventSourcingHandler
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
		return Objects.equals(accountId, castOther.accountId) && Objects.equals(nickname, castOther.nickname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId, nickname);
	}

	@Override
	public String toString() {
		return "AccountAggregate [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}