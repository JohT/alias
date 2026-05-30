# Integration Tests and Examples for Type-Alias Message Type Resolver (Axon 5)

This module contains integration tests and examples demonstrating how to use type aliases with Axon Framework 5.

## Overview

This example project shows:

- How to define domain events and commands with `@TypeAlias` annotations
- How to configure the `AliasableMessageTypeResolver` in an Axon 5 application
- How to verify that events are published with aliased message types in integration tests
- How the annotation processor automatically generates the type alias resource bundle

## Running the Tests

Run all integration tests:

```bash
mvn test
```

Or from the parent directory:

```bash
mvn test -f type-alias-axon-5-serializer-integration-test/pom.xml
```

## Example Structure

### Domain Model

The example uses a simple order scenario with:

#### Commands

- **`DoSomethingCommand`**: A simple command to trigger an event. Contains only an `aggregateId` field and relies on the default class-based type resolution for command routing (via `QualifiedName`).

```java
@ConstructorProperties({"aggregateId"})
public class DoSomethingCommand {
    private final String aggregateId;
    // ...
}
```

#### Events

- **`SomethingHappenedEvent`**: A domain event annotated with `@TypeAlias("SomethingHappened")`. This demonstrates how aliases are used for event type resolution.

```java
@TypeAlias("SomethingHappened")
public class SomethingHappenedEvent {
    private final String aggregateId;
    // ...
}
```

### Package Configuration

The `package-info.java` in the messages package:

```java
package org.alias.axon.serializer.example.messages;
```

This allows the annotation processor to collect all `@TypeAlias` annotations in the package and generate the `TypeAlias` resource bundle in the default package (required by `AliasableMessageTypeResolver.aliasThroughDefaultResourceBundle()`).

## Integration Tests

### Test 1: Alias Resolution

**`shouldResolveAliasFromAnnotation()`**

Verifies that the resolver correctly looks up the alias from the resource bundle:

```java
MessageTypeResolver resolver = AliasableMessageTypeResolver
    .aliasThroughDefaultResourceBundle(new ClassBasedMessageTypeResolver());

Optional<MessageType> result = resolver.resolve(SomethingHappenedEvent.class);

assertThat(result.isPresent(), is(true));
assertThat(result.get().name(), is("SomethingHappened"));
```

### Test 2: Event Publishing with Aliases

**`shouldPublishEventWithAliasedMessageType()`**

Uses `AxonTestFixture` to verify that when a command is dispatched, the resulting event carries the aliased message type name:

```java
fixture.given()
    .noPriorActivity()
    .when()
    .command(new DoSomethingCommand("id1"))
    .then()
    .success()
    .eventsSatisfy(events -> {
        assertThat(events, hasSize(1));
        assertThat(events.get(0).type().name(), is("SomethingHappened"));
    });
```

This test:

1. Configures Axon with the `AliasableMessageTypeResolver`
2. Dispatches a `DoSomethingCommand`
3. The command handler resolves the message type for `SomethingHappenedEvent` using the resolver
4. Verifies the published event has the alias name "SomethingHappened" instead of the full class name

## Key Configuration Points

### Annotation Processor Setup

The `pom.xml` includes explicit annotation processor path configuration:

```xml
<annotationProcessorPaths>
    <path>
        <groupId>io.github.joht.alias</groupId>
        <artifactId>type-alias</artifactId>
        <version>${project.version}</version>
    </path>
</annotationProcessorPaths>
```

This is required for Java 17+ to enable annotation processors on the classpath.

### Test Locale Consistency

The `pom.xml` configures Surefire with system properties to ensure consistent test behavior across different machines:

```xml
<systemPropertyVariables>
    <user.language>en</user.language>
    <user.country>US</user.country>
</systemPropertyVariables>
```

## How to Extend the Example

To add more domain events and commands:

1. Create new classes in `src/main/java/org/alias/axon/serializer/example/messages/`
2. Annotate events with `@TypeAlias("<YourAlias>")`
3. Commands don't need aliases (they use `QualifiedName` for routing)
4. Rebuild: `mvn clean compile`
5. The annotation processor automatically updates the `TypeAlias` resource bundle
6. Write integration tests to verify the behavior

## Architecture Overview

```text
┌─────────────────────────────────────────────────────────────┐
│                    Axon Test Fixture                        │
├─────────────────────────────────────────────────────────────┤
│  • Registers AliasableMessageTypeResolver                   │
│  • Configures command handling                              │
│  • Records published events                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│            AliasableMessageTypeResolver                      │
├─────────────────────────────────────────────────────────────┤
│  • Looks up FQCN in TypeAlias resource bundle               │
│  • Returns aliased MessageType if found                      │
│  • Falls back to ClassBasedMessageTypeResolver              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              Generated TypeAlias Resource Bundle             │
├─────────────────────────────────────────────────────────────┤
│  • Generated at compile time by annotation processor        │
│  • Maps FQCN → Alias for all @TypeAlias classes            │
│  • Located in default package (root of classpath)           │
└─────────────────────────────────────────────────────────────┘
```

## References

- [Axon Framework Documentation](https://docs.axoniq.io)
- [Axon Test Fixtures](https://docs.axoniq.io/reference-guide/axon-framework/testing)
- [type-alias Project](https://github.com/JohT/alias)
- [Java Annotation Processing](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/package-summary.html)
