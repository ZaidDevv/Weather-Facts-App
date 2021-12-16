package com.example.weather_facts_application;

import android.graphics.Bitmap;


import com.example.weather_facts_application.helpers.BackgroundImageHelper;
import com.example.weather_facts_application.models.WeatherData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SingletonApiClient {
    public static final String weatherBaseLink = "https://api.openweathermap.org/data/2.5/weather?appid=";
    public static final String imageProviderBaseLink = "https://api.unsplash.com/search/photos?per_page=1&orientation=portrait&page=1&query=";
    private Bitmap backgroundBmp;
    private WeatherData weatherData;
    private static SingletonApiClient singletonInstance;
    private final OkHttpClient client;


    private SingletonApiClient() {
        super();
        client = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .build();
    }

    public static synchronized SingletonApiClient getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new SingletonApiClient();
        }
        return singletonInstance;
    }

    public WeatherData callWeatherAPI(String query) {
        String weatherAPIRequestURL = weatherBaseLink.concat("8767a7b077344239edf505924a3f05d2").concat("&q=" + query).concat("&units=metric");
        Request request = new Request.Builder()
                .url(weatherAPIRequestURL)
                .build();

        try(Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String myResponse = Objects.requireNonNull(response.body()).string();
                try {
                    JSONObject jsonObject = new JSONObject(myResponse);
                    weatherData = new WeatherData(
                            jsonObject.getJSONObject("main").getDouble("temp"),
                            jsonObject.getJSONObject("main").getDouble("feels_like"),
                            jsonObject.getJSONObject("main").getDouble("humidity"),
                            jsonObject.getJSONObject("wind").getDouble("speed"),
                            jsonObject.getJSONObject("wind").getDouble("deg"),
                            jsonObject.getJSONObject("sys").getLong("sunrise"),
                            jsonObject.getJSONObject("sys").getLong("sunset")
                    );
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherData;
    }




    public WeatherData callWeatherAPIAsync(String query) {
        String weatherAPIRequestURL = weatherBaseLink.concat("8767a7b077344239edf505924a3f05d2").concat("&q=" + query).concat("&units=metric");
        Request request = new Request.Builder()
                .url(weatherAPIRequestURL)
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                countDownLatch.countDown();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String myResponse = Objects.requireNonNull(response.body()).string();
                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        weatherData = new WeatherData(
                                jsonObject.getJSONObject("main").getDouble("temp"),
                                jsonObject.getJSONObject("main").getDouble("feels_like"),
                                jsonObject.getJSONObject("main").getDouble("humidity"),
                                jsonObject.getJSONObject("wind").getDouble("speed"),
                                jsonObject.getJSONObject("wind").getDouble("deg"),
                                jsonObject.getJSONObject("sys").getLong("sunrise"),
                                jsonObject.getJSONObject("sys").getLong("sunset")
                        );
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return weatherData;
    }



    public Bitmap callImageApiAsync(String query){
        Request request = new Request.Builder()
                .addHeader("Authorization", "Client-ID bIaktwsmdTbFow0_rg5yQGBx6erCp3DS5WAe9cPkdGs")
                .url(imageProviderBaseLink + query)
                .get()
                .build();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String myResponse = Objects.requireNonNull(response.body()).string();
                if (response.isSuccessful()) {
                    try {
                        if (response.isSuccessful()) {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            String imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("thumb");
                            System.out.println(myResponse);
                            backgroundBmp = new BackgroundImageHelper().execute(new URL(imgUrl)).get();
                        }
                        countDownLatch.countDown();
                    } catch (JSONException | ExecutionException | InterruptedException | MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return backgroundBmp;
    }


    public Bitmap callImageApi(String query){
        Request request = new Request.Builder()
                .addHeader("Authorization", "Client-ID bIaktwsmdTbFow0_rg5yQGBx6erCp3DS5WAe9cPkdGs")
                .url(imageProviderBaseLink + query)
                .get()
                .build();

        try(Response response = client.newCall(request).execute()) {
            String myResponse = Objects.requireNonNull(response.body()).string();
            if (response.isSuccessful()) {
                try {
                    if (response.isSuccessful()) {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        String imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("thumb");
                        backgroundBmp = new BackgroundImageHelper().execute(new URL(imgUrl)).get();
                    }
                } catch (JSONException | ExecutionException | InterruptedException | MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return backgroundBmp;
    }
}