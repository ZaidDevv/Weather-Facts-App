package com.example.weather_facts_application.models;

public class WeatherData {
    private double temp;
    private double feels_like;
    private double humidity;
    public WeatherData(double temp, double feels_like, double humidity) {
        this.temp = temp;
        this.feels_like = feels_like;
        this.humidity = humidity;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public double getHumidity() {
        return humidity;
    }
}
