[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.joht.alias/type-alias.svg?style=shield)](https://maven-badges.herokuapp.com/maven-central/io.github.joht.alias/type-alias/)
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
  <version>3.0.0</version>
  <scope>provided</scope>
  <optional>true</optional>
</dependency>
```

### Building this project
Install maven and use `mvn install` to build this project, run all tests including the integration tests and copy the resulting artifacts into the local maven repository. A list of the most important commands can be found in [COMMANDS.md](COMMANDS.md).

### Changes
All changes are listed in [CHANGELOG.md](./CHANGELOG.md). The file is generated automatically during "generate-resources" build phase based on merged pull requests and their tags.

#### Breaking changes in Version 2.x

- `type-alias-jsonb-typereference` had been migrated to Java 11
- `type-alias-example` had been migrated to Java 11
- `type-alias-axon-serializer-integration-test` will no longer be published to maven central

#### Breaking changes in Version 3.x

- `type-alias-jsonb-typereference` had been migrated to Java 17
- `type-alias-example` had been migrated to Java 17
- `type-alias-axon-serializer-integration-test` had been migrated to Java 17

### Contents
- [type-alias](https://github.com/JohT/alias/tree/master/type-alias) 
contains the main module with the java annotation processing based file generator.
- [type-alias-example](https://github.com/JohT/alias/tree/master/type-alias-example) 
shows, how to use and customize "type-alias" code generation.
- [type-alias-axon-serializer](https://github.com/JohT/alias/tree/master/type-alias-axon-serializer)
shows, how to enhance axon serializer to use aliases.
- [type-alias-axon-serializer-integration-test](https://github.com/JohT/alias/tree/master/type-alias-axon-serializer-integration-test)
shows, how to configure axon serializer to use aliases.
