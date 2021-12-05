package com.example.speldemo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.security.SecureRandom;
import java.util.Properties;

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

        String str = "'Hello World'.length() > 10 and 'Hello World'.length()<20";
        Expression e5 = spelExpressionParser.parseExpression(str);
        System.out.println(e5.getValue()); // true

        System.out.println("____________");
        StandardEvaluationContext sec1 = new StandardEvaluationContext();
        sec1.setVariable("greeting","Hello USA");
        String msg = (String) spelExpressionParser.parseExpression("#greeting.substring(6)").getValue(sec1);
        System.out.println(msg); // USA

        StandardEvaluationContext sec2 = new StandardEvaluationContext();
        sec2.setVariable("greeting","Hello UK");
        msg = (String) spelExpressionParser.parseExpression("#greeting.substring(6)").getValue(sec2);
        System.out.println(msg); // UK
        System.out.println("____________");

        //User user = new User();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        User user = applicationContext.getBean("user", User.class);

        StandardEvaluationContext userContext = new StandardEvaluationContext(user);
        spelExpressionParser.parseExpression("country").setValue(userContext,"Canada");
        System.out.println(user.getCountry()); // Canada

        System.out.println("____________________");
        final Properties properties = System.getProperties();

        // instantiate an evaluation context
        StandardEvaluationContext propsContext = new StandardEvaluationContext();

        // set a property in the evaluation context. The property will be of type java.util.Properties
        propsContext.setVariable("systemProperties", properties);

        // set the expression, but do not evaluate it yet.
        // Expression expCountry = ;

        // use the evaluation context to parse an expression
        String valCountry = (String) spelExpressionParser.parseExpression("#systemProperties['user.country']")
                                    .getValue(propsContext);

        System.out.println("VM option is: "+ valCountry);
        spelExpressionParser.parseExpression("country").setValue(userContext, valCountry);
        System.out.println(user.getCountry()); // CU

        System.out.println("------------------");
        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user",new User());
        Expression expression = parser.parseExpression("#user.sayHello()");
        System.out.println(expression.getValue(context));


        //-Duser.language=en -Duser.country=CU -Duser.timezone=Europe/Rome
        /*
        StandardEvaluationContext propsContext = new StandardEvaluationContext();
        propsContext.setVariable("systemProperties",System.getProperties());
        Expression expCountry = parser.parseExpression("#systemProperties['user.country']");
        parser.parseExpression("country").setValue(userContext,expCountry.getValue(propsContext));
        System.out.println(user.getCountry());

        Expression expLanguage = parser.parseExpression("#systemProperties['user.language']");
        parser.parseExpression("language").setValue(userContext,expLanguage.getValue(propsContext));
        System.out.println(user.getLanguage());

        Expression expTimeZone = parser.parseExpression("#systemProperties['user.timezone']");
        parser.parseExpression("timeZone").setValue(userContext,expTimeZone.getValue(propsContext));
        System.out.println(user.getTimeZone());
        * */

    }

}
