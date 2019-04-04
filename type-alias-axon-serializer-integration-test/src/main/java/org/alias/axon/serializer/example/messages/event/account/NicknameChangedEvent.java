package org.alias.axon.serializer.example.messages.event.account;

import java.beans.ConstructorProperties;

import org.alias.annotation.TypeAlias;

@TypeAlias("NicknameChanged")
public class NicknameChangedEvent {

	private final String accountId;
	private final String nickname;

	@ConstructorProperties({ "accountId", "nickname" })
	public NicknameChangedEvent(String accountId, String nickname) {
		this.accountId = accountId;
		this.nickname = nickname;
	}

	public String getAccountId() {
		return accountId;
	}

	public String getNickname() {
		return nickname;
	}

	@Override
	public String toString() {
		return "NicknameChangedEvent [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}