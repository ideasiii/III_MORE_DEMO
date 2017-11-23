package com.iii.more.oobe;

/**
 * Created by joe on 2017/10/31.
 */

public abstract class OobeParameters
{
    public static final int METHOD_TIME_OUT = 0;
    public static final int METHOD_SENSOR_DETECT = 1;
    public static final int METHOD_RFID_DETECT = 2;
    
    public static final int RUNNABLE_HARDWARE_CHECK = 0;
    
    //  HardwareCheck
    
    public static final int CHECK_TIME = 500;
    
    public static final String LOGIC_OOBE = "{" +
            "\"stateArray\":[" +
            "{\"state\":0,\"tts\":\"你好！我是xx，你叫什麼名字呢？\",\"png\":\"OCTOBO_Expressions-14.png\",\"response\":\"STT\"}," +
            "{\"state\":1,\"tts\":\"你的名字真好聽，oo，你幫我取一個新名字好不好？\",\"png\":\"OCTOBO_Expressions-31.png\",\"response\":\"STT\"}," +
            "{\"state\":2,\"tts\":\"我是xx，謝謝你，我好喜歡這個名字喔！你知道嗎？我也好喜歡抱抱，你可以抱抱我嗎？\",\"png\":\"OCTOBO_Expressions-25.png\",\"response\":\"SensorTag\",\"wait\":5000}," +
            "{\"state\":3,\"tts\":\"好舒服喔！oo，我喜歡吃條紋魚，你可以幫我把條紋魚放到我面前的盤子嗎？\",\"png\":\"OCTOBO_Expressions-24.png\",\"response\":\"RFID\",\"wait\":5000}," +
            "{\"state\":4,\"tts\":\"謝謝你的條紋魚\",\"png\":\"OCTOBO_Expressions-37.png\",\"response\":\"no\",\"wait\":2000}," +
            "{\"state\":5,\"tts\":\"xx好喜歡oo喔！每天早上，你只要抱抱我搖一搖，我會餵你唱首歌；拿我的手做刷牙動作，我會帶你刷刷牙；我們可以一起吃飯；拍拍我的頭，還能一起睡覺！以後我們每天都要一起玩喔！\",\"response\":\"no\",\"movie\":\"edubot/video/oobe/obbe_movie.mp4\"}]" +
            "}";
    
    
}
