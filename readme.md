# Cloud APIM - SecLang Engine Core Rule Set

<p align="center">
  <a href="https://central.sonatype.com/artifact/com.cloud-apim/seclang-engine-coreruleset_2.12"><img src="https://img.shields.io/maven-central/v/com.cloud-apim/seclang-engine-coreruleset_2.12?color=blue" alt="Maven Central"></a>
  <a href="https://github.com/cloud-apim/seclang-engine-coreruleset/blob/main/LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
</p>

SecLang Engine Coreruleset is a Scala library meant to provide the [OWASP Core Rule Set (CRS) v4](https://coreruleset.org/) in an easy and consumable way to be embedded in a Scala application.

## Installation

### SBT

```scala
libraryDependencies += "com.cloud-apim" %% "seclang-engine-coreruleset" % "1.3.0"
```

### Maven

```xml
<dependency>
    <groupId>com.cloud-apim</groupId>
    <artifactId>seclang-engine-coreruleset_2.12</artifactId>
    <version>1.3.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'com.cloud-apim:seclang-engine-coreruleset_2.12:1.3.0'
```

## Usage

### Scala

```scala
import com.cloud.apim.seclang.scaladsl.SecLang
import com.cloud.apim.seclang.scaladsl.coreruleset.EmbeddedCRSPreset

// Create a factory with the embedded CRS preset
val presets = Map(
  "crs" -> EmbeddedCRSPreset.embedded
)
val factory = SecLang.factory(presets)

// Define your rules configuration using the preset
val rulesConfig = List(
  "@import_preset crs",
  "SecRuleEngine On"
)
// create an engine to analyse a request/response
val engine = factory.engine(rulesConfig)
// Evaluate a request
val result = engine.evaluate(requestContext, phases = List(1, 2))
```

### Java

```java
import com.cloud.apim.seclang.javadsl.JSecLang;
import com.cloud.apim.seclang.javadsl.JSecLangPreset;
import com.cloud.apim.seclang.javadsl.coreruleset.EmbeddedCRSPreset;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

// Create a factory with the embedded CRS preset
Map<String, JSecLangPreset> presets = new HashMap<>();
presets.put("crs", EmbeddedCRSPreset.embedded);

JSecLang.Factory factory = JSecLang.factory(presets);

// Define your rules configuration using the preset
List<String> rulesConfig = Arrays.asList(
    "@import_preset crs",
    "SecRuleEngine On"
);
// create an engine to analyse a request/response
JSecLangEngine engine = factory.engine(rulesConfig);
// Evaluate a request
JSecLang.Result result = engine.evaluate(requestContext, Arrays.asList(1, 2));
```