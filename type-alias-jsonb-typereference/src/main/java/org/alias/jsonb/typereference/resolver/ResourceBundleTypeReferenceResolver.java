package org.alias.jsonb.typereference.resolver;

import java.util.ResourceBundle;

public class ResourceBundleTypeReferenceResolver implements TypeReferenceResolver {

	private static final String DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME = "TypeAlias";

	private final ResourceBundle resouceBundle;

	public static final ResourceBundleTypeReferenceResolver defaultResolver() {
		return ResourceBundleTypeReferenceResolver.ofResourceBundleName(DEFAULT_TYPE_ALIAS_RESOURCE_BUNDLE_NAME);
	}

	public static final ResourceBundleTypeReferenceResolver ofResourceBundleName(String resourceBundleName) {
		ResourceBundle resourceBundle = ResourceBundle.getBundle(resourceBundleName);
		TypeReferenceResourceBundleValidator.failOnErrorsOf(resourceBundle);
		return new ResourceBundleTypeReferenceResolver(resourceBundle);
	}

	protected ResourceBundleTypeReferenceResolver(ResourceBundle resouceBundle) {
		this.resouceBundle = resouceBundle;
	}

	@Override
	public Class<?> getTypeForReference(String typename) {
		return (Class<?>) resouceBundle.getObject(typename);
	}

	@Override
	public String getReferenceForType(Class<?> type) {
		return resouceBundle.getString(type.getName());
	}
}