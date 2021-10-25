package org.alias.axon.serializer.example.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.junit.jupiter.api.Test;

public class MessageTypeAliasBundleTest {

	private ResourceBundle bundle = MessageAliases.getResourceBundle();

	@Test
	public void accountCreatedRegistered() {
		assertEquals(AccountCreatedEvent.class, bundle.getObject("AccountCreated"));
	}

	@Test
	public void nicknameChangedRegistered() {
		assertEquals(NicknameChangedEvent.class, bundle.getObject("NicknameChanged"));
	}

	@Test
	public void nicknamePresetRegistered() {
		assertEquals(NicknamePresetEvent.class, bundle.getObject("NicknamePreset"));
	}
}
