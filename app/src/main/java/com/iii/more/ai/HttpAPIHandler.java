package com.iii.more.ai;

import android.content.Context;
import android.support.annotation.NonNull;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/9/10.
 */

public class HttpAPIHandler extends BaseHandler
{
    public HttpAPIHandler(Context context)
    {
        super(context);
    }
    
    public void execute(@NonNull String text)
    {
        Thread tmp = new Thread(new HttpRunnable(text));
        tmp.start();
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
    
    private static String urlEncode(final String strText) throws UnsupportedEncodingException
    {
        return URLEncoder.encode(strText, HttpAPIParameters.FORMAT_TYPE);
    }
    
    private static String urlDecode(final String strText) throws UnsupportedEncodingException
    {
        return URLDecoder.decode(strText, HttpAPIParameters.FORMAT_TYPE);
    }
    
    class HttpRunnable implements Runnable
    {
        private String text = "";
        
        public HttpRunnable(String text)
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
            callBackMessage(ResponseCode.ERR_SUCCESS, HttpAPIParameters.CLASS_HTTP_API, HttpAPIParameters.METHOD_HTTP_GET_RESPONSE, message);
            
        }
    }
    
    
}
