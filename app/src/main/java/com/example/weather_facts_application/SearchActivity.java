package com.example.weather_facts_application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import jp.wasabeef.blurry.Blurry;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    EditText cityET;
    private String imgUrl;
    private ProgressBar bar;
    public static final String baseUrl = "https://api.unsplash.com/search/photos?per_page=1&orientation=portrait&page=1&query=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search2);
        cityET = findViewById(R.id.cityET);
        bar = findViewById(R.id.circularProgressIndicator);
        bar.setVisibility(View.INVISIBLE);
        checkPreferences();

    }

    private String validateInput(String input){
        if(input.isEmpty()){
            Context context = getApplicationContext();
            CharSequence text = "Please Enter a city!";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return "";
        }
        else{
            return input;
        }

    }

    public void searchCity(View view) {
        String userInput = cityET.getText().toString();
        if(!validateInput(userInput).isEmpty()){
            bar.setVisibility(View.VISIBLE);
            callImageApi(userInput.trim().replace(" ", "+"));
        }
    }
    private void checkPreferences(){
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);
        String imgLink = prefs.getString("img","");
        if(imgLink != null && !imgLink.isEmpty())
            setBackgroundImage(imgLink,(RelativeLayout) cityET.getParent().getParent());

    }
    private void setBackgroundImage(String link, ViewGroup layout) {
        ImageView v = new ImageView(getBaseContext());
        Thread thread = new Thread(() -> {
            try {
                BackgroundImageHelper helper = new BackgroundImageHelper();
                helper.setProgressBar(bar);
                Bitmap bmp = helper.execute(new URL(link)).get();
                Blurry.with(getBaseContext()).sampling(5).radius(7).color(Color.argb(35,167,167,167)).animate(1000).from(bmp).into(v);
                runOnUiThread(() -> {
                    layout.setBackground(v.getDrawable());
                });
            } catch (InterruptedException | ExecutionException | MalformedURLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void callImageApi(String query){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization","Client-ID " + getString(R.string.unsplash_key))
                .url(baseUrl+query)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String myResponse = Objects.requireNonNull(response.body()).string();
                SearchActivity.this.runOnUiThread(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(myResponse);
                        imgUrl = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("urls").getString("full");
                        Intent i = new Intent();
                        i.putExtra("imgUrl",imgUrl);
                        i.putExtra("query",query);
                        setResult(Activity.RESULT_OK, i);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });

    }
}