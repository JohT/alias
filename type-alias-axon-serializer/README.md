[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joht.alias/type-alias-axon-serializer.svg?style=shield)](https://maven-badges.herokuapp.com/maven-central/io.github.joht.alias/type-alias-axon-serializer/)

# Alias types and performance improvement for Axon Serializer
Enhances an axon serializer to use aliases instead of type names.
Since this is done using a ResourceBundle, the Class object can be provided directly (without ``ClassLoader.loadClass``), which should improve performance. 

#### Limitations
Since this module is written to extend axon serializer externally using a decorator to avoid inheritance,
there are limitations: 

##### Not fully decoratable 
The directly mapped classes of the ResourceBundle cannot be used during deserialization 
because the current serializer implementations uses inter-method calls, that are not decoratable.
This doesn't affect alias naming. It only limits performance optimization.
As a temporal (experimental) workaround, "AliasableResourceBundleClassloader" could be used for these implementation. However, it is not said that this is faster than the default ClassLoader.

##### Might by done easier
Without any changes inside axon and the decision to build decorators, 
the solution is not as easy as it could be (e.g. Composed decorators mixed with functions).
This might be improved in future.

#### How to configure it
To make sure, that the enhancement can be used in conjunction with (hopefully) any Serializer, 
it is implemented as a decorator. As an example, the JacksonSerializer might be enhanced like this:

```java
private static final Function<Configuration, Serializer> jacksonSerializer(ObjectMapper objectMapper) {
	JacksonSerializer serializer = JacksonSerializer.builder().objectMapper(objectMapper).build();
	return c -> TypeDecoratableSerializer.aliasThroughDefaultResourceBundle(serializer);
}
```
#### Structure of the ResourceBundle
The ResourceBundle needs to be provided up front and can be generated at compile time by "type-alias".
It doesn't need to be done that way to use this serializer enhancement, as long as the 
ResourceBundle looks like this example:

```java

import java.util.ListResourceBundle;
import javax.annotation.Generated;

@Generated("type-alias")
public class TypeAlias extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "AddressCreated", org.alias.example.address.AddressCreatedEvent.class },
				{ "org.alias.example.address.AddressCreatedEvent", "AddressCreated"},
		};
	}
}
```
#### How to use it with "type-alias"
In combination with the java annotation processing based code generator "type-alias",
types only need to be annotated with ``@TypeAlias`` like this:

```java
package org.alias.example.address;
import org.alias.annotation.TypeAlias;
@TypeAlias("AddressCreated")
public class AddressCreatedEvent {
...
}
```
The ResourceBundle will be generated automatically during compile (pure java, build system independent).
Since the annotation ``@TypeAlias`` is defined with ``RetentionPolicy.SOURCE``,
it will be removed after compilation. There is no runtime dependency to "type-alias" at all.

#### Example to try it out
To try it out and for a complete example have a look at "type-alias-axon-serializer-integration-test".