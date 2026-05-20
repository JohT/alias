# Commands

Overview of the commands to test, run, build and release this project.

## Prerequisites

- [Installing Apache Maven](https://maven.apache.org/install.html)

## Most important commands for development

- `mvn verify` Builds library modules and runs their tests (integration-test modules are not part of the root reactor)
- `mvn install` Includes `mvn verify` and copies the resulting artifacts into the local maven repository.
- To run integration tests, build the specific integration-test module explicitly (e.g., `mvn test -f type-alias-axon-5-serializer-integration-test/pom.xml`)

## (maintainer) Commands to release a new version

- `mvn clean` Starts off clean
- `mvn release:prepare` Prepares a new version
- `mvn release:perform` Performs the publish-ready release including tag and version number increment
- `git push–tags` Pushes the newly created tag to the remote repository
- `git push origin main` Pushes code changes (pom) to the remote repository