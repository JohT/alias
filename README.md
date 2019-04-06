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

- "type-alias" contains the main module with the java annotation processing based code generator.
- "type-alias-example" shows, how to use and customize "type-alias" code generation.
- "type-alias-axon-serializer" shows, how to enhance axon serializer to use aliases.
- "type-alias-axon-serializer-integration-test" shows, how to configure axon serializer to use aliases.
