package com.iii.more.download.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


/**
 * Created by joe on 2017/6/16.
 */

public class ImageDownloadHandler extends BaseHandler
{
    
    
    public ImageDownloadHandler(Context context)
    {
        super(context);
    }
    
    
    public void init(HashMap<String, String> data)
    {
        new DownloadFileFromURL().execute(data);
    }
    
    /**
     * Background Async Task to download file
     */
    class DownloadFileFromURL extends AsyncTask<HashMap<String, String>, String, Integer>
    {
        
        
        /**
         * Downloading file in background thread
         * key: download URL
         * value: save Path
         */
        @Override
        protected Integer doInBackground(HashMap<String, String>... map)
        {
            int count;
            try
            {
                for (String urlString : map[0].keySet())
                {
                    
                    URL url = new URL(urlString);
                    URLConnection connection = url.openConnection();
                    connection.connect();
                    
                    // getting file length
                    int lenghtOfFile = connection.getContentLength();
                    
                    // input stream to read file - with 8k buffer
                    InputStream input = new BufferedInputStream(url.openStream(), 8192);
                    
                    // Output stream to write file
                    OutputStream output = new FileOutputStream(map[0].get(urlString));
                    
                    byte data[] = new byte[1024];
                    
                    while ((count = input.read(data)) != -1)
                    {
                        // writing data to file
                        output.write(data, 0, count);
                    }
                    
                    // flushing output
                    output.flush();
                    
                    // closing streams
                    output.close();
                    input.close();
                }
                
            }
            catch (Exception e)
            {
                Logs.showError("[ImageDownloadHandler]Error: " + e.toString());
                return ResponseCode.ERR_IO_EXCEPTION;
            }
            
            return ResponseCode.ERR_SUCCESS;
        }
        
        
        /**
         * After completing background task
         **/
        @Override
        protected void onPostExecute(Integer responseCode)
        {
            HashMap<String, String> message = new HashMap<>();
            callBackMessage(responseCode, ImageDownloadParameters.CLASS_IMAGE_DOWNLOAD,
                    ImageDownloadParameters.METHOD_DOWNLOAD, message);
        }
        
    }
}

