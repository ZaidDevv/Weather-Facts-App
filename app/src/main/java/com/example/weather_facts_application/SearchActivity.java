package com.example.weather_facts_application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weather_facts_application.helpers.BackgroundImageHelper;
import com.example.weather_facts_application.models.WeatherData;


public class SearchActivity extends AppCompatActivity {
    private EditText cityET;
    private ProgressBar bar;
    private static SingletonApiClient apiClient;
    private WeatherData weatherData;
    private Bitmap backgroundBitmap;
    private String userInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = SingletonApiClient.getInstance();
        setContentView(R.layout.activity_search2);
        cityET = findViewById(R.id.cityET);
        bar = findViewById(R.id.circularProgressIndicator);
        bar.setVisibility(View.INVISIBLE);

    }

    private String validateInput(String input){
        if(input.isEmpty()){
            Toast.makeText(getApplicationContext(), "Please Enter a City/Country", Toast.LENGTH_SHORT).show();
            return "";
        }

        return input;
    }

    public void searchCity(View view) {
        userInput = cityET.getText().toString();
        bar.setVisibility(View.VISIBLE);
        Intent intent = new Intent();

        if (!validateInput(userInput).isEmpty()) {
            new Thread(() -> {
                backgroundBitmap = apiClient.callImageApi(userInput);
                if (backgroundBitmap != null) {
                    RelativeLayout mainLayout = (RelativeLayout) view.getParent().getParent();
                    Drawable background = new BackgroundImageHelper(getBaseContext()).blurBitmapToImage(backgroundBitmap).getDrawable();
                    this.runOnUiThread(() -> mainLayout.setBackground(background));
                }

                weatherData = apiClient.callWeatherAPI(userInput.trim().replace(" ", "+"));

                if (weatherData != null) {
                    intent.putExtra("background",backgroundBitmap);
                    intent.putExtra("weatherData", weatherData);
                    intent.putExtra("query", userInput);
                    setResult(2, intent);
                    finish();
                }
                bar.setVisibility(View.INVISIBLE);
            }).start();

        }
    }
}