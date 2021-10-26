package org.alias.annotation.internal.templates;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Annotation;

import org.alias.annotation.TypeAlias;
import org.alias.annotation.internal.templates.TypeAliasName.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TypeAliasNameTest {

	static final boolean PRIMARY = true;
	static final String ALIAS_NAME = "TestAlias";
	static final String FULL_NAME = "org.external.TestAlias";
	static final String ASSIGNED_TYPE = "org.alias.example";
	static final String ASSIGNED_TYPE_NAME = ASSIGNED_TYPE + ".TestAlias";

	/**
	 * class under test.
	 */
	private TypeAliasName alias;

	@BeforeEach
	public void setUp() {
		alias = allFields(TypeAliasName.builder()).build();
	}

	@Test
	public void containsAliasname() {
		assertEquals(ALIAS_NAME, alias.getAliasname());
	}

	@Test
	public void aliasNameTrimmed() {
		alias = TypeAliasName.builderBasedOn(alias).aliasName("  " + ALIAS_NAME + " ").build();
		assertEquals(ALIAS_NAME, alias.getAliasname());
	}

	@Test
	public void containsFullQualifiedName() {
		assertEquals(FULL_NAME, alias.getFullqualifiedname());
	}

	@Test
	public void containsPrimary() {
		assertEquals(PRIMARY, alias.isPrimary());
	}

	@Test
	public void primaryAliasOfNotPrimaryOne() {
		alias = TypeAliasName.alsoAsAlias(FULL_NAME).asPrimary();
		assertTrue(alias.isPrimary());
		assertEquals(FULL_NAME, alias.getAliasname());
		assertEquals(FULL_NAME, alias.getFullqualifiedname());
	}

	@Test
	public void takeValuesFromAnnotation() {
		TypeAlias annotation = new TypeAlias() {
			
			@Override
			public Class<? extends Annotation> annotationType() {
				return TypeAlias.class;
			}
			
			@Override
			public String value() {
				return ALIAS_NAME;
			}
			
			@Override
			public String type() {
				return FULL_NAME;
			}
			
			@Override
			public boolean primary() {
				return PRIMARY;
			}
		};
		alias = TypeAliasName.ofAnnotation(annotation).build();
		assertEquals(ALIAS_NAME, alias.getAliasname());
		assertEquals(FULL_NAME, alias.getFullqualifiedname());
		assertEquals(PRIMARY, alias.isPrimary());
	}

	@Test
	public void fullQualifiedNameTrimmed() {
		alias = TypeAliasName.builderBasedOn(alias).fullQualifiedName("   " + FULL_NAME).build();
		assertEquals(FULL_NAME, alias.getFullqualifiedname());
	}

	@Test
	public void fullQualifiedNameDefaultsToAssignedTypeName() {
		alias = TypeAliasName.builderBasedOn(alias).fullQualifiedName(null).build();
		assertEquals(ASSIGNED_TYPE_NAME, alias.getFullqualifiedname());
	}

	@Test
	public void containsAssignedTypeName() {
		assertEquals(ASSIGNED_TYPE_NAME, alias.getAssignedtypename());
	}

	@Test
	public void assignedTypeNameTrimmed() {
		alias = TypeAliasName.builderBasedOn(alias).assignedTypeName(ASSIGNED_TYPE_NAME + "  ").build();
		assertEquals(ASSIGNED_TYPE_NAME, alias.getAssignedtypename());
	}

	@Test
	public void assignedTypeNameDefaultsToFullQualifiedName() {
		alias = TypeAliasName.builderBasedOn(alias).assignedTypeName(" ").build();
		assertEquals(FULL_NAME, alias.getAssignedtypename());
	}

	@Test
	public void assignedToPackage() {
		assertTrue(alias.isAssignedToPackage(ASSIGNED_TYPE));
	}

	@Test
	public void notAssignedToPackage() {
		assertFalse(alias.isAssignedToPackage(ASSIGNED_TYPE + ".other"));
	}

	@Test
	public void emptyAliasNameAllowed() {
		alias = TypeAliasName.builderBasedOn(alias).aliasName("").build();
		assertEquals("", alias.getAliasname());
	}

	@Test
	public void nullAliasNameConvertedToEmptyString() {
		alias = TypeAliasName.builderBasedOn(alias).aliasName(null).build();
		assertEquals("", alias.getAliasname());
	}

	@Test
	public void failOnEmptyFullQualifiedName() {
		try {
			TypeAliasName.builderBasedOn(alias).fullQualifiedName(null).assignedTypeName(null).build();
			fail("Exppected Exception");
		} catch (IllegalArgumentException e) {
			String message = e.getMessage();
			assertTrue(message.toLowerCase().contains("fullqualifiedname"), message);
			assertTrue(message.toLowerCase().contains("may not be empty"), message);
		}
	}

	@Test
	public void replacesAliasnamePlaceholder() {
		assertEquals(ALIAS_NAME, alias.replacePlaceholderInLine("{(aliasname)}"));
	}

	@Test
	public void replacesFullqualifiednamePlaceholder() {
		assertEquals(FULL_NAME, alias.replacePlaceholderInLine("{(fullqualifiedname)}"));
	}

	@Test
	public void replacesAssignedpackagePlaceholder() {
		assertEquals(ASSIGNED_TYPE_NAME, alias.replacePlaceholderInLine("{(assignedtypename)}"));
	}

	@Test
	public void emptyReplacementOnConditionalLineForPrimaryAliases() {
		alias = TypeAliasName.builderBasedOn(alias).primary(false).build();
		assertEquals("", alias.replacePlaceholderInLine("blabla {(only.primary)}"));
	}
	
	@Test
	public void normalReplacementOnConditionalLineForPrimaryAliases() {
		alias = TypeAliasName.builderBasedOn(alias).primary(true).build();
		assertEquals("test", alias.replacePlaceholderInLine("test{(only.primary)}"));
	}
	
	@Test
	public void equalOnEquaContents() {
		TypeAliasName expectedEqual = TypeAliasName.builderBasedOn(alias).build();
		assertEquals(expectedEqual, alias);
		assertEquals(expectedEqual.hashCode(), alias.hashCode());
	}

	@Test
	public void notEqualOnDifferentAliasName() {
		TypeAliasName expectedNotEqual = TypeAliasName.builderBasedOn(alias).aliasName("Other").build();
		assertFalse(alias.equals(expectedNotEqual));
	}

	@Test
	public void notEqualOnDifferentFullQualifiedTypeName() {
		TypeAliasName expectedNotEqual = TypeAliasName.builderBasedOn(alias).fullQualifiedName("Other").build();
		assertFalse(alias.equals(expectedNotEqual));
	}

	@Test
	public void notEqualOnDifferentAssignedTypeName() {
		TypeAliasName expectedNotEqual = TypeAliasName.builderBasedOn(alias).assignedTypeName("Other").build();
		assertFalse(alias.equals(expectedNotEqual));
	}

	@Test
	public void notEqualOnDifferentPrimary() {
		TypeAliasName expectedNotEqual = TypeAliasName.builderBasedOn(alias).primary(false).build();
		assertFalse(alias.equals(expectedNotEqual));
	}
	
	@Test
	public void notEqualToNull() {
		assertFalse(alias.equals(null));
	}

	@Test
	public void notEqualToDifferentObject() {
		assertFalse(alias.equals(new Object()));
	}

	private static Builder allFields(Builder builder) {
		return builder.aliasName(ALIAS_NAME)
				.fullQualifiedName(FULL_NAME)
				.assignedTypeName(ASSIGNED_TYPE_NAME)
				.primary(PRIMARY);
	}
}