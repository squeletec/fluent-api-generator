# Fluent API generator
Annotation processing based code generator used to generate fluent API for various situations or define 
custom templates to generate totally custom derived code.

## User guide

### 1. Maven dependencies

TBD. Not yet deployed to maven central.

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
    @GenerateParametersBuilder
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