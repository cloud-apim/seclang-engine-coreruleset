# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SecLang Engine Coreruleset is a Scala library that provides the OWASP Core Rule Set (CRS) v4 for embedding in Scala applications. It depends on `seclang-engine` as its core dependency.

The library is cross-built for Scala 2.12 and 2.13.

## Build Commands

```bash
# Download CRS rules (required before packaging, defaults to v4.29.0, or ./setup.sh v4.30.0)
./setup.sh

# Check that the installed CRS is complete and comes from a single version (also runs before every package)
sbt checkCrs

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
- **Resources**: `src/main/resources/crs/` - CRS rule files (populated by `setup.sh`). `rules/` and `crs-setup.conf` are gitignored and generated; only `recommanded.conf` is hand-maintained
- **setup.sh**: Downloads a CRS version from GitHub, replaces `rules/` and installs its `crs-setup.conf.example` as the only setup file, `crs-setup.conf` (the preset loads every `.conf` of the directory, so a second setup file would be loaded too). Fails if the result mixes versions

The library provides both Scala and Java APIs to make the OWASP CRS accessible from either language.

## Dependencies

- Scala 2.12.21 (default), 2.13.18 and 3.8.4, declared as `crossScalaVersions` in `build.sbt`
- `com.cloud-apim:seclang-engine:2.5.1` - Core SecLang engine (published for 2.12, 2.13 and 3)
- `munit` - Test framework
