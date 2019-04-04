package org.alias.axon.serializer.example.messages.command.account;

import java.beans.ConstructorProperties;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CreateAccountCommand {

	@TargetAggregateIdentifier
	private final String accountId;

	@ConstructorProperties({ "accountId" })
	public CreateAccountCommand(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	@Override
	public String toString() {
		return "CreateAccountCommand [accountId=" + accountId + "]";
	}
}