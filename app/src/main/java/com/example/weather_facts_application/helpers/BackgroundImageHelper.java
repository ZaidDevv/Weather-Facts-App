package com.example.weather_facts_application.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.ViewGroup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class BackgroundImageHelper extends AsyncTask<URL,Integer, BitmapDrawable> {

    @Override
    protected BitmapDrawable doInBackground(URL... urls) {
        try {
            URL imgUrl = urls[0];
            Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());
            BitmapDrawable background = new BitmapDrawable(bmp);
            return background;
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
