package com.iii.more.oobe.track;

import android.content.Context;
import android.support.annotation.NonNull;

import com.iii.more.main.MainApplication;

import java.util.HashMap;
import java.util.Map;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/11/30.
 */

public class OobeTracker
{
    private Context mContext = null;
    private HashMap<String, String> defaultData = null;
    
    
    public OobeTracker(@NonNull Context context)
    {
        mContext = context;
        init();
    }
    
    
    public void tracker(HashMap<String, String> data)
    {
        HashMap<String, String> trackerData = new HashMap<>(defaultData);
        
        for (Map.Entry<String, String> e : data.entrySet())
        {
            trackerData.put(e.getKey(), e.getValue());
        }
        //debug using
        Logs.showTrace("[OobeTracker] ######## send Tracker Data: " + trackerData);
        ((MainApplication) mContext.getApplicationContext()).sendToTracker(trackerData);
        
    }
    
    
    private void init()
    {
        defaultData = new HashMap<>();
        defaultData.put("Source", "0");
        defaultData.put("Description", "data from Activity");
        defaultData.put("Activity", "oobe");
        defaultData.put("RobotFace", "{\"File\": \"\"}");
        defaultData.put("Sensor", "{\"Type\": \"\",\"Value\":\"\"}");
        defaultData.put("Scene", "");
        defaultData.put("Microphone", "{\"Text\": \"\"}");
        defaultData.put("Speaker", "{\"Type\": \"\",\"TTS\":{\"Text\": \"\",\"Pitch\": \"\",\"Speed\": " +
                "\"\"},\"Media\":{\"Type\": \"\",\"URL\":\"\",\"Local\":\"\"}}");
        
        
    }
    
    
}
