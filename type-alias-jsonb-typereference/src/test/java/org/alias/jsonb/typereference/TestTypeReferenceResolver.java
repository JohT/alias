package org.alias.jsonb.typereference;

import org.alias.jsonb.typereference.resolver.ResourceBundleTypeReferenceResolver;
import org.alias.jsonb.typereference.resolver.TypeReferenceResolver;
import org.junit.Ignore;

/**
 * Demonstrates how to provide an own {@link TypeReferenceResolver} for the {@link TypeReferenceWrapperAdapter}.
 * 
 * @author JohT
 */
@Ignore
public class TestTypeReferenceResolver implements TypeReferenceResolver {

	private TypeReferenceResolver delegate = ResourceBundleTypeReferenceResolver.ofResourceBundleName("TypeAlias");
	
	@Override
	public Class<?> getTypeForReference(String name) {
		return delegate.getTypeForReference(name);
	}

	@Override
	public String getReferenceForType(Class<?> type) {
		return delegate.getReferenceForType(type);
	}

	@Override
	public String toString() {
		return "TestTypeReferenceResolver [delegate=" + delegate + "]";
	}
}
