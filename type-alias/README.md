# Type Alias
Java identifies types by their class name. 
The class name heavily depends on implementation details (e.g. where the class is located).
Finding the right name and right place may change during development.
As soon as libraries use the class name to identify e.g. an serialized object before reconstructing it,
renaming and moving classes leads to problems (e.g. runtime exceptions). 

**type-alias** enables naming those types distinctly besides their class name. 
This is done by using the annotation `@TypeAlias`.

#### How to use it

##### 1. Place Annotation @TypeAlias
```java
package org.alias.example.address;
import org.alias.annotation.TypeAlias;
@TypeAlias("AddressCreated")
public class AddressCreatedEvent {
...
}
```
##### 2. Generated default resource bundle
Without further configuration, the file *TypeAlias.java* is generated for the default package in the *generated-sources/annotations* folder during compilation.
The name of the folder, that contains generated files from java annotation processing, may differ. 

The generated resource bundle contains all aliases as keys as well as the full qualified type names. 
This makes it easier and faster to query both the type for an alias and the alias for a type.
The type of the value distinguishes if it is the type (Class) or the alias (String).

Without the header comments, the file will contain the following lines:

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

##### 3. Customizing file name and template using @TypeAliasGeneratedFile inside package-info.java

```java
@org.alias.annotation.TypeAliasGeneratedFile(value="CustomizedFileName", template = Template.PROPERTY_FILE)
package org.alias.example;
```

##### 4. Generated customized property file
The file *org/alias/example/CustomizedFileName.properties* is generated in the *generated-sources/annotations* folder during compilation.
The name of the folder, that contains generated files from java annotation processing, may differ. 
It will contain the same entries as above, because the address package is inside the package "org.alias.example".
Without the header comments, the file will contain the following line:

```
AddressCreated=org.alias.example.address.AddressCreatedEvent
```

##### 5. Customizing ResourceBundle generation
```java
@TypeAliasGeneratedFile(value = "CustomizedResourceBundle", template = Template.RESOURCE_BUNDLE_SIMPLE)
package org.alias.example;
import org.alias.annotation.TypeAliasGeneratedFile;
import org.alias.annotation.TypeAliasGeneratedFile.Template;;
```

##### 6. Generated customized ResourceBundle
The file *org/alias/example/CustomizedResourceBundle.java* is generated in the *generated-sources/annotations* folder during compilation. 
The name of the folder, that contains generated files from java annotation processing, may differ. 

The simple version of the resource bundle contains only the aliases as keys and their assigned Class
objects as value. 
It will look like this (documentation header left out for brevity):

```java
package org.alias.example;

import java.util.ListResourceBundle;
import javax.annotation.Generated;

@Generated("type-alias")
public class CustomizedResourceBundle extends ListResourceBundle {

	@Override
	protected Object[][] getContents() {
		return new Object[][] {
				{ "AddressCreated", org.alias.example.address.AddressCreatedEvent.class },
		};
	}
}
```

##### 7. Multiple external types
If it is not possible to annotate a type (external libraries, ...),
the alias annotations can also be placed on another type referencing the external type.
This can be done using the `type` parameter of `@TypeAlias`.
If it is necessary to specify more than one external type alias, 
a list of `@TypeAlias` annotations can be defined using a surrounding `@TypeAliases` annotateion.


```java
@TypeAliases({
		@TypeAlias(value = "AliasForExternalAlias", type = "org.alias.example.customized.external.NotAnnotatedExternalResourceBundleFileType")
})
```

##### 8. Dependencies
To minimize dependencies, **type alias** depends only on java itself.
Furthermore, the annotations are only visible during compilation and therefore doesn't show up in byte code.
The dependency to **type-alias** can and should be optional. 
It does not need to be present at runtime, nor in a WAR or EAR.
It is just like a build step, whereas it is independent from any build system (pure java).

As long as their are no additional dependencies and the module has no effect on the compiled code,
a separate api module is left out for purpose. This may change in future.

##### 9. Unit Test generated files
If the contents of the generated files are important for you, it is advisable to always write
unit tests for those contents. Loading a ResourceBundle and querying some entries is
very easy to do in a unit test. It helps to detect reconfigured file generations,
renamed or moved (external) types etc. . 

### Decisions
* **Yet another annotation?**
It is the most straightforward approach to place the alias name on top of each type. 
Since the annotation *@TypeAlias* is declared with *RetentionPolicy.SOURCE*,
it is removed after compilation and is therefore not present in byte code.  

* **No meta-annotation support (at least in v1)?**
Since **type-alias** uses pure java annotation processing,
*claiming unnecessary annotations may cause a performance slowdown in some environments*
(see JavaDoc of javax.annotation.processing.Processor.getSupportedAnnotationTypes()).

* **Why is there no separate API project (at least in v1)?**
Since **type-alias** doesn't need additional dependencies, annotations and their processor need to be present both, it is only relevant at compile time, it has one specialized use case and there is only
one implementation, there is (at least for v1) no separate API project containing the annotations.

* **Why ResourceBundles?**
ResourceBundles are pure java, easy to load and can contain Java Objects (not only Strings).
Although they are meant to be used for internalization, they are also great key/value registers.
**type-alias** is able to provide a register of Class objects, that can be looked up by their alias name.
Using this feature makes loading classes with Class.forName("...") obsolete.

* **Why property files?**
One of the most common ways to read key/value registers are (pure java) property files.
Every generated file starts with an comment header, containing all necessary informations.

* **Why is the type parameter a String instead of a Class type?**
Compile-time annotation processing differs from runtime reflection annotation queries.
It is not possible to use a Class and will lead to an Exception.
See the JavaDoc of `AnnotatedConstruct.getAnnotation(Class)`for more details.
It is advisable to always provide unit tests to check important contents of
generated property- and resource bundle files.