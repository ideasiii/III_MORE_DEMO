package com.iii.more.http.server;

import android.content.Context;
import android.support.annotation.NonNull;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/9/22.
 */

public class DeviceHttpServerHandler extends BaseHandler
{
    public DeviceHttpServerHandler(Context context)
    {
        super(context);
    }
    
    
    public void connectToServerByPost()
    {
        HashMap<String, String> postDataParams = new HashMap<String, String>();
        Thread connect = new Thread(new HttpPostRunnable(DeviceHttpServerParameters.URL_SERVER, postDataParams));
        connect.start();
        
    }
    
    public void connectToServerByGet(@NonNull String text)
    {
        Thread tmp = new Thread(new HttpGetRunnable(text));
        tmp.start();
    }
    
    
    private String httpGet(String text)
    {
        Logs.showTrace("[DeviceHttpServerHandler] httpGet Text:" + text);
        if (text.length() == 0)
        {
            return "";
        }
        
        try
        {
            String content = "";
            Logs.showTrace("[DeviceHttpServerHandler] http call: "+DeviceHttpServerParameters.URL_SERVER + urlEncode(text));
            URL url = new URL(DeviceHttpServerParameters.URL_SERVER + urlEncode(text));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(DeviceHttpServerParameters.TIME_OUT_CONNECT);
            connection.setReadTimeout(DeviceHttpServerParameters.TIME_OUT_READ);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                content += line + "\n";
            }
            return content;
        }
        
        catch (IOException e)
        {
            Logs.showError("[HttpAPIHandler] ERROR: " + e.toString());
        }
       
        
        return "";
        
    }
    
    
    private String httpPost(String requestURL, HashMap<String, String> postDataParams)
    {
        URL url = null;
        String response = "";
        try
        {
            url = new URL(requestURL);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setConnectTimeout(DeviceHttpServerParameters.TIME_OUT_CONNECT);
            connection.setReadTimeout(DeviceHttpServerParameters.TIME_OUT_READ);
            
            connection.setRequestMethod("POST");
            
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            
            OutputStream outputStream = connection.getOutputStream();
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, DeviceHttpServerParameters.FORMAT_TYPE));
            writer.write(getPostDataString(postDataParams));
            
            writer.flush();
            writer.close();
            
            outputStream.close();
            
            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpsURLConnection.HTTP_OK)
            {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while ((line = br.readLine()) != null)
                {
                    response += line;
                }
            }
            else
            {
                Logs.showError("[DeviceHttpServerHandler] ERROR HTTP Response Code:" + responseCode);
                response = "";
            }
        }
        catch (IOException e)
        {
            Logs.showError("[DeviceHttpServerHandler] " + e.toString());
        }
        
        return response;
    }
    
    private String getPostDataString(HashMap<String, String> params) throws
            UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                result.append("&");
            }
            
            result.append(URLEncoder.encode(entry.getKey(), DeviceHttpServerParameters.FORMAT_TYPE));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), DeviceHttpServerParameters.FORMAT_TYPE));
        }
        
        return result.toString();
    }
    
    
    private static String urlEncode(final String strText) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(strText, DeviceHttpServerParameters.FORMAT_TYPE);
    }
    
    private static String urlDecode(final String strText) throws UnsupportedEncodingException
    {
        return URLDecoder.decode(strText, DeviceHttpServerParameters.FORMAT_TYPE);
    }
    
    private class HttpGetRunnable implements Runnable
    {
        private String text = "";
        
        private HttpGetRunnable(String text)
        {
            this.text = text;
        }
        
        @Override
        public void run()
        {
            String result = httpGet(text);
            HashMap<String, String> message = new HashMap<>();
            
            if (result.length() == 0)
            {
                message.put("message", "");
            }
            else
            {
                message.put("message", result);
            }
            callBackMessage(ResponseCode.ERR_SUCCESS, DeviceHttpServerParameters.CLASS_DEVICE_HTTP_SERVER, DeviceHttpServerParameters.METHOD_HTTP_GET_RESPONSE, message);
            
        }
    }
    
    private class HttpPostRunnable implements Runnable
    {
        private String requestURL = "";
        private HashMap<String, String> postDataParams = null;
        
        private HttpPostRunnable(String requestURL, HashMap<String, String> postDataParams)
        {
            this.requestURL = requestURL;
            this.postDataParams = postDataParams;
        }
        
        @Override
        public void run()
        {
            String responseData = httpPost(requestURL, postDataParams);
            HashMap<String, String> message = new HashMap<>();
            
            if (!responseData.isEmpty())
            {
                message.put("message", responseData);
                callBackMessage(ResponseCode.ERR_SUCCESS, DeviceHttpServerParameters.CLASS_DEVICE_HTTP_SERVER,
                        DeviceHttpServerParameters.METHOD_HTTP_POST_RESPONSE, message);
            }
            else
            {
                message.put("message", "IO Exception!");
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DeviceHttpServerParameters.CLASS_DEVICE_HTTP_SERVER,
                        DeviceHttpServerParameters.METHOD_HTTP_POST_RESPONSE, message);
            }
        }
    }
    
    
}
