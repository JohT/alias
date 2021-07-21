# Commands

Overview of the commands to test, run, build and release this project.

## Prerequisites
- [Installing Apache Maven](https://maven.apache.org/install.html)

## Most important commands for development:
- `mvn verify` Builds the project and runs all tests including integration tests 
- `mvn install` Includes `mvn verify` and copies the resulting artifacts into the local maven repository.

## (maintainer) Commands to release a new version:
- `mvn clean` Starts off clean
- `mvn release:prepare` Prepares a new version
- `mvn release:perform` Performs the publish-ready release including tag and version number increment
- `git push–tags` Pushes the newly created tag to the remote repository
- `git push origin master` Pushes code changes (pom) to the remote repository