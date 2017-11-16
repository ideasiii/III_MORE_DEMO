package com.iii.more.interrupt.logic;

import android.content.Context;
import android.support.annotation.NonNull;

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
 * Created by joe on 2017/9/22.
 */

/**
 * ### pending to write
 */

public class InterruptLogicHandler extends BaseHandler
{
    private JSONArray mInterruptLogicBehaviorDataArray = null;
    private JSONArray mInterruptEmotionBehaviorDataArray = null;
    private String mEventData = "";
    private HashMap<String, String> mEmotionHashMapData = null;
    
    
    private ArrayList<LogicBrainElement> mLogicBrainArrayListData = null;
    private ArrayList<EmotionBrainElement> mEmotionBrainArrayListData = null;


    public InterruptLogicHandler(Context context)
    {
        super(context);
        try
        {
            mInterruptLogicBehaviorDataArray = new JSONArray(InterruptLogicParameters.DEFAULT_LOGIC_BEHAVIOR_DATA);
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] default maybe ERROR: " + e.toString());
        }
        mLogicBrainArrayListData = new ArrayList<>();
        mEmotionBrainArrayListData = new ArrayList<>();
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
                if (jsonEmotionBrainElement.has(InterruptLogicParameters.JSON_STRING_EMOTION_ID) &&
                        jsonEmotionBrainElement.has(InterruptLogicParameters.JSON_STRING_DATA_TYPE) &&
                        jsonEmotionBrainElement.has(InterruptLogicParameters.JSON_STRING_EMOTION_NAME) &&
                        jsonEmotionBrainElement.has(InterruptLogicParameters.JSON_STRING_EMOTION_MAPPING_IMAGE_NAME) &&
                        jsonEmotionBrainElement.has(InterruptLogicParameters.JSON_STRING_ID))
                {
                    mEmotionBrainArrayListData.add(new EmotionBrainElement(jsonEmotionBrainElement.getInt(InterruptLogicParameters.JSON_STRING_ID),
                            jsonEmotionBrainElement.getInt(InterruptLogicParameters.JSON_STRING_EMOTION_ID),
                            jsonEmotionBrainElement.getString(InterruptLogicParameters.JSON_STRING_EMOTION_NAME),
                            jsonEmotionBrainElement.getString(InterruptLogicParameters.JSON_STRING_EMOTION_MAPPING_IMAGE_NAME),
                            jsonEmotionBrainElement.getString(InterruptLogicParameters.JSON_STRING_DATA_TYPE)));
                }
            }

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

    public void setInterruptLogicBehaviorDataArray(@NonNull String logicBehavior)
    {
        Logs.showTrace("[InterruptLogicHandler] set logic behavior data: " + logicBehavior);
        try
        {
            mInterruptLogicBehaviorDataArray = new JSONArray(logicBehavior);
            mLogicBrainArrayListData.clear();
            try
            {
                for (int i = 0; i < mInterruptLogicBehaviorDataArray.length(); i++)
                {
                    JSONObject jsonBrainElement = (JSONObject) mInterruptLogicBehaviorDataArray.get(i);
                    if (jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_ACTION_PRIORITY)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_SENSORS)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_DESCRIPTION)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TAG)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TRIGGER_RULE)
                            && jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_VALUE))
                    {
                        JSONArray jsonSensorElement = jsonBrainElement.getJSONArray(InterruptLogicParameters.JSON_STRING_SENSORS);
                        ArrayList<String> sensorData = new ArrayList<>();
                        for (int j = 0; j < jsonSensorElement.length(); j++)
                        {
                            sensorData.add((String) jsonSensorElement.get(j));
                        }
                        
                        mLogicBrainArrayListData.add(new LogicBrainElement(sensorData, jsonBrainElement.getInt(InterruptLogicParameters.JSON_STRING_TRIGGER_RULE),
                                jsonBrainElement.getInt(InterruptLogicParameters.JSON_STRING_ACTION_PRIORITY),
                                jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_TAG),
                                jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT),
                                jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_VALUE),
                                jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_DESCRIPTION)));
                    }
                    else
                    {
                        Logs.showError("[InterruptLogicHandler] data parse ERROR: some data LOST");
                    }
                }

                //for debugging use
                /*Logs.showTrace("[InterruptLogicHandler] unsorted mLogicBrainArrayListData####");
                for (int i = 0; i < mLogicBrainArrayListData.size(); i++)
                {
                    mLogicBrainArrayListData.get(i).print();
                }*/
                
                //sort by action priority
                Collections.sort(mLogicBrainArrayListData,
                        new Comparator<LogicBrainElement>()
                        {
                            @Override
                            public int compare(LogicBrainElement o1, LogicBrainElement o2)
                            {
                                return o1.getActionPriority() - o2.getActionPriority();
                            }
                        });

                //for debugging use
                Logs.showTrace("[InterruptLogicHandler] sorted mLogicBrainArrayListData===");
                for (int i = 0; i < mLogicBrainArrayListData.size(); i++)
                {
                    mLogicBrainArrayListData.get(i).print();
                }
            }
            catch (JSONException e)
            {
                Logs.showError("[InterruptLogicHandler] data Logic parse ERROR: " + e.toString());
            }
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] set Interrupt Logic Behavior ERROR: " + logicBehavior);
        }
    }
    
    public void setEmotionEventData(@NonNull HashMap<String, String> emotionEventData)
    {
        Logs.showTrace("[InterruptLogicHandler] emotion event data: " + emotionEventData);
        mEmotionHashMapData = emotionEventData;
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
        
        for (int i = 0; i < mEmotionBrainArrayListData.size(); i++)
        {
            String strValue = emotionHashMapData.get(mEmotionBrainArrayListData.get(i).emotionName);
            if (null != strValue)
            {
                try
                {
                    float value = Float.valueOf(strValue);
                    data.add(new EmotionElement(mEmotionBrainArrayListData.get(i).emotionName, value));
                }
                catch (Exception e)
                {
                    Logs.showError("[InterruptLogicHandler] covertEmotionHashMapDataToArrayList ERROR:" + e.toString());
                }
                
            }
            
            
        }
        return data;
        
        
    }
    
    public void setDeviceEventData(@NonNull String deviceEventData)
    {
        Logs.showTrace("[InterruptLogicHandler] device event data: " + deviceEventData);
        mEventData = deviceEventData;
    }
    
    public void startEventDataAnalysis()
    {
        if (null == mEventData || mEventData.length() < 1)
        {
            Logs.showTrace("[InterruptLogicHandler] startEventDataAnalysis() no mEventData");
            return;
        }

        HashMap<String, String> eventHashMapData = convertToHashMapData(mEventData);
        if (null == eventHashMapData)
        {
            Logs.showTrace("[InterruptLogicHandler] startEventDataAnalysis() no eventHashMapData");
            return;
        }

        Logs.showTrace("[InterruptLogicHandler] Get event HashMap: " + eventHashMapData);

        HashMap<String, String> result = logicJudgement(eventHashMapData);
        if (null != result)
        {
            Logs.showTrace("[InterruptLogicHandler] result TRIGGER_RESULT:" +
                    result.get(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT));
            Logs.showTrace("[InterruptLogicHandler] result DESCRIPTION:" +
                    result.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION));

            callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                    InterruptLogicParameters.METHOD_LOGIC_RESPONSE, makeDisplayJson(result));
        }
        else if (eventHashMapData.containsKey(InterruptLogicParameters.STRING_RFID))
        {
            // TODO RFID is not written in trigger rules, check separately
            String rfidValue = eventHashMapData.get(InterruptLogicParameters.STRING_RFID);
            int rfidInteger = Integer.parseInt(rfidValue);

            if (0 != rfidInteger)
            {
                result = new HashMap<>();
                result.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, "RFID");
                Logs.showTrace("[InterruptLogicHandler] RFID data@@: " + rfidValue);
                result.put(InterruptLogicParameters.JSON_STRING_TAG, rfidValue);

                callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                        InterruptLogicParameters.METHOD_LOGIC_RESPONSE, result);
            }
        }
    }
    
    private HashMap<String, String> makeDisplayJson(HashMap<String, String> inputHashMap)
    {
        HashMap<String, String> message = new HashMap<>();
        message.put(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT, inputHashMap.get(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT));
        message.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, inputHashMap.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION));

        JSONObject animate = new JSONObject();
        try
        {
            animate.put("type", 2);
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
            data.put("file", inputHashMap.get(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT));
            JSONArray show = new JSONArray();
            show.put(data);
            
            JSONObject display = new JSONObject();
            display.put("enable", 1);
            display.put("show", show);
            message.put("display", display.toString());
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] makeDisplayJson ERROR" + e.toString());
        }

        return message;
    }

    private HashMap<String, String> makeEmotionDisplayJson(String emotionID, String emotionMappingImageID)
    {
        HashMap<String, String> message = new HashMap<>();
        message.put(InterruptLogicParameters.JSON_STRING_EMOTION_NAME, emotionID);
        message.put(InterruptLogicParameters.JSON_STRING_EMOTION_MAPPING_IMAGE_NAME, emotionMappingImageID);
        
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

    private HashMap<String, String> logicJudgement(HashMap<String, String> eventHashMapData)
    {
        if (null == eventHashMapData || null == mLogicBrainArrayListData)
        {
            return null;
        }

        for (int i = 0; i < mLogicBrainArrayListData.size(); i++)
        {
            LogicBrainElement judgeBrainElement = mLogicBrainArrayListData.get(i);
            int triggerCount = 0;

            //Logs.showTrace("[InterruptLogicHandler] checking actionPriority: " + String.valueOf(i + 1));

            for (int j = 0; j < judgeBrainElement.sensors.size(); j++)
            {
                String targetSensorName = judgeBrainElement.sensors.get(j);
                Logs.showTrace("[InterruptLogicHandler] checking sensor: " + targetSensorName);

                String strValue = eventHashMapData.get(targetSensorName);
                if (null != strValue)
                {
                    Double value = Double.valueOf(strValue);
                    //Logs.showTrace("[InterruptLogicHandler] sensor: " + targetSensorName + " get value: " + String.valueOf(value));

                    if (isSensorValueAboveTriggeringThreshold(targetSensorName, value))
                    {
                        triggerCount++;
                    }
                }
            }

            if (triggerCount == judgeBrainElement.triggerRule)
            {
                // trigger rule matched
                HashMap<String, String> result = new HashMap<>();
                result.put(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT, judgeBrainElement.triggerResult);
                result.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, judgeBrainElement.description);
                return result;
            }
        }

        // no matched rules
        return null;
    }
    
    // data example: {"model":001,"s_bright":6,"s_head":[1,0,0,0],"s_cheek":[0,0],"s_rfid":0000039183}
    private HashMap<String, String> convertToHashMapData(String data)
    {
        try
        {
            HashMap<String, String> hashMapData = new HashMap<>();
            JSONObject json = new JSONObject(data);
            
            if (json.has("s_hand"))
            {
                JSONArray handReadings = json.getJSONArray("s_hand");
                if (handReadings.length() >= 4)
                {
                    hashMapData.put(InterruptLogicParameters.STRING_F1, Integer.toString(handReadings.getInt(0)));
                    hashMapData.put(InterruptLogicParameters.STRING_F2, Integer.toString(handReadings.getInt(1)));
                    hashMapData.put(InterruptLogicParameters.STRING_C, Integer.toString(handReadings.getInt(2)));
                    hashMapData.put(InterruptLogicParameters.STRING_D, Integer.toString(handReadings.getInt(3)));
                }
            }
            
            if (json.has("s_cheek"))
            {
                JSONArray cheekReadings = json.getJSONArray("s_cheek");
                if (cheekReadings.length() >= 2)
                {
                    hashMapData.put(InterruptLogicParameters.STRING_FSR1, Integer.toString(cheekReadings.getInt(0)));
                    hashMapData.put(InterruptLogicParameters.STRING_FSR2, Integer.toString(cheekReadings.getInt(1)));
                }
            }
            
            if (json.has("s_bright"))
            {
                int brightReading = json.getInt("s_bright");
                hashMapData.put(InterruptLogicParameters.STRING_H, Integer.toString(brightReading));
            }
            
            if (json.has("s_rfid"))
            {
                int rfidRead = json.getInt("s_rfid");
                hashMapData.put(InterruptLogicParameters.STRING_RFID, Integer.toString(rfidRead));
            }

            return hashMapData;
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] data broken!");
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isSensorValueAboveTriggeringThreshold(String sensorName, double value)
    {
        //FSR1 & FSR2 (臉頰) 沒有擠壓情況下會有時會大於 0，需去雜訊
        if (sensorName.equals("FSR1") || sensorName.equals("FSR2"))
        {
            return value >= InterruptLogicParameters.SENSOR_CHEEK_TRIGGER_THRESHOLD;
        }

        //開燈補正　H需大於255
        if (sensorName.equals("H"))
        {
            return value >= InterruptLogicParameters.SENSOR_AMBIENT_LIGHT_TRIGGER_THRESHOLD;
        }

        return value >= InterruptLogicParameters.SENSOR_GENERAL_TRIGGER_THRESHOLD;
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
        
        public EmotionBrainElement(int id, int emotionID, @NonNull String emotionName,
                @NonNull String emotionMappingImageName, @NonNull String dataType)
        {
            this.id = id;
            this.emotionID = emotionID;
            this.emotionName = emotionName;
            this.emotionMappingImageName = emotionMappingImageName;
            this.dataType = dataType;
        }
        
        public void print()
        {
            Logs.showTrace("***********************");
            Logs.showTrace("[EmotionBrainElement] emotion_id: " + String.valueOf(emotionID));
            Logs.showTrace("[EmotionBrainElement] img_name: " + emotionMappingImageName);
            Logs.showTrace("[EmotionBrainElement] data_type: " + dataType);
            Logs.showTrace("[EmotionBrainElement] id: " + String.valueOf(id));
            Logs.showTrace("[EmotionBrainElement] emotion_name: " + emotionName);
            Logs.showTrace("***********************");
        }
    }

    private class LogicBrainElement
    {
        public ArrayList<String> sensors = null;
        public int triggerRule = 0;
        public int actionPriority = 0;
        public String tag = null;
        public String triggerResult = null;
        public String value = null;
        public String description = null;
        
        public LogicBrainElement(@NonNull ArrayList<String> sensors, int triggerRule, int actionPriority,
                @NonNull String tag, @NonNull String triggerResult, @NonNull String value,
                @NonNull String description)
        {
            this.sensors = sensors;
            this.triggerRule = triggerRule;
            this.actionPriority = actionPriority;
            this.tag = tag;
            this.triggerResult = triggerResult;
            this.value = value;
            this.description = description;
        }
        
        public int getActionPriority()
        {
            return actionPriority;
        }
        
        public void print()
        {
            Logs.showTrace("***********************");
            Logs.showTrace("[LogicBrainElement] description: " + description);
            Logs.showTrace("[LogicBrainElement] actionPriority: " + String.valueOf(actionPriority));
            Logs.showTrace("[LogicBrainElement] tag: " + tag);
            Logs.showTrace("[LogicBrainElement] triggerResult: " + triggerResult);
            Logs.showTrace("[LogicBrainElement] triggerRule: " + String.valueOf(triggerRule));
            Logs.showTrace("[LogicBrainElement] sensors: " + sensors);
            Logs.showTrace("***********************");
        }
    }
    
}
