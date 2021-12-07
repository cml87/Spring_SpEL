package com.example.speldemo;

import com.example.speldemo.data.City;
import com.example.speldemo.data.Order;
import com.example.speldemo.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.util.Iterator;
import java.util.List;


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