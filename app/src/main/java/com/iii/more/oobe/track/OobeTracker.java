package com.iii.more.oobe.track;

import android.content.Context;
import android.support.annotation.NonNull;

import com.iii.more.main.MainApplication;

import java.util.HashMap;
import java.util.Map;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/12/01
 */

public class OobeTracker
{
    private Context mContext = null;
    private HashMap<String, Object> defaultData = null;
    
    
    public OobeTracker(@NonNull Context context)
    {
        mContext = context;
        init();
    }
    
    
    public void tracker(HashMap<String, Object> data)
    {
        HashMap<String, Object> trackerData = new HashMap<>(defaultData);
        
        for (Map.Entry<String, Object> e : data.entrySet())
        {
            trackerData.put(e.getKey(), e.getValue());
        }
        //debug using
        Logs.showTrace("[OobeTracker] ######## send Tracker Data: " + trackerData);
        ((MainApplication) mContext.getApplicationContext()).sendToTrackerWithObjectMap(trackerData);
        
    }
    
    
    private void init()
    {
        defaultData = new HashMap<>();
        defaultData.put("Source", "0");
        defaultData.put("Description", "data from Activity");
        defaultData.put("Activity", "oobe");
        
        HashMap<String, Object> robotFace = new HashMap<>();
        robotFace.put("File", "");
        
        defaultData.put("RobotFace", robotFace);
        
        HashMap<String, Object> sensor = new HashMap<>();
        sensor.put("Type", "");
        sensor.put("Value", "");
        defaultData.put("Sensor", sensor);
        
        defaultData.put("Scene", "");
        
        HashMap<String, Object> microphone = new HashMap<>();
        microphone.put("Text", "");
        defaultData.put("Microphone", microphone);
        
        HashMap<String, Object> speaker = new HashMap<>();
        speaker.put("Type", "");
        
        HashMap<String, Object> tts = new HashMap<>();
        tts.put("Text", "");
        tts.put("Pitch", "");
        tts.put("Speed", "");
        
        speaker.put("TTS", tts);
        HashMap<String, Object> media = new HashMap<>();
        media.put("Type", "");
        media.put("URL", "");
        media.put("Local", "");
        speaker.put("Media", media);
        
        defaultData.put("Speaker", speaker);
        
        
    }
    
    
}
