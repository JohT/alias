package org.alias.jsonb.typereference.resolver;

/**
 * Enumeration of local pre defined {@link TypeReferenceResolver}s.
 * 
 * @author JohT
 */
enum DefaultTypeReferenceResolver implements TypeReferenceResolver {

	BY_CLASS_NAME {

		@Override
		public Class<?> getTypeForReference(String typename) {
			try {
				return Class.forName(typename);
			} catch (ClassNotFoundException e) {
				String message = String.format("Could not resolve %s: %s", typename, e.getMessage());
				throw new IllegalArgumentException(message, e);
			}
		}

		@Override
		public String getReferenceForType(Class<?> type) {
			return type.getName();
		}
	},

	;
}