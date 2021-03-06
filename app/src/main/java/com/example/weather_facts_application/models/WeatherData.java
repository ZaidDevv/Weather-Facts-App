package com.example.weather_facts_application.models;

import android.content.res.Resources;

import com.example.weather_facts_application.R;

import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherData implements Serializable {
    private final double temp;
    private final double feels_like;
    private final double humidity;
    private final double windSpeed;
    private final double windDegrees;
    private final long sunrise;
    private final long sunset;


    public WeatherData(double temp, double feels_like, double humidity, double windSpeed, double windDegrees, long sunrise, long sunset) {
        this.temp = temp;
        this.feels_like = feels_like;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.windDegrees = windDegrees;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDegrees() {
        return windDegrees;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
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
