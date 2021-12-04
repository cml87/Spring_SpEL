package com.example.speldemo;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

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

    }

}