[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joht.alias/type-alias-jsonb-typereference.svg?style=shield)](https://maven-badges.herokuapp.com/maven-central/io.github.joht.alias/type-alias-jsonb-typereference/)

# "type-alias-jsonb-typereference"
This module shows an example on how to work with type references in conjunction with
"Java API for JSON Binding (JSON-B)". For details please visit http://json-b.net.

#### About type references
Type references are necessary, when the type is not static and therefore not known up front. Without any further configuration, it wouldn't be possible to deserialize
a list of Animal interface types, because a interface cannot be instantiated
and the original type gets lost during serialization. Adding a type reference for each dynamically typed object during serialization solves that problem.

#### How to use it

##### 1. Add the adapter for the interface or super class to the configuration
```java
JsonbConfig config = new JsonbConfig().withAdapters(new TypeReferenceEnhancer<Animal>() {});
```
##### 2. Provide a custom TypeReferenceResolver (Optional)
```java
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
```
##### 3. Register the custom TypeReferenceResolver for the ServiceLoader (Optional)
File ``\META-INF\org.alias.jsonb.typereference.resolver.TypeReferenceResolver`` contains

```
mypackage.TestTypeReferenceResolver
```

#### Further details
The unit test folder shows the full example with all optional configurations.

##### TypeReferenceEnhancer (JsonbAdapter)
This module provides the ``TypeReferenceEnhancer``, which is a generic ``JsonbAdapter``.
The configured type will be enhanced by the property ``"__typeref"`` during serialization, which is then used to deserialize the referenced type. This may be done in conjunction with a generated ``ResourceBundle`` of the module  ``type-alias``. 

##### Providing a TypeReferenceResolver
Since ``TypeReferenceEnhancer`` needs to know, which type is meant by the String in the
``"__typeref"`` property, an implementation of ``TypeReferenceResolver`` needs to be provided. This is done using a ``ServiceLoader``. To provide your own implementation, 
there needs to be a file ``META-IF\services\org.alias.jsonb.typereference.resolver.TypeReferenceResolver``
containing the full qualified name of your implementation.
Without that, the default implementation tries to find the ResourceBundle "TypeAlias" inside the default package, which needs to contain a register of all dynamically used
types (full qualified name as key) and their alias names (as value) and
all alias names (as key) and their assigned Classes.

##### Providing a JsonbConfigProvider
``TypeReferenceEnhancer`` converts the wrapped object on its own.
If it should use a custom ``JsonbConfig``, you can provide it by implementing ``JsonbConfigProvider``. The lookup is also done using a ``ServiceLoader``.
The file name is ``META-IF\services\org.alias.jsonb.typereference.configprovider.JsonbConfigProvider``.
Without that, the default configuration is used. 