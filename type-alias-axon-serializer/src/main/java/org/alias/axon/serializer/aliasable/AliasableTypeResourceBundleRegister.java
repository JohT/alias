package org.alias.axon.serializer.aliasable;

import java.util.ResourceBundle;
import java.util.function.Function;

/**
 * This function creates an {@link AliasableResolvableType} out of the given {@link AliasableType}, <br>
 * based on the given {@link ResourceBundle}.
 * <p>
 * It can be used bidirectional with a type name before serialization to get its alias, <br>
 * as well as with an alias after deserialization to resolve its type (and type name). This is only possible using a
 * {@link ResourceBundle} that contains all aliases as keys with their classes as values, <br>
 * as well as all full qualified type names as keys mapped to their aliases as value.
 * 
 * @author JohT
 */
public class AliasableTypeResourceBundleRegister implements Function<AliasableType, AliasableType> {

	private final ResourceBundle resourceBundle;

	/**
	 * Provides a {@link Function}, that takes the original type name as a {@link String} and returns an
	 * {@link AliasableType}. This is done using a {@link ResourceBundle} to look up alias names (to get {@link Class}es)
	 * and to look up full qualified names to get their alias name (bidirectional). The {@link ResourceBundle} will be
	 * validated at first to be sure, that its structure is suitable for this.
	 * 
	 * @param resourceBundle - {@link ResourceBundle}
	 * @return {@link Function} {@link AliasableType} parameter and {@link AliasableType} result.
	 */
	public static final Function<String, AliasableType> of(ResourceBundle resourceBundle) {
		AliasableTypeResourceBundleValidator.failOnErrorsOf(resourceBundle);
		return AliasableType.noAlias().andThen(new AliasableTypeResourceBundleRegister(resourceBundle));
	}

	protected AliasableTypeResourceBundleRegister(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	/**
	 * Looks up the given key using the {@link ResourceBundle} and decides based on the result value, if it is a type name
	 * mapped to an alias, an alias mapped to a type name,<br>
	 * or if there is no entry which leads to the default behavior (key is used as type name).
	 * <p>
	 * {@inheritDoc}
	 */
	@Override
	public AliasableType apply(AliasableType baseType) {
		String baseTypeName = baseType.getTypeName();
		if (!resourceBundle.containsKey(baseTypeName)) {
			return baseType;
		}
		Object value = resourceBundle.getObject(baseTypeName);
		if (value instanceof Class) {
			return AliasableResolvableType.ofType((Class<?>) value).alias(baseTypeName).build();
		}
		if (value instanceof String) {
			return AliasableResolvableType.builder().typeName(baseTypeName).alias((String) value).build();
		}
		String message = "Unexpected value %s from ResourceBundle %s for key %s";
		throw new IllegalArgumentException(String.format(message, String.valueOf(value), resourceBundle.getBaseBundleName(), baseType));
	}

	@Override
	public String toString() {
		return "AliasableTypeResourceBundleRegister [resourceBundle=" + resourceBundle + "]";
	}
}