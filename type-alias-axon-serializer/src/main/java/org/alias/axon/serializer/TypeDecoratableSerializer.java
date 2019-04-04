package org.alias.axon.serializer;

import java.util.ResourceBundle;
import java.util.function.Function;

import org.alias.axon.serializer.aliasable.AliasableResolvableSerializedType;
import org.alias.axon.serializer.aliasable.AliasableTypeResourceBundleRegister;
import org.alias.axon.serializer.resolvable.ResolvableSerializedType;
import org.alias.axon.serializer.resolvable.ResolvableType;
import org.axonframework.serialization.Converter;
import org.axonframework.serialization.SerializedObject;
import org.axonframework.serialization.SerializedType;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.SimpleSerializedType;

/**
 * This {@link Serializer}-Decorator extends the functionality of the wrapped {@link Serializer}. <br>
 * It supports extending the used {@link SerializedType}. <br>
 * This is done by specifying a {@link Function}, that takes a {@link SerializedType} and returns a
 * {@link ResolvableSerializedType}. So it is possible to provide a decorator for a {@link SerializedType}, that is
 * applied every time the {@link SerializedType} is used (as method parameter or as method return type).
 * <p>
 * Since {@link SerializedType} is used before serialization and after deserialization, and it is not able to hold
 * already resolved types ({@link Class} of the full qualified {@link String}), the {@link SerializedType} decorator
 * function should also implement {@link ResolvableType}.
 * 
 * @author JohT
 */
public class TypeDecoratableSerializer implements Serializer {

	public static final String DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME = "TypeAlias";

	private final Serializer delegate;
	private final Function<SerializedType, ResolvableSerializedType> typeDecorator;

	/**
	 * Extends the given {@link Serializer} to use the default {@link ResourceBundle}
	 * {@value #DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME} to look for type aliases and to resolve the type ({@link String} to
	 * {@link Class} object).
	 * 
	 * @param delegate {@link Serializer}
	 * @return {@link Serializer}
	 * @see #aliasThroughResourceBundle(Serializer, ResourceBundle)
	 */
	public static final Serializer aliasThroughDefaultResourceBundle(Serializer delegate) {
		return aliasThroughResourceBundle(delegate, ResourceBundle.getBundle(DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME));
	}

	/**
	 * Extends the given {@link Serializer} to use a {@link ResourceBundle} to look for type aliases and to resolve the type
	 * ({@link String} to {@link Class} object).
	 * <p>
	 * <b>Important note about the structure of the {@link ResourceBundle}:<b><br>
	 * The resource bundle must contain the {@link String} aliases as keys assigned to their {@link Class}es as value. <br>
	 * It must also contain the full qualified type name {@link String}s as keys assigned to their {@link String} alias.<br>
	 * This is needed, to support serialization, deserialization and resolved types (skipping
	 * {@link Class#forName(String)}).
	 * 
	 * @param delegate       {@link Serializer}
	 * @param resourceBundle {@link ResourceBundle}
	 * @return {@link Serializer}
	 */
	public static final Serializer aliasThroughResourceBundle(Serializer delegate, ResourceBundle resourceBundle) {
		return new TypeDecoratableSerializer(delegate, (originalType) -> {
			return AliasableResolvableSerializedType.wraps(originalType, AliasableTypeResourceBundleRegister.of(resourceBundle));
		});
	}

	public TypeDecoratableSerializer(Serializer delegate, Function<SerializedType, ResolvableSerializedType> typeDecorator) {
		this.delegate = delegate;
		this.typeDecorator = typeDecorator;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public SerializedType typeForClass(Class type) {
		return typeDecorator.apply(delegate.typeForClass(type));
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Class classForType(SerializedType type) {
		if (SimpleSerializedType.emptyType().equals(type)) {
			return delegate.classForType(type);
		}
		ResolvableSerializedType resolvableType = typeDecorator.apply(type);
		if (!resolvableType.isApplicableForDeserialization()) {
			// delegate to original behavior with undecorated, original SerializedType as parameter
			return delegate.classForType(type);
		}
		return resolvableType.getResolvedType().get();
	}

	@Override
	public <T> SerializedObject<T> serialize(Object object, Class<T> expectedRepresentation) {
		// Remark: Since the delegate is called in any case, it depends on its implementation,
		// whether it is fully decoratable or not. If the implementation use inter method calls
		// to methods of the decorated interface (e.g. "classForName"), the delegates implementation
		// will be called, not the one in this decorator.
		return new TypeDecoratableSerializedObject<>(delegate.serialize(object, expectedRepresentation), typeDecorator);
	}

	@Override
	public <S, T> T deserialize(SerializedObject<S> serializedObject) {
		TypeDecoratableSerializedObject<S> deserialized = new TypeDecoratableSerializedObject<>(serializedObject, typeDecorator);
		if (!deserialized.getType().isApplicableForDeserialization()) {
			// delegate to original behavior with undecorated, original SerializedObject as parameter
			return delegate.deserialize(serializedObject);
		}
		return delegate.deserialize(deserialized);
	}

	@Override
	public <T> boolean canSerializeTo(Class<T> expectedRepresentation) {
		return delegate.canSerializeTo(expectedRepresentation);
	}

	@Override
	public Converter getConverter() {
		return delegate.getConverter();
	}

	@Override
	public String toString() {
		return "TypeDecoratableSerializer [delegate=" + delegate + ", typeDecorator=" + typeDecorator + "]";
	}
}