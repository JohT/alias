# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| 1.1.1   | :white_check_mark: |
| < 1.1.1 | :x:                |

## Reporting a Vulnerability

Create an [issue](https://github.com/JohT/alias/issues/new/choose) 
and add the label "security" to report a vulnerability. Since this is right now maintained in free time, 
it might take a while. Providing a detailed description or a pull request with the fix are
the best ways to support and speed up the process.

## Tracking Vulnerabilities

The free version of [snyk.io](https://snyk.io) is used to track security issues.

## Priority

[type-alias](https://github.com/JohT/alias/tree/master/type-alias) 
contains the main module with the java annotation processing based file generator. 
Any security issue within this module will be fixed with priority.

Every other module is more or less an example on how to use the main module. 
They are not meant to be used directly. Issues there will only be fixed ocasionally.
