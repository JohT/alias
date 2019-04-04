package org.alias.axon.serializer.resolvable;

import java.util.Optional;

public interface ResolvableType {

	Optional<Class<?>> getResolvedType();

	/**
	 * Returns <code>true</code>, if the implementation can be used to reconstruct a deserialized object.
	 * <p>
	 * This is by default bound to the presence of {@link #getResolvedType()}.
	 * 
	 * @return <code>true</code>, if useable during deserialization.
	 */
	default boolean isApplicableForDeserialization() {
		return getResolvedType().isPresent();
	}
}
