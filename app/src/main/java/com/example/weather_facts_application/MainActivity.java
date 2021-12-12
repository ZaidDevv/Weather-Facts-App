package com.example.weather_facts_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.os.Bundle;

import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import com.example.weather_facts_application.helpers.BackgroundImageHelper;
import com.example.weather_facts_application.models.WeatherData;

import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.blurry.Blurry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String imgLink;
    private LinearLayout mainLayout;
    private static final int SEARCH_ACTIVITY_REQUEST = 4;
    private String userQuery;
    private TextView tempValueTV;
    private TextView feelsLikeValueTV;
    private TextView welcomeTV;
    private TextView windValueTV;
    private TextView humidityValueTV;
    private TextView sunriseValueTV;
    private TextView sunsetValueTV;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST) {
            assert data != null;
            imgLink = data.getStringExtra("imgUrl");
            userQuery = data.getStringExtra("query");
            setBackgroundImage(imgLink, mainLayout);
            callWeatherAPI(userQuery);
            saveUserPreference();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);
        Button searchBtn = (Button) findViewById(R.id.searchBT);
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
        if(savedInstanceState != null){
            return;
        }
        checkPreferences();
    }

    private void animateBackground(ImageView imageView){
        Animation fadeOut = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_out);
        imageView.startAnimation(fadeOut);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade_in);
                imageView.startAnimation(fadeIn);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void callWeatherAPI(String query){
        String weatherBaseLink = "https://api.openweathermap.org/data/2.5/weather?appid=";
        weatherBaseLink = weatherBaseLink.concat(getString(R.string.open_weather_key)).concat("&q=" + query).concat("&units=metric");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(weatherBaseLink)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                MainActivity.this.runOnUiThread(() -> {
                    try {
                        if (response.code() == 200) {
                            String myResponse = Objects.requireNonNull(response.body()).string();
                            JSONObject jsonObject = new JSONObject(myResponse);
                            WeatherData weatherData = new WeatherData(
                                    jsonObject.getJSONObject("main").getDouble("temp"),
                                    jsonObject.getJSONObject("main").getDouble("feels_like"),
                                    jsonObject.getJSONObject("main").getDouble("humidity"),
                                    jsonObject.getJSONObject("wind").getDouble("speed"),
                                    jsonObject.getJSONObject("wind").getDouble("deg"),
                                    jsonObject.getJSONObject("sys").getLong("sunrise"),
                                    jsonObject.getJSONObject("sys").getLong("sunset")
                            );
                            setResults(weatherData);
                        }
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private String getEmojiFromTemperature(double temperature){
        if(temperature < 13)
            return "\uD83E\uDD76 ";

        else if(temperature >= 13 && temperature <= 22)
            return "☺️ ";

        else
            return "\uD83E\uDD75  ";

    }

    private void setResults(WeatherData weatherData){
        String emojiTemp = weatherData.getTemp() < 0 ? "❄️️ " : "\uD83C\uDF21️";
        String emojiFeelsLike = getEmojiFromTemperature(weatherData.getFeels_like());
        tempValueTV.setText(emojiTemp.concat(String.valueOf(weatherData.getTemp())).concat(" ℃"));
        feelsLikeValueTV.setText(emojiFeelsLike.concat(String.valueOf(weatherData.getFeels_like())).concat(" ℃"));
        welcomeTV.setText(
                getString(R.string.welcome_message,
                        WordUtils.capitalize(userQuery.replace("+", " "),' ')));

        humidityValueTV.setText("\uD83D\uDCA6  ".concat(String.valueOf(weatherData.getHumidity())).concat(" %"));
        windValueTV.setText("\uD83D\uDCA8 ".concat(String.valueOf(weatherData.getWindSpeed())).concat(" km/h"));
        sunriseValueTV.setText("\uD83C\uDF05  ".concat(epochToDateTime(weatherData.getSunrise())));
        sunsetValueTV.setText("\uD83C\uDF07  ".concat(epochToDateTime(weatherData.getSunset())));
    }

    private String epochToDateTime(long epoch){
        Date date = new Date(epoch * 1000);
        DateFormat format = new SimpleDateFormat("hh:mm aaa");
        return format.format(date);
    }

    private void checkPreferences(){
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        imgLink = prefs.getString("img","");
        userQuery = prefs.getString("query","");
        if(imgLink != null && !imgLink.isEmpty())
            setBackgroundImage(imgLink,mainLayout);
        if(userQuery != null && !userQuery.isEmpty()){
            callWeatherAPI(userQuery);
        }

    }
    private void setBackgroundImage(String link, ViewGroup layout) {
        ImageView v = new ImageView(getBaseContext());
        Thread thread = new Thread(() -> {
            try {
                BackgroundImageHelper helper = new BackgroundImageHelper();
                Bitmap bmp = helper.execute(new URL(link)).get();
                Blurry.with(getBaseContext()).sampling(5).radius(7).color(Color.argb(35,167,167,167)).animate(1000).from(bmp).into(v);
                runOnUiThread(() -> {
                    animateBackground(v);
                    layout.setBackground(v.getDrawable());
                });

            } catch (InterruptedException | MalformedURLException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        thread.start();

    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveUserPreference();
    }

    private void saveUserPreference(){
        final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("query", userQuery);
        editor.putString("img", imgLink);
        editor.apply();
    }

}

