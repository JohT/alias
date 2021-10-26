package org.alias.axon.serializer.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ResourceBundle;

import org.alias.axon.serializer.TypeAliasResourceBundleFixture;
import org.junit.jupiter.api.Test;

public class AliasableResourceBundleClassloaderTest {

	private static final String ALIAS = "Alias";
	private static final Class<?> TYPE = AliasableResourceBundleClassloaderTest.class;
	private static final ResourceBundle RESOURCE_BUNDLE = TypeAliasResourceBundleFixture.containing(ALIAS, TYPE);
	private static final ResourceBundle INVALID_RESOURCE_BUNDLE = TypeAliasResourceBundleFixture.withWrongValueTypeFor(ALIAS);
	private static final ClassLoader CLASSLOADER = AliasableResourceBundleClassloader.class.getClassLoader();

	/**
	 * class under test.
	 */
	private ClassLoader classloader = new AliasableResourceBundleClassloader(RESOURCE_BUNDLE, CLASSLOADER);

	@Test
	public void classShouldBeFoundByAliasName() throws ClassNotFoundException {
		assertEquals(TYPE, classloader.loadClass(ALIAS));
	}

	@Test
	public void classShouldBeFoundByTypeName() throws ClassNotFoundException {
		assertEquals(TYPE, classloader.loadClass(TYPE.getName()));
	}

	@Test
	public void useParentClassloaderAsFallbackForNotRegisteredClasses() throws ClassNotFoundException {
		Class<String> validClassWithoutAlias = String.class;
		assertEquals(validClassWithoutAlias, classloader.loadClass(validClassWithoutAlias.getName()));
	}

	@Test
	public void errorWhenResourceBundleIsInvalid() throws ClassNotFoundException {
		classloader = new AliasableResourceBundleClassloader(INVALID_RESOURCE_BUNDLE, CLASSLOADER);
		try {
			classloader.loadClass(ALIAS);
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			String message = e.getMessage();
			assertTrue(message.contains(INVALID_RESOURCE_BUNDLE.getBaseBundleName()), message);
			assertTrue(message.contains(ALIAS), message);
		}
	}
}