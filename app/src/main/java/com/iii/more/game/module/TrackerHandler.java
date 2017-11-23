package com.iii.more.game.module;

import android.app.Activity;

import com.iii.more.main.MainApplication;

import java.util.HashMap;

/**
 * Created by jugo on 2017/11/21
 */

public class TrackerHandler
{
    
    private MainApplication application = null;
    private HashMap<String, String> track = null;
    
    public TrackerHandler(Activity activity)
    {
        application = (MainApplication) activity.getApplication();
        track = new HashMap<String, String>();
        application.startTracker();
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        track.clear();
        track = null;
        super.finalize();
    }
    
    public void send()
    {
        if (0 < track.size())
        {
            application.sendToTracker(track);
        }
    }
    
    public void clear()
    {
        track.clear();
    }
    
    public TrackerHandler setSource(String strValue)
    {
        track.put("Source", strValue);
        return this;
    }
    
    public TrackerHandler setDescription(String strValue)
    {
        track.put("Description", strValue);
        return this;
    }
    
    public TrackerHandler setActivity(String strValue)
    {
        track.put("Activity", strValue);
        return this;
    }
    
    public TrackerHandler setRobotFace(String strFileName)
    {
        track.put("RobotFace", String.format("{\"File\":\"%s\"}", strFileName));
        return this;
    }
    
    public TrackerHandler setSensor(String strType, String strValue)
    {
        track.put("Sensor", "{" + "\"Type\":\"" + strType + "\",\"Value\":\"" + strValue + "\"}");
        return this;
    }
    
    public TrackerHandler setScene(String strValue)
    {
        track.put("Scene", strValue);
        return this;
    }
    
    public TrackerHandler setMicrophone(String strText)
    {
        track.put("Microphone", "{" + "\"Text\":\"" + strText + "\"" + "}");
        return this;
    }
    
    public TrackerHandler setSpeaker(String strType, String strTTSText, String strTTSPitch, String strTTSSpeed, String strMediaUrl)
    {
        track.put("Speaker", "{\"Type\":\"" + strType + "\",\"TTS\":{\"Text\":\"" + strTTSText + "\",\"Pitch\":\"" + strTTSPitch + "\",\"Speed\":\"" + strTTSSpeed + "\"},\"Media\":{\"URL\":\"" + strMediaUrl + "\"} }");
        return this;
    }
}

/*
{
   "Source":"0",
   "Description":"data from Activity",
   "Activity":"game|oobe|story",
   "RobotFace":{
      "File":"(drawable的檔名)"
   },
   "Sensor":{
      "Type":"clap_hand|shake_hand|pat_hat|squeeze|rfid",
      "Value":"(sensor 讀到的數值)"
   },
   "Scene":"(目前在Activity內哪個場景或步驟)",
   "Microphone":{
      "Text":"(語音轉文字的辨識結果)"
   },
   "Speaker":{
      "Type":"tts|media",
      "TTS":{
         "Text":"(文字轉語音的文字)",
         "Pitch":"1",
         "Speed":"1"
      },
      "Media":{
         "URL":"(目前撥放的媒體的路徑)"
      }
   }
}
 */
