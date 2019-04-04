package org.alias.axon.serializer;

import java.util.function.Function;

import org.alias.axon.serializer.resolvable.ResolvableSerializedType;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;

/**
 * This {@link SerializedObject}-Decorator extends the functionality of the wrapped {@link SerializedObject}. <br>
 * It supports extending the used {@link SerializedType}. <br>
 * This is done by specifying a {@link Function}, that takes a {@link SerializedType} and returns a
 * {@link ResolvableSerializedType}. So it is possible to provide a decorator for a {@link SerializedType}, that is
 * applied every time the {@link SerializedType} is used (as method parameter or as method return type).
 * 
 * @author JohT
 *
 * @param <S>
 */
class TypeDecoratableSerializedObject<S> implements SerializedObject<S> {
	private final SerializedObject<S> delegate;
	private final Function<SerializedType, ResolvableSerializedType> typeDecorator;

	public TypeDecoratableSerializedObject(SerializedObject<S> delegate,
			Function<SerializedType, ResolvableSerializedType> typeDecorator) {
		this.delegate = delegate;
		this.typeDecorator = typeDecorator;
	}

	@Override
	public Class<S> getContentType() {
		return delegate.getContentType();
	}

	@Override
	public ResolvableSerializedType getType() {
		return typeDecorator.apply(delegate.getType());
	}

	@Override
	public S getData() {
		return delegate.getData();
	}

	@Override
	public String toString() {
		return "TypeDecoratableSerializedObject [delegate=" + delegate + ", typeDecorator=" + typeDecorator + "]";
	}
}