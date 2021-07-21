# Integration tests and example for using "type-alias-axon-serializer"
This example project, which also contains the integration tests, shows,
how to use aliases for serialization with axon as CQRS- and Eventsourcing-framework.

It uses Java EE 8 on an managed wildfly server with an in memory databases,
so there is no need to setup anything. Just start the maven build using `mvn install`.

The integration tests are inside the test-package "...integration",
if you want to have a look at the unit tests.

### References

- [axon Framework](https://axoniq.io)
- [arquillian-wildfly-example](https://github.com/tolis-e/arquillian-wildfly-example)
