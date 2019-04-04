package org.alias.axon.serializer.resolvable;

import java.util.Optional;

import org.axonframework.serialization.SerializedType;

/**
 * Extends the {@link SerializedType} to a {@link ResolvableType}.
 * <p>
 * {@link ResolvableType}s are able to provide the {@link Class} object for the type <br>
 * derived from {@link SerializedType#getName()}.
 * 
 * @author JohT
 */
public interface ResolvableSerializedType extends ResolvableType, SerializedType {

	/**
	 * Creates a {@link ResolvableSerializedType} based on the given {@link SerializedType} <br>
	 * and skips type resolving. {@link ResolvableSerializedType#getResolvedType()} will never be
	 * {@link Optional#isPresent()}.
	 * 
	 * @param serializedType {@link SerializedType}
	 * @return {@link ResolvableSerializedType}
	 */
	static ResolvableSerializedType notResolvable(SerializedType serializedType) {
		return new NotResolvableSerializedType(serializedType);
	}

	/**
	 * Creates a {@link ResolvableSerializedType} based on the given {@link SerializedType} <br>
	 * by using {@link ClassLoader#getSystemClassLoader()} to get the {@link Class} object from the type name.
	 * 
	 * @param serializedType {@link SerializedType}
	 * @return {@link ResolvableSerializedType}
	 * @see #classloaderResolvable(SerializedType, ClassLoader)
	 */
	static ResolvableSerializedType systemClassloaderResolvable(SerializedType serializedType) {
		return classloaderResolvable(serializedType, ClassLoader.getSystemClassLoader());
	}

	/**
	 * Creates a {@link ResolvableSerializedType} based on the given {@link SerializedType} <br>
	 * and uses the given {@link ClassLoader} to get the {@link Class} object from the type name.
	 * 
	 * @param serializedType {@link SerializedType}
	 * @param classLoader    {@link ClassLoader}
	 * @return {@link ResolvableSerializedType}
	 */
	static ResolvableSerializedType classloaderResolvable(SerializedType serializedType, ClassLoader classLoader) {
		return new ClassloaderResolvableSerializedType(serializedType, classLoader);
	}
}
