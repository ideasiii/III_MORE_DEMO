package com.iii.more.game.module;

import android.app.Activity;

import com.iii.more.main.MainApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by joe on 2017/11/21.
 */

public class TrackerHandler
{
    
    private MainApplication application = null;
    private HashMap<String, String> track = null;
    
    public TrackerHandler(Activity activity)
    {
        application = (MainApplication) activity.getApplication();
        track = new HashMap<String, String>();
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
        JSONObject jRobotFace = new JSONObject();
        try
        {
            jRobotFace.put("File", strFileName);
            track.put("RobotFace", jRobotFace.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        
        return this;
    }
    
    public TrackerHandler setUserFace(String strAffective)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.put("Affective", strAffective);
            track.put("UserFace", jobj.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }
    
    public TrackerHandler setSensor(String strType, String strValue)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.put("Type", strType);
            jobj.put("Value", strValue);
            track.put("Sensor", jobj.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }
    
    public TrackerHandler setScene(String strValue)
    {
        track.put("Scene", strValue);
        return this;
    }
    
    public TrackerHandler setMicrophone(String strText)
    {
        JSONObject jobj = new JSONObject();
        try
        {
            jobj.put("Text", strText);
            track.put("Microphone", jobj.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return this;
    }
    
    public TrackerHandler setSpeaker(String strType, String strTTSText, String strTTSPitch, String strTTSSpeed, String strMediaUrl)
    {
        JSONObject jobj = new JSONObject();
        JSONObject jTTS = new JSONObject();
        JSONObject jMedia = new JSONObject();
        try
        {
            jobj.put("Type", strType);
            
            jTTS.put("Text", strTTSText);
            jTTS.put("Pitch", strTTSPitch);
            jTTS.put("Speed", strTTSSpeed);
            jobj.put("TTS",jTTS);
            
            jMedia.put("URL", strMediaUrl);
            jobj.put("Media", jMedia);
            
            track.put("Speaker", jobj.toString());
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
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
   "UserFace":{
      "Affective":"(觸發的規則的 tag 加上一些不需要的東西)"
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
