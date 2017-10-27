package com.iii.more.http.server;

/**
 * Created by joe on 2017/10/22.
 */

public abstract class DeviceHttpServerParameters
{
    public static final int CLASS_DEVICE_HTTP_SERVER = 1599;
    public static final int METHOD_HTTP_POST_RESPONSE = 0;
    
    public static final int METHOD_HTTP_GET_RESPONSE = 1;
    
    public static final String URL_SERVER = "http://service.inmedia.com.tw/task/api/botdata.do?jdatas=";
    public static final String URL_DEFAULT_PARAM = "{\"data_type\":\"OCTOBO\"}";//"{%22data_type%22:%22OCTOBO%22}";
    
    public static final String FORMAT_TYPE = "utf-8";
    
    public static final int TIME_OUT_CONNECT = 15000;
    public static final int TIME_OUT_READ = 15000;
}
