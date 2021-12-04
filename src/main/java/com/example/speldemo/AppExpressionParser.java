package com.example.speldemo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.security.SecureRandom;

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

        StandardEvaluationContext sec3 = new StandardEvaluationContext(user);
        spelExpressionParser.parseExpression("country").setValue(sec3,"Canada");
        System.out.println(user.getCountry()); // Canada

    }

}
