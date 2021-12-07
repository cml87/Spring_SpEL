package com.example.speldemo;

import com.example.speldemo.data.Order;
import com.example.speldemo.data.Shipping;
import com.example.speldemo.data.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.security.SecureRandom;
import java.util.Properties;

public class AppExpressionParser {

    public static void main(String[] args){


        SpelExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("user0",new User());
        Expression expression = parser.parseExpression("#user0.sayHello()");
        System.out.println(expression.getValue(context));

        System.out.println("------------------------");

        //User user = new User();
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
        User user = applicationContext.getBean("user", User.class);
        System.out.println(user.toString());

        Order order = applicationContext.getBean("order", Order.class);
        System.out.println(order);

        System.out.println("------------------------");

        System.out.printf("Shipping locations for the user of country [%s]: %s", user.getCountry(), order.getShippingLocations());
        System.out.printf("Shipping locations for the user of country [%s] out of its country's capital: %s", user.getCountry(),
                order.getNonCapitalShippingLocations());

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
