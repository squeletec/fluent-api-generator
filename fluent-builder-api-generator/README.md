# Fluent builder API generator
![Released version](https://img.shields.io/maven-central/v/foundation.fluent.api/fluent-builder-api-generator.svg)

Annotations and templates providing various builder-like 

## User guide

### 1. Maven dependencies

To use the annotations which are driving the code generation, include following
maven dependency:

```xml
<dependency>
    <groupId>foundation.fluent.api</groupId>
    <artifactId>fluent-builder-api-generator</artifactId>
    <version>2.12</version>
</dependency>
```

Processing itself is done by `fluent-api-generator`. For instructions how to configure it for your maven project, see
[fluent-api-generator](../README.md)

### 2. Fluent builder

Fluent builder (or simply builder) is an object, that allows chaining of it's methods. There are typically two types of
methods - fluent methods, and terminal methods (typically one).

```java
Person johnDoe = new PersonBuilder().firstName("John").lastName("Doe").age(65).build();
```

It's useful pattern. In order to turn classic POJO into fluent builder, this library provides annotations to generate
them.
Following example shows how to generate fluent builder on top of POJO:

```java
@FluentBuilder
public class Person {
    private String firstName;
    private String lastName;
    private int age;

    public void setFirstName(String value) {
        firstName = value;
    }

    public void setLastName(String value) {
        lastName = value;
    }

    public void setAge(int value) {
        age = value;
    }

    // Getters
}
```

The annotation `@FluentBuilder` causes following code to be generated:
```java
@Generated("Generated code using /fluent/api/templates/builder/implementation.jtwig")
public class PersonBuilder {
    private final Person object;
    
    public PersonBuilder(Person object) {
        this.object = object;
    }

    public PersonBuilder() {
        this(new Person());
    }

    public PersonBuilder firstName(String value) {
        object.setFirstName(value);
        return this;
    }

    public PersonBuilder lastName(String value) {
        object.setLastName(value);
        return this;
    }

    public PersonBuilder age(int value) {
        object.setAge(value);
        return this;
    }

    public Person build() {
        return object;
    }
}
```

#### 2.1 Builder combinations

Module contains two annotations for builder generator:
`@FluentBuilder` - generates java class

`@FluentBuilderApi` - generates java interface

Combination of both - generates java interface and it's implementation

#### 2.2 Parameters for customization of the builder

| Parameter   | Description | Default behavior |
| ----------- | ----------- | ---------------- |
| packageName | Specify package name, where to generate the class | Package name of the annotated class, or class containing annotated field or method's parameter |
| methodName  | Name of the terminal method | Constant `build` |
| className   | Name of the generated class | Name of the product class, with method name derived suffix (e.g. `build` -> `Builder`) |
| factoryMethod | Name of the fluent interface factory method | If not defined, no factory method is generated. |
| setterPattern | Regex pattern to identify setter method | Default is `(set)(.*)` - Keep in mind, that it has to contain group, that will be extracted and used as resulting method name (e.g. `setName` -> `name`) |
| setterNameGroup | Regex group identifier | `"$2"` |
| methodAnnotation | Annotations to add on the ending method | None |


### 3. Fluent sender

Compared to builder, the sender is not simply returning the product, but passing it internally
to a consumer.
The consumer is the method / constructor, that has one parameter annotated with annotation
`@FluentSender`.

### 4. Fluent parameters

### 5. Named parameters