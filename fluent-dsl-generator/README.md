# Fluent API generator

Annotation processing based code generator used to generate fluent API binding to  simple automation methods.

## Example

Automation interface with annotation metadata describing, how to invoke it using fluent API:

```java
@Dsl(className = "User", factoryMethod = "newUser")
public interface Automation {

    // These annotations can be externalzed
    public @interface User {}
    @Keyword public @interface enters {}
    @Keyword public @interface and {}
    @Keyword public @interface at {}
    @Keyword public @interface must {}
    @Keyword public @interface see {}
    @Keyword public @interface with {}
    @Keyword public @interface order {}

    public void userLogin(@User @enters String username, @and String password, @at String url);

    public void verifyLogonMessage(@User @must @see String message);

    public void exactOrderVerification(@User @must @see @order @with String orderId, Check<Object> check);

}
```

Generated fluent interface sentence examples

```java
public class GeneratedUserDslTest {

    private final User John = newUser(new Automation());

    private final String validUserName = "John Doe";
    private final String validPassword = "$3cr3T";
    private final String invalidPassword = "password";
    private final String loginPage = "http://my.server.com/login";

    @Test
    public void successfulLoginScreenTest() {
        When (John). entersUsername (validUserName). andPassword (validPassword). atUrl (loginPage);
        then (John). mustSeeMessage ("Welcome My Name!");
    }

    @Test
    public void unsuccessfulLoginScreenTest() {
        When (John). entersUsername (validUserName). andPassword (invalidPassword). atUrl (loginPage);
        then (John). mustSeeMessage ("Invalid username or password!");
    }

    @Test
    public void testDirectDsl() {
        John.entersUsername(validUserName).andPassword(validPassword).atUrl(loginPage);
        John.mustSeeMessage("Welcome " + validUserName + "!");
    }

}
```