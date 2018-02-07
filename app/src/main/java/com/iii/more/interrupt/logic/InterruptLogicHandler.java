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
 * Created by joe on 2017/9/22
 */

public class InterruptLogicHandler extends BaseHandler
{
    private JSONArray mInterruptRules = null;
    //private String mEventData = "";
    private ArrayList<InterruptRule> mLogicBrainArrayListData = null;

    public InterruptLogicHandler(Context context)
    {
        super(context);

        try
        {
            mInterruptRules = new JSONArray(InterruptLogicParameters.DEFAULT_LOGIC_BEHAVIOR_DATA);
        }
        catch (JSONException e)
        {
            Logs.showError("[InterruptLogicHandler] default maybe ERROR: " + e.toString());
        }

        mLogicBrainArrayListData = new ArrayList<>();
    }

    public void refillInterruptRules(@NonNull String src)
    {
        Logs.showTrace("[InterruptLogicHandler] set logic behavior data: " + src);

        try
        {
            mInterruptRules = new JSONArray(src);
            mLogicBrainArrayListData.clear();

            for (int i = 0; i < mInterruptRules.length(); i++)
            {
                JSONObject jsonBrainElement = (JSONObject) mInterruptRules.get(i);
                if (!jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_ACTION_PRIORITY)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_SENSORS)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_DESCRIPTION)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TAG)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_TRIGGER_RULE)
                    || !jsonBrainElement.has(InterruptLogicParameters.JSON_STRING_VALUE))
                {
                    Logs.showError("[InterruptLogicHandler] data parse ERROR: some data LOST");
                    continue;
                }

                JSONArray jsonSensorElement = jsonBrainElement.getJSONArray(InterruptLogicParameters.JSON_STRING_SENSORS);
                ArrayList<String> sensorData = new ArrayList<>();
                for (int j = 0; j < jsonSensorElement.length(); j++)
                {
                    sensorData.add((String) jsonSensorElement.get(j));
                }

                InterruptRule newElement = new InterruptRule(sensorData,
                    jsonBrainElement.getInt(InterruptLogicParameters.JSON_STRING_TRIGGER_RULE),
                    jsonBrainElement.getInt(InterruptLogicParameters.JSON_STRING_ACTION_PRIORITY),
                    jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_TAG),
                    jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT),
                    jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_VALUE),
                    jsonBrainElement.getString(InterruptLogicParameters.JSON_STRING_DESCRIPTION));
                mLogicBrainArrayListData.add(newElement);
            }

            // sort by action priority
            Collections.sort(mLogicBrainArrayListData,
                    new Comparator<InterruptRule>()
                    {
                        @Override
                        public int compare(InterruptRule o1, InterruptRule o2)
                        {
                            return o1.getActionPriority() - o2.getActionPriority();
                        }
                    });

            // for debugging
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

    public void startEventDataAnalysis(@NonNull String deviceEventData)
    {
        HashMap<String, String> res = eventDataAnalysisSync(deviceEventData);
        if (res != null)
        {
            callBackMessage(ResponseCode.ERR_SUCCESS, InterruptLogicParameters.CLASS_INTERRUPT_LOGIC,
                InterruptLogicParameters.METHOD_LOGIC_RESPONSE, res);
        }
    }

    /**
     * Synchronous version of startEventDataAnalysis() which returns result immediately,
     * without going through android.os.Handler
     */
    public HashMap<String, String> eventDataAnalysisSync(@NonNull String deviceEventData)
    {
        Logs.showTrace("[InterruptLogicHandler] device event data: " + deviceEventData);

        if (deviceEventData.length() < 1)
        {
            Logs.showTrace("[InterruptLogicHandler] startEventDataAnalysis() no mEventData");
            return null;
        }

        HashMap<String, String> eventHashMapData = convertToHashMapData(deviceEventData);
        HashMap<String, String> result;

        if (null == eventHashMapData)
        {
            Logs.showTrace("[InterruptLogicHandler] startEventDataAnalysis() no eventHashMapData");
            return null;
        }
        else if (eventHashMapData.containsKey(InterruptLogicParameters.STRING_RFID))
        {
            // RFID tag is not included in Task Composer rules, just return raw data
            String rfidValue = eventHashMapData.get(InterruptLogicParameters.STRING_RFID);
            Long rfidInteger = Long.parseLong(rfidValue);

            // why not "0".equals(rfidValue)?
            // because we cannot predict whether the zero value from sensor is written in "0" or "0000000000"
            if (0 != rfidInteger)
            {
                Logs.showTrace("[InterruptLogicHandler] intercepted reading from RFID sensor," +
                    "skip calling logicJudgement()");

                result = new HashMap<>();
                result.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, "RFID");
                Logs.showTrace("[InterruptLogicHandler] RFID data@@: " + rfidValue);
                result.put(InterruptLogicParameters.JSON_STRING_TAG, rfidValue);

                return result;
            }
        }

        Logs.showTrace("[InterruptLogicHandler] Get event HashMap: " + eventHashMapData);

        result = logicJudgement(eventHashMapData);
        if (null != result)
        {
            Logs.showTrace("[InterruptLogicHandler] result TRIGGER_RESULT:" +
                result.get(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT));
            Logs.showTrace("[InterruptLogicHandler] result DESCRIPTION:" +
                result.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION));

            return makeDisplayJson(result);
        }

        return null;
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
            data.put("color", "#6d94d5");
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
            InterruptRule judgeBrainElement = mLogicBrainArrayListData.get(i);
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

            if (triggerCount == judgeBrainElement.triggerCount)
            {
                // trigger rule matched
                HashMap<String, String> result = new HashMap<>();
                result.put(InterruptLogicParameters.JSON_STRING_TRIGGER_RESULT, judgeBrainElement.triggerResult);
                result.put(InterruptLogicParameters.JSON_STRING_DESCRIPTION, judgeBrainElement.description);
                return result;
            }
        }

        // no match
        return null;
    }

    // data example: {"model":"001","s_hand":[0,29989,7686,0],"s_cheek":[0,0],"s_rfid":"2812938271","s_bright":74}
    // RFID tag 數字從 10 位數至 13 位數都有可能·所以必須用 Long 類型儲存
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
                    int f1 = convertToUint8(handReadings.getInt(0));
                    int f2 = convertToUint8(handReadings.getInt(1));
                    int c = convertToUint8(handReadings.getInt(2));
                    int d = convertToUint8(handReadings.getInt(3));

                    hashMapData.put(InterruptLogicParameters.STRING_F1, Integer.toString(f1));
                    hashMapData.put(InterruptLogicParameters.STRING_F2, Integer.toString(f2));
                    hashMapData.put(InterruptLogicParameters.STRING_C, Integer.toString(c));
                    hashMapData.put(InterruptLogicParameters.STRING_D, Integer.toString(d));
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
                hashMapData.put(InterruptLogicParameters.STRING_RFID, json.getString("s_rfid"));
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

    // ..... e.g., 舉例 val 是 -19755, 而我假設該數值其實應該是 uint8, 範圍是 0~65535
    // 那就將 -19755 這個數字轉化成 uint8 表示, 即 45781
    private static int convertToUint8(int val)
    {
        return 65535 - val + 1;
    }

    private static boolean isSensorValueAboveTriggeringThreshold(String sensorName, double value)
    {
        if (sensorName.equals("FSR1") || sensorName.equals("FSR2"))
        {
            return value >= InterruptLogicParameters.SENSOR_CHEEK_TRIGGER_THRESHOLD;
        }

        if (sensorName.equals("H"))
        {
            return value >= InterruptLogicParameters.SENSOR_AMBIENT_LIGHT_TRIGGER_THRESHOLD;
        }

        return value <= InterruptLogicParameters.SENSOR_HAND_TRIGGER_THRESHOLD;
    }


    private static class InterruptRule
    {
        ArrayList<String> sensors = null;
        int triggerCount = 0;
        int actionPriority = 0;
        String tag = null;
        String triggerResult = null;
        String value = null;
        String description = null;

        InterruptRule(@NonNull ArrayList<String> sensors, int triggerCount, int actionPriority,
                      @NonNull String tag, @NonNull String triggerResult, @NonNull String value,
                      @NonNull String description)
        {
            this.sensors = sensors;
            this.triggerCount = triggerCount;
            this.actionPriority = actionPriority;
            this.tag = tag;
            this.triggerResult = triggerResult;
            this.value = value;
            this.description = description;
        }

        int getActionPriority()
        {
            return actionPriority;
        }

        void print()
        {
            Logs.showTrace("***********************");
            Logs.showTrace("[InterruptRule] description: " + description);
            Logs.showTrace("[InterruptRule] actionPriority: " + String.valueOf(actionPriority));
            Logs.showTrace("[InterruptRule] tag: " + tag);
            Logs.showTrace("[InterruptRule] triggerResult: " + triggerResult);
            Logs.showTrace("[InterruptRule] triggerCount: " + String.valueOf(triggerCount));
            Logs.showTrace("[InterruptRule] sensors: " + sensors);
            Logs.showTrace("***********************");
        }
    }
}
