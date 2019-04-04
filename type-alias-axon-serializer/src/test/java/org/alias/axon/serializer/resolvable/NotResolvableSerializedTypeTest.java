package org.alias.axon.serializer.resolvable;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.SimpleSerializedType;
import org.junit.Test;

public class NotResolvableSerializedTypeTest {

	private static final String TYPE = "TestType";
	private static final String REVISION = "v2";

	private SerializedType serializedType = new SimpleSerializedType(TYPE, REVISION);

	/**
	 * class under test.
	 */
	private ResolvableSerializedType resolvableType;

	@Test
	public void notResolvable() {
		resolvableType = ResolvableSerializedType.notResolvable(serializedType);
		assertEquals(Optional.empty(), resolvableType.getResolvedType());
	}

	@Test
	public void nameFromWrappedType() {
		resolvableType = ResolvableSerializedType.notResolvable(serializedType);
		assertEquals(TYPE, resolvableType.getName());
	}

	@Test
	public void revisionFromWrappedType() {
		resolvableType = ResolvableSerializedType.notResolvable(serializedType);
		assertEquals(REVISION, resolvableType.getRevision());
	}
}