# alias
Java identifies types by their class name. 
The class name heavily depends on implementation details (e.g. where the class is located).
Finding the right name and right place may change during development.
As soon as libraries use the class name to identify e.g. an serialized object before reconstructing it,
renaming and moving classes leads to problems (e.g. runtime exceptions). 

**type-alias** enables naming those types distinctly besides their class name. 
This is done at compile time (no runtime dependencies) by using the annotation `@TypeAlias`.
The result is a generated file (e.g. ResourceBundle or Properties),
that contains a register of all aliases and their current type name.

- "type-alias" contains the main module with the java annotation processing based code generator.
- "type-alias-example" shows, how to use and customize "type-alias" code generation.
- "type-alias-axon-serializer" shows, how to enhance axon serializer to use aliases.
- "type-alias-axon-serializer-integration-test" shows, how to configure axon serializer to use aliases.
