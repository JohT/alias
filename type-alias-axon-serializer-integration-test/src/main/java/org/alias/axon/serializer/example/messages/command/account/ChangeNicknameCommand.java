package org.alias.axon.serializer.example.messages.command.account;

import org.alias.axon.serializer.example.messages.CommandTargetAggregateIdentifier;

public class ChangeNicknameCommand {

	@CommandTargetAggregateIdentifier
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