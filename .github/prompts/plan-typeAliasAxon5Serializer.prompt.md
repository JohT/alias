# Plan: Axon 5 Serializer Adapter (type-alias-axon-5-serializer)

## Context / Key Findings

### Why a new artifact (not single v4+v5)
- Axon 4: `Serializer` at `org.axonframework.serialization.Serializer` (in `axon-configuration`)
- Axon 5: `Serializer` moved to `org.axonframework.conversion.Serializer` — different package = binary incompatible
- `axon-configuration` does not exist in Axon 5; replaced by `axon-messaging`, `axon-conversion`, etc.
- Two artifacts accepted: `type-alias-axon-5-serializer` + `type-alias-axon-5-serializer-integration-test`

### Axon 5 type aliasing hook
- **Axon 4**: hook via `Serializer.typeForClass(Class)` (store alias) and `classForType(SerializedType)` (resolve alias)
- **Axon 5**: hook is `MessageTypeResolver` (in `axon-messaging`)
  - `@FunctionalInterface` with `Optional<MessageType> resolve(Class<?> payloadType)`
  - Used for BOTH write path (storing type name) AND handler routing (read path)
  - Class resolution at handler invocation uses the handler's declared parameter type directly — no `classForType` needed
  - Existing implementations: `ClassBasedMessageTypeResolver` (FQCN), `AnnotationMessageTypeResolver` (@Event/@Command annotations), `NamespaceMessageTypeResolver` (explicit map), `HierarchicalMessageTypeResolver` (chain)
- The new library is actually **simpler** than the v4 one (single hook point vs. two)

### Key decisions
- Java 17+ for v5 library (Axon 5 requires Java 17)
- Depend only on `type-alias` (core module) + `axon-messaging` 5.1.0 (both optional scope)
- No dependency on `type-alias-axon-serializer`
- Alias format: flat, e.g. `MessageType("AccountCreated", MessageType.DEFAULT_VERSION)` — alias as full qualified name
- No Axon-internal type aliases (only user domain types)
- Integration test: standalone JUnit 5, no WildFly/CDI/Arquillian
- Integration test scope: minimal — prove aliasing works end-to-end

### Axon 5.1.0 Maven artifacts (org.axonframework)
- `axon-messaging` 5.1.0 — MessageTypeResolver, MessageType, QualifiedName
- `axon-modelling` 5.1.0 — aggregate support (@AggregateRoot, @CommandHandler, @EventSourcingHandler)
- `axon-eventsourcing` 5.1.0 — event storage
- `axon-test` 5.1.0 — AggregateTestFixture
- `axon-conversion` 5.1.0 — JacksonConverter (new) and deprecated Serializer (stash/legacy)

---

## Plan: type-alias-axon-5-serializer + integration-test

### Phase 1: Core library — type-alias-axon-5-serializer

**Step 1.1** Create `type-alias-axon-5-serializer/pom.xml`
- Parent: `alias-parent 2.0.1-SNAPSHOT`
- Java 17 source/target (unlike v4's Java 1.8)
- Dependencies (all optional):
  - `type-alias` (provided scope) — for @TypeAlias annotation + ResourceBundle structure
  - `axon-messaging` 5.1.0 (optional) — MessageTypeResolver, MessageType
- Test dependencies: junit-jupiter, hamcrest-core, mockito-core
- Build: `-proc:none` (no annotation processing), maven-compiler-plugin Java 17

**Step 1.2** Create `AliasableMessageTypeResolver.java`
- Package: `org.alias.axon.serializer` (same base package as v4 for consistency)
- `implements MessageTypeResolver`
- Fields: `MessageTypeResolver delegate`, `Function<Class<?>, Optional<String>> aliasLookup`
- Factory methods (analogous to TypeDecoratableSerializer):
  - `aliasThroughDefaultResourceBundle(MessageTypeResolver delegate)` — uses "TypeAlias" ResourceBundle
  - `aliasThroughResourceBundle(MessageTypeResolver delegate, ResourceBundle resourceBundle)` — explicit bundle
- `resolve(Class<?> payloadType)`:
  - Look up `payloadType.getName()` in ResourceBundle; if a String value exists → it's the alias
  - Return `Optional.of(new MessageType(alias, MessageType.DEFAULT_VERSION))`
  - Else → `delegate.resolve(payloadType)`
- Note: ResourceBundle lookup is self-contained (20 lines), no dependency on v4 library

**Step 1.3** Unit tests for `AliasableMessageTypeResolver` — **MUST HAVE** in BDD/TDD style
- `AliasableMessageTypeResolverTest.java` (Behavior Driven Development style with clear spec):
  - **Given** a ResourceBundle with an alias mapping, **when** resolving a type, **then** return MessageType with alias name
  - **Given** a ResourceBundle without an alias mapping, **when** resolving a type, **then** delegate to fallback resolver  
  - **Given** default factory method used, **when** resolving a type, **then** use "TypeAlias" ResourceBundle by convention
  - **Given** null or empty alias values in ResourceBundle, **when** resolving, **then** safely handle and delegate
  - Spec-style naming: `shouldReturnAliasWhenFoundInResourceBundle()`, `shouldDelegateWhenNoAliasFound()`, etc.
- Tests written first (TDD), implementation follows
- All tests must pass with 100% code coverage for `AliasableMessageTypeResolver`

**Step 1.4** package-info.java pointing to `AliasableMessageTypeResolver#aliasThroughResourceBundle`

### Phase 2: Integration test — type-alias-axon-5-serializer-integration-test

**Step 2.1** Create `type-alias-axon-5-serializer-integration-test/pom.xml`
- Parent: `alias-parent 2.0.1-SNAPSHOT`
- Java 17 source/target
- Dependencies:
  - `type-alias-axon-5-serializer` (compile)
  - `type-alias` (provided) — annotation processor
  - `axon-messaging` 5.1.0
  - `axon-modelling` 5.1.0
  - `axon-eventsourcing` 5.1.0
  - `axon-test` 5.1.0 (test scope)
  - `axon-conversion` 5.1.0 (test scope) — JacksonConverter
  - `jackson-databind` (test scope) — JSON serialization
  - `junit-jupiter` (test scope)
  - `hamcrest-core` (test scope)
- NO arquillian, NO wildfly, NO reactor, NO CDI, NO JEE APIs
- Build: maven-compiler-plugin Java 17, maven-surefire-plugin

**Step 2.2** Domain model — minimal test aggregate
- `messages/` package with `@TypeAlias`-annotated events:
  - `SomethingHappenedEvent` with `@TypeAlias("SomethingHappened")`
- `@TypeAliases` + `@TypeAliasGeneratedFile` on package-info.java → generates ResourceBundle
- Aggregate: `SomethingAggregate` with @CommandHandler and @EventSourcingHandler

**Step 2.3** Integration tests (plain JUnit 5, BDD style, **MUST HAVE**)
- `TypeAliasMessageTypeResolverIT.java` (Behavior Driven Development):
  1. **Given** an alias defined via `@TypeAlias("SomethingHappened")`, **when** resolving `SomethingHappenedEvent.class`, **then** return `MessageType("SomethingHappened", ...)`
  2. **Given** an `AggregateTestFixture` configured with `AliasableMessageTypeResolver` and domain event with alias, **when** command dispatched and event published, **then** stored event's `MessageType.name()` equals "SomethingHappened" (not FQCN)
  3. **Given** events stored under alias, **when** aggregate re-sourced from event store, **then** state correctly reconstructed
- Test names follow spec style: `shouldResolveAliasFromAnnotation()`, `shouldStoreEventUnderAliasName()`, `shouldRestoreAggregateFromAliasedEvents()`
- All integration tests must pass

### Phase 3: Root POM update

**Step 3.1** Add `type-alias-axon-5-serializer` to root `pom.xml` modules list
- `type-alias-axon-5-serializer-integration-test` should NOT be in root modules (same as v4 pattern)

### Relevant files
- `pom.xml` (root) — add module entry for `type-alias-axon-5-serializer`
- `type-alias-axon-serializer/pom.xml` — reference for v4 structure/patterns
- `type-alias-axon-serializer/src/main/java/org/alias/axon/serializer/TypeDecoratableSerializer.java` — reference API pattern
- `type-alias-axon-serializer/src/main/java/org/alias/axon/serializer/aliasable/AliasableTypeResourceBundleRegister.java` — reference ResourceBundle logic (DO NOT depend on; re-implement simply)
- `type-alias-axon-serializer-integration-test/src/main/java/.../messages/` — reference for @TypeAlias annotations pattern

### Verification steps
1. `mvn install -pl type-alias-axon-5-serializer` — library builds without errors
2. `mvn test -pl type-alias-axon-5-serializer` — unit tests pass
3. `mvn test -pl type-alias-axon-5-serializer-integration-test` — integration tests pass
4. Verify no compile dependency on `type-alias-axon-serializer` (v4 library)
5. Verify `axon-messaging` is `optional` scope in library pom
6. `mvn install` from root — root build includes new library module

### Decisions
- Single artifact impossible: binary incompatibility between Axon 4/5 Serializer packages
- Axon 5 hook: `MessageTypeResolver` (simpler than v4's two-hook Serializer approach)
- Alias format: flat `MessageType(alias, DEFAULT_VERSION)` — same as Axon 5's FQCN-based default, just alias string replaces FQCN
- No Axon-internal aliases (GapAwareTrackingToken etc.) — only user domain types
- No WildFly/CDI: removed entirely for v5 integration test
- Java 17+ for v5 module (Axon 5 requirement)

### Out of scope
- Backward compatibility reading events stored with v4 alias names (upcaster territory)
- Spring Boot auto-configuration bean for AliasableMessageTypeResolver
- Support for @Event/@Command native Axon 5 annotations (users can chain AnnotationMessageTypeResolver before our resolver)

### Guidelines

#### Think Before Coding

**Don't assume. Don't hide confusion. Surface tradeoffs.**

Before implementing:
- State your assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them - don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

#### Simplicity First

**Minimum code that solves the problem. Nothing speculative.**

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

Ask yourself: "Would a senior engineer say this is overcomplicated?" If yes, simplify.

#### Goal-Driven Execution

**Define success criteria. Loop until verified.**

Transform tasks into verifiable goals:
- "Add validation" → "Write tests for invalid inputs, then make them pass"
- "Fix the bug" → "Write a test that reproduces it, then make it pass"
- "Refactor X" → "Ensure tests pass before and after"

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
3. [Step] → verify: [check]
```

#### Unit Tests are Non-Negotiable

**BDD/TDD style. Tests first, implementation second. Clear spec.**

- **Every new class MUST have unit tests** — no exceptions.
- Tests written in **Behavior Driven Development (BDD) style**:
  - Use `Given/When/Then` structure in test names and comments
  - Spec-style test names: `shouldReturnAliasWhenFoundInResourceBundle()`, not `testResolveAlias()`
  - Each test verifies ONE behavior, not multiple assertions on different concerns
  - Red → Green → Refactor cycle (TDD)
- **100% code coverage** for production code
- Use meaningful assertion messages (not default Hamcrest/JUnit messages)
- Mock external dependencies; test behavior, not implementation details
- No empty test files or placeholder tests