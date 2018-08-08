package com.iii.more.main.track;


import java.util.HashMap;

public abstract class BotTrackerTemplate
{
    protected HashMap<String, Object> data;

    protected BotTrackerTemplate(String source, String description)
    {
        data = new HashMap<>();
        data.put("Source", source);
        data.put("Description", description);
    }

    public HashMap<String, Object> getData()
    {
        return data;
    }
}


/*
// 從 Activity 傳給 tracker 的資料
{
    "Source": "0", // 固定為 0
    "Description": "data from Activity", // 這筆資料的描述
    "Activity": "game|oobe|story", // 資料是從哪個 Activity 送出來的
    "RobotFace":
    {
        "File": "startfish05.png" // robot 臉部圖檔名稱
    },
    "Sensor": // sensor 觸發
    {
        "Type": "clap_hand|shake_hand|pat_hat|squeeze|rfid", // 觸發了甚麼事件
        "Value": "(sensor 讀到的數值)"
    },
    "Scene": "???", // 目前在 Activity 內哪個場景或步驟
    "Microphone": // 聲音輸入
    {
        "Text": "我要吃晚餐" // 語音轉文字的辨識結果
    },
    "Speaker": // 聲音輸出
    {
        "Type": "tts|media",
        "TTS": // if (Type == tts) (聲音的來源是 TTS)
        {
            "Text": "我知道", // 文字轉語音的文字
            "Pitch": "1",
            "Speed": "1"
        },
        "Media": // if (Type == media) (聲音的來源是媒體檔案)
        {
            "Type": "url|local",
            "URL":"https://...", // if (Type == url) (媒體路徑 [若撥放的是網路上的檔案])
            "Local":"??" // if (Type == local) (媒體路徑 [若撥放的是裝置本地的檔案])
        }
    },
    "create_date": "2017-11-31 00:00:00" // 資料進入資料庫的時間
}

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
