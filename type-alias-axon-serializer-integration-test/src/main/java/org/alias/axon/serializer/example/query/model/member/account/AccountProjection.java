package org.alias.axon.serializer.example.query.model.member.account;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.alias.annotation.TypeAlias;
import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.axonframework.eventhandling.EventHandler;

@Dependent
@TypeAlias("AccountProjection")
public class AccountProjection {

	@Inject
	private AccountRepository account;

	@EventHandler
	private void onCreated(AccountCreatedEvent event) {
		account.create(keyOf(event.getAccountId()));
	}

	@EventHandler
	private void onNicknameChanged(NicknameChangedEvent event) {
		account.read(keyOf(event.getAccountId())).setNickname(event.getNickname());
	}

	private static AccountEntityKey keyOf(String accountId) {
		return new AccountEntityKey(accountId);
	}
}