# Fluent API generator
Annotation processing based code generator used to generate fluent API for various situations or define 
custom templates to generate totally custom derived code.

## User guide

### 1. Predefined use cases

#### 1.1 Builder for call of factory method / constructor with many arguments
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
