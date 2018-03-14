package com.iii.more.main.track;

import java.util.HashMap;

/**
 * Bot tracker data, 臉部情緒辨識 SDK 傳給 tracker 的資料
 */
public final class BotTrackerDataParsedFaceEmotion extends BotTrackerTemplate
{
    public BotTrackerDataParsedFaceEmotion(String ttsText, String ttsPitch, String ttsSpeed,
                                           String faceEmotionName, String robotFaceFile)
    {
        super("2", "face affectiva present");

        HashMap<String, String> val = new HashMap<>();
        val.put("Text", ttsText);
        val.put("Pitch", ttsPitch);
        val.put("Speed", ttsSpeed);
        data.put("TTS", val);

        data.put("FaceEmotionName", faceEmotionName);

        HashMap<String, String> val2 = new HashMap<>();
        val2.put("File", robotFaceFile);
        data.put("RobotFace", val2);
    }
}

/*
// ??????????????
{
    "Source": "2", // 固定為 2
    "Description": "多型態智能 face affectiva present", // 這筆資料的描述
    "TTS":
    {
        "Text": "疑?怎麼了", // 文字轉語音的文字
        "Pitch": "1",
        "Speed": "1"
    },
    "FaceEmotionName": "ANGER|FEAR|JOY|SADNESS|SURPRISE|ATTENTION", // SDK 偵測到的使用者臉部情緒
    "RobotFace":
    {
        "File": "starfish05.png" // robot 臉部圖檔名稱
    },
    "create_date": "2017-11-31 00:00:00" // 資料進入資料庫的時間
}
*/
