package org.alias.axon.serializer.example.query.model.member.account;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.alias.annotation.TypeAlias;
import org.axonframework.queryhandling.QueryHandler;

@ApplicationScoped
@TypeAlias("AccountQuery")
public class AccountQueryHandler {

	@Inject
	private AccountRepository account;

	@QueryHandler(queryName = "memberqueryAccountNickname")
	public String getAccountNickname(String accountId) {
		return account.read(keyOf(accountId)).getNickname();
	}

	private static AccountEntityKey keyOf(String accountId) {
		return new AccountEntityKey(accountId);
	}
}