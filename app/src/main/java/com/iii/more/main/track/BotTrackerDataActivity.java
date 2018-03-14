package com.iii.more.main.track;

import java.util.HashMap;

/**
 * Bot tracker data, 從 Activity 傳給 tracker 的資料
 */
public final class BotTrackerDataActivity extends BotTrackerTemplate
{
    public BotTrackerDataActivity(String activity, String scene)
    {
        super("0", "data from Activity");

        data.put("Activity", activity);
        data.put("Scene", scene);

        setRobotFace("");
        setSensor("", "");
        setMicrophone("");
        setSpeakerMedia("", "");
    }

    public BotTrackerDataActivity setRobotFace(String file)
    {
        HashMap<String, String> o = new HashMap<>();
        o.put("File", file);
        data.put("RobotFace", o);

        return this;
    }

    public BotTrackerDataActivity setSensor(String type, String value)
    {
        HashMap<String, String> o = new HashMap<>();
        o.put("Type", type);
        o.put("Type", value);
        data.put("Sensor", o);

        return this;
    }

    public BotTrackerDataActivity setMicrophone(String text)
    {
        HashMap<String, String> o = new HashMap<>();
        o.put("Text", text);
        data.put("Microphone", o);

        return this;
    }

    public BotTrackerDataActivity setSpeakerTTS(String text, String pitch, String speed)
    {
        HashMap<String, String> oInO = new HashMap<>();
        oInO.put("Text", text);
        oInO.put("Pitch", pitch);
        oInO.put("Speed", speed);

        HashMap<String, Object> o = new HashMap<>();
        o.put("Type", "tts");
        o.put("TTS", oInO);

        data.put("Speaker", o);

        return this;
    }

    public BotTrackerDataActivity setSpeakerMedia(String type, String uri)
    {
        HashMap<String, String> oInO = new HashMap<>();
        oInO.put("Type", type);
        if ("url".equals(type))
        {
            oInO.put("URL", uri);
        }
        else if ("local".equals(type))
        {
            oInO.put("Local", uri);
        }
        else
        {
            oInO.put("TEXT", uri);
        }

        HashMap<String, Object> o = new HashMap<>();
        o.put("Type", "media");
        o.put("Media", oInO);

        data.put("Speaker", o);

        return this;
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
*/