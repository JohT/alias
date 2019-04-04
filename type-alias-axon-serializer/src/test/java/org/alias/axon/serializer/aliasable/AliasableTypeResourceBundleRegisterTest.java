package org.alias.axon.serializer.aliasable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ResourceBundle;

import org.alias.axon.serializer.resolvable.ResolvableType;
import org.junit.Test;

public class AliasableTypeResourceBundleRegisterTest {

	private static final String REGISTERED_ALIAS = AliasTypeFixtureResourceBundle.ALIAS;
	private static final Class<?> REGISTERED_TYPE = AliasTypeFixtureResourceBundle.TYPE;
	private static final ResourceBundle RESOURCE_BUNDLE = new AliasTypeFixtureResourceBundle();
	private static final ResourceBundle INVALID_RESOURCE_BUNDLE = new AliasTypeFixtureInvalidResourceBundle();

	/**
	 * class under test.
	 */
	private AliasableTypeResourceBundleRegister register = new AliasableTypeResourceBundleRegister(RESOURCE_BUNDLE);

	@Test
	public void notRegisteredTypeNameRemainsTheSame() {
		AliasableType baseType = AliasableType.noAliasFor("NotRegistered");
		AliasableType result = register.apply(baseType);
		assertEquals(baseType.getTypeName(), result.getTypeName());
		assertFalse(result.getAlias().isPresent());
	}

	@Test
	public void registeredType() {
		AliasableType baseType = AliasableType.noAliasFor(REGISTERED_TYPE.getName());
		AliasableType result = register.apply(baseType);
		assertEquals(baseType.getTypeName(), result.getTypeName());
		assertEquals(REGISTERED_ALIAS, result.getAlias().get());
	}

	@Test
	public void registeredAlias() {
		AliasableType aliasNameAsBaseType = AliasableType.noAliasFor(REGISTERED_ALIAS);
		AliasableType result = register.apply(aliasNameAsBaseType);
		assertEquals(REGISTERED_TYPE.getName(), result.getTypeName());
		assertEquals(aliasNameAsBaseType.getTypeName(), result.getAlias().get());
	}

	@Test
	public void providesResolveableType() {
		AliasableType result = register.apply(AliasableType.noAliasFor(REGISTERED_ALIAS));
		assertTrue(result instanceof ResolvableType);
	}

	@Test
	public void resolvesRegisteredAlias() {
		ResolvableType result = (ResolvableType) register.apply(AliasableType.noAliasFor(REGISTERED_ALIAS));
		assertEquals(REGISTERED_TYPE, result.getResolvedType().get());
	}

	@Test
	public void ailOnUnexpectedValueType() {
		register = new AliasableTypeResourceBundleRegister(INVALID_RESOURCE_BUNDLE);
		try {
			register.apply(AliasableType.noAliasFor(AliasTypeFixtureInvalidResourceBundle.KEY_TO_UNEXPECTED_VALUE_TYPE));
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			assertContains("Unexpected value", e.getMessage());
			assertContains(AliasTypeFixtureInvalidResourceBundle.UNEXPECTED_VALUE_TYPE.toString(), e.getMessage());
			assertContains(AliasTypeFixtureInvalidResourceBundle.KEY_TO_UNEXPECTED_VALUE_TYPE, e.getMessage());
			assertContains(INVALID_RESOURCE_BUNDLE.getBaseBundleName(), e.getMessage());
		}
	}

	private void assertContains(String expectedPart, String text) {
		assertTrue(expectedPart + " should be contained in " + text,
				text.contains(expectedPart));
	}

	@Test
	public void allErrorsOfInvalidResourceBundleReported() {
		try {
			AliasableTypeResourceBundleRegister.of(INVALID_RESOURCE_BUNDLE);
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			for (String expectedErrorMessageElement : AliasTypeFixtureInvalidResourceBundle.WRONG_ENTRIES) {
				assertContains(expectedErrorMessageElement, e.getMessage());
			}
		}
	}
}