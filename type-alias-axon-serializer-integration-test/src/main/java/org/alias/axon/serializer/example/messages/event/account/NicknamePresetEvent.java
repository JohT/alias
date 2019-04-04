package org.alias.axon.serializer.example.messages.event.account;

import java.beans.ConstructorProperties;

import org.alias.annotation.TypeAlias;

@TypeAlias("NicknamePreset")
public class NicknamePresetEvent extends NicknameChangedEvent {

	private static final String NO_NICKNAME = "";

	public static final NicknamePresetEvent noNicknameFor(String accountId) {
		return new NicknamePresetEvent(accountId, NO_NICKNAME);
	}

	@ConstructorProperties({ "accountId", "nickname" })
	public NicknamePresetEvent(String accountId, String nickname) {
		super(accountId, nickname);
	}
}