package org.alias.axon.serializer.example.messages;

import java.util.ResourceBundle;

/**
 * Provides a type-safe way to obtain the {@link ResourceBundle},<br>
 * that contains the type aliases for all message types.
 * 
 * @author JohT
 */
public final class MessageAliases {

	public static final String MESSAGE_ALIASES_RESOURCE_BUNDLE_LOCATION = "org.alias.axon.serializer.example.messages";
	public static final String MESSAGE_ALIASES_RESOURCE_BUNDLE_NAME = "MessageTypeAliases";

	private MessageAliases() {
		super();
	}

	public static final ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(MESSAGE_ALIASES_RESOURCE_BUNDLE_LOCATION + "." + MESSAGE_ALIASES_RESOURCE_BUNDLE_NAME);
	}
}
