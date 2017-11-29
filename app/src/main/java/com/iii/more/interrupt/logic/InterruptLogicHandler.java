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
   
    private String mEventData = "";
   
    private ArrayList<LogicBrainElement> mLogicBrainArrayListData = null;
    

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
            data.put("host", "https://ryejuice.sytes.net/edubot/OCTOBO_Expressions/");
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
