package org.alias.axon.serializer.example.query.model.member.account;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alias.axon.serializer.example.messages.query.account.AccountNicknameQuery;
import org.alias.axon.serializer.example.messaging.boundary.query.model.QueryModelQueryHandler;

@ApplicationScoped
public class AccountQueryHandler {

	@Inject
	private AccountRepository account;

	@QueryModelQueryHandler
	public String getAccountNickname(AccountNicknameQuery query) {
		return account.read(keyOf(query.getAccountId())).getNickname();
	}

	private static AccountEntityKey keyOf(String accountId) {
		return new AccountEntityKey(accountId);
	}
}