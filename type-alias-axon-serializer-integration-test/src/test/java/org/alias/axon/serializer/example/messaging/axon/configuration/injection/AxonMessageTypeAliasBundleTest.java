package org.alias.axon.serializer.example.messaging.axon.configuration.injection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ResourceBundle;

import org.alias.axon.serializer.example.messages.MessageAliases;
import org.axonframework.eventhandling.GapAwareTrackingToken;
import org.axonframework.messaging.MetaData;
import org.junit.jupiter.api.Test;

public class AxonMessageTypeAliasBundleTest {

	private ResourceBundle bundle = MessageAliases.getResourceBundle();

	@Test
	public void methodMetaDataHasAlias() {
		assertEquals(MetaData.class, bundle.getObject("MessagingMetaData"));
	}

	@Test
	public void messagingGapAwareTrackingTokenHasAlias() {
		assertEquals(GapAwareTrackingToken.class, bundle.getObject("MessagingGapAwareTrackingToken"));
	}
}
