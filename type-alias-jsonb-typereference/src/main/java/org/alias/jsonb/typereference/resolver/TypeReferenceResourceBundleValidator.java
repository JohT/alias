package org.alias.jsonb.typereference.resolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Validates the structure of the {@link ResourceBundle} to determines, <br>
 * if it can be used as a register for {@link Class} types and their alias names.
 * 
 * @author JohT
 */
//copy from "type-alias-axon-serializer"
//TODO new module with it and the other classes in this package?
class TypeReferenceResourceBundleValidator {

	private final ResourceBundle resourceBundle;

	/**
	 * Returns a {@link Collection} of error message {@link String}s or an empty {@link Collection}, if the
	 * {@link ResourceBundle} is valid.
	 * 
	 * @param resourceBundle {@link ResourceBundle}
	 * @return {@link Collection} of error message {@link String}s
	 */
	public static final Collection<String> errorsOf(ResourceBundle resourceBundle) {
		return new TypeReferenceResourceBundleValidator(resourceBundle).validate();
	}

	/**
	 * Throws an {@link IllegalArgumentException}, if there are validation errors for the {@link ResourceBundle}.
	 * 
	 * @param resourceBundle {@link ResourceBundle}
	 */
	public static final void failOnErrorsOf(ResourceBundle resourceBundle) {
		failOn(errorsOf(resourceBundle), resourceBundle.getBaseBundleName());
	}

	protected TypeReferenceResourceBundleValidator(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}

	public Collection<String> validate() {
		Collection<String> errormessages = new ArrayList<String>();
		for (String key : resourceBundle.keySet()) {
			if (key.isEmpty()) {
				errormessages.add(String.format("Empty keys are not allowed"));
				continue;
			}
			Object value = resourceBundle.getObject(key);
			String keyFromValue = resourceBundleValueAsString(value);
			if (keyFromValue.isEmpty()) {
				errormessages.add(String.format("The value %s of key '%s' is not a String or Class type.", value, key));
				continue;
			}
			if (!resourceBundle.containsKey(keyFromValue)) {
				errormessages.add(String.format("The value '%s' of key '%s' should also be available as key (bidirectional register).",
						keyFromValue, key));
				continue;
			}
			Object reverseValue = resourceBundle.getObject(keyFromValue);
			String expectedKey = resourceBundleValueAsString(reverseValue);
			if (!expectedKey.equals(key)) {
				errormessages.add(String.format("The value '%s' should contain the same value as the key '%s'.", reverseValue, key));
				continue;
			}
		}
		return errormessages;
	}

	private static void failOn(Collection<String> errormessages, String resourceBundleName) {
		if (errormessages.isEmpty()) {
			return;
		}
		String errors = errormessages.stream().collect(Collectors.joining("\n"));
		String message = "The ResourceBundle '%s' is not valid to be used as a register for type aliases: %s";
		throw new IllegalArgumentException(String.format(message, resourceBundleName, errors));
	}

	private static String resourceBundleValueAsString(Object value) {
		if (value instanceof Class<?>) {
			return ((Class<?>) value).getName();
		} else if (value instanceof String) {
			return (String) value;
		}
		return "";
	}

	@Override
	public String toString() {
		return "AliasableTypeResourceBundleValidator [resourceBundle=" + resourceBundle + "]";
	}
}