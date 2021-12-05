package com.example.speldemo;

import org.springframework.stereotype.Component;

@Component("user")
public class User {

    private String name, age, country, language, timeZone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String sayHello (){
        return "Hello from user object!";
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", country='" + country + '\'' +
                ", language='" + language + '\'' +
                ", timeZone='" + timeZone + '\'' +
                '}';
    }
}
