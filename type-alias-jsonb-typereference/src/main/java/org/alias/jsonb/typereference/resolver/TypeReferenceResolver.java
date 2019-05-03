package org.alias.jsonb.typereference.resolver;

/**
 * Resolves a type reference, represented by a {@link String}, <br>
 * to its corresponding {@link Class} and vice-versa.
 * 
 * @author JohT
 */
public interface TypeReferenceResolver {

	/**
	 * Gets the {@link Class}, that is represented by the given type reference {@link String}.
	 * 
	 * @param name {@link String}
	 * @return {@link Class}
	 */
	Class<?> getTypeForReference(String name);

	/**
	 * Gets the type reference {@link String} that represents the given {@link Class}.
	 * 
	 * @param type {@link Class}
	 * @return
	 */
	String getReferenceForType(Class<?> type);

	/**
	 * Provides a default {@link TypeReferenceResolver} implementation <br>
	 * using the full qualified type name as reference.
	 * 
	 * @return {@link TypeReferenceResolver}
	 */
	static TypeReferenceResolver defaultResolver() {
		return DefaultTypeReferenceResolver.BY_CLASS_NAME;
	}
}