package com.iii.more.main;

import android.content.Context;
import android.os.Message;

import com.iii.more.cockpit.CockpitService;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;

/**
 * 將 CockpitService 丟來的訊息盡量丟給內部的 listener 處理
 * 如果必須丟回給 MainApplication 處理，則使用 TellMeWhatToDo 介面的方法委託出去
 */
class CockpitListenerBridge
{
    CockpitConnectionEventListener cockpitConnectionEventListener;
    CockpitSensorEventListener cockpitSensorEventListener;
    CockpitFilmMakingEventListener cockpitFilmMakingEventListener;

    // 判斷 sensor 傳來的數值組合是否要觸發特殊事件的 handler
    InterruptLogicHandler sensorInterruptLogicHandler;

    private TellMeWhatToDo mTellMeWhatToDo;

    /** 必須要丟回給 MainApplication 處理的訊息都放在這裡 */
    interface TellMeWhatToDo
    {
        // 當收到要假造偵測到臉部情緒的指令時的 callback
        void onFaceEmotionDetected(String emotionName);
    }

    CockpitListenerBridge(Context context)
    {
        sensorInterruptLogicHandler = new InterruptLogicHandler(context);
    }

    void setEventDelegate(TellMeWhatToDo d)
    {
        mTellMeWhatToDo = d;
    }

    void handleMessage(Message msg)
    {
        switch (msg.what)
        {
            case CockpitService.MSG_WHAT:
                handleCockpitServiceMessage(msg);
                break;
            case InterruptLogicParameters.CLASS_INTERRUPT_LOGIC:
                handleInterruptLogicMessage(msg);
                break;
            default:
                Logs.showTrace("handleMessage() unknown msg.what: " + msg.what);
        }
    }

    /**
     * 處理來自 CockpitService 的訊息
     */
    private void handleCockpitServiceMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case CockpitService.EVENT_DATA_TEXT:
                String text = (String) msg.obj;
                Logs.showTrace("handleCockpitServiceMessage() plain text, text = `" + text + "`");

                if (null != sensorInterruptLogicHandler)
                {
                    sensorInterruptLogicHandler.setDeviceEventData(text);
                    sensorInterruptLogicHandler.startEventDataAnalysis();
                }
                break;
            case CockpitService.EVENT_DATA_FACE_EMOTION:
                if (null != mTellMeWhatToDo)
                {
                    String emotionName = (String) msg.obj;
                    mTellMeWhatToDo.onFaceEmotionDetected(emotionName);
                }
                break;
            case CockpitService.EVENT_DATA_FILM_MAKING:
                handleCockpitServiceFilmMakingEvents(msg);
                break;
            default:
                handleCockpitServiceConnectionEvents(msg);
        }
    }

    /**
     * 處理來自 InterruptLogicHandler 的事件
     */
    private void handleInterruptLogicMessage(Message msg)
    {
        if (null == cockpitSensorEventListener)
        {
            return;
        }

        HashMap<String, String> message = (HashMap<String, String>) msg.obj;

        switch (msg.arg2)
        {
            case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                String trigger_result = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
                Logs.showTrace("[MainApplication] handleInterruptLogicMessage() " +
                        "trigger_result = " + trigger_result);

                switch (trigger_result)
                {
                    case "握手":
                        cockpitSensorEventListener.onShakeHands(null);
                        break;
                    case "拍手":
                        cockpitSensorEventListener.onClapHands(null);
                        break;
                    case "擠壓":
                        cockpitSensorEventListener.onPinchCheeks(null);
                        break;
                    case "拍頭":
                        cockpitSensorEventListener.onPatHead(null);
                        break;
                    case "RFID":
                        // TODO remove or change this quick and dirty code
                        String reading = message.get(InterruptLogicParameters.JSON_STRING_TAG);
                        cockpitSensorEventListener.onScannedRfid(null, reading);
                        break;
                    default:
                        Logs.showTrace("handleInterruptLogicMessage() unknown trigger_result: " + trigger_result);
                }
                break;
            default:
                Logs.showTrace("handleInterruptLogicMessage() unknown msg.arg2: " + msg.arg2);
        }
    }

    /**
     * 處理 CockpitService 與影片製作有關的事件
     */
    private void handleCockpitServiceFilmMakingEvents(Message msg)
    {
        if (null == cockpitFilmMakingEventListener)
        {
            return;
        }

        try
        {
            JSONObject j = (JSONObject) msg.obj;

            String action = j.getString("action");
            String text = j.getString("text");
            Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " +
                    "film making action = `" + action + "`, text = `" + text + "`");

            switch (action)
            {
                case "tts":
                    String language = j.getString("language");
                    cockpitFilmMakingEventListener.onTTS(null, text, language);
                    break;
                case "showFaceImage":
                    cockpitFilmMakingEventListener.onEmotionImage(null, text);
                    break;
                default:
                    Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " +
                            "film making unknown action = `" + action);
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 處理 CockpitService 與連線狀態有關的事件
     */
    private void handleCockpitServiceConnectionEvents(Message msg)
    {
        if (null == cockpitConnectionEventListener)
        {
            return;
        }

        switch (msg.arg1)
        {
            case CockpitService.EVENT_NO_DEVICE:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onNoDevice()");
                cockpitConnectionEventListener.onNoDevice(null);
                break;
            case CockpitService.EVENT_READY:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onReady()");
                cockpitConnectionEventListener.onReady(null);
                break;
            case CockpitService.EVENT_PROTOCOL_NOT_SUPPORTED:
            case CockpitService.EVENT_CDC_DRIVER_NOT_WORKING:
            case CockpitService.EVENT_USB_DEVICE_NOT_WORKING:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onProtocolNotSupported()");
                cockpitConnectionEventListener.onProtocolNotSupported(null);
                break;
            case CockpitService.EVENT_PERMISSION_GRANTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onPermissionGranted()");
                cockpitConnectionEventListener.onPermissionGranted(null);
                break;
            case CockpitService.EVENT_PERMISSION_NOT_GRANTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onPermissionNotGranted()");
                cockpitConnectionEventListener.onPermissionNotGranted(null);
                break;
            case CockpitService.EVENT_DISCONNECTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onDisconnected()");
                cockpitConnectionEventListener.onDisconnected(null);
                break;
            default:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() unhandled msg.arg1: " + msg.arg1);
        }
    }
}
