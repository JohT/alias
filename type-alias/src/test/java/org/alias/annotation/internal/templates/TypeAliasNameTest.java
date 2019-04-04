package org.alias.annotation.internal.templates;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TypeAliasNameTest {

	private static final String ALIAS_NAME = "TestAlias";
	private static final String FULL_NAME = "FullName";

	/**
	 * class under test.
	 */
	private TypeAliasName alias;

	@Test
	public void containsAliasname() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertEquals(ALIAS_NAME, alias.getAliasname());
	}

	@Test
	public void containsFullQualifiedName() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertEquals(FULL_NAME, alias.getFullqualifiedname());
	}

	@Test
	public void replacesAliasnamePlaceholder() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertEquals(ALIAS_NAME, alias.replacePlaceholderInLine("{(aliasname)}"));
	}

	@Test
	public void replacesFullqualifiednamePlaceholder() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertEquals(FULL_NAME, alias.replacePlaceholderInLine("{(fullqualifiedname)}"));
	}

	@Test
	public void equalOnEquaContents() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		TypeAliasName expectedEqual = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertEquals(expectedEqual, alias);
		assertEquals(expectedEqual.hashCode(), alias.hashCode());
	}

	@Test
	public void equalOnDifferentAlias() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		TypeAliasName expectedNotEqual = new TypeAliasName("Other" + ALIAS_NAME, FULL_NAME);
		assertFalse(alias.equals(expectedNotEqual));
	}

	@Test
	public void equalOnDifferentFullQualifiedTypeName() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		TypeAliasName expectedNotEqual = new TypeAliasName(ALIAS_NAME, "Other" + FULL_NAME);
		assertFalse(alias.equals(expectedNotEqual));
	}

	@Test
	public void notEqualToNull() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertFalse(alias.equals(null));
	}

	@Test
	public void notEqualToDifferentObject() {
		alias = new TypeAliasName(ALIAS_NAME, FULL_NAME);
		assertFalse(alias.equals(new Object()));
	}

}