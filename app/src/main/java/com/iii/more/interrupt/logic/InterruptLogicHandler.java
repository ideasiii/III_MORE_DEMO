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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/9/22.
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
                Logs.showError("[InterruptLogicHandler] data parse ERROR: " + e.toString());
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
        if (null != mEventData && mEventData.length() > 0)
        {
            HashMap<String, String> eventHashMapData = convertToHashMapData(mEventData);
            Logs.showTrace("[InterruptLogicHandler] Get event HashMap: " + eventHashMapData);
            
            
            HashMap<String, String> result = logicJudgement(eventHashMapData);
            if (null != result)
            {
                //debugging using
                Logs.showTrace("[InterruptLogicHandler] result TRIGGER_RESULT:" +
                        result.get(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT));
                Logs.showTrace("[InterruptLogicHandler] result DESCRIPTION:" +
                        result.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION));
                
                //callback
                callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                        InterruptLogicParameters.METHOD_LOGIC_RESPONSE, makeDisplayJson(result));
                
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
    
    
    private HashMap<String, String> logicJudgement(HashMap<String, String> eventHashMapData)
    {
        HashMap<String, String> result = null;
        if (null != eventHashMapData && null != mLogicBrainArrayListData)
        {
            
            //this loop is
            //mLogicBrainHashMapData maybe use array list to sort priority
            for (int i = 0; i < mLogicBrainArrayListData.size(); i++)
            {
                LogicBrainElement judgeBrainElement = mLogicBrainArrayListData.get(i);
                int count = 0;
                
                //debugging using
                Logs.showTrace("[InterruptLogicHandler] now check actionPriority: " + String.valueOf(i + 1));
                
                for (int j = 0; j < judgeBrainElement.sensors.size(); j++)
                {
                    //debugging using
                    Logs.showTrace("[InterruptLogicHandler] now check sensor: " + judgeBrainElement.sensors.get(j));
                    String strValue = eventHashMapData.get(judgeBrainElement.sensors.get(j));
                    if (null != strValue)
                    {
                        Double value = Double.valueOf(strValue);
                        
                        //debugging using
                        Logs.showTrace("[InterruptLogicHandler] sensor: " + judgeBrainElement.sensors.get(j) + " get value: " + String.valueOf(value));
                        
                        //FSR1 & FSR2 沒有擠壓情況下會有時會大於 0 需補正 100.0
                        if (judgeBrainElement.sensors.get(j).equals("FSR1") || judgeBrainElement.sensors.get(j).equals("FSR2"))
                        {
                            if (value > 100.0)
                            {
                                count++;
                            }
                            
                        }
                        //開燈補正　H需大於255
                        else if (judgeBrainElement.sensors.get(j).equals("H"))
                        {
                            if (value > 256.0)
                            {
                                count++;
                            }
                        }
                        else if (value > 0.0)
                        {
                            count++;
                        }
                    }
                }
                if (count == judgeBrainElement.triggerRule)
                {
                    result = new HashMap<>();
                    result.put(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT, judgeBrainElement.triggerResult);
                    result.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, judgeBrainElement.description);
                    break;
                }
            }
        }
        
        
        return result;
    }
    
    //f1[0],f2[0],C[0],D[0],H[255],FSR1[0],FSR2[0],X[0],Y[0],Z[0]
    private HashMap<String, String> convertToHashMapData(String data)
    {
        HashMap<String, String> hashMapData = new HashMap<>();
        
        if (data.contains(InterruptLogicParameters.STRING_RFID))
        {
            Pattern patterns = Pattern.compile(InterruptLogicParameters.PATTERN_EVENT_RFID_DATA);
            Matcher matcher = patterns.matcher(data);
            if (matcher.find())
            {
                hashMapData.put(InterruptLogicParameters.STRING_RFID, matcher.group(1));
            }
        }
        else
        {
            try
            {
                Pattern patterns = Pattern.compile(InterruptLogicParameters.PATTERN_EVENT_DATA);
                Matcher matcher = patterns.matcher(data);
                HashMap<Integer, String> datas = new HashMap<>();
                if (matcher.find())
                {
                    Logs.showTrace("[InterruptLogicHandler] matcher group!");
                    for (int i = 1; i < matcher.groupCount(); i++)
                    {
                        datas.put(i, matcher.group(i));
                        Logs.showTrace("[InterruptLogicHandler]matcher.group(" + String.valueOf(i) + "): " + matcher.group(i));
                    }
                }
                hashMapData.put(InterruptLogicParameters.STRING_F1, datas.get(InterruptLogicParameters.INT_F1));
                hashMapData.put(InterruptLogicParameters.STRING_F2, datas.get(InterruptLogicParameters.INT_F2));
                hashMapData.put(InterruptLogicParameters.STRING_C, datas.get(InterruptLogicParameters.INT_C));
                hashMapData.put(InterruptLogicParameters.STRING_D, datas.get(InterruptLogicParameters.INT_D));
                hashMapData.put(InterruptLogicParameters.STRING_H, datas.get(InterruptLogicParameters.INT_H));
                hashMapData.put(InterruptLogicParameters.STRING_FSR1, datas.get(InterruptLogicParameters.INT_FSR1));
                hashMapData.put(InterruptLogicParameters.STRING_FSR2, datas.get(InterruptLogicParameters.INT_FSR2));
                
            }
            catch (Exception e)
            {
                Logs.showError("[MainActivity] data broken!");
                hashMapData = null;
            }
        }
        
        return hashMapData;
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
