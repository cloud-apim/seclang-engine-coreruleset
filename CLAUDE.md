# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SecLang Engine Coreruleset is a Scala library that provides the OWASP Core Rule Set (CRS) v4 for embedding in Scala applications. It depends on `seclang-engine` (v1.1.0) as its core dependency.

## Build Commands

```bash
# Download CRS rules (required before packaging, defaults to v4.22.0)
./setup.sh

# Compile the project
sbt compile

# Run tests
sbt test

# Package the library
sbt package

# Full build workflow (compile, package, publish)
sbt ';compile;package;publishSigned;sonaRelease'
```

## Architecture

- **Scala API**: `src/main/scala/com/cloud/apim/seclang/scaladsl/` - Scala DSL for CRS
- **Java API**: `src/main/java/com/cloud/apim/seclang/javadsl/` - Java-friendly API (`EmbeddedCRSPreset`)
- **Resources**: `src/main/resources/crs/` - CRS rule files (populated by `setup.sh`)
- **setup.sh**: Downloads and extracts CRS rules from GitHub for a given version

The library provides both Scala and Java APIs to make the OWASP CRS accessible from either language.

## Dependencies

- Scala 2.12.21
- `com.cloud-apim:seclang-engine:1.1.0` - Core SecLang engine
- `munit` - Test framework
