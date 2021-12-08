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
SpEL expressions are always strings enclosed in double quotes. If we are accessing variables in the expression, we use a hash symbol (#) before the variable name, like `"#variableName"`. Furthermore, if we use SpEL expression in metadata, like annotations and xml, we must enclose the whole expression in curly braces, like `"#{expression}"`. Strings inside a SpEL expression must be enclosed with single quotes `'...'`.  The block represented by `#{}` is called an <u>evaluation block</u>.

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

**In general, SpEL expressions allow calling methods and performing all common mathematical and boolean operations of normal Java code, <u>all inside a string</u>. With the `@Value` annotation, which can only receive a String argument, we can then inject the result of these complex operations into a bean's primitive field or class type dependency, as well as into any method/constructor argument. Without SpEL, the `@Value` annotation would be limited to receiving simple String literals or properties.**

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

## Injecting into Lists and Maps
We can combine SpEL evaluation blocks, `#{}`, with properties evaluation blocks, `${}`, to inject Lists and Maps into fields. Suppose we have an application.properties files with properties as:
```text
// application.properties
student.booksRead=Harry Potter,The Hobbit,Game of Thrones
student.hobbies={indoor: 'reading, drawing', outdoor: 'fishing, hiking, bushcraft'}
```
We can inject the 'booksRead' list, and the 'student.hobbies' map into a list and a map field, respectively, as:
```java
@Value("#{'${student.booksRead}'.split(',')}")
private List<String> booksRead;

@Value("#{${student.hobbies}}")
private Map<String, List<String>> hobbies;
```

## Accessing and filtering Lists and Maps

With SpEL we can inject lists into fields of a bean, which are of type list too. Here is an example. Suppose we have a bean with possible shipping locations (cities) for a given user according to his country:
```java
@Component("city")
public class City {

    private String name;
    private double shipping; // shipping cost
    private Boolean isCapital; // whether this city is the capital of its country

    // getters and setters
    
}
```
```java
@Component("shipping")
public class Shipping {

    // shipping locations (cities) available per country
    private Map<String, List<City>> locationsByCountry;

    // shipping charges by locations
    private Map<String, List<City>> chargesByLocation;

    public Shipping() {
        List<City> usCities = new ArrayList<>();
        usCities.add(new City("Alabama",8.50,false));
        usCities.add(new City("New Jersey",10.50,false));
        usCities.add(new City("New York",10.50,false));
        usCities.add(new City("Washington",10.50,true));

        List<City> ukCities = new ArrayList<>();
        ukCities.add(new City("London",25.50,true));
        ukCities.add(new City("Cambridge",20.50,false));
        ukCities.add(new City("Leeds",15.50,false));

        List<City> dkCities = new ArrayList<>();
        dkCities.add(new City("Copenhagen",20.50,true));
        dkCities.add(new City("Hadsund",12.50,false));
        dkCities.add(new City("Arden",14.50,false));

        List<City> myCities = new ArrayList<>();
        myCities.add(new City("Kuala Lumpur",5.50,true));
        myCities.add(new City("Johor Bahru",3.50,false));

        this.locationsByCountry = new HashMap<>();
        this.locationsByCountry.put("US",usCities);
        this.locationsByCountry.put("UK",ukCities);
        this.locationsByCountry.put("DK",dkCities);
        this.locationsByCountry.put("MY",myCities);

        this.chargesByLocation = new HashMap<>();
        this.chargesByLocation.put("US", usCities);
        this.chargesByLocation.put("UK", ukCities);
        this.chargesByLocation.put("DK", dkCities);
        this.chargesByLocation.put("MY", myCities);
    }

    // getters and setters
     
}
```
Then, in an `order` bean we can inject this information into the `shippingLocations` field, filtering by the user's country as: 
```java
@Component("order")
public class Order {

    // ...

    //populate this list with available shipping locations for the user's country
    @Value("#{shipping.locationsByCountry[user.country]}")
    private List<City> shippingLocations;

    // populate this with the shipping locations (cities), out of the user country's capital city.
    // 'isCapital' is a field of the 'city' bean 
    @Value("#{order.shippingLocations.?[isCapital != true]}")
    private List<City> nonCapitalShippingLocations;
    
    // ...

}
```
What the `@Value` annotation in the `shippingLocations` field has here is a normal access-by-key to a Java map object; the key is the `user` bean's country field.

The `@Value` annotation on field `nonCapitalShippingLocations`, on the other hand, does a filtering. It filters the cities held by field `shippingLocations` to get only those out of the user, or order, country's capital. Notice here how we can <u>access injected fields of a bean to set other fields in the same bean</u>. 

The `shipping` bean as a field `locationsByCountry` holding a map of countries to possible shipping location in the country. Suppose we want to separate the elements in this map object corresponding to western countries. We could do this and inject the result into a another field of the `order` bean as:

```java
    // ...
    
    @Value("#{(shipping.locationsByCountry.?[key=='UK' or key=='US' or key=='DK'])}")
    private Map<String,List<City>> westernShippingLocations;
    
    // ...
```
Last, lets say we want to inject into a field of the `order` bean the number of "cheap" shipping locations for a given user/order. As cheap we will intend shipping cost below 10. We can set the `noOfCheapShippingLocations` as:
```java
    // ...
    // 'shipping' is a field of the 'city' bean
    // 'order.shippingLocation' is a list of cities
    @Value("#{order.shippingLocations.?[shipping<10].size()}")
    private Integer noOfCheapShippingLocations;
    // ...
```

## Expression Template
Expression templates let us mix literal text with one or more evaluation blocks, concatenating all of them in a single string. In plain Java code SpEL expression templating is done as:
```java
    parser.parseExpression("#{name} your order total is", new TemplateParserContext());
```
The `TemplateParserContext` is an object specifying the markers for the start and end of an evaluation block, among other things. The default for the start is '#{' and for the end '}'. These are the default prefix and suffix, and can be personalized/customized if desired.

A use example in our `order` bean can be an `orderSummary` dependency set as:
```java
    // ...
    @Value("#{user.name} your order total is #{order.formattedAmount} and the payable " +
            "amount with 5% discount is #{order.amount - order.discount}")
    private String orderSummary; 
    // ...
```
Notice how different evaluation blocks are evaluated and inserted inside a same string to be injected into the field.

## SpEL with xml
If our application defines and wires its beans through xml, we will need to inject the SpEL expression in this file as well. Below is how we would do all what has been shown above, but with xml:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">

    <bean id="user" class="com.example.speldemoxml.data.User">
        <property name="name" value="#{'John Doe'}"/>
        <property name="age" value="#{30}"/>
        <property name="country" value="#{systemProperties['user.country']}"/>
        <property name="timeZone" value="#{systemProperties['user.timezone']}"/>
    </bean>


    <bean id="city" class="com.example.speldemoxml.data.City">
    </bean>
    <bean id="shipping" class="com.example.speldemoxml.data.Shipping">
    </bean>

    <bean id="order" class="com.example.speldemoxml.data.Order">
        <property name="amount" value="#{100.55 + 500.75 + 400.66}"/>
    </bean>

    <bean id="order2" class="com.example.speldemoxml.data.Order">
        <property name="discount" value="#{order.amount >= 1000 ? order.amount * 5 / 100 : 0 }"/>
        <property name="daysToDeliver" value="#{user.country == 'US' and user.timeZone == 'America/New_York' ? 3 : 14}" />
        <property name="formattedAmount" value="#{T(java.text.NumberFormat).getCurrencyInstance(T(java.util.Locale).getDefault()).format(order.amount)}"/>
        <property name="shippingLocations" value="#{shipping.locationsByCountry[user.country]}"/>
        <property name="westernShippingLocations" value="#{(shipping.locationsByCountry.?[key == 'UK' or key == 'US' or key == 'DK'])}"/>
    </bean>

    <bean id="order3" class="com.example.speldemoxml.data.Order">
        <property name="nonCapitalShippingLocations" value="#{order2.shippingLocations.?[isCapital != true]}"/>
        <property name="noOfCheapShippingLocations" value="#{order2.shippingLocations.?[shipping &lt; 10].size()}"/>
        <property name="orderSummary" value="#{user.name} your order total is #{order2.formattedAmount} and the payable amount with 5% discount is #{order.amount - order2.discount}"/>
    </bean>
</beans>
```
And this is a main() class to run the beans defined in this xml  (I don't understand it though):
```java
@Configuration
@ImportResource({"classpath*:applicationContext.xml"})
public class AppExpressionParserXml {

    @Autowired
    User user;

    @Autowired
    Order order;

    @Autowired
    Order order2;

    @Autowired
    Order order3;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppExpressionParserXml.class);
        
        try {
            
            AppExpressionParserXml AppExpressionParserXml = (AppExpressionParserXml) context.getBean("appExpressionParserXml");
            
            System.out.println("Customer Name: " + AppExpressionParserXml.getUser().getName());
            System.out.println("Age: " + AppExpressionParserXml.getUser().getAge());
            System.out.println("Country: " + AppExpressionParserXml.getUser().getCountry());
            System.out.println("Order Amount: " + AppExpressionParserXml.getOrder().getAmount());
            System.out.println("Discount: " + AppExpressionParserXml.getOrder2().getDiscount());
            System.out.println("Days to deliver: " + AppExpressionParserXml.getOrder2().getDaysToDeliver());
            System.out.println("Formatted Amount: " + AppExpressionParserXml.getOrder2().getFormattedAmount());
            System.out.println("Shipping Locations: " );
            for(City city : AppExpressionParserXml.getOrder2().getShippingLocations()) {
                System.out.println(city.getName());
            }

            System.out.println("Western Shipping Locations: " );
            for(Iterator i = AppExpressionParserXml.getOrder2().getWesternShippingLocations().values().iterator(); i.hasNext();) {
                List<City> cities = (List<City>)i.next();
                for(City city : cities) {
                    System.out.println(city.getName());
                }
            }

            System.out.println("Non Capital Shipping Locations: " );
            for(City city : AppExpressionParserXml.getOrder3().getNonCapitalShippingLocations()) {
                System.out.println(city.getName());
            }

            System.out.println("Cheap Shipping Locations: " + AppExpressionParserXml.getOrder3().getNoOfCheapShippingLocations());

            System.out.println("Order Summary " + AppExpressionParserXml.getOrder3().getOrderSummary());

        } finally {
            context.close();
        }
    }

    public User getUser() {
        return user;
    }

    public Order getOrder() {
        return order;
    }

    public Order getOrder2() {
        return order2;
    }

    public Order getOrder3() {
        return order3;
    }
}
```

## Typical usages of SpEL
In general, and as we have seen above, typical use case of SpEL are:
1. Dependency inject an existing bean, or its fields, into another bean.
2. Dependency inject a bean based on environment conditions
3. Access and manipulate object graph at run time??

SpEL is a good choice for dependency injection based on conditional situations.

# The @Value annotation 
### by https://stackabuse.com/the-value-annotation-in-spring/

`@Value` is a Java annotation used at the field level, or method/constructor level, or parameters level. It indicates a default value for the affected argument, to be injected during beans creation.

`@Value` is processed by the BeanPostProcessor class. Therefore, methods annotated with it will be invoked when Spring is building the application context, either from Java configuration classes or xml files.

There are two types of _evaluation blocks_ we can use in the string we pass to `@Value`. One is `${}`, and is used to pass application or system properties, like `@Value("${car.color}")` or `@Value("${user.name})`. The other is `#{}`, and is used to pass SpEL expressions, as we show below. 

## Basic assignment
With `@Value` we can insert simple literals into fields of a bean. Spring will convert the values into integers or booleans as needed:
```java
@Value("John")
private String trainee;

@Value("100")
private int hoursOfCode;

@Value("true")
private boolean passedAssesmentTest;
```

## Spring Environment and default values
Another use case is injecting values from properties file `application.properties`. Notice how it is specified a default value to be used when the property is not defined:
```text
// application.properties
car.brand=Audi
```
```java
@Value("${car.brand")
private String brand;

@Value("${car.color: white}")
private String color;
```

## System Variables
We can also access system variables which are stored as properties by Spring when our application at starts:
```java
@Value("${user.name}")
// Or
@Value("${username}")
private String userName;

@Value("${number.of.processors}")
// Or
@Value("${number_of_processors}")
private int numberOfProcessors;

@Value("${java.home}")
private String java;
```

## Global method value
Using `@Value` at a method level will pass the argument of this annotation equally to all parameters of the method. For example, in the fallowing method both the car's color and brand will be set after the car's brand passed to `@Value`:
```java
@Value("${car.brand}")
public CarData setCarData(String color, String brand) {
        carData.setCarColor(color);
        carData.setCarBrand(brand);
}
```

## Parameter method value
If we want to pass values to specific parameters of a method, we use `@Value` at the parameter level as:
```java
@Value("${car.brand}")
public CarData setCarData(@Value("${car.color}") String color, String brand) {
    carData.setCarColor(color); // will get car.color
    carData.setCarBrand(brand); // will get car.brand
}
```

## @Value with SpEL
SpEL expressions used in conjunction with the `@Value` annotation allow injecting the result of evaluating almost any Java code statement into fields, or parameters of a method/constructor. For example:
```java
// load an specific system propety
@Value("#{systemProperties['user.name']}")
private String userName;

// load all properties in a field of the 'order' bean
Value("#{systemProperties}")
public Map<String, String> systemProperties;
```
```java
// print all system properties in the main()
order.systemProperties.forEach((key, value) -> System.out.println(key + ":" + value));
```