package com.example.weather_facts_application;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weather_facts_application.helpers.BackgroundImageHelper;
import com.example.weather_facts_application.models.WeatherData;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private String imgLink;
    private LinearLayout mainLayout;
    private static final int SEARCH_ACTIVITY_REQUEST = 4;
    private String userQuery;
    private SingletonApiClient apiClient;
    private TextView tempValueTV;
    private TextView feelsLikeValueTV;
    private TextView welcomeTV;
    private TextView windValueTV;
    private TextView humidityValueTV;
    private TextView sunriseValueTV;
    private final String MY_PREFS_NAME = "MyPrefsFile";
    private TextView sunsetValueTV;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST) {
            assert data != null;
            Bitmap bitmap = data.getParcelableExtra("background");
            WeatherData weatherData = (WeatherData) data.getSerializableExtra("weatherData");
            userQuery = data.getStringExtra("query");
            imgLink = SingletonApiClient.imageProviderBaseLink.concat(userQuery);
            Drawable background = new BackgroundImageHelper(getApplicationContext()).blurBitmapToImage(bitmap).getDrawable();
            mainLayout.setBackground(background);
            updateWeatherUI(weatherData,userQuery);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = SingletonApiClient.getInstance();
        setContentView(R.layout.activity_main);
        Button searchBtn = (Button) findViewById(R.id.searchBT);
        mainLayout = findViewById(R.id.mainLayout);
        tempValueTV = findViewById(R.id.tempValueTV);
        feelsLikeValueTV = findViewById(R.id.feelsLikeValueTV);
        welcomeTV = findViewById(R.id.welcomeTV);
        windValueTV = findViewById(R.id.windValueTV);
        humidityValueTV = findViewById(R.id.humidityValueTV);
        sunriseValueTV = findViewById(R.id.sunriseValueTV);
        sunsetValueTV = findViewById(R.id.sunsetValueTV);

        searchBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
            startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST);
        });

        checkPreferences();
    }

    private void checkPreferences(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String savedLink = prefs.getString("img","");
        String savedQuery = prefs.getString("query","");
        if(savedLink != null && !savedLink.isEmpty()) {
            Bitmap backgroundImage = apiClient.callImageApiAsync(savedLink);
            if(backgroundImage != null){
                mainLayout.setBackground(new BackgroundImageHelper(getApplicationContext()).blurBitmapToImage(backgroundImage).getDrawable());
            }
        }
        if(savedQuery != null && !savedQuery.isEmpty()){
            WeatherData weatherData = apiClient.callWeatherAPIAsync(savedQuery);
            if(weatherData != null){
                updateWeatherUI(weatherData,savedQuery);
            }
        }
    }

    private void updateWeatherUI(WeatherData weatherData,String userQuery){
        String emojiTemp = weatherData.getTemp() < 0 ? "❄️️ " : "\uD83C\uDF21️";
        String emojiFeelsLike = getEmojiFromTemperature(weatherData.getFeels_like());

        welcomeTV.setText("Greetings from ".concat(WordUtils.capitalize(userQuery.replace("+", " "),' ')).concat("\uD83D\uDC4B."));
        tempValueTV.setText(emojiTemp.concat(String.valueOf(weatherData.getTemp())).concat(" ℃"));
        sunsetValueTV.setText("\uD83C\uDF05  ".concat(epochToDateTime(weatherData.getSunset())));
        sunriseValueTV.setText("\uD83C\uDF05  ".concat(epochToDateTime(weatherData.getSunrise())));
        windValueTV.setText("\uD83D\uDCA6  ".concat(String.valueOf(weatherData.getWindSpeed())).concat(" km/h"));
        humidityValueTV.setText("\uD83D\uDCA6  ".concat(String.valueOf(weatherData.getHumidity())).concat(" %"));
        feelsLikeValueTV.setText(emojiFeelsLike.concat(String.valueOf(weatherData.getFeels_like())).concat(" ℃"));

    }


    private String getEmojiFromTemperature(double temperature){
        if(temperature < 13)
            return "\uD83E\uDD76 ";

        else if(temperature >= 13 && temperature <= 22)
            return "☺️ ";

        else
            return "\uD83E\uDD75  ";

    }

    private String epochToDateTime(long epoch){
        Date date = new Date(epoch * 1000);
        DateFormat format = new SimpleDateFormat("hh:mm aaa");
        return format.format(date);
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("welcomeTV", (String) welcomeTV.getText());
        editor.putString("tempValueTV", (String) tempValueTV.getText());
        editor.putString("sunsetValueTV", (String) sunsetValueTV.getText());
        editor.putString("sunriseValueTV", (String) sunriseValueTV.getText());
        editor.putString("windValueTV", (String) windValueTV.getText());
        editor.putString("humidityValueTV", (String) humidityValueTV.getText());
        editor.putString("humidityValueTV", (String) humidityValueTV.getText());
        editor.putString("feelsLikeValueTV", (String) feelsLikeValueTV.getText());

        editor.putString("query", userQuery);
        editor.putString("img", imgLink);
        editor.apply();
    }

}

