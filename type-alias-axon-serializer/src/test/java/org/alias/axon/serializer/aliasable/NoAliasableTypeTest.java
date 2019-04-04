package org.alias.axon.serializer.aliasable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class NoAliasableTypeTest {

	private static final String TYPE_NAME = "TestTypeName";

	private NoAliasableType type = new NoAliasableType(TYPE_NAME);

	@Test
	public void containsTypeName() {
		assertEquals(type.toString(), TYPE_NAME, type.getTypeName());
	}
	
	@Test
	public void hasNoAlias() {
		assertFalse(type.toString(), type.getAlias().isPresent());
	}
}
