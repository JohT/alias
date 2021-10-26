package org.alias.axon.serializer.aliasable;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.alias.axon.serializer.aliasable.AliasableResolvableType.Builder;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

public class AliasableResolvableTypeTest {

	private static final String ALIAS = "Alias";
	private static final Class<?> RESOLVED_TYPE = AliasableResolvableType.class;
	private static final String TYPE_NAME = RESOLVED_TYPE.getName();

	/**
	 * class under test;
	 */
	private AliasableResolvableType type;

	@Test
	public void containsAlias() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertEquals(ALIAS, type.getAlias().get());
	}

	@Test
	public void containsTypeName() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertEquals(TYPE_NAME, type.getTypeName());
	}

	@Test
	public void containsResolvedType() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertEquals(RESOLVED_TYPE, type.getResolvedType().get());
	}

	@Test
	public void noAliasIsDefault() {
		type = setAllMandatoryFieldsInto(AliasableResolvableType.builder()).build();
		assertFalse(type.getAlias().isPresent(), type.toString());
	}

	@Test
	public void noResolvedTypeIsDefault() {
		type = setAllMandatoryFieldsInto(AliasableResolvableType.builder()).build();
		assertFalse(type.getResolvedType().isPresent(), type.toString());
	}

	@Test
	public void presetForTypeNameWithoutAlias() {
		type = AliasableResolvableType.ofTypeName(TYPE_NAME);
		assertEquals(TYPE_NAME, type.getTypeName());
		assertFalse(type.getAlias().isPresent(), type.toString());
		assertFalse(type.getResolvedType().isPresent(), type.toString());
	}

	@Test
	public void presetForTypeWithoutAlias() {
		type = AliasableResolvableType.ofType(RESOLVED_TYPE).build();
		assertEquals(TYPE_NAME, type.getTypeName());
		assertEquals(RESOLVED_TYPE, type.getResolvedType().get());
		assertFalse(type.getAlias().isPresent(), type.toString());
	}

	@Test
	public void typeNameMayNotBeNull() {
		try {
			setAllMandatoryFieldsInto(AliasableResolvableType.builder()).typeName(null).build();
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			String errormessage = e.getMessage();
			assertTrue(errormessage.contains("typeName"));
			assertTrue(errormessage.contains("null"));
		}
	}

	@Test
	public void typeNameMayNotBeEmpty() {
		try {
			setAllMandatoryFieldsInto(AliasableResolvableType.builder()).typeName(" ").build();
			fail("Expected exception");
		} catch (IllegalArgumentException e) {
			String errormessage = e.getMessage();
			assertTrue(errormessage.contains("typeName"));
			assertTrue(errormessage.contains("empty"));
		}
	}

	@Test
	public void typenameCanBeDerivedFromResolvedType() {
		Class<?> resolvedType = String.class;
		Builder builder = setAllMandatoryFieldsInto(AliasableResolvableType.builder());
		type = builder.resolvedType(resolvedType).typeNameFromResolvedType().build();
		assertEquals(resolvedType.getName(), type.getTypeName());
	}

	@Test
	public void buildFromTakesAllFieldsFromTemplate() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertEquals(type, AliasableResolvableType.builderFrom(type).build());
	}

	@Test
	public void notEqualComparedToNull() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertFalse(type.equals(null), type.toString());
	}

	@Test
	@SuppressWarnings("unlikely-arg-type")
	public void notEqualComparedToOtherType() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertFalse(type.equals(""), type.toString());
	}

	@Test
	public void notEqualWhenTypeNameDifferent() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertThat(type, is(not(AliasableResolvableType.builderFrom(type).typeName("Other").build())));
	}

	@Test
	public void notEqualWhenTypeResolvedTypeDifferent() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertThat(type, is(not(AliasableResolvableType.builderFrom(type).resolvedType(Void.class).build())));
	}

	@Test
	public void notEqualWhenAliasDifferent() {
		type = setAllFieldsInto(AliasableResolvableType.builder()).build();
		assertThat(type, is(not(AliasableResolvableType.builderFrom(type).alias("Other").build())));
	}

	private static AliasableResolvableType.Builder setAllFieldsInto(AliasableResolvableType.Builder builder) {
		return setAllMandatoryFieldsInto(builder).alias(ALIAS).resolvedType(RESOLVED_TYPE);
	}

	private static AliasableResolvableType.Builder setAllMandatoryFieldsInto(AliasableResolvableType.Builder builder) {
		return builder.typeName(TYPE_NAME);
	}
}