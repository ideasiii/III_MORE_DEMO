package com.iii.more.main;

/**
 * Created by joe on 2017/4/12
 */

public abstract class Parameters
{
    public static final boolean OOBE_DEBUG_ENABLE = false;//BuildConfig.OOBE;
    
    public static final String ID_CHILD_NAME = "child_name";
    public static final String ID_ROBOT_NAME = "edubot_name";
    public static final String STRING_DEFAULT_ROBOT_NAME = "章魚寶";

    public static final String TASK_COMPOSER_DATA = "taskcomposerdata";
    public static final String CLOCK_INTENT_DATA = "clockintentdata";
    
    public static final boolean IS_STORY_MODE_USE_TASK_COMPOSER_EMOTION_TTS = true;
    
    
    public static final String CMP_HOST_IP = "54.199.198.94";
    public static final int CMP_HOST_PORT = 2310;
    
    // 布偶的藍牙裝置名稱
    static final String DEFAULT_DEVICE_ID = "module_01";

    // 此 app 在 tracker 的 APP ID
    static final String TRACKER_APP_ID = "1510555888386";
    
    static final String INTERNET_COCKPIT_SERVER_ADDRESS = "wss://ryejuice.sytes.net:2406/puppet";
    
    static final String INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT =
        "[{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":1,\"action\":1," +
            "\"tag\":\"SHAKE_HANDS\",\"trigger\":\"OCTOBO_Expressions-35.png\",\"value\":\"1\"," +
            "\"desc\":\"握手\"},{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":2," +
            "\"action\":2,\"tag\":\"CLAP_HANDS\",\"trigger\":\"OCTOBO_Expressions-24.png\"," +
            "\"value\":\"1\",\"desc\":\"拍手\"},{\"sensors\":[\"FSR1\",\"FSR2\"]," +
            "\"trigger_rule\":2,\"action\":3,\"tag\":\"EXTRUSION\"," +
            "\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"擠壓\"}," +
            "{\"sensors\":[\"X\",\"Y\",\"Z\"],\"trigger_rule\":1,\"action\":4,\"tag\":\"SHAKE\"," +
            "\"trigger\":\"OCTOBO_Expressions-38.png\",\"value\":\"1\",\"desc\":\"搖晃\"}," +
            "{\"sensors\":[\"H\"],\"trigger_rule\":1,\"action\":5,\"tag\":\"TURN_ON_THE_LIGHT\"," +
            "\"trigger\":\"ON\",\"value\":\"1\",\"desc\":\"開燈\"},{\"sensors\":[\"FSR1\"," +
            "\"FSR2\"],\"trigger_rule\":1,\"action\":6,\"tag\":\"PAT_HEAD\"," +
            "\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"拍頭\"}]";
    
    static final String INTERRUPT_EMOTION_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT = "[{trigger_value: " +
        "\"60\",priority: 3,emotion_name: \"ANGER\",emotion_id: 1,trigger_time: 1,img_name: " +
        "\"OCTOBO_Expressions-08.png\",contents: [{tts: \"你看起來好生氣，發生什麼事了？\",id: 1,pitch: \"1.0\"," +
        "speed: \"1.0\"},{tts: \"我想你一定很生氣。我幫你把生氣的事情吹走，呼~好了，現在不要生氣了！我們繼續來玩吧！\",id: 2,pitch: " +
        "\"1.0\",speed: \"1.0\"}],emotion_type: \"EMOTION\",data_type: \"OCTOBO\",id: 1}," +
        "{trigger_value: \"60\",priority: 7,emotion_name: \"DISGUST\",emotion_id: 2,trigger_time:" +
        " 1,img_name: \"OCTOBO_Expressions-04.png\",contents: [ ],emotion_type: \"EMOTION\"," +
        "data_type: \"OCTOBO\",id: 2},{trigger_value: \"60\",priority: 4,emotion_name: \"FEAR\"," +
        "emotion_id: 3,trigger_time: 1,img_name: \"OCTOBO_Expressions-28.png\",contents: [ ]," +
        "emotion_type: \"EMOTION\",data_type: \"OCTOBO\",id: 3},{trigger_value: \"60\",priority: " +
        "2,emotion_name: \"JOY\",emotion_id: 4,trigger_time: 1,img_name: " +
        "\"OCTOBO_Expressions-31.png\",contents: [{tts: \"你笑得好開心喔！什麼事情這麼好笑？\",id: 1,pitch: " +
        "\"1.0\",speed: \"1.0\"},{tts: \"那現在我們繼續來玩吧！\",id: 2,pitch: \"1.0\",speed: \"1.0\"}]," +
        "emotion_type: \"EMOTION\",data_type: \"OCTOBO\",id: 4},{trigger_value: \"60\",priority: " +
        "5,emotion_name: \"SADNESS\",emotion_id: 5,trigger_time: 1,img_name: " +
        "\"OCTOBO_Expressions-05.png\",contents: [{tts: \"你看起來好難過，怎麼了？你還好嗎？\",id: 1,pitch: " +
        "\"1.0\",speed: \"1.0\"},{tts: \"我想你一定很傷心，我想給你一個擁抱，來，抱一下！\",id: 2,pitch: \"1.0\",speed: " +
        "\"1.0\"}],emotion_type: \"EMOTION\",data_type: \"OCTOBO\",id: 5},{trigger_value: \"60\"," +
        "priority: 6,emotion_name: \"SURPRISE\",emotion_id: 6,trigger_time: 1,img_name: " +
        "\"OCTOBO_Expressions-21.png\",contents: [{tts: \"咦，怎麼了？\",id: 1,pitch: \"1.0\",speed: " +
        "\"1.0\"},{tts: \"呼！我剛剛嚇了一跳呢！好了，現在沒事了，我們繼續來玩吧！\",id: 2,pitch: \"1.0\",speed: \"1.0\"}]," +
        "emotion_type: \"EMOTION\",data_type: \"OCTOBO\",id: 6},{trigger_value: \"60\",priority: " +
        "8,emotion_name: \"CONTEMPT\",emotion_id: 7,trigger_time: 1,img_name: " +
        "\"OCTOBO_Expressions-38.png\",contents: [ ],emotion_type: \"EMOTION\",data_type: " +
        "\"OCTOBO\",id: 7},{trigger_value: \"-1\",priority: 1,emotion_name: \"ATTENTION\"," +
        "emotion_id: 10,trigger_time: 3,img_name: \"OCTOBO_Expressions-16.png\",contents: [ ]," +
        "emotion_type: \"EXPRESSION\",data_type: \"OCTOBO\",id: 8}]";
    
}
