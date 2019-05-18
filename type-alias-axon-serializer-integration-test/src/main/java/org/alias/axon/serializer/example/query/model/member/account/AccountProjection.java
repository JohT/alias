package org.alias.axon.serializer.example.query.model.member.account;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelEventHandler;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelProjection;

//Note: Without @Dependent, injection is not done and the account will be null leading to a NullPointerException.
//Note: Without @ProcessingGroup, the name of the package is used as fallback which also leads to a NullPointerException.
//For details see: https://github.com/AxonFramework/AxonFramework/issues/940
@Dependent
@QueryModelProjection(processingGroup = "query.model.member.account")
public class AccountProjection {

	@Inject
	private AccountRepository account;

	@QueryModelEventHandler
	private void onCreated(AccountCreatedEvent event) {
		account.create(keyOf(event.getAccountId()));
	}

	@QueryModelEventHandler
	private void onNicknameChanged(NicknameChangedEvent event) {
		account.setNickname(keyOf(event.getAccountId()), event.getNickname());
	}

	private static AccountEntityKey keyOf(String accountId) {
		return new AccountEntityKey(accountId);
	}
}