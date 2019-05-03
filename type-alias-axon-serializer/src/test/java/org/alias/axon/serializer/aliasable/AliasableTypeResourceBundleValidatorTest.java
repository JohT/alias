package org.alias.axon.serializer.aliasable;

import static org.junit.Assert.assertTrue;

import java.util.ResourceBundle;

import org.alias.axon.serializer.TypeAliasResourceBundleFixture;
import org.junit.Test;

public class AliasableTypeResourceBundleValidatorTest {

	private static final String ALIAS = "Alias";
	private static final Class<?> TYPE = AliasableTypeResourceBundleRegisterTest.class;
	private static final ResourceBundle VALID_RESOURCE_BUNDLE = TypeAliasResourceBundleFixture.containing(ALIAS, TYPE);
	private static final ResourceBundle INVALID_RESOURCE_BUNDLE = new AliasTypeFixtureInvalidResourceBundle();

	/**
	 * class under test.
	 */
	private AliasableTypeResourceBundleValidator validator = new AliasableTypeResourceBundleValidator(INVALID_RESOURCE_BUNDLE);

	@Test
	public void doNotFailWhenThereAreNoErrors() {
		AliasableTypeResourceBundleValidator.failOnErrorsOf(VALID_RESOURCE_BUNDLE);
	}

	@Test
	public void failsOnEmptyKey() {
		String messages = validator.validate().toString();
		assertContains("Empty keys are not allowed", messages);
	}

	@Test
	public void failsOnUnexpectedValueType() {
		String messages = validator.validate().toString();
		assertContains(AliasTypeFixtureInvalidResourceBundle.KEY_TO_UNEXPECTED_VALUE_TYPE, messages);
		assertContains(AliasTypeFixtureInvalidResourceBundle.UNEXPECTED_VALUE_TYPE.toString(), messages);
		assertContains("is not a String or Class type.", messages);
	}

	@Test
	public void failsOnUValueThatDoesNotExistAsAKey() {
		String messages = validator.validate().toString();
		assertContains(AliasTypeFixtureInvalidResourceBundle.KEY_TO_VALUE_THAT_DOES_NOT_EXIST_AS_KEY, messages);
		assertContains(AliasTypeFixtureInvalidResourceBundle.VALUE_THAT_DOES_NOT_EXIST_AS_KEY.toString(), messages);
		assertContains("is not a String or Class type.", messages);
		assertContains("should also be available as key", messages);
	}

	/**
	 * The alias name should lead to the assigned {@link Class}. The name of the assigned {@link Class} should also be
	 * available as key and should again lead to the alias. If this circle is not fulfilled, a error should be reported.
	 */
	@Test
	public void failsOnNonMatchingBidirectionalEntries() {
		String messages = validator.validate().toString();
		assertContains(AliasTypeFixtureInvalidResourceBundle.EXPECTED_ALIAS_AS_KEY, messages);
		assertContains(AliasTypeFixtureInvalidResourceBundle.NOT_MATCHING_ALIAS_AS_VALUE.toString(), messages);
		assertContains("should contain the same value as the key", messages);
	}

	@Test
	public void allErrorsOfInvalidResourceBundleReported() {
		String messages = validator.validate().toString();
		for (String expectedErrorMessageElement : AliasTypeFixtureInvalidResourceBundle.WRONG_ENTRIES) {
			assertContains(expectedErrorMessageElement, messages);
		}
	}

	private static void assertContains(String expectedElement, String messages) {
		assertTrue(expectedElement + " should be contained in " + messages,
				messages.contains(expectedElement));
	}
}
