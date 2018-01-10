package com.iii.more.ai;

import android.content.Context;
import android.support.annotation.NonNull;


import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by joe on 2017/9/10.
 */

public class HttpAPIHandler extends BaseHandler
{
    private int testCounter = 0;
    
    public HttpAPIHandler(Context context)
    {
        super(context);
    }
    
    
    public void executeByGet(@NonNull String text)
    {
        Thread tmp = new Thread(new HttpGetRunnable(text));
        tmp.start();
    }
    
    public void executeByPost(@NonNull HashMap<String, String> data, boolean isJSONFormat)
    {
        Thread connect = new Thread(new HttpPostRunnable(HttpAPIParameters.URL_SERVER, data, isJSONFormat));
        connect.start();
    }
    
    public void executeByPost(String serverURL, @NonNull HashMap<String, String> data, boolean isJSONFormat)
    {
        Logs.showTrace("[HttpAPIHandler] executeByPost: URL:" + serverURL + "postData: " + data);
        
        if (HttpAPIParameters.isTest)
        {
            HashMap<String, String> message = new HashMap<>();
            String responseData = HttpAPIParameters.ERROR_POST_DEFAULT_RETURN;
            if (testCounter++ >= HttpAPIParameters.TEST_MAX_COUNT)
            {
                responseData = HttpAPIParameters.TEST_RESUME_PLAY_POST_DEFAULT_RETURN;
                testCounter = 0;
            }
            else
            {
                responseData = HttpAPIParameters.TEST_STT_POST_DEFAULT_RETURN;
            }
            if (!responseData.isEmpty())
            {
                message.put("message", responseData);
                callBackMessage(ResponseCode.ERR_SUCCESS, HttpAPIParameters.CLASS_HTTP_API,
                    HttpAPIParameters.METHOD_HTTP_POST_RESPONSE, message);
            }
        }
        else
        {
            Thread connect = new Thread(new HttpPostRunnable(serverURL, data, isJSONFormat));
            connect.start();
        }
    }
    
    
    private String httpGet(String text)
    {
        Logs.showTrace("[HttpAPIHandler] httpGet Text:" + text);
        if (text.length() == 0)
        {
            return "";
        }
        
        try
        {
            String content = "";
            URL url = new URL(HttpAPIParameters.URL_SERVER + urlEncode(stringModify(text)));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(HttpAPIParameters.TIME_OUT_CONNECT);
            connection.setReadTimeout(HttpAPIParameters.TIME_OUT_READ);
            connection.connect();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                content += line + "\n";
            }
            JSONObject tmp = new JSONObject(content);
            return urlDecode(tmp.getString("response"));
        }
        
        catch (IOException e)
        {
            Logs.showError("[HttpAPIHandler] ERROR: " + e.toString());
        }
        catch (JSONException e)
        {
            Logs.showError("[HttpAPIHandler] ERROR: " + e.toString());
        }
        
        return "";
        
    }
    
    private String httpPost(String requestURL, HashMap<String, String> postDataParams, boolean writeByJSON)
    {
        URL url = null;
        String response = "";
        try
        {
            url = new URL(requestURL);
            
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setConnectTimeout(HttpAPIParameters.TIME_OUT_CONNECT);
            connection.setReadTimeout(HttpAPIParameters.TIME_OUT_READ);
            
            connection.setRequestMethod("POST");
            
            connection.setDoInput(true);
            connection.setDoOutput(true);
            
            
            OutputStream outputStream = connection.getOutputStream();
            
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream,
                HttpAPIParameters.FORMAT_TYPE));
            if (writeByJSON)
            {
                JSONObject postJsonData = new JSONObject(postDataParams);
                writer.write(postJsonData.toString());
            }
            else
            {
                writer.write(getPostDataString(postDataParams));
            }
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
                Logs.showError("[HttpAPIHandler] ERROR HTTP Response Code:" + responseCode);
                response = "";
            }
        }
        catch (IOException e)
        {
            Logs.showError("[HttpAPIHandler] " + e.toString());
        }
        
        
        return response;
        
    }
    
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException
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
            
            result.append(URLEncoder.encode(entry.getKey(), HttpAPIParameters.FORMAT_TYPE));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), HttpAPIParameters.FORMAT_TYPE));
        }
        
        return result.toString();
    }
    
    
    private static String urlEncode(final String strText) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(strText, HttpAPIParameters.FORMAT_TYPE);
    }
    
    private static String stringModify(String text)
    {
        String finalText = "";
        text = text.replace("三芝", "3隻");
        text = text.replace("市值", "4隻");
        text = text.replace("兩隻", "2隻");
        text = text.replace("朱", "豬");
        finalText = text.replace("住大哥", "豬大哥");
        
        return finalText;
    }
    
    
    private static String urlDecode(final String strText) throws UnsupportedEncodingException
    {
        return URLDecoder.decode(strText, HttpAPIParameters.FORMAT_TYPE);
    }
    
    class HttpGetRunnable implements Runnable
    {
        private String text = "";
        
        public HttpGetRunnable(String text)
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
                message.put("message", HttpAPIParameters.ERROR_DEFAULT_RETURN);
            }
            else
            {
                message.put("message", result);
            }
            callBackMessage(ResponseCode.ERR_SUCCESS, HttpAPIParameters.CLASS_HTTP_API, HttpAPIParameters
                .METHOD_HTTP_GET_RESPONSE, message);
            
        }
    }
    
    private class HttpPostRunnable implements Runnable
    {
        private String requestURL = "";
        private HashMap<String, String> postDataParams = null;
        private boolean isJSONFormat = false;
        
        private HttpPostRunnable(String requestURL, HashMap<String, String> postDataParams, boolean
            isJSONFormat)
        {
            this.requestURL = requestURL;
            this.postDataParams = postDataParams;
            this.isJSONFormat = isJSONFormat;
        }
        
        @Override
        public void run()
        {
            String responseData = httpPost(requestURL, postDataParams, isJSONFormat);
            HashMap<String, String> message = new HashMap<>();
            
            if (!responseData.isEmpty())
            {
                message.put("message", responseData);
                callBackMessage(ResponseCode.ERR_SUCCESS, HttpAPIParameters.CLASS_HTTP_API,
                    HttpAPIParameters.METHOD_HTTP_POST_RESPONSE, message);
            }
            else
            {
                message.put("message", "IO Exception!");
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, HttpAPIParameters.CLASS_HTTP_API,
                    HttpAPIParameters.METHOD_HTTP_POST_RESPONSE, message);
            }
        }
    }
    
    
}
