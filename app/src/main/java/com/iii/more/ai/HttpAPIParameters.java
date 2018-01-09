package com.iii.more.ai;

/**
 * Created by joe on 2017/9/09.
 */

public abstract class HttpAPIParameters
{
    public static final int CLASS_HTTP_API = 1587;
    public static final int METHOD_HTTP_GET_RESPONSE = 0;
    public static final int METHOD_HTTP_POST_RESPONSE = 1;
    
    
    public static final String URL_SERVER = "https://chatbot.srm.pw/chatbot/?Text=";
    
    public static final String ERROR_DEFAULT_RETURN = "我不清楚呢，你可以再說一次嗎?";
    
    public static final String FORMAT_TYPE = "utf-8";
    
    public static final int TIME_OUT_CONNECT = 5000;
    public static final int TIME_OUT_READ = 5000;
    
}
