package org.alias.axon.serializer.resolvable;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.UnknownSerializedType;
import org.junit.Test;

public class ClassloaderResolvableSerializedTypeTest {

	private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();
	private static final Class<ResolvableSerializedType> EXISTING_CLASS = ResolvableSerializedType.class;
	private static final String NON_EXISTING_CLASSNAME = "1234";
	private static final String REVISION = "v2";

	/**
	 * class under test.
	 */
	private ResolvableSerializedType resolvableType;

	@Test
	public void existingTypeResolved() {
		String classname = EXISTING_CLASS.getName();
		resolvableType = new ClassloaderResolvableSerializedType(serializedTypeOf(classname), CLASS_LOADER);
		assertEquals(Optional.of(EXISTING_CLASS), resolvableType.getResolvedType());
	}

	@Test
	public void existingTypeResolvedUsingFactoryMethodOnInterface() {
		String classname = EXISTING_CLASS.getName();
		resolvableType = ResolvableSerializedType.classloaderResolvable(serializedTypeOf(classname), CLASS_LOADER);
		assertEquals(EXISTING_CLASS, resolvableType.getResolvedType().get());
	}

	@Test
	public void nonExistingTypeNameLeadsToUnknownSerializedType() {
		resolvableType = new ClassloaderResolvableSerializedType(serializedTypeOf(NON_EXISTING_CLASSNAME), CLASS_LOADER);
		assertEquals(UnknownSerializedType.class, resolvableType.getResolvedType().get());
	}

	@Test
	public void nameFromWrappedType() {
		String classname = EXISTING_CLASS.getName();
		resolvableType = new ClassloaderResolvableSerializedType(serializedTypeOf(classname), CLASS_LOADER);
		assertEquals(classname, resolvableType.getName());
	}

	@Test
	public void revisionFromWrappedType() {
		String classname = EXISTING_CLASS.getName();
		resolvableType = new ClassloaderResolvableSerializedType(serializedTypeOf(classname), CLASS_LOADER);
		assertEquals(REVISION, resolvableType.getRevision());
	}

	private static SimpleSerializedType serializedTypeOf(String classname) {
		return new SimpleSerializedType(classname, REVISION);
	}
}