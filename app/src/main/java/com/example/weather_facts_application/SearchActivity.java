package com.example.weather_facts_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.weather_facts_application.helpers.BackgroundImageHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    EditText cityET;
    private String imgUrl;

    private String baseUrl = "https://api.unsplash.com/search/photos?per_page=1&orientation=portrait&page=1&query=";
    private String q;
    public static final String querySaved="QKEY";
    public static final String imgSaved="IMGSAVED";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        cityET = findViewById(R.id.cityET);

    }

    private boolean validateInput(String input){
        if(input.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please Enter a city!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }
        else{
            return true;
        }

    }

    public void searchCity(View view) {
        String userInput = cityET.getText().toString();
        boolean valid = validateInput(userInput);
        if(valid){
            callImageApi(userInput);
        }
    }

    private void callImageApi(String query){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization","Client-ID " + getString(R.string.unsplash_key))
                .url(baseUrl+query.trim())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String myResponse = response.body().string();
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(myResponse);
                            imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("full");
                            Intent i = new Intent();
                            i.putExtra("imgUrl",imgUrl);
                            i.putExtra("query",query);
                            q = query;
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
}