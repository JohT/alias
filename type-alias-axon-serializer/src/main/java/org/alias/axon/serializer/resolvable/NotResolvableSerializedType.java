package org.alias.axon.serializer.resolvable;

import java.util.Optional;

import org.axonframework.serialization.SerializedType;

class NotResolvableSerializedType implements ResolvableSerializedType {

	private final SerializedType serializedType;

	public NotResolvableSerializedType(SerializedType serializedType) {
		this.serializedType = serializedType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Class<?>> getResolvedType() {
		return Optional.empty();
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
		return "NotResolvableSerializedType [serializedType=" + serializedType + "]";
	}
}