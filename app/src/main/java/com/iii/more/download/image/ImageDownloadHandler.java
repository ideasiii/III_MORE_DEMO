package com.iii.more.download.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/6/16.
 */

public class ImageDownloadHandler extends AsyncTask<String, Void, Bitmap>
{
    ImageView bmImage = null;
    
    public ImageDownloadHandler(ImageView bmImage)
    {
        this.bmImage = bmImage;
    }
    
    protected Bitmap doInBackground(String... urls)
    {
        String urlString = urls[0];
        Bitmap mIcon11 = null;
        try
        {
            InputStream in = new java.net.URL(urlString).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        }
        catch (Exception e)
        {
            Logs.showError(e.toString());
        }
        return mIcon11;
    }
    
    protected void onPostExecute(Bitmap result)
    {
        bmImage.setImageBitmap(result);
    }
}


