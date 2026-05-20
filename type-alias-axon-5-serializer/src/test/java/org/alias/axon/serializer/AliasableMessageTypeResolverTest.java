package org.alias.axon.serializer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ListResourceBundle;
import java.util.Optional;
import java.util.ResourceBundle;

import org.axonframework.messaging.core.MessageType;
import org.axonframework.messaging.core.MessageTypeResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link AliasableMessageTypeResolver} in BDD style.
 */
@ExtendWith(MockitoExtension.class)
class AliasableMessageTypeResolverTest {

    private static final String ALIAS = "SomethingHappened";
    private static final String FQCN = SomethingHappenedEvent.class.getName();
    private static final MessageType DELEGATE_RESULT = new MessageType("DelegateResult", MessageType.DEFAULT_VERSION);

    @Mock
    private MessageTypeResolver delegate;

    private ResourceBundle bundleWithAlias;
    private ResourceBundle emptyBundle;

    @BeforeEach
    void setUp() {
        bundleWithAlias = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] { { FQCN, ALIAS } };
            }
        };
        emptyBundle = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] {};
            }
        };
    }

    // --- Given alias in ResourceBundle ---

    @Test
    void shouldReturnAliasWhenFoundInResourceBundle() {
        // Given
        MessageTypeResolver resolver = AliasableMessageTypeResolver.aliasThroughResourceBundle(delegate, bundleWithAlias);

        // When
        Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

        // Then
        assertThat("Expected alias MessageType to be present", result.isPresent(), is(true));
        assertThat("Expected alias name", result.get().name(), equalTo(ALIAS));
        assertThat("Expected DEFAULT_VERSION", result.get().version(), equalTo(MessageType.DEFAULT_VERSION));
        verifyNoMoreInteractions(delegate);
    }

    // --- Given no alias in ResourceBundle ---

    @Test
    void shouldDelegateWhenNoAliasFound() {
        // Given
        when(delegate.resolve(SomethingHappenedEvent.class)).thenReturn(Optional.of(DELEGATE_RESULT));
        MessageTypeResolver resolver = AliasableMessageTypeResolver.aliasThroughResourceBundle(delegate, emptyBundle);

        // When
        Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

        // Then
        assertThat("Expected delegate result", result, equalTo(Optional.of(DELEGATE_RESULT)));
        verify(delegate).resolve(SomethingHappenedEvent.class);
    }

    // --- Given ResourceBundle entry with non-String value ---

    @Test
    void shouldDelegateWhenResourceBundleValueIsNotAString() {
        // Given
        ResourceBundle bundleWithNonStringValue = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] { { FQCN, Integer.valueOf(42) } };
            }
        };
        when(delegate.resolve(SomethingHappenedEvent.class)).thenReturn(Optional.of(DELEGATE_RESULT));
        MessageTypeResolver resolver = AliasableMessageTypeResolver.aliasThroughResourceBundle(delegate, bundleWithNonStringValue);

        // When
        Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

        // Then
        assertThat("Expected delegate result when value is not a String", result, equalTo(Optional.of(DELEGATE_RESULT)));
        verify(delegate).resolve(SomethingHappenedEvent.class);
    }

    // --- Given empty string alias in ResourceBundle ---

    @Test
    void shouldDelegateWhenAliasIsEmpty() {
        // Given
        ResourceBundle bundleWithEmptyAlias = new ListResourceBundle() {
            @Override
            protected Object[][] getContents() {
                return new Object[][] { { FQCN, "" } };
            }
        };
        when(delegate.resolve(SomethingHappenedEvent.class)).thenReturn(Optional.of(DELEGATE_RESULT));
        MessageTypeResolver resolver = AliasableMessageTypeResolver.aliasThroughResourceBundle(delegate, bundleWithEmptyAlias);

        // When
        Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

        // Then
        assertThat("Expected delegate result when alias is empty", result, equalTo(Optional.of(DELEGATE_RESULT)));
        verify(delegate).resolve(SomethingHappenedEvent.class);
    }

    // --- Given multiple types, only one with alias ---

    @Test
    void shouldOnlyAliasTypesRegisteredInResourceBundle() {
        // Given
        MessageTypeResolver resolver = AliasableMessageTypeResolver.aliasThroughResourceBundle(delegate, bundleWithAlias);
        when(delegate.resolve(UnregisteredEvent.class)).thenReturn(Optional.of(DELEGATE_RESULT));

        // When
        Optional<MessageType> aliasedResult = resolver.resolve(SomethingHappenedEvent.class);
        Optional<MessageType> unregisteredResult = resolver.resolve(UnregisteredEvent.class);

        // Then
        assertThat("Expected alias for registered type", aliasedResult.get().name(), equalTo(ALIAS));
        assertThat("Expected delegate result for unregistered type", unregisteredResult, equalTo(Optional.of(DELEGATE_RESULT)));
    }

    // --- Test helper inner classes ---

    private static final class SomethingHappenedEvent {
    }

    private static final class UnregisteredEvent {
    }
}
