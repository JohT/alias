package org.alias.axon.serializer.aliasable;

import java.util.Optional;
import java.util.function.Function;

import org.alias.axon.serializer.resolvable.ResolvableSerializedType;
import org.alias.axon.serializer.resolvable.ResolvableType;
import org.axonframework.serialization.SerializedType;

/**
 * This {@link ResolvableSerializedType} extends the naming capabilities of the wrapped {@link SerializedType}.<br>
 * By specifying a {@link Function} that takes the original {@link SerializedType#getName()} and provides a
 * {@link AliasableType}, it also supports {@link AliasableType}s.
 * 
 * @author JohT
 */
public class AliasableResolvableSerializedType implements ResolvableSerializedType {

	private final SerializedType delegate;
	private final Function<String, AliasableType> aliasableTypeResolver;
	private transient AliasableType aliasableType = null; // holds lazy loaded result of typeResolver

	public static ResolvableSerializedType wraps(SerializedType originalSerializedType, Function<String, AliasableType> typeResolver) {
		return new AliasableResolvableSerializedType(originalSerializedType, typeResolver);
	}

	protected AliasableResolvableSerializedType(SerializedType delegate, Function<String, AliasableType> aliasableTypeResolver) {
		this.delegate = delegate;
		this.aliasableTypeResolver = aliasableTypeResolver;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return getResolvedType().map((ignore) -> getTypeName()).orElse(getAlias().orElse(getTypeName()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getRevision() {
		return delegate.getRevision();
	}

	@Override
	public Optional<Class<?>> getResolvedType() {
		AliasableType type = resolveAliasableType();
		if (type instanceof ResolvableType) {
			return ((ResolvableType) type).getResolvedType();
		}
		return Optional.empty();
	}

	private String getTypeName() {
		return resolveAliasableType().getTypeName();
	}

	private Optional<String> getAlias() {
		return resolveAliasableType().getAlias();
	}

	private AliasableType resolveAliasableType() {
		if (this.aliasableType == null) {
			this.aliasableType = aliasableTypeResolver.apply(delegate.getName());
		}
		return this.aliasableType;
	}

	@Override
	public String toString() {
		return "AliasableResolvableSerializedType [delegate=" + delegate + ", aliasableTypeResolver=" + aliasableTypeResolver + "]";
	}
}