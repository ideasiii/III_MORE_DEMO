package com.iii.more.emotion.interrupt;

import android.content.Context;
import android.support.annotation.NonNull;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


public class FaceEmotionInterruptHandler extends BaseHandler
{
    private ArrayList<EmotionBrainElement> mEmotionBrainArrayListData = null;
    private HashMap<String, String> mEmotionHashMapData = null;
    
    private EmotionElement nowEmotionState = new EmotionElement(FaceEmotionInterruptParameters
        .STRING_NATURAL, FaceEmotionInterruptParameters.INT_NATURAL_RULE, 0);
    
    public FaceEmotionInterruptHandler(Context context)
    {
        super(context);
        mEmotionBrainArrayListData = new ArrayList<>();
    }
    
    
    public void setEmotionEventData(@NonNull HashMap<String, String> emotionEventData)
    {
        Logs.showTrace("[FaceEmotionInterruptHandler] emotion event data: " + emotionEventData);
        mEmotionHashMapData = emotionEventData;
        startEmotionEventDataAnalysis();
    }
    
    private void startEmotionEventDataAnalysis()
    {
        EmotionElement data = runFaceEmotionRule(mEmotionHashMapData);
        
        //debug using
        /*
        if (null != data)
        {
            //data.print();
        }
        else
        {
            //Logs.showTrace("[FaceEmotionInterruptHandler] No Rule for face emotion");
        }
        */
        judgeNowEmotionState(data);
    }
    
    
    private EmotionElement runFaceEmotionRule(@NonNull HashMap<String, String> emotionHashMapData)
    {
        EmotionElement data = null;
        
        for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
        {
            //debug using
            //Logs.showTrace("[FaceEmotionInterruptHandler] now check emotionName: " +
            //    mEmotionBrainArrayListData.get(i).emotionName);
            String strNowEmotionValue = emotionHashMapData.get(mEmotionBrainArrayListData.get(i).emotionName);
            if (null != strNowEmotionValue)
            {
                try
                {
                    if (strNowEmotionValue.equals(mEmotionBrainArrayListData.get(i).triggerValue) &&
                        mEmotionBrainArrayListData.get(i).emotionType.equals("EXPRESSION"))
                    {
                        //debug using
                        //Logs.showTrace("[FaceEmotionInterruptHandler] strNowEmotionValue: " +
                        //    strNowEmotionValue);
                        
                        data = new EmotionElement(mEmotionBrainArrayListData.get(i).emotionName,
                            mEmotionBrainArrayListData.get(i).triggerTime, -1);
                        data.emotionHashMapValue = emotionHashMapData;
                        
                    }
                    else if (mEmotionBrainArrayListData.get(i).emotionType.equals("EMOTION"))
                    {
                        
                        float floatNowEmotionValue = Float.valueOf(strNowEmotionValue);
                        float ruleEmotionValue = Float.valueOf(mEmotionBrainArrayListData.get(i)
                            .triggerValue);
                        if (floatNowEmotionValue >= ruleEmotionValue)
                        {
                            data = new EmotionElement(mEmotionBrainArrayListData.get(i).emotionName,
                                mEmotionBrainArrayListData.get(i).triggerTime, floatNowEmotionValue);
                            data.emotionHashMapValue = emotionHashMapData;
                            
                            break;
                        }
                        else
                        {
                            //debug using
                            //Logs.showError("[FaceEmotionInterruptHandler] runFaceEmotionRule ERROR:" + " " +
                            //    "floatNowEmotionValue is" + String.valueOf(floatNowEmotionValue) + " but " +
                             //   "ruleEmotionValue is" + String.valueOf(ruleEmotionValue));
                        }
                    }
                    
                }
                catch (Exception e)
                {
                    Logs.showError("[FaceEmotionInterruptHandler] runFaceEmotionRule ERROR:" + e.toString());
                }
            }
            else
            {
                //debug using
               // Logs.showError("[FaceEmotionInterruptHandler] runFaceEmotionRule ERROR while get " +
               //     "strNowEmotionValue is null");
            }
        }
        
        return data;
    }
    
    private JSONObject getEmotionTTS(ArrayList<JSONObject> ttsEmotionData, float emotionScore)
    {
        try
        {
            for (JSONObject ttsData : ttsEmotionData)
            {
                if (ttsData.has(FaceEmotionInterruptParameters.JSON_INTEGER_TTS_SCORE_RANGE_MIN) && ttsData
                    .has(FaceEmotionInterruptParameters.JSON_INTEGER_TTS_SCORE_RANGE_MAX))
                {
                    if (ttsData.getInt(FaceEmotionInterruptParameters.JSON_INTEGER_TTS_SCORE_RANGE_MIN) ==
                        -1 && ttsData.getInt(FaceEmotionInterruptParameters
                        .JSON_INTEGER_TTS_SCORE_RANGE_MAX) == -1)
                    {
                        break;
                    }
                    else if (emotionScore >= ttsData.getInt(FaceEmotionInterruptParameters
                        .JSON_INTEGER_TTS_SCORE_RANGE_MIN) && emotionScore <= ttsData.getInt
                        (FaceEmotionInterruptParameters.JSON_INTEGER_TTS_SCORE_RANGE_MAX))
                    {
                        return ttsData;
                    }
                }
                else
                {
                    break;
                }
            }
        }
        catch (Exception e)
        {
            Logs.showError("[FaceEmotionInterruptHandler] getEmotionTTS ERROR" + e.toString());
        }
        
        return ttsEmotionData.get(randomReturnNum(0, ttsEmotionData.size() - 1));
        
    }
    
    private int randomReturnNum(int min, int max)
    {
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }
    
    private synchronized void judgeNowEmotionState(EmotionElement newFaceData)
    {
        if (newFaceData == null)
        {
            //無符合規則，則將其臉部改為NATURAL值，並將reset trigger time
            nowEmotionState.emotionName = FaceEmotionInterruptParameters.STRING_NATURAL;
            nowEmotionState.emotionTriggerTime = 0;
            nowEmotionState.emotionTriggerTimeRule = FaceEmotionInterruptParameters.INT_NATURAL_RULE;
        }
        else
        {
            if (nowEmotionState.emotionName.equals(newFaceData.emotionName))
            {
                nowEmotionState.emotionTriggerTime++;
                
                nowEmotionState.emotionTriggerValue = newFaceData.emotionTriggerValue;
                
                if (nowEmotionState.emotionTriggerTime >= newFaceData.emotionTriggerTimeRule)
                {
                    //連續且觸發次數大於觸發規則
                    // Logs.showTrace("[FaceEmotionInterruptHandler] 觸發規則名稱: " +
                    //        nowEmotionState.emotionName + " 觸發次數: " + newFaceData.emotionTriggerTimeRule);
                    //callback to application
                    HashMap<String, String> message = new HashMap<>();
                    message.put(FaceEmotionInterruptParameters.STRING_EMOTION_NAME, nowEmotionState
                        .emotionName);
                    message.put(FaceEmotionInterruptParameters.STRING_EMOTION_VALUE, String.valueOf
                        (nowEmotionState.emotionTriggerValue));
                    
                    EmotionBrainElement emotionBrainElement = getEmotionBrainElementByEmotionName
                        (nowEmotionState.emotionName);
                    if (null != emotionBrainElement)
                    {
                        //debug using
                        //emotionBrainElement.print();
                        
                        //put all emotion value to message
                        for (String key : newFaceData.emotionHashMapValue.keySet())
                        {
                            message.put(key, newFaceData.emotionHashMapValue.get(key));
                        }
                        
                        
                        message.put(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME,
                            emotionBrainElement.emotionMappingImageName);
                        
                        if (emotionBrainElement.emotionMappingTTS.size() > 0)
                        {
                            
                            JSONObject ttsData = getEmotionTTS(emotionBrainElement.emotionMappingTTS,
                                nowEmotionState.emotionTriggerValue);
                            try
                            {
                                message.put(FaceEmotionInterruptParameters.STRING_TTS_TEXT, ttsData
                                    .getString("tts"));
                                message.put(FaceEmotionInterruptParameters.STRING_TTS_PITCH, ttsData
                                    .getString("pitch"));
                                message.put(FaceEmotionInterruptParameters.STRING_TTS_SPEED, ttsData
                                    .getString("speed"));
                            }
                            catch (JSONException e)
                            {
                                Logs.showError("[FaceEmotionInterruptHandler] get TTS RULE ERROR" + e
                                    .toString());
                            }
                            
                          
                        }
                        callBackMessage(ResponseCode.ERR_SUCCESS, FaceEmotionInterruptParameters
                            .CLASS_FACE_EMOTION_INTERRUPT, FaceEmotionInterruptParameters.METHOD_EVENT,
                            message);
                        
                        
                    }
                    
                    
                    //reset
                    nowEmotionState.emotionName = FaceEmotionInterruptParameters.STRING_NATURAL;
                    nowEmotionState.emotionTriggerTime = 0;
                }
            }
            else
            {
                //不連續 則將 nowEmotion data 設置成新的new face data
                nowEmotionState.emotionName = newFaceData.emotionName;
                //初始化trigger time
                nowEmotionState.emotionTriggerTime = 1;
            }
            
        }
    }
    
    
    private EmotionBrainElement getEmotionBrainElementByEmotionName(String emotionName)
    {
        for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
        {
            if (mEmotionBrainArrayListData.get(i).emotionName.equals(emotionName))
            {
                return mEmotionBrainArrayListData.get(i);
            }
            
        }
        
        return null;
        
    }
    
    
    public void setInterruptEmotionLogicBehaviorDataArray(@NonNull String emotionLogicBehavior)
    {
        //debug using
        //Logs.showTrace("[FaceEmotionInterruptHandler] set emotion logic behavior data: " +
        // emotionLogicBehavior);
        
        try
        {
            JSONArray mInterruptEmotionBehaviorDataArray = new JSONArray(emotionLogicBehavior);
            for (int i = 0; i < mInterruptEmotionBehaviorDataArray.length(); i++)
            {
                JSONObject jsonEmotionBrainElement = mInterruptEmotionBehaviorDataArray.getJSONObject(i);
                
                JSONArray contents = jsonEmotionBrainElement.getJSONArray(FaceEmotionInterruptParameters
                    .JSON_STRING_CONTENTS);
                
                ArrayList<JSONObject> contentArrayList = new ArrayList<>();
                
                for (int j = 0; j < contents.length(); j++)
                {
                    contentArrayList.add(contents.getJSONObject(j));
                }
                
                mEmotionBrainArrayListData.add(new EmotionBrainElement(jsonEmotionBrainElement.getInt
                    (FaceEmotionInterruptParameters.JSON_STRING_ID), jsonEmotionBrainElement.getInt
                    (FaceEmotionInterruptParameters.JSON_STRING_EMOTION_ID), jsonEmotionBrainElement
                    .getString(FaceEmotionInterruptParameters.JSON_STRING_EMOTION_NAME),
                    jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters
                        .JSON_STRING_EMOTION_MAPPING_IMAGE_NAME), jsonEmotionBrainElement.getString
                    (FaceEmotionInterruptParameters.JSON_STRING_DATA_TYPE), jsonEmotionBrainElement.getInt
                    (FaceEmotionInterruptParameters.JSON_STRING_PRIORITY), jsonEmotionBrainElement.getInt
                    (FaceEmotionInterruptParameters.JSON_STRING_TRIGGER_TIME), jsonEmotionBrainElement
                    .getString(FaceEmotionInterruptParameters.JSON_STRING_TRIGGER_VALUE),
                    jsonEmotionBrainElement.getString(FaceEmotionInterruptParameters
                        .JSON_STRING_EMOTION_TYPE), contentArrayList));
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
            Logs.showError("[FaceEmotionInterruptHandler] data emotion parse ERROR: " + e.toString());
        }
    }
    
    
    private class EmotionElement
    {
        String emotionName = null;
        int emotionTriggerTimeRule = 0;
        int emotionTriggerTime = 0;
        
        float emotionTriggerValue = 0;
        
        HashMap<String, String> emotionHashMapValue = new HashMap<>();
        
        EmotionElement(@NonNull String emotionName, int emotionTriggerTimeRule, float emotionTriggerValue)
        {
            this.emotionName = emotionName;
            this.emotionTriggerTimeRule = emotionTriggerTimeRule;
            this.emotionTriggerValue = emotionTriggerValue;
        }
        
        void print()
        {
            Logs.showTrace("[FaceEmotionInterruptHandler][EmotionElement] EmotionName: " + emotionName +
                "emotionTriggerTime: " + "" + String.valueOf(emotionTriggerTimeRule));
        }
    }
    
    private class EmotionBrainElement
    {
        int emotionID = -1;
        String emotionName = null;
        String emotionMappingImageName = null;
        int id = -1;
        String dataType = null;
        
        int priority = -1;
        String triggerValue = null;
        int triggerTime = -1;
        ArrayList<JSONObject> emotionMappingTTS = null;
        String emotionType = null;
        
        EmotionBrainElement(int id, int emotionID, @NonNull String emotionName, @NonNull String
            emotionMappingImageName, @NonNull String dataType, int priority, int triggerTime, @NonNull
            String triggerValue, @NonNull String emotionType, ArrayList<JSONObject> emotionMappingTTS)
        {
            this.id = id;
            this.emotionID = emotionID;
            this.emotionName = emotionName;
            this.emotionMappingImageName = emotionMappingImageName;
            this.dataType = dataType;
            
            this.priority = priority;
            
            if (FaceEmotionInterruptParameters.isDebugging)
            {
                if (emotionName.equals("ATTENTION"))
                {
                    this.triggerTime = FaceEmotionInterruptParameters.attentionTriggerTime;
                }
                else
                {
                    this.triggerTime = triggerTime;
                }
            }
            else
            {
                this.triggerTime = triggerTime;
            }
            this.triggerValue = triggerValue;
            this.emotionType = emotionType;
            this.emotionMappingTTS = emotionMappingTTS;
            
            
        }
        
        void print()
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
                    Logs.showTrace("[EmotionBrainElement] emotionMappingTTS (" + String.valueOf(i) + "): "
                        + emotionMappingTTS.get(i));
                }
            }
            Logs.showTrace("*****************************************************");
        }
    }
    
}
