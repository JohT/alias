package org.alias.axon.serializer;

import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.axonframework.messaging.core.MessageType;
import org.axonframework.messaging.core.MessageTypeResolver;

/**
 * A {@link MessageTypeResolver} that looks up type aliases from a {@link ResourceBundle} and delegates to a fallback
 * resolver when no alias is found.
 * <p>
 * Use {@link #aliasThroughDefaultResourceBundle(MessageTypeResolver)} to apply aliases defined in the default
 * {@value #DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME} resource bundle, or
 * {@link #aliasThroughResourceBundle(MessageTypeResolver, ResourceBundle)} to supply an explicit bundle.
 *
 * @see #aliasThroughResourceBundle(MessageTypeResolver, ResourceBundle)
 * @author JohT
 */
public class AliasableMessageTypeResolver implements MessageTypeResolver {

    public static final String DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME = "TypeAlias";

    private final MessageTypeResolver delegate;
    private final ResourceBundle resourceBundle;

    /**
     * Creates a {@link MessageTypeResolver} that uses the default {@value #DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME}
     * resource bundle to look up type aliases.
     *
     * @param delegate fallback {@link MessageTypeResolver}
     * @return {@link MessageTypeResolver}
     */
    public static MessageTypeResolver aliasThroughDefaultResourceBundle(MessageTypeResolver delegate) {
        return aliasThroughResourceBundle(delegate, ResourceBundle.getBundle(DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME));
    }

    /**
     * Creates a {@link MessageTypeResolver} that uses the given {@link ResourceBundle} to look up type aliases.
     * <p>
     * The resource bundle must contain the fully qualified class name as key mapped to the alias as a {@link String}
     * value.
     *
     * @param delegate       fallback {@link MessageTypeResolver}
     * @param resourceBundle {@link ResourceBundle} with alias mappings
     * @return {@link MessageTypeResolver}
     */
    public static MessageTypeResolver aliasThroughResourceBundle(MessageTypeResolver delegate, ResourceBundle resourceBundle) {
        return new AliasableMessageTypeResolver(delegate, resourceBundle);
    }

    private AliasableMessageTypeResolver(MessageTypeResolver delegate, ResourceBundle resourceBundle) {
        this.delegate = delegate;
        this.resourceBundle = resourceBundle;
    }

    @Override
    public Optional<MessageType> resolve(Class<?> payloadType) {
        String alias = aliasFor(payloadType.getName());
        if (alias != null && !alias.isEmpty()) {
            return Optional.of(new MessageType(alias, MessageType.DEFAULT_VERSION));
        }
        return delegate.resolve(payloadType);
    }

    private String aliasFor(String typeName) {
        try {
            Object value = resourceBundle.getObject(typeName);
            if (value instanceof String) {
                return (String) value;
            }
        } catch (MissingResourceException ignored) {
            // no alias registered — delegate
        }
        return null;
    }

    @Override
    public String toString() {
        return "AliasableMessageTypeResolver [delegate=" + delegate + ", resourceBundle=" + resourceBundle + "]";
    }
}
