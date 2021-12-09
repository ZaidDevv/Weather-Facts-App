package com.example.weather_facts_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.weather_facts_application.helpers.BackgroundImageHelper;
import com.example.weather_facts_application.models.WeatherData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.weather_facts_application.SearchActivity.imgSaved;
import static com.example.weather_facts_application.SearchActivity.querySaved;
import static com.google.android.material.internal.ContextUtils.getActivity;

public class MainActivity extends AppCompatActivity {

    private String imgLink;
    private LinearLayout mainLayout;
    private static final int SEARCH_ACTIVITY_REQUEST = 4;
    private Button searchBtn;
    private String userQuery;
    private TextView cityName,tempValueTV,feelsLikeValueTV;
    private WeatherData weatherData;
    private static String weatherBaseLink = "https://api.openweathermap.org/data/2.5/weather?appid=";
    private TextView timeTV;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case SEARCH_ACTIVITY_REQUEST:
                imgLink = data.getStringExtra("imgUrl");
                userQuery = data.getStringExtra("query");
                setBackgroundImage(imgLink,mainLayout);
                callWeatherAPI(userQuery);

                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);
        searchBtn = (Button) findViewById(R.id.searchBT);
        cityName = (TextView) findViewById(R.id.cityName);
        tempValueTV = findViewById(R.id.tempValueTV);
        feelsLikeValueTV = findViewById(R.id.feelsLikeValueTV);
        checkPreferences();

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                startActivityForResult(intent, SEARCH_ACTIVITY_REQUEST);
            }
        });
    }


    private void callWeatherAPI(String query){
        weatherBaseLink = "https://api.openweathermap.org/data/2.5/weather?appid=";
        weatherBaseLink = weatherBaseLink.concat(getString(R.string.open_weather_key)).concat("&q=" + query).concat("&units=metric");
        System.out.println(weatherBaseLink);
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
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String myResponse = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse).getJSONObject("main");
                            weatherData = new WeatherData(jsonObject.getDouble("temp"),jsonObject.getDouble("feels_like"),jsonObject.getDouble("humidity"));
                            tempValueTV.setText(weatherData.getTemp() + "℃");
                            feelsLikeValueTV.setText(weatherData.getFeels_like() +"℃");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    private void checkPreferences(){
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        imgLink = prefs.getString("img","");
        userQuery = prefs.getString("query","");
        if(imgLink != null && !imgLink.isEmpty())
            setBackgroundImage(imgLink,mainLayout);
        if(userQuery != null && !userQuery.isEmpty())
            callWeatherAPI(userQuery);
    }
    private void setBackgroundImage(String link, LinearLayout layout) {
        try {
            URL imgUrl = new URL(link);
            BitmapDrawable bmp = new BackgroundImageHelper().execute(imgUrl).get();
            layout.setBackground(bmp);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        final String MY_PREFS_NAME = "MyPrefsFile";
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("query", userQuery);
        editor.putString("img", imgLink);
        editor.commit();
        editor.apply();

    }

}

