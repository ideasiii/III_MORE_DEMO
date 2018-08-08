package com.iii.more.main.track;

import java.util.HashMap;

/**
 * Bot tracker data, source 1
 */
public final class BotTrackerDataRawFaceEmotion extends BotTrackerTemplate
{
    public BotTrackerDataRawFaceEmotion(String time, String anger, String disgust, String fear, String joy, String sadness,
                                        String surprise, String contempt, String engagement, String valence)
    {
        super("1", "raw data from face com.iii.more.emotion recognition SDK");


        HashMap<String, String> val = new HashMap<>();
        val.put("time", time);
        val.put("ANGER", anger);
        val.put("DISGUST", disgust);
        val.put("FEAR", fear);
        val.put("JOY", joy);
        val.put("SADNESS", sadness);
        val.put("SURPRISE", surprise);
        val.put("CONTEMPT", contempt);
        val.put("ENGAGEMENT", engagement);
        val.put("VALENCE", valence);
        data.put("Value", val);
    }
}

/*
// 臉部情緒辨識 SDK 傳給 tracker 的資料
{
    "Source": "1", // 固定為 1
    "Description": "raw data from face com.iii.more.emotion recognition SDK", // 這筆資料的描述
    "Value": { // SDK 來的讀數
        "time":"2017_11_14_10:18:45", // 從 SDK 拿到數值的時間
        "ANGER":"26.3",
        "DISGUST":"10.0",
        "FEAR":"0.26",
        "JOY":"0.32",
        "SADNESS":"0.56",
        "SURPRISE":"0.12",
        "CONTEMPT":"0.45",
        "ENGAGEMENT": "0.1",
        "VALENCE": "0.09"
    },
    "create_date": "2017-11-31 00:00:00" // 資料進入資料庫的時間
}
*/