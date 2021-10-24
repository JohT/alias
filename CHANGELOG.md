# CHANGELOG.md

## **v1.1.2** Update of test and example dependencies (latest)

### Maintenance:
- [Renovate](https://github.com/apps/renovate) activated and configured
- Test and example dependencies updated
- Integration test fixed

## **v1.1.1** Integration tests

### Features:
- Renewed integration test environment using a managed wildfly (latest distributing) with arquillian.

### Fixes:
- Version updates to fix integration test security vulnerabilities. The main module (type-alias) hadn't been changed. With only one test dependency it isn't affected by security vulnerabilities introduced by  dependencies.

## **v1.1.0** Support for multiple aliases

### Features:
- [Support for multiple aliases](https://github.com/JohT/alias/issues/4)