# Fluent API generator
![Released version](https://img.shields.io/maven-central/v/foundation.fluent.api/fluent-api-generator.svg)
[![Build Status](https://travis-ci.org/c0stra/fluent-api-generator.svg?branch=master)](https://travis-ci.org/c0stra/fluent-api-generator)

Annotation processing based code generator used to generate fluent API for various situations or define 
custom templates to generate totally custom derived code.

## User guide

### 1. Maven dependencies

To use the annotations which are driving the code generation, include following
maven dependency:

```xml
<dependency>
    <groupId>foundation.fluent.api</groupId>
    <artifactId>fluent-api-generator-annotations</artifactId>
    <version>2.0</version>
</dependency>
```

### 1.1 Trigger code generator from standard class-path (not recommended)

Simplest way to trigger the code generation, is to simply include the generator in your
dependencies, so the `javac` will automatically detect the annotation processor, and use it.

Keep in mind, that using this approach you introduce the processor and it's transitive dependencies
to your project's run-time dependencies, which may not be desired.
Therefore it's not the recommended approach.

To include the dependency use following:
```xml
<dependency>
    <groupId>foundation.fluent.api</groupId>
    <artifactId>fluent-api-generator</artifactId>
    <version>2.0</version>
</dependency>
```

### 1.2 Trigger code generator from annotationProcessor path

Correct way to use the annotation processor is to specify it only for the `javac`, but not as runtime dependency.
For that you can pass it to annotation processor path.

Using maven you can achieve it this way:
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <annotationProcessorPath>
                        <groupId>foundation.fluent.api</groupId>
                        <artifactId>fluent-api-generator</artifactId>
                        <version>2.0</version>
                    </annotationProcessorPath>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

This is configuration of the standard compilation. That means, that the code generation is
always part of your compilation phase.

### 1.3 Trigger annotation processing on-demand

Normally, if the source for the code generation is static java code, it's fine to run it
all the time, during every compilation.

But sometimes it can be desired to fully controll the code generation yourself, meaning
to trigger it on-demand, and maybe include the result in your project.

This can be achieved e.g. using the `processor` maven plugin, if not bound to any
phase of maven lifecycle.

Then you can use following configuration:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.bsc.maven</groupId>
            <artifactId>maven-processor-plugin</artifactId>
            <version>3.3.3</version>
            <dependencies>
                <dependency>
                    <groupId>foundation.fluent.api</groupId>
                    <artifactId>fluent-api-generator</artifactId>
                    <version>2.0</version>
                </dependency>
            </dependencies>
            <configuration>
                <outputDirectory>src/main/generated</outputDirectory>
                <processors>
                    <processor>fluent.api.generator.processor.GeneratingProcessor</processor>
                </processors>
            </configuration>
        </plugin>
    </plugins>
</build>
```

Once you have this in your `pom.xml`, then you can trigger the code generation
explicitely using:
```text
mvn processor:process
```
Or using integration with your IDE.

For more details about the plugin see: [https://github.com/bsorrentino/maven-annotation-plugin](https://github.com/bsorrentino/maven-annotation-plugin)


### 2. Predefined use cases

The code generator comes with couple of ready to use annotations, which can be used directly to generate code
derived from some model Java element (class, method, field).

#### 2.1 Builder for call of factory method / constructor with many arguments
Let's have following class, which we need to be immutable, so we
pass all field values to constructor:

```java
public class Person {
    public enum Gender { MALE, FEMALE }
    private final String firstName;
    private final String lastName;
    private final ZonedDateTime birth;
    private final Gender gender;
    public Person(String firstName, String lastName, ZonedDateTime birth, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.gender = gender;
    }

    // ... methods

}
```

You can use following annotation on the constructor:
```java
    @FluentParameters
    public Person(String firstName, String lastName, ZonedDateTime birth, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birth = birth;
        this.gender = gender;
    }
```
And the generator will create new class `PersonBuilder` for you, which you can then use like this:
```java
Person person = new PersonBuilder()
    .firstName("John")
    .lastName("Doe")
    .birth(birthDate)
    .gender(MALE)
    .build();
```

Targets: constructor, instance method, static method.

#### 2.2 Fluent builder for beans with setters


### 3. Custom fluent API generator development

#### 3.1 Define new annotation

In order to introduce new fluent API code generator, you have to do following:
 
1. Define your annotation, that will be used to mark code, which should trigger the code generator.

2. Narrow the scope in which your annotation can be used

3. Associate the annotation with templates using the `@Templates` annotation. That also marks
your annotation for annotation processing using the code generator.

4. You can define parameters on the annotation, which will become available in your templates.

5. Create your templates using JTWIG syntax (see http://jtwig.org/documentation/reference)

An example of the annotation is e.g. `GenerateParametersBuilder`:
```java
package fluent.api.generator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Templates("/fluent/api/generator/templates/parameter-builder.java")
public @interface GenerateParametersBuilder {
    String methodName() default "build";
}

```

#### 3.2 Java model usable in JTWIG templates

TBD

## Release notes

### Version 2.0 (released on 31st October 2018)

- Changed annotations to:
  @FluentParameters
  @FluentBuilder
  @FluentSender
- Added possibility to specify custom terminal method name using `methodName` attribute.
- Default method name is now not hardcoded, but detected from method name, which is annotated, or
  contains annotated type.
- Added possibility to generate factory method, so it's not necessary to use constructor.
- Added detection of default constructor of target entity, and creating default constructor of parameter-less factory
  method

### Version 1.4 (released on 24th September 2018)

- Delivered [#1 Add proper support for sender and parameters builder for cases, when the method or containint class / interface is generic](https://github.com/c0stra/fluent-api-generator/issues/1)
- Delivered [#2 Support proper generic boundaries in generated code](https://github.com/c0stra/fluent-api-generator/issues/2)

