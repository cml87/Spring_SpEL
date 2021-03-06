package com.example.speldemo.data;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component("city")
public class City {

    private String name;
    private double shipping; // shipping cost
    private Boolean isCapital;

    public City(){}

    public City(String name, double shipping, Boolean isCapital) {
        this.name = name;
        this.shipping = shipping;
        this.isCapital = isCapital;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getShipping() {
        return shipping;
    }

    public void setShipping(double shipping) {
        this.shipping = shipping;
    }

    public Boolean getIsCapital() {
        return isCapital;
    }

    public void setIsCapital(Boolean capital) {
        isCapital = capital;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", shipping=" + shipping +
                ", isCapital=" + isCapital +
                '}';
    }
}
