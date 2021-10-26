package org.alias.axon.serializer.aliasable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class NoAliasableTypeTest {

	private static final String TYPE_NAME = "TestTypeName";

	private NoAliasableType type = new NoAliasableType(TYPE_NAME);

	@Test
	public void containsTypeName() {
		assertEquals(TYPE_NAME, type.getTypeName(), type.toString());
	}
	
	@Test
	public void hasNoAlias() {
		assertFalse(type.getAlias().isPresent(), String.valueOf(type));
	}
}
