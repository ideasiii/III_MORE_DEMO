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
    
    public static final String ERROR_POST_DEFAULT_RETURN ="{\n" + "\t\"TAG\":1,\n" + "\t\"TTS\":\"ㄟ嘿嘿，我網路腦袋怪怪的，聽不太懂，我只能開始繼續播故事喔\"\n" + "}";
    public static final String TEST_STT_POST_DEFAULT_RETURN ="{\n" + "\t\"TAG\":0,\n" + "\t\"TTS\":\"ㄟ嘿嘿，你覺得三支小豬的故事哪裡好笑\"\n" + "}";
    public static final String TEST_RESUME_PLAY_POST_DEFAULT_RETURN ="{\n" + "\t\"TAG\":1,\n" + "\t\"TTS\":\"喔喔，是喔，ㄟ嘿嘿，聽不太懂ㄟ，那我繼續播故事喔\"\n" + "}";
    
    public static final int TEST_MAX_COUNT = 3;
    
    
    public static final boolean isTest = false;
    
}
