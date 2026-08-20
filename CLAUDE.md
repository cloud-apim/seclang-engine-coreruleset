# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SecLang Engine Coreruleset is a Scala library that provides the OWASP Core Rule Set (CRS) v4 for embedding in Scala applications. It depends on `seclang-engine` as its core dependency.

The library is cross-built for Scala 2.12 and 2.13. Note that `seclang-engine` is only published for 2.12 for now, so the 2.13 axis will fail dependency resolution until the 2.13 artifact of the engine is released.

## Build Commands

```bash
# Download CRS rules (required before packaging, defaults to v4.22.0)
./setup.sh

# Compile the project (default Scala version, 2.12)
sbt compile

# Compile/test/package for every cross Scala version
sbt '+compile'
sbt '+test'
sbt '+package'

# Target one specific version
sbt '++2.13.18; compile'

# Full build workflow (compile, package, publish for all versions)
sbt ';+compile;+package;+publishSigned;sonaRelease'
```

## Architecture

- **Scala API**: `src/main/scala/com/cloud/apim/seclang/scaladsl/` - Scala DSL for CRS
- **Java API**: `src/main/java/com/cloud/apim/seclang/javadsl/` - Java-friendly API (`EmbeddedCRSPreset`)
- **Resources**: `src/main/resources/crs/` - CRS rule files (populated by `setup.sh`)
- **setup.sh**: Downloads and extracts CRS rules from GitHub for a given version

The library provides both Scala and Java APIs to make the OWASP CRS accessible from either language.

## Dependencies

- Scala 2.12.21 (default) and 2.13.18, declared as `crossScalaVersions` in `build.sbt`
- `com.cloud-apim:seclang-engine:1.5.0` - Core SecLang engine
- `munit` - Test framework
