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
    <version>2.16</version>
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
    <version>2.16</version>
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
                        <version>2.16</version>
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
                    <version>2.16</version>
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

For creating fluent interface, which contain methods for every parameter of a method or
constructor, there is annotation:

```java
@FluentParameters
```

It can be used on: constructor, instance method, static method.

##### 2.1.1 Constructor builder
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

##### 2.1.2 Factory method builder
To create factory method builder you can annotate any method with the annotation:

```java
public class PersonFactory {
    @FluentParameters
    public static Person create(String firstName, String lastName, ZonedDateTime birth, Gender gender) {
        return new Person(firstName, lastName, birth, gender);
    }    
}
```

You'll get following fluent interface, very similar to previous, but by default with naming derived
from the factory method like this:
```java
Person person = new PersonFactoryCreator()
    .firstName("John")
    .lastName("Doe")
    .birth(birthDate)
    .gender(MALE)
    .create();
```
In case of instance factory method, the fluent interface constructor accepts the factory instance:
```java
Person person = new PersonFactoryCreator(factory)
    .firstName("John")
    .lastName("Doe")
    .birth(birthDate)
    .gender(MALE)
    .create();
```
##### 2.1.3 All parameters and configuration for parameters builder
The annotation `@FluentParameters` has following parameters to customize the generated code:

| Parameter   | Description | Default behavior |
| ----------- | ----------- | ---------------- |
| packageName | Specify package name, where to generate the class | Package name of the class, containing the annotated method / constructor |
| methodName  | Name of the terminal method | Name of the annotated factory method, or `build` for constructor |
| className   | Name of the generated class | Name of the class, containing the annotated method / constructor with method name derived suffix (e.g. `build` -> `Builder`) |
| factoryMethod | Name of the fluent interface factory method | If not defined, no factory method is generated. |

Return value of the builder terminal method is:
- return type of the factory method in case of annotated factory method (it will be `void`, if the return type is `void`)
- class owning the annotated constructor in case of constructor

There are actually two different annotations:
- `@fluent.api.FluentParameters`: generates just a simple builder class.
- `@fluent.api.FluentParametersApi`: generates separate interface, and it's implementation.

##### 2.1.4 Example of designing "readable" fluent interface
The goal of fluent API (fluent interface) is not only chaining of the methods, but also being
a platform for designing readable "english like" language. Meaning, that if you ignore
java specific syntax, and expand Camel case to normal words, you get meaningful english
sentence.

An example of such readable sentence can be achieved by this example:
```java
    @FluentParameters(factoryMethod = "createObjectWith", methodName = "andSend")
    public static void call(int anInt, String aString, LocalDateTime aTime, List<Double> aList) {
        fixtureInterface.call(anInt, aString, aTime, aList);
    }
```
You get generated code, that can be used using static import like this:
```java
createObjectWith().anInt(5).aString("value").aTime(time).aList(list).andSend();
```

There are many options how to achieve readability, and it's up to everybody's taste.
Another example (very unusual for Java, but nicely readable) can be:
```java
    @FluentParameters(className = "Create", factoryMethod = "objectWith", methodName = "andSend")
    public static void call(int anInt, String aString, LocalDateTime aTime, List<Double> aList) {
        fixtureInterface.call(anInt, aString, aTime, aList);
    }
```

Resulting in following sentence:
```java
Create.objectWith().anInt(5).aString("value").aTime(time).aList(list).andSend();
```
#### 2.2 Fluent builder for bean with setters

For creating fluent interface, which contain methods for every setter method of a class, e.g. PoJo bean,
use annotation:

```java
@FluentBuilder
```

It can be used on: class, field, method parameter.

To generate fluent builder for PoJo, simply annotate any of the targets mentioned above, e.g. the class itself:
```java
@FluentBuilder
public class PoJo {
    private String firstName;
    private String lastName;
    private Gender gender;
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }
}
```
Then you get a builder:
```java
PoJo pojo = new PoJoBuilder()
    .firstName("John")
    .lastName("Doe")
    .gender(MALE)
    .build();
```
##### 2.2.1 All parameters and configuration for parameters builder
The annotation `@FluentBuilder` has following parameters to customize the generated code:

| Parameter   | Description | Default behavior |
| ----------- | ----------- | ---------------- |
| packageName | Specify package name, where to generate the class | Package name of the class, containing the annotated method / constructor |
| methodName  | Name of the terminal method | `build` |
| className   | Name of the generated class | Name of the annotated class with method name derived suffix (e.g. `build` -> `Builder`) |
| factoryMethod | Name of the fluent interface factory method | If not defined, no factory method is generated. |

The generated builder always contains constructor (and factory method) with one parameter - the initial instance
to be updated fluently.

If the annotated class has default constructor, then there is additional constructor (and factory method)
without arguments created.

Return value of the builder terminal method is:
- return type of the factory method in case of annotated factory method
- class owning the annotated constructor in case of constructor

There are actually two different annotations:
- `@fluent.api.FluentBuilder`: generates just a simple builder class.
- `@fluent.api.FluentBuilderApi`: generates separate interface, and it's implementation.

#### 2.3 Fluent sender for beans with setter
Very similar to fluent builder described above is `@FluentSender`. It also generates fluent interface for applying setter
methods on an underlying object (e.g. a PoJo).

Additionally it associates this builder with consumer method of the result, and generates terminal method so, that it
invokes the consumer, passing it the result. If there are other parameters to be passed to the consumer,
they have to be provided to the constructor, as well as the factory instance in case of instance
factory method.

This annotation can be only applied on method parameter:
```java
public interface Consumer {
    void send(@FluentSender PoJo pojo, int additionalParameter);    
}
```
It will be used this way:
```java
new ConsumerSender(consumer, 5)
    .firstName("John")
    .lastName("Doe")
    .gender(MALE)
    .send();
```

##### 2.3.1 All parameters and configuration for parameters builder
The annotation `@FluentSender` has following parameters to customize the generated code:

| Parameter   | Description | Default behavior |
| ----------- | ----------- | ---------------- |
| packageName | Specify package name, where to generate the class | Package name of the class, containing the method with annotated parameter |
| methodName  | Name of the terminal method | Name of the method with annotated parameter |
| className   | Name of the generated class | Name of the class containing method with annotated parameter, with method name derived suffix (e.g. `build` -> `Builder`) |
| factoryMethod | Name of the fluent interface factory method | If not defined, no factory method is generated. |

Return value of the builder terminal method is:
- return type of the factory method in case of annotated factory method (it will be `void`, if the return type is `void`)

There are actually two different annotations:
- `@fluent.api.FluentSender`: generates just a simple builder class.
- `@fluent.api.FluentSenderApi`: generates separate interface, and it's implementation.

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

### Version 2.6 (released on 29th November 2018)
- Upgrade of dependency on `@End` metod check.

### Version 2.5 (released on 29th November 2018)
- Introduced simple model of annotations present on any element
- Implemented scanner of additional named elements for use in templates using custom annotations annotated with `@Parameter`

### Version 2.4 (released on 28th November 2018)
- Introduced Fluent config generator
- Fixed issue in annotation scanner if no default value exists for annotation's property.

### Version 2.3 (released on 25th November 2018)
- ParameterScanner to support generics properly.

### Version 2.2 (released on 20th November 2018)
- Introduced generator of fluent validators
- Generators of various builders separated to specific module. Not any-more part of annotations.

### Version 2.1 (released on 15th November 2018)
- Fixed template loading. Not any more using class path loader (that would only use annotation processor classpath),
  but using compiled project's class path.
- Introduced more features to type model: isArray, isPublic, isEnum, isSubclassOf(type)

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

