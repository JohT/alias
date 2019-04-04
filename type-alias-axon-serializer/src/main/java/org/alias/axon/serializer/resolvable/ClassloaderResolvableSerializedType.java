package org.alias.axon.serializer.resolvable;

import java.util.Optional;

import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.UnknownSerializedType;

class ClassloaderResolvableSerializedType implements ResolvableSerializedType {

	private final SerializedType serializedType;
	private final ClassLoader classLoader;

	public ClassloaderResolvableSerializedType(SerializedType serializedType, ClassLoader classloader) {
		this.serializedType = serializedType;
		classLoader = classloader;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Class<?>> getResolvedType() {
		try {
			return Optional.of(classLoader.loadClass(serializedType.getName()));
		} catch (ClassNotFoundException e) {
			return Optional.of(UnknownSerializedType.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return serializedType.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRevision() {
		return serializedType.getRevision();
	}

	@Override
	public String toString() {
		return "ClassloaderResolvableSerializedType [serializedType=" + serializedType + ", classLoader=" + classLoader + "]";
	}
}