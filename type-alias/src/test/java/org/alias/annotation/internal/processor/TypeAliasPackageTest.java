package org.alias.annotation.internal.processor;

import static java.util.Arrays.asList;
import static org.alias.annotation.internal.templates.TypeAliasName.alsoAsAlias;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.alias.annotation.internal.templates.TypeAliasName;
import org.junit.Test;

public class TypeAliasPackageTest {

	private static final TypeAliasPackageInfo PACKAGE_INFO = TypeAliasPackageInfo.create().packageName("org").fileName("Alias").build();
	private static final TypeAliasPackageInfo DIFFERENT_PACKAGE_INFO = TypeAliasPackageInfo.defaultPackageInfo();
	private static final Collection<TypeAliasName> TYPES_IN_PACKAGE = asList(TypeAliasName.alsoAsAlias("org.example.One"),
			alsoAsAlias("org.example.Two"));
	private static final Collection<TypeAliasName> DIFFERENT_TYPES_IN_PACKAGE = asList(TypeAliasName.alsoAsAlias("org.example.One"));

	/**
	 * class under test.
	 */
	private TypeAliasPackage packageContent = new TypeAliasPackage(PACKAGE_INFO, TYPES_IN_PACKAGE);

	@Test
	public void containsPackageInfo() {
		packageContent = new TypeAliasPackage(PACKAGE_INFO, TYPES_IN_PACKAGE);
		assertEquals(PACKAGE_INFO, packageContent.getInfo());
	}

	@Test
	public void containsTypes() {
		assertEquals(TYPES_IN_PACKAGE, packageContent.getTypesInPackage());
	}

	@Test
	public void equalOnEqualContent() {
		TypeAliasPackage equalPackageContent = new TypeAliasPackage(PACKAGE_INFO, TYPES_IN_PACKAGE);
		assertTrue(equalPackageContent.toString(), packageContent.equals(equalPackageContent));
	}

	@Test
	public void notEqualOnDifferentPackageInfo() {
		TypeAliasPackage different = new TypeAliasPackage(DIFFERENT_PACKAGE_INFO, TYPES_IN_PACKAGE);
		assertFalse(packageContent.toString(), packageContent.equals(different));
	}

	@Test
	public void notEqualOnDifferentTypes() {
		TypeAliasPackage different = new TypeAliasPackage(PACKAGE_INFO, DIFFERENT_TYPES_IN_PACKAGE);
		assertFalse(packageContent.toString(), packageContent.equals(different));
	}

	@Test
	public void notEqualComparedToNull() {
		assertFalse(packageContent.toString(), packageContent.equals(null));
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void notEqualComparedToAnotherType() {
		assertFalse(packageContent.toString(), packageContent.equals("Other Type"));
	}
}