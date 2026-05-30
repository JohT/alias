[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Type-Alias Message Type Resolver for Axon Framework 5

A lightweight `MessageTypeResolver` implementation for Axon Framework 5 that uses type aliases instead of fully qualified class names. This reduces message payload size and enables cleaner, more maintainable event type naming.

## Why Use Aliases?

In Axon 5, `MessageType` is used to identify command and event types in the routing and serialization process. By default, Axon uses fully qualified class names (e.g., `org.example.events.OrderCreatedEvent`), which:

- Are verbose and tightly coupled to package structure
- Make refactoring (package moves) break event compatibility
- Create larger message payloads
- Are less user-friendly in message serialization formats

By using aliases (e.g., `OrderCreated`), you gain:

- **Backwards Compatibility**: Rename or move classes without breaking event deserialization
- **Cleaner Naming**: Use simple, business-friendly names for event types
- **Reduced Payload Size**: Shorter type names significantly reduce serialized message size, benefiting network bandwidth and event store storage
- **Flexibility**: Update the alias mapping independently from code

## Alternative Approaches in Axon 5

Axon Framework 5 provides several built-in solutions for custom message type naming. Here's how they compare:

### 1. Annotation-Based Approach (Simplest)

Use `@Event`, `@Command`, or `@Query` annotations directly on your message classes:

```java
@Event(namespace = "orders", name = "OrderCreated", version = "1.0")
public class OrderCreatedEvent {
    private final String orderId;
    // ...
}

@Command(namespace = "orders", name = "CreateOrder", version = "1.0")
public class CreateOrderCommand {
    private final String orderId;
    // ...
}
```

**Pros**: Built-in, no external dependencies, declarative  
**Cons**: Distributed configuration (scattered across classes), no compile-time validation

### 2. NamespaceMessageTypeResolver (Similar to Type-Alias)

Axon 5's built-in centralized resolver for explicit mappings:

```java
NamespaceMessageTypeResolver resolver = NamespaceMessageTypeResolver
    .namespace("orders")
    .message(OrderCreatedEvent.class, "OrderCreated", "1.0")
    .message(OrderShippedEvent.class, "OrderShipped", "1.0")
    .fallback(new ClassBasedMessageTypeResolver());
```

**Pros**: Built-in, centralized configuration, fluent API, fallback support  
**Cons**: Configuration-time only, no compile-time validation

### 3. HierarchicalMessageTypeResolver (Gradual Migration)

Chain resolvers together to support multiple resolution strategies:

```java
HierarchicalMessageTypeResolver resolver = new HierarchicalMessageTypeResolver(
    newResolver,  // Try new aliases first
    oldResolver   // Fall back to old class names
);
```

**Pros**: Enables gradual migration from old to new message type names  
**Cons**: Requires explicit chaining logic

### 4. Type-Alias (This Library)

Compile-time generated, annotation-based approach with centralized resource bundle:

```java
@TypeAlias("OrderCreated")
public class OrderCreatedEvent { /* ... */ }
```

**Pros**:

- Compile-time validation catches missing aliases before runtime
- Single source of truth in generated `TypeAlias` resource bundle
- Audit trail of all aliases (generated class documents mappings)
- Shareable across multiple services/modules

**Cons**:

- Requires annotation processor setup
- Additional dependency (type-alias library)
- Slightly more complex build configuration

## When to Use Type-Alias

Choose **Type-Alias** if you need:

✅ **Compile-time validation** - Catch configuration errors before runtime  
✅ **Shared alias definitions** - Reuse the same `TypeAlias` resource bundle across multiple services  
✅ **Centralized audit trail** - Generated `TypeAlias` class documents all message type mappings  
✅ **Build-time guarantees** - Ensure every `@TypeAlias` annotation is properly processed  

Choose **built-in alternatives** if you prefer:

✅ **Simplicity** - Direct annotations or simple builder configuration  
✅ **No dependencies** - Use only Axon Framework's native APIs  
✅ **Flexibility** - Change mappings at runtime without recompilation  

## How to Configure It

### 1. Add the Dependency

```xml
<dependency>
    <groupId>io.github.joht.alias</groupId>
    <artifactId>type-alias-axon-5-serializer</artifactId>
    <version>2.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Create Type Aliases

Annotate your command and event classes with `@TypeAlias`:

```java
package org.example.orders.events;

import org.alias.annotation.TypeAlias;

@TypeAlias("OrderCreated")
public class OrderCreatedEvent {
    private final String orderId;
    
    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }
    
    public String getOrderId() {
        return orderId;
    }
}
```

### 3. Generate the Resource Bundle

Create a `package-info.java` to trigger annotation processing:

```java
package org.example.orders.events;

import org.alias.annotation.TypeAliases;
```

The `type-alias` annotation processor will automatically generate a `TypeAlias` resource bundle at compile time containing all `@TypeAlias` annotated types.

### 4. Register the Resolver in Axon Configuration

When configuring your Axon application, register the `AliasableMessageTypeResolver`:

```java
import org.alias.axon.serializer.AliasableMessageTypeResolver;
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer;
import org.axonframework.messaging.core.ClassBasedMessageTypeResolver;

EventSourcingConfigurer configurer = EventSourcingConfigurer.create()
    .messaging(m -> m.registerMessageTypeResolver(
        config -> AliasableMessageTypeResolver.aliasThroughDefaultResourceBundle(
            new ClassBasedMessageTypeResolver()
        )
    ));
```

For routing to work correctly with commands:

- Commands are routed by the fully qualified name of the command class (via `QualifiedName`)
- Events published through the resolver will use the alias name from the type resolver
- The resolver falls back to the delegate resolver (default class-based) if no alias is found

## ResourceBundle Structure

The generated `TypeAlias` resource bundle is a Java `ListResourceBundle` with bidirectional mappings:

```java
import java.util.ListResourceBundle;
import javax.annotation.Generated;

@Generated("type-alias")
public class TypeAlias extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
            // Alias -> Class (for deserialization hints)
            { "OrderCreated", org.example.orders.events.OrderCreatedEvent.class },
            // FQCN -> Alias (used by the resolver)
            { "org.example.orders.events.OrderCreatedEvent", "OrderCreated" },
        };
    }
}
```

The resolver looks up the fully qualified class name and retrieves the alias string value.

## Using with Type-Alias Annotation Processor

To avoid manual maintenance, use the `type-alias` annotation processor:

1. Add the annotation processor dependency:

```xml
<dependency>
    <groupId>io.github.joht.alias</groupId>
    <artifactId>type-alias</artifactId>
    <version>2.0.1-SNAPSHOT</version>
    <optional>true</optional>
    <scope>provided</scope>
</dependency>
```

2. Configure Maven compiler with explicit annotation processor paths (required for Java 17+):

```xml
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>io.github.joht.alias</groupId>
                <artifactId>type-alias</artifactId>
                <version>2.0.1-SNAPSHOT</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

3. Simply annotate classes, and the resource bundle will be generated automatically during compilation.

## Complete Example

See `type-alias-axon-5-serializer-integration-test` for a complete working example with:

- Domain events and commands annotated with `@TypeAlias`
- Integration tests demonstrating the resolver in action
- Axon test fixture configuration with the resolver

Run the example:

```bash
cd type-alias-axon-5-serializer-integration-test
mvn test
```

## Axon 5 Specifics

This module targets **Axon Framework 5.1.0** and later. Key differences from Axon 4:

- **MessageTypeResolver**: Axon 5 uses a functional interface `MessageTypeResolver` instead of serializer decorators
- **MessageType**: Encapsulates type name and version (e.g., `new MessageType("OrderCreated", "1.0")`)
- **QualifiedName**: Used for command routing based on fully qualified class names
- **Simpler Integration**: No need to decorate serializers; just register the resolver in messaging configuration

For Axon 4 compatibility, use `type-alias-axon-serializer` instead.

## Comparison Table

| Feature | Type-Alias | Annotations | NamespaceResolver | HierarchicalResolver |
|---------|-----------|-------------|-------------------|----------------------|
| Built-in | ❌ | ✅ | ✅ | ✅ |
| Centralized Config | ✅ | ❌ | ✅ | ❌ |
| Compile-time Validation | ✅ | ❌ | ❌ | ❌ |
| No Dependencies | ❌ | ✅ | ✅ | ✅ |
| Shared Across Services | ✅ | ❌ | ❌ | ❌ |
| Setup Complexity | Medium | Low | Medium | Low |
| Gradual Migration | Via Fallback | Via Fallback | Via Fallback | ✅ Primary Use |

## References

- [Axon Framework Documentation](https://docs.axoniq.io)
- [Axon 5 MessageTypeResolver API](https://docs.axoniq.io/reference-guide/axon-framework/messaging-concepts/message-type-resolution)
- [type-alias Project](https://github.com/JohT/alias)
- [Java Annotation Processing API](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/annotation/processing/package-summary.html)
