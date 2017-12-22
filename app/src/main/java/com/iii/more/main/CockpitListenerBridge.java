package com.iii.more.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Message;

import com.iii.more.cockpit.CockpitService;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;
import com.iii.more.main.listeners.CockpitConnectionEventListener;
import com.iii.more.main.listeners.CockpitFilmMakingEventListener;
import com.iii.more.main.listeners.CockpitSensorEventListener;

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
    private CockpitConnectionEventListener mCockpitConnectionEventListener;
    private CockpitSensorEventListener mCockpitSensorEventListener;
    private CockpitFilmMakingEventListener mCockpitFilmMakingEventListener;

    private MediaPlayer mRfidScannedSoundPlayer;
    private MediaPlayer mNormalSensorEventSoundPlayer;
    private MediaPlayer mBloodySensorEventSoundPlayer;

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
        void onFaceEmotionDetected(String emotionName);

        void onSetParameter(String action);
    }

    CockpitListenerBridge(Context context)
    {
        mContext = context;
        sensorInterruptLogicHandler = new InterruptLogicHandler(context);
    }

    void setConnectionListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitConnectionEventListener()");
        mCockpitConnectionEventListener = l;
    }

    void setSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] setSensorEventListener()");
        mCockpitSensorEventListener = l;
    }

    void setFilmMakingEventListener(CockpitFilmMakingEventListener l)
    {
        Logs.showTrace("[MainApplication] setFilmMakingEventListener()");
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
        switch (msg.arg1)
        {
            case CockpitService.EVENT_DATA_TEXT:
                String text = (String) msg.obj;
                Logs.showTrace("handleCockpitServiceMessage() plain text = `" + text + "`");

                if (null != sensorInterruptLogicHandler)
                {
                    sensorInterruptLogicHandler.startEventDataAnalysis(text);
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
            case CockpitService.EVENT_DATA_PARAMETERS:
                handleCockpitServiceParameterEvents(msg);
                break;
            default:
                handleCockpitServiceConnectionEvents(msg);
        }
    }

    /** 處理來自 InterruptLogicHandler 的事件 */
    private void handleInterruptLogicMessage(Message msg)
    {
        if (null == mCockpitSensorEventListener)
        {
            return;
        }

        HashMap<String, String> message = (HashMap<String, String>) msg.obj;

        switch (msg.arg2)
        {
            case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                String triggerResult = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
                Logs.showTrace("[MainApplication] handleInterruptLogicMessage() " +
                        "trigger_result = " + triggerResult);

                switch (triggerResult)
                {
                    case "握手":
                        playSensorEventSound();
                        mCockpitSensorEventListener.onShakeHands(null);
                        break;
                    case "拍手":
                        playSensorEventSound();
                        mCockpitSensorEventListener.onClapHands(null);
                        break;
                    case "擠壓":
                        playSensorEventSound();
                        mCockpitSensorEventListener.onPinchCheeks(null);
                        break;
                    case "拍頭":
                        playSensorEventSound();
                        mCockpitSensorEventListener.onPatHead(null);
                        break;
                    case "RFID":
                        playRfidEventSound();
                        String tag = message.get(InterruptLogicParameters.JSON_STRING_TAG);
                        mCockpitSensorEventListener.onScannedRfid(null, tag);
                        break;
                    default:
                        Logs.showTrace("handleInterruptLogicMessage() unknown triggerResult: " + triggerResult);
                }

                break;
            default:
                Logs.showTrace("handleInterruptLogicMessage() unknown msg.arg2: " + msg.arg2);
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
            Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " +
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
                    if (null != mTellMeWhatToDo)
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
                    mCockpitFilmMakingEventListener.onTTS(null, text, language);
                    break;
                case "showFaceImage":
                    mCockpitFilmMakingEventListener.onEmotionImage(null, text);
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

    /** 處理 CockpitService 與連線狀態有關的事件 */
    private void handleCockpitServiceConnectionEvents(Message msg)
    {
        if (null == mCockpitConnectionEventListener)
        {
            return;
        }

        switch (msg.arg1)
        {
            case CockpitService.EVENT_NO_DEVICE:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onNoDevice()");
                mCockpitConnectionEventListener.onNoDevice(null);
                break;
            case CockpitService.EVENT_READY:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onReady()");
                mCockpitConnectionEventListener.onReady(null);
                break;
            case CockpitService.EVENT_PROTOCOL_NOT_SUPPORTED:
            case CockpitService.EVENT_CDC_DRIVER_NOT_WORKING:
            case CockpitService.EVENT_USB_DEVICE_NOT_WORKING:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onProtocolNotSupported()");
                mCockpitConnectionEventListener.onProtocolNotSupported(null);
                break;
            case CockpitService.EVENT_PERMISSION_GRANTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onPermissionGranted()");
                mCockpitConnectionEventListener.onPermissionGranted(null);
                break;
            case CockpitService.EVENT_PERMISSION_NOT_GRANTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onPermissionNotGranted()");
                mCockpitConnectionEventListener.onPermissionNotGranted(null);
                break;
            case CockpitService.EVENT_DISCONNECTED:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onDisconnected()");
                mCockpitConnectionEventListener.onDisconnected(null);
                break;
            default:
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() unhandled msg.arg1: " + msg.arg1);
        }
    }

    /** 播放 RFID 偵測事件音效 */
    private void playRfidEventSound()
    {
        if (!mPlaySoundOnRfidScanned)
        {
            return;
        }

        if (mRfidScannedSoundPlayer == null)
        {
            mRfidScannedSoundPlayer = MediaPlayer.create(mContext, R.raw.rfid_scanned);
        }

        replayMediaPlayer(mRfidScannedSoundPlayer);
    }

    /** 播放 sensor 事件音效 */
    private void playSensorEventSound()
    {
        if (!mPlaySoundOnSensorEvent)
        {
            return;
        }

        if (mUseBloodySensorEventSound)
        {
            playR18SensorEventSound();
        }
        else
        {
            playNormalSensorEventSound();
        }
    }

    /** 播放一般的 sensor 事件音效 */
    private void playNormalSensorEventSound()
    {
        if (mNormalSensorEventSoundPlayer == null)
        {
            mNormalSensorEventSoundPlayer = MediaPlayer.create(mContext, R.raw.shake_hand);
        }

        replayMediaPlayer(mNormalSensorEventSoundPlayer);
    }

    /** 播放很奇怪的 sensor 事件音效 */
    private void playR18SensorEventSound()
    {
        if (mBloodySensorEventSoundPlayer == null)
        {
            mBloodySensorEventSoundPlayer = MediaPlayer.create(mContext, R.raw.shake_hand_bloody);
        }

        replayMediaPlayer(mBloodySensorEventSoundPlayer);
    }

    /** 將 MediaPlayer 重頭播放 */
    private static void replayMediaPlayer(MediaPlayer mp)
    {
        if (mp.isPlaying())
        {
            mp.seekTo(0);
        }
        else
        {
            mp.start();
        }
    }
}
