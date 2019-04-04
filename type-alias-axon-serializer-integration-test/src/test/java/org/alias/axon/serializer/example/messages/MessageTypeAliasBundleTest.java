package org.alias.axon.serializer.example.messages;

import static org.junit.Assert.assertEquals;

import java.util.ResourceBundle;

import org.alias.axon.serializer.example.messages.event.account.AccountCreatedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknameChangedEvent;
import org.alias.axon.serializer.example.messages.event.account.NicknamePresetEvent;
import org.junit.Before;
import org.junit.Test;

public class MessageTypeAliasBundleTest {

	private static final String BUNDLE_NAME = "TypeAlias";

	private ResourceBundle bundle;

	@Before
	public void setUp() {
		bundle = ResourceBundle.getBundle(BUNDLE_NAME);
	}

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
