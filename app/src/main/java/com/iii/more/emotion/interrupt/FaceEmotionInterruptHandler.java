package com.iii.more.emotion.interrupt;

import android.content.Context;
import android.support.annotation.NonNull;


import com.iii.more.interrupt.logic.InterruptLogicParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/11/13.
 */

public class FaceEmotionInterruptHandler extends BaseHandler
{
    private HashMap<String, String> mFaceData = null;
    private JSONArray mInterruptEmotionBehaviorDataArray = null;
    private boolean isRecordMode = false;
    private ArrayList<EmotionBrainElement> mEmotionBrainArrayListData = null;
    private HashMap<String, String> mEmotionHashMapData = null;
    
    public FaceEmotionInterruptHandler(Context context)
    {
        super(context);
        mEmotionBrainArrayListData = new ArrayList<>();
    }
    
    
    public void setEmotionEventData(@NonNull HashMap<String, String> emotionEventData)
    {
        Logs.showTrace("[InterruptLogicHandler] emotion event data: " + emotionEventData);
        mEmotionHashMapData = emotionEventData;
        //startEmotionEventDataAnalysis();
    }
    
    public void startEmotionEventDataAnalysis()
    {
        ArrayList<EmotionElement> emotionArrayList = covertEmotionHashMapDataToArrayList(mEmotionHashMapData);
        if (emotionArrayList.size() > 0)
        {
            EmotionElement maxEmotion = Collections.max(emotionArrayList, new Comparator<EmotionElement>()
            {
                @Override
                public int compare(EmotionElement a, EmotionElement b)
                {
                    if (a.emotionValue > b.emotionValue)
                    {
                        return 1; // highest value first
                    }
                    else if (a.emotionValue == b.emotionValue)
                    {
                        return 0;
                    }
                    else
                    {
                        return -1;
                    }
                }
            });
            
            
            Logs.showTrace("[InterruptLogicHandler] MAX Emotion:");
            maxEmotion.print();
            
            
            if (maxEmotion.emotionValue > InterruptLogicParameters.LOW_BOUND_EMOTION_VALUE)
            {
                String emotionMappingImageData = getEmotionMappingImageData(maxEmotion.emotionID);
                
                
                callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                        InterruptLogicParameters.METHOD_EMOTION_LOGIC_RESPONSE,
                        makeEmotionDisplayJson(maxEmotion.emotionID, emotionMappingImageData));
                
                
            }
            else
            {
                String emotionMappingImageData = getEmotionMappingImageData("NEUTRAL");
                
                
                callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                        InterruptLogicParameters.METHOD_EMOTION_LOGIC_RESPONSE,
                        makeEmotionDisplayJson(maxEmotion.emotionID, emotionMappingImageData));
                
            }
        }
        
    }
    
    private String getEmotionMappingImageData(@NonNull String dataName)
    {
        for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
        {
            if (dataName.equals(mEmotionBrainArrayListData.get(i).emotionName))
            {
                return mEmotionBrainArrayListData.get(i).emotionMappingImageName;
            }
        }
        return null;
    }
    
    private ArrayList<EmotionElement> covertEmotionHashMapDataToArrayList(@NonNull HashMap<String, String> emotionHashMapData)
    {
        ArrayList<EmotionElement> data = new ArrayList<>();
        boolean isChangeEmotion = false;
        
        for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
        {
            String strNowEmotionValue = emotionHashMapData.get(mEmotionBrainArrayListData.get(i).emotionName);
            if (null != strNowEmotionValue)
            {
                try
                {
                    if (strNowEmotionValue.equals(mEmotionBrainArrayListData.get(i).triggerValue) &&
                            mEmotionBrainArrayListData.get(i).emotionType.equals("EXPRESSION"))
                    {
                        //change state
                        isChangeEmotion = true;
                        //return ;
                    }
                    else
                    {
                        
                        float floatNowEmotionValue = Float.valueOf(strNowEmotionValue);
                        float ruleEmotionValue = Float.valueOf(mEmotionBrainArrayListData.get(i).triggerValue);
                        if (floatNowEmotionValue >= ruleEmotionValue)
                        {
                            //change state
                            isChangeEmotion = true;
                           // return ;
                        }
                        else
                        {
                            //change state
                            
                        }
                        
                    }
                    
                    
                }
                catch (Exception e)
                {
                    Logs.showError("[InterruptLogicHandler] covertEmotionHashMapDataToArrayList ERROR:" + e.toString());
                }
                
            }
            
            
        }
        if (!isChangeEmotion)
        {
            return null;
        }
        return data;
        
        
    }
    
    private HashMap<String, String> makeEmotionDisplayJson(String emotionID, String emotionMappingImageID)
    {
        HashMap<String, String> message = new HashMap<>();
        message.put(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_NAME, emotionID);
        message.put(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_MAPPING_IMAGE_NAME, emotionMappingImageID);
        
        JSONObject animate = new JSONObject();
        try
        {
            animate.put("type", 0);
            animate.put("duration", 3000);
            animate.put("repeat", 0);
            animate.put("interpolate", 1);
            
            //create display json
            JSONObject data = new JSONObject();
            data.put("time", 0);
            data.put("host", "https://smabuild.sytes.net/edubot/OCTOBO_Expressions/");
            data.put("color", "#FFA0C9EC");
            data.put("description", "快樂");
            data.put("animation", animate);
            data.put("text", new JSONObject());
            data.put("file", emotionMappingImageID);
            JSONArray show = new JSONArray();
            show.put(data);
            
            JSONObject display = new JSONObject();
            display.put("enable", 1);
            display.put("show", show);
            message.put("display", display.toString());
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] makeEmotionDisplayJson ERROR" + e.toString());
        }
        
        return message;
    }
    
    
    public void setInterruptEmotionLogicBehaviorDataArray(@NonNull String emotionLogicBehavior)
    {
        Logs.showTrace("[InterruptLogicHandler] set emotion logic behavior data: " + emotionLogicBehavior);
        
        try
        {
            mInterruptEmotionBehaviorDataArray = new JSONArray(emotionLogicBehavior);
            for (int i = 0; i < mInterruptEmotionBehaviorDataArray.length(); i++)
            {
                JSONObject jsonEmotionBrainElement = mInterruptEmotionBehaviorDataArray.getJSONObject(i);
                
                JSONArray contents = jsonEmotionBrainElement.getJSONArray(FaceEmotionInterruptParameters.JSON_STRING_CONTENTS);
                
                ArrayList<JSONObject> contentArrayList = new ArrayList<JSONObject>();
                
                for (int j = 0; j < contents.length(); j++)
                {
                    contentArrayList.add(contents.getJSONObject(j));
                }
                
                mEmotionBrainArrayListData.add(new EmotionBrainElement(
                        jsonEmotionBrainElement.getInt(FaceEmotionInterruptParameters.JSON_STRING_ID),
                        jsonEmotionBrainElement.getInt(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_ID),
                        jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_NAME),
                        jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_MAPPING_IMAGE_NAME),
                        jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters.JSON_STRING_DATA_TYPE),
                        jsonEmotionBrainElement.getInt(FaceEmotionInterruptParameters.JSON_STRING_PRIORITY),
                        jsonEmotionBrainElement.getInt(FaceEmotionInterruptParameters.JSON_STRING_TRIGGER_TIME),
                        jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters.JSON_STRING_TRIGGER_VALUE),
                        jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_TYPE),
                        contentArrayList));
            }
            
            //sort it
            Collections.sort(mEmotionBrainArrayListData, new Comparator<EmotionBrainElement>()
            {
                @Override
                public int compare(EmotionBrainElement o1, EmotionBrainElement o2)
                {
                    if (o1.priority > o2.priority)
                    {
                        return 1; // lowest value first
                    }
                    else if (o1.priority == o2.priority)
                    {
                        return 0;
                    }
                    else
                    {
                        return -1;
                    }
                }
            });
            //debug using
            for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
            {
                mEmotionBrainArrayListData.get(i).print();
            }
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] data emotion parse ERROR: " + e.toString());
        }
    }
    
    
    private class EmotionElement
    {
        public String emotionID = null;
        public float emotionValue = -1;
        
        public EmotionElement(@NonNull String emotionID, float emotionValue)
        {
            this.emotionID = emotionID;
            this.emotionValue = emotionValue;
        }
        
        public void print()
        {
            Logs.showTrace("[InterruptLogicHandler][EmotionElement] EmotionID: " + emotionID +
                    " value: " + String.valueOf(emotionValue));
        }
    }
    
    private class EmotionBrainElement
    {
        public int emotionID = -1;
        public String emotionName = null;
        public String emotionMappingImageName = null;
        public int id = -1;
        public String dataType = null;
        
        public int priority = -1;
        public String triggerValue = null;
        public int triggerTime = -1;
        public ArrayList<JSONObject> emotionMappingTTS = null;
        public String emotionType = null;
        
        public EmotionBrainElement(int id, int emotionID, @NonNull String emotionName,
                @NonNull String emotionMappingImageName, @NonNull String dataType,
                int priority, int triggerTime, @NonNull String triggerValue,
                @NonNull String emotionType, ArrayList<JSONObject> emotionMappingTTS)
        {
            this.id = id;
            this.emotionID = emotionID;
            this.emotionName = emotionName;
            this.emotionMappingImageName = emotionMappingImageName;
            this.dataType = dataType;
            
            this.priority = priority;
            this.triggerTime = triggerTime;
            this.triggerValue = triggerValue;
            this.emotionType = emotionType;
            this.emotionMappingTTS = emotionMappingTTS;
            
            
        }
        
        public void print()
        {
            Logs.showTrace("*****************************************************");
            Logs.showTrace("[EmotionBrainElement] emotion_id: " + String.valueOf(emotionID));
            Logs.showTrace("[EmotionBrainElement] img_name: " + emotionMappingImageName);
            Logs.showTrace("[EmotionBrainElement] emotion_name: " + emotionName);
            Logs.showTrace("[EmotionBrainElement] priority: " + priority);
            Logs.showTrace("[EmotionBrainElement] trigger_time: " + triggerTime);
            Logs.showTrace("[EmotionBrainElement] trigger_value: " + triggerValue);
            Logs.showTrace("[EmotionBrainElement] emotion_type: " + emotionType);
            if (null != emotionMappingTTS)
            {
                for (int i = 0; i < emotionMappingTTS.size(); i++)
                {
                    Logs.showTrace("[EmotionBrainElement] emotionMappingTTS " +
                            String.valueOf(i) + " :" + emotionMappingTTS.get(i));
                }
            }
            Logs.showTrace("*****************************************************");
        }
    }
    
}
