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
    
    public static final int CHECK_TIME = 100;
    
    public static final String LOGIC_OOBE = "{" +
            "\"stateArray\":[" +
            "{\"state\":0,\"tts\":\"你好！我是xx，你叫什麼名字呢？\",\"png\":\"OCTOBO_Expressions-14.png\",\"response\":\"STT\",\"regretTime\":10}," +
            "{\"state\":1,\"tts\":\"你的名字叫作oo，對嗎？\",\"png\":\"OCTOBO_Expressions-27.png\",\"response\":\"STT\",\"regretTime\":3}," +
            "{\"state\":2,\"tts\":\"你的名字真好聽，oo，你幫我取一個新名字好不好？\",\"png\":\"OCTOBO_Expressions-31.png\",\"response\":\"STT\",\"regretTime\":10}," +
            "{\"state\":3,\"tts\":\"我的名字叫作xx，對嗎？\",\"png\":\"OCTOBO_Expressions-27.png\",\"response\":\"STT\",\"regretTime\":3}," +
            "{\"state\":4,\"tts\":\"我是xx，謝謝你，我好喜歡這個名字喔！你知道嗎？我好喜歡跟人握手喔，你可以跟我握握手嗎？\",\"png\":\"OCTOBO_Expressions-25.png\",\"response\":\"SensorTag\",\"wait\":5000,\"regretTime\":1000,\"regretTTS\":[{\"text\":\"偷偷告訴你，我的手是那腸腸的那一根喔！\"}]}," +
            "{\"state\":5,\"tts\":\"好舒服喔！oo，我喜歡吃條紋魚，你可以幫我把條紋魚放到我面前的盤子嗎？\",\"png\":\"OCTOBO_Expressions-24.png\",\"response\":\"RFID\",\"wait\":3000,\"regretTime\":1000, \"regretTTS\":[{\"text\":\"條文魚呀、條文魚優！\"},{\"text\":\"我要吃條文魚，條文魚很好吃喔\"}]}," +
            "{\"state\":6,\"tts\":\"謝謝你的條紋魚\",\"png\":\"OCTOBO_Expressions-37.png\",\"response\":\"no\",\"wait\":2000,\"regretTime\":3}," +
            "{\"state\":7,\"tts\":\"\",\"response\":\"no\",\"movie\":\"edubot/video/oobe/obbe_movie.mp4\",\"regretTime\":3}" +
            "]}";
    
    
    public static String [] NO_THESAURUS = {"不","不是","不對","錯"};
    
    
}
