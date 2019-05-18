package org.alias.axon.serializer.example.messages.query.account;

import java.util.Objects;

import org.alias.annotation.TypeAlias;

@TypeAlias("AccountNicknameQuery")
public class AccountNicknameQuery {

	private final String accountId;

	public AccountNicknameQuery(String accountId) {
		this.accountId = accountId.trim();
	}

	public String getAccountId() {
		return accountId;
	}

	@Override
	public String toString() {
		return "AccountNicknameQuery [accountId=" + accountId + "]";
	}

	@Override
	public boolean equals(final Object other) {
		if (other == null) {
			return false;
		}
		if (!getClass().equals(other.getClass())) {
			return false;
		}
		AccountNicknameQuery castOther = (AccountNicknameQuery) other;
		return Objects.equals(accountId, castOther.accountId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(accountId);
	}
}