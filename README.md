# alias
Java identifies types by their class name. 
The class name heavily depends on implementation details (e.g. where the class is located).
Finding the right name and right place may change during development.
As soon as the class name is used to identify e.g. an deserialized object before reconstructing it,
a changed class name leads to a runtime exception. 

**type-alias** enables naming those types distinctly besides their class name. 
This is done at compile time (no runtime dependencies) by using the annotation `@TypeAlias`.
The result is a generated file (e.g. ResourceBundle or Properties),
that contains a register of all aliases and their current type name.

**type-alias** only depends on Java itself and is not needed during runtime.
The annotations are not present in byte code (not accessible via reflection),
The generated alias name register is a standard Java file (ResourceBundle or Property-File).

### Quickstart
Include the following compile-time-only dependency.
It provides the annotations `@TypeAlias`, `@TypeAliases` to attach alias names,
`@TypeAliasGeneratedFile` for customization and the java annotation processing based file generator,
that generates (by default) the ResourceBundle `TypeAlias.java` inside the default package containing all aliases.

```xml
<dependency>
  <groupId>io.github.joht.alias</groupId>
  <artifactId>type-alias</artifactId>
  <version>1.0.0-RC11</version>
  <scope>provided</scope>
  <optional>true</optional>
</dependency>
```

### Contents
- "type-alias" contains the main module with the java annotation processing based file generator.
- "type-alias-example" shows, how to use and customize "type-alias" code generation.
- "type-alias-axon-serializer" shows, how to enhance axon serializer to use aliases.
- "type-alias-axon-serializer-integration-test" shows, how to configure axon serializer to use aliases.
