package org.alias.axon.serializer.example.messages.command.account;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ChangeNicknameCommand {

	@TargetAggregateIdentifier
	private final String accountId;

	private final String nickname;

	public ChangeNicknameCommand(String accountId, String nickname) {
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
		return "ChangeNicknameCommand [accountId=" + accountId + ", nickname=" + nickname + "]";
	}
}