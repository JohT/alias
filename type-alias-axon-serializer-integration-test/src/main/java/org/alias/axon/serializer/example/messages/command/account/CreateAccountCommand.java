package org.alias.axon.serializer.example.messages.command.account;

import java.beans.ConstructorProperties;

import org.alias.axon.serializer.example.messages.CommandTargetAggregateIdentifier;

public class CreateAccountCommand {

	@CommandTargetAggregateIdentifier
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