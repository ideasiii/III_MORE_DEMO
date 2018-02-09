package com.iii.more.main;

import android.content.Context;
import android.os.Message;

import com.iii.more.cockpit.CockpitService;
import com.iii.more.cockpit.InternetCockpitService;
import com.iii.more.cockpit.OtgCockpitService;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;
import com.iii.more.main.listeners.CockpitConnectionEventListener;
import com.iii.more.main.listeners.CockpitFilmMakingEventListener;
import com.iii.more.main.listeners.CockpitSensorEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import sdk.ideas.common.Logs;

/**
 * 將 CockpitService 丟來的訊息盡量委託給內部的 listener 處理
 * 如果必須丟回給 MainApplication 處理，則使用 TellMeWhatToDo 介面的方法委託出去
 */
class CockpitListenerBridge
{
    private CockpitConnectionEventListener mCockpitConnectionEventListener;
    private CockpitSensorEventListener mCockpitSensorEventListener;
    private CockpitFilmMakingEventListener mCockpitFilmMakingEventListener;

    private boolean mPlaySoundOnRfidScanned = true;
    private boolean mPlaySoundOnSensorEvent = true;
    private boolean mUseBloodySensorEventSound = false;

    private Context mContext;

    // 判斷 sensor 傳來的數值組合是否要觸發特殊事件的 handler
    InterruptLogicHandler sensorInterruptLogicHandler;

    private TellMeWhatToDo mTellMeWhatToDo;

    /** 必須要丟回給 MainApplication 處理的訊息都放在這裡 */
    interface TellMeWhatToDo
    {
        /** 當收到要假造偵測到臉部情緒的指令時的 callback */
        void onFaceEmotionDetected(String emotionName, int score);

        /** 當需要設定參數時的 callback */
        void onSetParameter(String action);

        /** 當需要從 Activity (from) 跳到 Activity (to) 時的 callback*/
        void onJumpActivity(String from, String to);
    }

    CockpitListenerBridge(Context context)
    {
        mContext = context;
        sensorInterruptLogicHandler = new InterruptLogicHandler(context);
    }

    void setConnectionListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("setCockpitConnectionEventListener()");
        mCockpitConnectionEventListener = l;
    }

    void setSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("setSensorEventListener()");
        mCockpitSensorEventListener = l;
    }

    void setFilmMakingEventListener(CockpitFilmMakingEventListener l)
    {
        Logs.showTrace("setFilmMakingEventListener()");
        mCockpitFilmMakingEventListener = l;
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

    /** 處理來自 CockpitService 的訊息 */
    private void handleCockpitServiceMessage(Message msg)
    {
        switch (msg.arg2)
        {
            case CockpitService.EVENT_DATA_TEXT:
                String text = (String) msg.obj;
                Logs.showTrace("handleCockpitServiceMessage() plain text = `" + text + "`");

                Class<? extends CockpitService> sender = findOriginCockpit(msg);
                if (sender == null)
                {
                    Logs.showTrace("handleCockpitServiceMessage() unknown sender: " + msg.arg1);
                    return;
                }

                if (sensorInterruptLogicHandler != null)
                {
                    Map<String, String> ilhResp = sensorInterruptLogicHandler.eventDataAnalysisSync(text);
                    if (ilhResp != null)
                    {
                        delegateInterruptLogicResponse(sender, ilhResp);
                    }
                }
                break;
            case CockpitService.EVENT_DATA_FACE_EMOTION:
                if (mTellMeWhatToDo != null)
                {
                    JSONObject j = (JSONObject) msg.obj;
                    try
                    {
                        String emotionName = j.getString("emotionName");
                        int score = j.getInt("score");
                        mTellMeWhatToDo.onFaceEmotionDetected(emotionName, score);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case CockpitService.EVENT_DATA_FILM_MAKING:
                handleCockpitServiceFilmMakingEvents(msg);
                break;
            case CockpitService.EVENT_DATA_PARAMETERS:
                handleCockpitServiceParameterEvents(msg);
                break;
            case CockpitService.EVENT_JUMP_ACTIVITY:
                if (mTellMeWhatToDo != null)
                {
                    JSONObject j = (JSONObject) msg.obj;
                    try
                    {
                        String f = j.getString("from");
                        String t = j.getString("to");
                        mTellMeWhatToDo.onJumpActivity(f, t);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                handleCockpitServiceConnectionEvents(msg);
        }
    }

    /** 處理來自 InterruptLogicHandler 的事件 */
    private void handleInterruptLogicMessage(Message msg)
    {
        switch (msg.arg2)
        {
            case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                // 非同步的 event 沒辦法知道這是哪個子 CockpitService 的輸入所導出的分析結果
                HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                delegateInterruptLogicResponse(CockpitService.class, message);
                break;
            default:
                Logs.showTrace("handleInterruptLogicMessage() unknown msg.arg2: " + msg.arg2);
        }
    }

    /** 試著將 InterruptLogicHandler 傳來的回應委託給外部註冊者 */
    private void delegateInterruptLogicResponse(Class<? extends CockpitService> sender, Map<String, String> ilhResp)
    {
        if (null == mCockpitSensorEventListener)
        {
            Logs.showTrace("delegateInterruptLogicResponse() mCockpitSensorEventListener == null");
            return;
        }

        String triggerResult = ilhResp.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
        Logs.showTrace("delegateInterruptLogicResponse() trigger_result = " + triggerResult);

        switch (triggerResult)
        {
            case "握手":
                playSensorEventSound();
                mCockpitSensorEventListener.onShakeHands(sender);
                break;
            case "拍手":
                playSensorEventSound();
                mCockpitSensorEventListener.onClapHands(sender);
                break;
            case "擠壓":
                playSensorEventSound();
                mCockpitSensorEventListener.onPinchCheeks(sender);
                break;
            case "拍頭":
                playSensorEventSound();
                mCockpitSensorEventListener.onPatHead(sender);
                break;
            case "RFID":
                playRfidEventSound();
                String tag = ilhResp.get(InterruptLogicParameters.JSON_STRING_TAG);
                mCockpitSensorEventListener.onScannedRfid(sender, tag);
                break;
            default:
                Logs.showTrace("delegateInterruptLogicResponse() unknown triggerResult: " + triggerResult);
        }
    }

    /** 處理 CockpitService 與參數設定有關的事件 */
    private void handleCockpitServiceParameterEvents(Message msg)
    {
        try
        {
            JSONObject j = (JSONObject) msg.obj;

            String action = j.getString("action");
            String text = j.getString("text");
            Logs.showTrace("handleCockpitServiceMessage() " +
                "parameter action = `" + action + "`, text = `" + text + "`");

            switch (action)
            {
                case "toggleRfidScannedSound":
                    mPlaySoundOnRfidScanned = !mPlaySoundOnRfidScanned;
                    break;
                case "toggleSensorEventTriggeredSound":
                    mPlaySoundOnSensorEvent = !mPlaySoundOnSensorEvent;
                    break;
                case "switchShakeHandSound":
                    mUseBloodySensorEventSound = !mUseBloodySensorEventSound;
                    break;
                default:
                    if (mTellMeWhatToDo != null)
                    {
                        mTellMeWhatToDo.onSetParameter(action);
                    }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /** 處理 CockpitService 與影片製作有關的事件 */
    private void handleCockpitServiceFilmMakingEvents(Message msg)
    {
        if (null == mCockpitFilmMakingEventListener)
        {
            return;
        }

        Class<? extends CockpitService> sender = findOriginCockpit(msg);
        if (sender == null)
        {
            Logs.showTrace("handleCockpitServiceFilmMakingEvents() unknown sender: " + msg.arg1);
            return;
        }

        try
        {
            JSONObject j = (JSONObject) msg.obj;

            String action = j.getString("action");
            String text = j.getString("text");
            Logs.showTrace("handleCockpitServiceFilmMakingEvents() " +
                    "film making action = `" + action + "`, text = `" + text + "`");

            switch (action)
            {
                case "tts":
                    String language = j.getString("language");
                    mCockpitFilmMakingEventListener.onTTS(sender, text, language);
                    break;
                case "showFaceImage":
                    mCockpitFilmMakingEventListener.onEmotionImage(sender, text);
                    break;
                default:
                    Logs.showTrace("handleCockpitServiceFilmMakingEvents() " +
                            "film making unknown action = `" + action);
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /** 處理 CockpitService 與連線狀態有關的事件 */
    private void handleCockpitServiceConnectionEvents(Message msg)
    {
        if (null == mCockpitConnectionEventListener)
        {
            return;
        }

        Class<? extends CockpitService> sender = findOriginCockpit(msg);
        if (sender == null)
        {
            Logs.showTrace("handleCockpitServiceConnectionEvents() unknown sender: " + msg.arg1);
            return;
        }

        switch (msg.arg1)
        {
            case CockpitService.EVENT_NO_DEVICE:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onNoDevice()");
                mCockpitConnectionEventListener.onNoDevice(sender);
                break;
            case CockpitService.EVENT_READY:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onReady()");
                mCockpitConnectionEventListener.onReady(sender);
                break;
            case CockpitService.EVENT_PROTOCOL_NOT_SUPPORTED:
            case CockpitService.EVENT_CDC_DRIVER_NOT_WORKING:
            case CockpitService.EVENT_USB_DEVICE_NOT_WORKING:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onProtocolNotSupported()");
                mCockpitConnectionEventListener.onProtocolNotSupported(sender);
                break;
            case CockpitService.EVENT_PERMISSION_GRANTED:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onPermissionGranted()");
                mCockpitConnectionEventListener.onPermissionGranted(sender);
                break;
            case CockpitService.EVENT_PERMISSION_NOT_GRANTED:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onPermissionNotGranted()");
                mCockpitConnectionEventListener.onPermissionNotGranted(sender);
                break;
            case CockpitService.EVENT_DISCONNECTED:
                Logs.showTrace("handleCockpitServiceConnectionEvents() onDisconnected()");
                mCockpitConnectionEventListener.onDisconnected(sender);
                break;
            default:
                Logs.showTrace("handleCockpitServiceConnectionEvents() unhandled msg.arg1: " + msg.arg1);
        }
    }

    /** 找出 msg 是由哪個 CockpitService 發出 */
    private Class<? extends CockpitService> findOriginCockpit(Message msg)
    {
        if (msg == null)
        {
            return null;
        }

        switch (msg.arg1)
        {
            case OtgCockpitService.MSG_ARG1:
                return OtgCockpitService.class;
            case InternetCockpitService.MSG_ARG1:
                return InternetCockpitService.class;
        }

        return CockpitService.class;
    }

    /** 播放 RFID 偵測事件音效 */
    private void playRfidEventSound()
    {
        MainApplication app = Tools.getApp(mContext);
        app.replaySoundEffect(R.raw.rfid_scanned);
}

    /** 播放 sensor 事件音效 */
    private void playSensorEventSound()
    {
        if (!mPlaySoundOnSensorEvent)
        {
            return;
        }

        MainApplication app = Tools.getApp(mContext);

        if (mUseBloodySensorEventSound)
        {
            app.replaySoundEffect(R.raw.shake_hand_bloody);
        }
        else
        {
            app.replaySoundEffect(R.raw.shake_hand);
        }
    }
}
