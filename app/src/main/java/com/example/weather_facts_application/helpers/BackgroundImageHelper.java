package com.example.weather_facts_application.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class BackgroundImageHelper extends AsyncTask<URL,Integer, Bitmap> {

    ProgressBar bar;

    public void setProgressBar(ProgressBar bar) {
        this.bar = bar;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (this.bar != null) {
            bar.setProgress(values[0]);

        }
    }

    @Override
    protected Bitmap doInBackground(URL... urls) {
        try {
            URL imgUrl = urls[0];
            return BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if (this.bar != null) {
            bar.clearAnimation();
        }
    }

}
