package org.alias.axon.serializer.example.messages.event.account;

import java.beans.ConstructorProperties;

import org.alias.annotation.TypeAlias;

@TypeAlias("AccountCreated")
public class AccountCreatedEvent {

    private final String accountId;

	@ConstructorProperties({ "accountId" })
	public AccountCreatedEvent(String accountId) {
		this.accountId = accountId;
    }

	public String getAccountId() {
        return accountId;
    }

	@Override
	public String toString() {
		return "AccountCreatedEvent [accountId=" + accountId + "]";
	}
}
