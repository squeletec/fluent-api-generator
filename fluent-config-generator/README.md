# Fluent API generator
![Released version](https://img.shields.io/maven-central/v/foundation.fluent.api/fluent-config-generator.svg)

Predefined annotation processing based fluent builder of configuration bean.

## User guide

### 1. Maven dependencies

To use the annotations which are driving the code generation, include following
maven dependency:

```xml
<dependency>
    <groupId>foundation.fluent.api</groupId>
    <artifactId>fluent-config-generator</artifactId>
    <version>2.7</version>
</dependency>
```

For maven configuration of annotation processor to trigger code generation see: [../README.md](../README.md)

### 2. Config with all parameters optional

First define the configuration items in a simple class:

