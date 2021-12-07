package com.example.speldemo.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

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


    //populate this list with available shipping locations for the user's country
    @Value("#{shipping.locationsByCountry[user.country]}")
    private List<City> shippingLocations;


    public List<City> getShippingLocations() {
        return shippingLocations;
    }

    public void setShippingLocations(List<City> shippingLocations) {
        this.shippingLocations = shippingLocations;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public void setFormattedAmount(String formattedAmount) {
        this.formattedAmount = formattedAmount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getDaysToDeliver() {
        return daysToDeliver;
    }

    public void setDaysToDeliver(int daysToDeliver) {
        this.daysToDeliver = daysToDeliver;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public String toString() {
        return "Order{" +
                "amount=" + amount +
                ", discount=" + discount +
                ", daysToDeliver=" + daysToDeliver +
                ", origin='" + origin + '\'' +
                ", formattedAmount='" + formattedAmount + '\'' +
                '}';
    }
}
