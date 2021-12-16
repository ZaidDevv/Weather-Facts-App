package com.example.weather_facts_application.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.IOException;
import java.net.URL;

import jp.wasabeef.blurry.Blurry;

public class BackgroundImageHelper extends AsyncTask<URL,Integer, Bitmap> {

    private ProgressBar bar;

    private Context context;
    public BackgroundImageHelper(Context context) {
        super();
        this.context = context;
    }

    public BackgroundImageHelper() {
        super();
    }

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
            bar.setVisibility(View.INVISIBLE);
        }
    }

    public ImageView blurBitmapToImage(Bitmap bmp){
        ImageView v = new ImageView(context);
        Blurry.with(context).sampling(2).radius(2).color(Color.argb(20,167,167,167)).from(bmp).into(v);
        return v;

    }

}
