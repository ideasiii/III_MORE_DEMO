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
            "{\"state\":0,\"tts\":\"你好！我是xx，你叫什麼名字呢？\",\"png\":\"p_o_excited_01.png\",\"response\":\"STT\",\"regretTime\":9999,\"regretTTS\":[{\"text\":\"抱歉，我的耳多怪怪的，你的名字是？\"}]}," +
            "{\"state\":1,\"tts\":\"你的名字叫作oo，對嗎？\",\"png\":\"g_o_speak.gif\",\"response\":\"STT\",\"regretTime\":10}," +
            "{\"state\":2,\"tts\":\"你的名字真好聽，oo，你幫我取一個新名字好不好？\",\"png\":\"p_o_happy_01.png\",\"response\":\"STT\",\"regretTime\":9999,\"regretTTS\":[{\"text\":\"抱歉，我的耳多怪怪的，我的新名字是？\"}]}," +
            "{\"state\":3,\"tts\":\"我的名字叫作xx，對嗎？\",\"png\":\"g_o_speak.gif\",\"response\":\"STT\",\"regretTime\":10}," +
            "{\"state\":4,\"tts\":\"我是xx，謝謝你，我好喜歡這個名字喔！你知道嗎？我好喜歡跟人握手喔，你可以跟我握握手嗎？\",\"png\":\"p_o_excited_03.png\",\"response\":\"SensorTag\",\"wait\":5000,\"regretTime\":1000,\"regretTTS\":[{\"text\":\"偷偷告訴你，我的手是有好幾條喔！\"}]}," +
            "{\"state\":5,\"tts\":\"好舒服喔！oo，我喜歡吃條紋魚，你可以幫我把條紋魚放到我面前的盤子嗎？\",\"png\":\"p_o_excited_04.png\",\"response\":\"RFID\",\"wait\":3000,\"regretTime\":1000, \"regretTTS\":[{\"text\":\"我沒有吃到條紋魚耶，你可以再餵我一次嗎?\"},{\"text\":\"剛剛你一定是放太快了，魚魚滑掉了，可以再把條紋魚放到我盤子上嗎?\"}]}," +
            "{\"state\":6,\"tts\":\"謝謝你的條紋魚\",\"png\":\"p_o_happy_03.png\",\"response\":\"no\",\"wait\":2000,\"regretTime\":3}," +
            "{\"state\":7,\"tts\":\"\",\"response\":\"no\",\"movie\":\"edubot/video/oobe/obbe_movie.mp4\",\"regretTime\":3}" +
            "]}";
    
    
    public static String [] NO_THESAURUS = {"不","不是","不對","錯","部隊"};
    
    public static String ID_RFID_STRIPED_FISH = "112199103115";
}
