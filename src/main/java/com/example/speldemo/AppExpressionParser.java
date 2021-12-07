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

        System.out.println("\n\nWestern shipping locations are: "+ order.getWesternShippingLocations());

        System.out.println("\nNof cheap shipping locations (cost < 10) for the order: "+order.getNoOfCheapShippingLocations());

        System.out.println("\nOrder summary is: "+order.getOrderSummary());

    }

}
