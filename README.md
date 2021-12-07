# Spring SpEL 
These are my notes from course <span style="color:aquamarine">Spring Framework: Spring Expression Language (SpEL)</span>, by Buddhini Samarakkody, **pluralsight**.

It will cover the topics:
- Writing and parsing basic SpEL expressions
- Using evaluation context
- Using SpEL with @Value annotation
- Collection manipulatin with SpEL
- Expression templating

Spring Expression Language SpEL is a powerful tool that allow to manipulate and query objects at run time. Also manipulating objects graph at run time ??. This makes possible dynamic beans wiring. It's available since Spring 3. SpEL is part of Spring Core.

With SpEL we evaluate string expression at run time. The result of the evaluation can be used to dynamically inject beans,  or values into beans.

## SpEL syntax
SpEL expressions are always strings enclosed in double quotes. If we are accessing variables in the expression, we use a hash symbol (#) before the variable name, like `"#variableName"`. Furthermore, if we use SpEL expression in metadata, like annotations and xml, we must enclose the whole expression in curly braces, like `"#{expression}"`. Strings inside a SpEL expression must be enclosed with single quotes `'...'`.  

SpEL can be used in plain old Java code, it only needs Spring core libraries. Here is a pom file with the needed dependencies, followed by some example use cases in a main() method.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>learn-spring-expression-language</artifactId>
    <version>0.0.1-SNAPSHOT</version>

    <name>Learn Spring Expression Language SpEL</name>
    <description>Demo project for Spring SpEL</description>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>${java.version}</source>
                    <target>${java.version}</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencies>
     
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>5.2.0.RELEASE</version>
        </dependency>

    </dependencies>
</project>
```

```java
public class AppExpressionParser {

    public static void main(String[] args){

        SpelExpressionParser parser = new SpelExpressionParser();

        Expression e1 = parser.parseExpression("'Hello World'");

        String message = (String) e1.getValue();
        System.out.println(message); // Hello World

        Expression e2 = parser.parseExpression("'Hello World'.length()");
        System.out.println(e2.getValue()); // 11

        Expression e3 = parser.parseExpression("'Hello World'.length()*10");
        System.out.println(e3.getValue()); // 110

        Expression e4 = parser.parseExpression("'Hello World'.length() > 10");
        System.out.println(e4.getValue()); // true

        
        // use of logical operator
        String str = "'Hello World'.length() > 10 and 'Hello World'.length()<20";
        Expression e5 = parser.parseExpression(str);
        System.out.println(e5.getValue()); // true

    }
}
```

## Evaluation context

### setting and accessing variables (objects) in an evaluation context
An Evaluation Context is a "context" in which we define key-value pairs for variables or properties. Keys must always be strings. Values can be any object.

When we parse a SpEL string having variables we obtain an `Expression` object. If we want the values of the variables to be substituted, we need to pass to the `getValue()` method seen above the context where those variables are defined.  For example:
 ```java
        StandardEvaluationContext sec1 = new StandardEvaluationContext();
        sec1.setVariable("greeting","Hello USA");
        String msg = (String) parser.parseExpression("#greeting.substring(6)").getValue(sec1);
        System.out.println(msg); // USA

        StandardEvaluationContext sec2 = new StandardEvaluationContext();
        sec2.setVariable("greeting","Hello UK");
        msg = (String) parser.parseExpression("#greeting.substring(6)").getValue(sec2);
        System.out.println(msg); // UK
```
In other words, an evaluation context allow resolving fields when evaluating expressions.

In the example above, `greetings` is a variable defined in the evaluation context, and whose value is a String object. In the string expression we then access method `substring()` of this object. 

In general, with SpEL we can access members fields and methods of any object added to the evaluation context. Suppose we have an object `User` with a method `sayHello()` as shown in below. We can call this method using SpEL if we first add the object to an evaluation context:
```java
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        
        context.setVariable("user",new User());

        Expression expression = parser.parseExpression("#user.sayHello()");

        System.out.println(expression.getValue(context));
```

This technique can be used to access values of 

### setting fields of a bean
If an evaluation context is initialized with an object, we can later use it to set values of fields of this object. The object can be a Spring bean, or a simple POJO. The pattern is as fallow:
```java
@Component("user")
public class User {

    private String name, age, country, language, timeZone;

    public String sayHello (){
        return "Hello from user object!";
    }
    
    // getters and setters
}
```
```java
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        User user = applicationContext.getBean("user", User.class);
   
        StandardEvaluationContext sec3 = new StandardEvaluationContext(user);
        parser.parseExpression("country").setValue(sec3,"Canada");
        System.out.println(user.getCountry()); // Canada
```

### accessing VM properties at run time
Whenever we run a Java program we can pass properties to the JVM running it. Often useful properties are the language and the locale. Suppose we specify the following VM properties in IntelliJ (Edit Configuration/VM options):
```text
-Duser.language=en -Duser.country=CU -Duser.timezone=Europe/Rome
```

Using SpEL it is possible to access these properties at run time as:
```java
        //get system properties
        final Properties properties = System.getProperties();

        // instantiate an evaluation context
        StandardEvaluationContext propsContext = new StandardEvaluationContext();

        // set a property in the evaluation context. The property will be of type java.util.Properties
        propsContext.setVariable("systemProperties", properties);

        // set the expression, but do not evaluate it yet.
        // Expression expCountry = ;

        // use the evaluation context to parse an expression
        String valCountry = (String) parser.parseExpression("#systemProperties['user.country']")
                                    .getValue(propsContext);

        System.out.println("VM option is: "+ valCountry); // VM option is: CU
```

I don't understand the notation `"#systemProperties['user.country']"` though. 

## the @Value annotation with SpEL expressions
The `@Value` annotation is used to specify default values or inject values into fields of Spring managed beans. It can be placed in fields, methods and constructor parameters to specify default values.
It must always be passed a String, which in turn may signify a simple String literal, a system or application property or a SpEL expression.

**See https://stackabuse.com/the-value-annotation-in-spring/**

Below are some examples for SpEL expressions:
```java
@Value("#{'John Doe'}")
private String name;
```
```java
@Value("#{30}")
private int age;
```


We can use it to specify a common value to all parameters of a method, for example a setter:
```java
@Value("#{systemProperties['user.timezone']}")
public void setTimeZone(String timeZone){
    this.timeZone = timeZone;
}
```
If we want to use `@Value` to pass specific values to each method parameter, we can use it at the parameter level instead.

<i>**`@Value` is processed by the BeanPostProcessor class. Therefore, it will be invoked when Spring is building the Spring context and instantiating beans.**</i>

SpEL expressions allow calling methods and performing all common mathematical and boolean operations, <u> **all inside a string**</u>. With the `@Value` annotation, which can only receive a String argument, we can then inject the result of these complex operations into a bean's primitive field or class type dependency, as well as into any method/constructor argument. Without SpEL, the `@Value` annotation would be limited to receiving simple String literals or properties.

Moreover, insider a SpEL expression we can access system properties through the predefined variable `systemProperties`. In the example below, we use setter injection to set the country, language and timeZone fields of the `user` bean. We pass to each setter argument the result of interpreting the SpEL expression inside the `@Value` annotation annotating it. Notice also how we access fields of the `user` bean to inject values in fields of the `order` bean, all through SpEL expressions and the `@Value` annotation, and how static methods are called inside a SpEL expression: `T(java.text.NumberFormat)`.
```java

@Component("user")
public class User {

    @Value("#{'John Doe'}")
    private String name;
    @Value("#{30}")
    private int age;
    private String country;
    private String language;
    private String timeZone;

    public User(){}

    public User(@Value("#{systemProperties['user.country']}")String country,
                @Value("#{systemProperties['user.language']}")String language) {
        this.country = country;
        this.language = language;
    }

    @Value("#{systemProperties['user.country']}")
    public void setCountry(String country) {
        this.country = country;
    }

    @Value("#{systemProperties['user.language']}")
    public void setLanguage(String language) {
        this.language = language;
    }

    // systemProperties is a predefined variable
    @Value("#{systemProperties['user.timezone']}")
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    // other getters and setters
}
```
If in the `user` bean we want instead to inject the country and language primitive dependencies through the constructor, we would need to annotate it with `@Autowired`.
An `order` bean that uses the `user` bean in SpEL expressions is:
```java
@Component("order")
public class Order {

    @Value("#{100.55 + 500.75 + 400.66}")
    private double amount;

    @Value("#{order.amount >= 1000 ? order.amount *5/100 : 0}")
    private double discount;

    @Value("#{user.country == 'US' and user.timeZone == 'America/New York' ? 3 : 14}")
    private int daysToDeliver;

    @Value("#{user.country}")
    private String origin;

    @Value("#{ T(java.text.NumberFormat).getCurrencyInstance(T(java.util.Locale).getDefault()).format(order.amount)}")
    private String formattedAmount;

   // getters and setters   
 
}
```
From the `main()` we can test these two beans as (VM options: `-Duser.language=en -Duser.country=US -Duser.timezone=Europe/New_York`) :
```java
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class); 

        User user = applicationContext.getBean("user", User.class);
        System.out.println(user.toString());
        // User{name='John Doe', age=30, country='US', language='en', timeZone='Europe/New_York'}

        Order order = applicationContext.getBean("order", Order.class);
        System.out.println(order);
        // Order{amount=1001.96, discount=50.098, daysToDeliver=14, origin='US', formattedAmount='$1,001.96'}
```

## Accessing and manipulating Lists and Maps

