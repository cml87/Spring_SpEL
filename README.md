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
SpEL expressions are always strings enclosed in double quotes. If we are accessing defined variables in the expression, we use a hash symbol (#) before the variable, like `"#variableName"`. Furthermore, if we use SpEL expression in metadata, like annotations and xml, we must enclose the whole expression in curly braces, like `"#{expression}"`. Strings inside a SpEL expression must be enclosed with single quotes  

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

        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();

        Expression e1 = spelExpressionParser.parseExpression("'Hello World'");

        String message = (String) e1.getValue();
        System.out.println(message); // Hello World

        Expression e2 = spelExpressionParser.parseExpression("'Hello World'.length()");
        System.out.println(e2.getValue()); // 11

        Expression e3 = spelExpressionParser.parseExpression("'Hello World'.length()*10");
        System.out.println(e3.getValue()); // 110

        Expression e4 = spelExpressionParser.parseExpression("'Hello World'.length() > 10");
        System.out.println(e4.getValue()); // true

        
        // use of logical operator
        String str = "'Hello World'.length() > 10 and 'Hello World'.length()<20";
        Expression e5 = spelExpressionParser.parseExpression(str);
        System.out.println(e5.getValue()); // true

    }
}
```

## Evaluation context

An Evaluation Context is a "context" in which we define key-value pairs for properties. When we want to parse an expression having variables, we need to specify the context in which we want to look for and substitute the values of those variables. For example: 