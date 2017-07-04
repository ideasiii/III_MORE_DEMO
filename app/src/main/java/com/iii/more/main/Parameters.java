package com.iii.more.main;

/**
 * Created by joe on 2017/4/12.
 */

public abstract class Parameters
{
    public static final String STRING_SERVICE_START_UP_GREETINGS = "您好，需要什麼服務?";
    public static final String ID_SERVICE_START_UP_GREETINGS = "1dfdabd7-cbc8-4432-a407-11a1d9494b6c";
    
    public static final String STRING_SERVICE_UNKNOWN = "我不太清楚您在講什麼";
   
    public static final String ID_SERVICE_UNKNOWN = "281fd4bc-b13d-4431-bf20-6c6bff4cbac2";
    
    public static final String STRING_SERVICE_SPOTIFY_UNAUTHORIZED  = "Spotify未授權此歌手";
    
    public static final String ID_SERVICE_SPOTIFY_UNAUTHORIZED = "281fd4bc-b13d-4431-bf20-3c3bff4cbac2";
    
    public static final String STRING_SERVICE_IO_EXCEPTION = "目前連線有些問題";
    public static final String ID_SERVICE_IO_EXCEPTION = "281fd4bc-b13d-4431-ff20-6c6bfa4cbcc1";
    
    public static final String STRING_SERVICE_INIT_SUCCESS="初始化已成功，開始接收指令";
    public static final String ID_SERVICE_INIT_SUCCESS ="d3515c80-14ed-4379-43ad-bd98-df97eed73491";
    
    
    public static final String ID_SERVICE_TTS_BEGIN = "d8762071-4379-43ad-bd98-df97eed73491";
    
    public static final String ID_SERVICE_MUSIC_BEGIN = "8feefaaa-14ed-441e-85e5-e9c9e378a77f";
    public static final String ID_SERVICE_STORY_BEGIN = "d3515c80-e737-454e-899e-9e0af2010b98";
   
    public static final String CMP_HOST_IP = "175.98.119.121";
    public static final int CMP_HOST_PORT = 2310;
    // for HTC10 1e-10f;  //for sampo 1e-25f;
    public static final float MEDIA_PLAYED_SPHINX_THRESHOLD = 1e-20f;//1e-20f;//// for HTC10 1e-15f;
    public static final float DEFAULT_SPHINX_THRESHOLD = 1e-20f;//1e-20f;//1e-11f;
    
    public static final String IDEAS_SPHINX_KEY_WORD = "hi ideas";
    public static final int ANIMATE_DURING = 3000;
    
}
