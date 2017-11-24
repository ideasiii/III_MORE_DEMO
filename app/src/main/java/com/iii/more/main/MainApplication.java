package com.iii.more.main;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.iii.more.cmp.semantic.SemanticDeviceID;
import com.iii.more.cockpit.CockpitService;
import com.iii.more.cockpit.InternetCockpitService;
import com.iii.more.cockpit.OtgCockpitService;
import com.iii.more.emotion.EmotionHandler;
import com.iii.more.emotion.EmotionParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptHandler;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tracker.Tracker;
import sdk.ideas.common.CtrlType;
import sdk.ideas.tool.speech.tts.TextToSpeechHandler;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by joe on 2017/10/30
 */

public class MainApplication extends Application
{
    private final InClassHandler mInClassHandler = new InClassHandler(this);
    
    private CockpitService mCockpitService;
    private InternetCockpitService mInternetCockpitService;
    private OtgCockpitService mOtgCockpitService;
    
    private Tracker mTracker = new Tracker(this);
    private TextToSpeechHandler mTtsHandler = new TextToSpeechHandler(this);
    
    private CockpitConnectionEventListener mCockpitConnectionEventListener;
    private CockpitSensorEventListener mCockpitSensorEventListener;
    private CockpitFilmMakingEventListener mCockpitFilmMakingEventListener;
    private FaceEmotionEventListener mFaceEmotionEventListener = null;
    private TTSEventListener mTtsEventListener;
    
    // this logic handler does not handle emotion logic
    private InterruptLogicHandler mInterruptLogicHandler = new InterruptLogicHandler(this);
    
    private FaceEmotionInterruptHandler mFaceEmotionInterruptHandler = new FaceEmotionInterruptHandler(this);
    private EmotionHandler mEmotionHandler = null;
    private static boolean isFaceEmotionStart = false;
    
    public MainApplication()
    {
    }
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        initCockpitService();
        initInterruptLogic();
        initFaceEmotionInterrupt();
        initTTS();
    }
    
    /**
     * 取得章魚 or 使用者的名字
     */
    public String getName(String id)
    {
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        return prefs.getString(id, "");
    }
    
    /**
     * 設定章魚 or 使用者的名字
     */
    public void setName(String id, String name)
    {
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(id, name);
        editor.apply();
    }
    
    public void setCockpitConnectionEventListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitConnectionEventListener()");
        mCockpitConnectionEventListener = l;
    }
    
    public void setCockpitSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitSensorEventListener()");
        mCockpitSensorEventListener = l;
    }
    
    public void setCockpitFilmMakingEventListener(CockpitFilmMakingEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitFilmMakingEventListener()");
        mCockpitFilmMakingEventListener = l;
    }
    
    public void setFaceEmotionEventListener(FaceEmotionEventListener l)
    {
        Logs.showTrace("[MainApplication] setEmotionEventListener()");
        mFaceEmotionEventListener = l;
    }
    
    public void setTTSEventListener(TTSEventListener l)
    {
        Logs.showTrace("[MainApplication] setTTSEventListener()");
        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            System.out.println(ste);
        }
        
        mTtsEventListener = l;
    }
    
    public void startFaceEmotion()
    {
        if (null == mEmotionHandler)
        {
            mEmotionHandler = new EmotionHandler(this);
            mEmotionHandler.setHandler(mInClassHandler);
            mEmotionHandler.init();
        }
        
        if (!isFaceEmotionStart)
        {
            mEmotionHandler.start();
            isFaceEmotionStart = true;
        }
    }
    
    public void stopFaceEmotion()
    {
        if (null != mEmotionHandler)
        {
            if (isFaceEmotionStart)
            {
                mEmotionHandler.stop();
                isFaceEmotionStart = false;
            }
        }
    }
    
    public void startTracker()
    {
        mTracker.setHandler(mInClassHandler);
        mTracker.startTracker(Parameters.TRACKER_APP_ID);
    }
    
    public void sendToTracker(HashMap<String, String> data)
    {
        mTracker.track(data);
    }
    
    /**
     * 初始化 TTS 服務
     */
    public void initTTS()
    {
        mTtsHandler.setHandler(mInClassHandler);
        mTtsHandler.init();
    }
    
    /**
     * 設定 TTS pitch & speech rate
     */
    public void setTTSPitch(float fpitch, float frate)
    {
        mTtsHandler.setPitch(fpitch);
        mTtsHandler.setSpeechRate(frate);
    }
    
    /**
     * 設定 TTS 的輸出語言
     */
    public void setTTSLanguage(Locale language)
    {
        mTtsHandler.setLocale(language);
    }
    
    /**
     * 取得 TTS 的輸出語言
     */
    public Locale getTTSLanguage()
    {
        return mTtsHandler.getLocale();
    }
    
    /**
     * 將 text 轉為語音輸出
     */
    public void playTTS(String text, String textId)
    {
        Logs.showTrace("[MainApplication] playTTS()");
        mTtsHandler.textToSpeech(text, textId);
    }
    
    /**
     * 停止正在進行的 TTS
     */
    public void stopTTS()
    {
        Logs.showTrace("[MainApplication] stopTTS()");
        mTtsHandler.stop();
    }
    
    private void initCockpitService()
    {
        CockpitService.startThenBindService(this, InternetCockpitService.class, mCockpitServiceConnection,
                null);
        CockpitService.startThenBindService(this, OtgCockpitService.class, mCockpitServiceConnection, null);
    }
    
    /**
     * 初始化 FaceEmotionInterruptHandler 處理 face emotion 邏輯的部分
     */
    private void initFaceEmotionInterrupt()
    {
        String interruptEmotionBehaviorDataArrayInput = "";
        try
        {
            SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
            String message = prefs.getString(Parameters.TASK_COMPOSER_DATA, "non-json text");
            JSONObject tmp = new JSONObject(message);
            
            JSONObject rules = tmp.getJSONObject("rules");
            interruptEmotionBehaviorDataArrayInput = rules.getJSONArray("emotion").toString();
            Logs.showTrace("[MainApplication] Use SharedPreferences for interrupt logic behavior");
        }
        catch (JSONException e)
        {
            Logs.showError("[MainApplication] Use fallback input for interrupt logic behavior");
            interruptEmotionBehaviorDataArrayInput = Parameters
                    .INTERRUPT_EMOTION_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT;
        }
        mFaceEmotionInterruptHandler.setInterruptEmotionLogicBehaviorDataArray
                (interruptEmotionBehaviorDataArrayInput);
        mFaceEmotionInterruptHandler.setHandler(mInClassHandler);
    }
    
    /**
     * 初始化 InterruptLogicHandler 處理 sensor 邏輯的部分
     */
    private void initInterruptLogic()
    {
        String interruptLogicBehaviorDataArrayInput;
        
        try
        {
            SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
            String message = prefs.getString(Parameters.TASK_COMPOSER_DATA, "non-json text");
            JSONObject tmp = new JSONObject(message);
            
            JSONObject rules = tmp.getJSONObject("rules");
            interruptLogicBehaviorDataArrayInput = rules.getJSONArray("action").toString();
            Logs.showTrace("[MainApplication] Use SharedPreferences for interrupt logic behavior");
        }
        catch (JSONException e)
        {
            Logs.showError("[MainApplication] Use fallback input for interrupt logic behavior");
            interruptLogicBehaviorDataArrayInput = Parameters
                    .INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT;
        }
        
        mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(interruptLogicBehaviorDataArrayInput);
        mInterruptLogicHandler.setHandler(mInClassHandler);
    }
    
    /**
     * CockpitService 的 ServiceConnection
     */
    private final ServiceConnection mCockpitServiceConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Logs.showTrace("[MainApplication] onServiceConnected()");
            
            mCockpitService = ((CockpitService.Binder) service).getService();
            
            if (mCockpitService instanceof InternetCockpitService)
            {
                mInternetCockpitService = (InternetCockpitService) mCockpitService;
                mInternetCockpitService.setDeviceId(SemanticDeviceID.getDeviceID(getApplicationContext()));
                mInternetCockpitService.setServerAddress(Parameters.INTERNET_COCKPIT_SERVER_ADDRESS);
            }
            else if (mCockpitService instanceof OtgCockpitService)
            {
                mOtgCockpitService = (OtgCockpitService) mCockpitService;
            }
            
            mCockpitService.setHandler(mInClassHandler);
            mCockpitService.connect();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Logs.showTrace("[MainApplication] onServiceDisconnected()");
            
            if (mCockpitService instanceof InternetCockpitService)
            {
                mInternetCockpitService.setHandler(null);
                mInternetCockpitService = null;
            }
            else if (mCockpitService instanceof OtgCockpitService)
            {
                mOtgCockpitService.setHandler(null);
                mOtgCockpitService = null;
            }
            
            
            if (mCockpitService != null)
            {
                mCockpitService.setHandler(null);
                mCockpitService = null;
            }
        }
    };
    
    private static final class InClassHandler extends Handler
    {
        private final WeakReference<MainApplication> mWeakSelf;
        
        private InClassHandler(MainApplication app)
        {
            mWeakSelf = new WeakReference<>(app);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            MainApplication app = mWeakSelf.get();
            if (app == null)
            {
                Logs.showTrace("[MainApplication] [InClassHandler] WeakReference is null");
                return;
            }
            
            switch (msg.what)
            {
                case CockpitService.MSG_WHAT:
                    app.handleCockpitServiceMessage(msg);
                    break;
                case InterruptLogicParameters.CLASS_INTERRUPT_LOGIC:
                    app.handleInterruptLogicMessage(msg);
                    break;
                case EmotionParameters.CLASS_EMOTION:
                    app.handleMessageFaceEmotionMessage(msg);
                    break;
                case CtrlType.MSG_RESPONSE_TRACKER_HANDLER:
                    app.handleTrackerMessage(msg);
                    break;
                case FaceEmotionInterruptParameters.CLASS_FACE_EMOTION_INTERRUPT:
                    app.handleMessageFaceEmotionInterruptMessage(msg);
                    break;
                case CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER:
                    app.handleTTSMessage(msg);
                    break;
                default:
                    Logs.showTrace("[MainApplication] [InClassHandler] unhandled msg.what: " + msg.what);
            }
        }
    }
    
    //
    //### pending to write
    private void handleMessageFaceEmotionInterruptMessage(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        Logs.showTrace("[MainApplication] FaceEmotionInterruptMessage: " + message);
        
        if (null != mFaceEmotionEventListener)
        {
            HashMap<String, String> ttsHashMap = null;
            HashMap<String, String> imgHashMap = null;
            if (message.containsKey(FaceEmotionInterruptParameters.STRING_TTS_TEXT))
            {
                ttsHashMap = new HashMap<>();
                ttsHashMap.put(FaceEmotionInterruptParameters.STRING_TTS_TEXT, message.get
                        (FaceEmotionInterruptParameters.STRING_TTS_TEXT));
                ttsHashMap.put(FaceEmotionInterruptParameters.STRING_TTS_PITCH, message.get
                        (FaceEmotionInterruptParameters.STRING_TTS_PITCH));
                ttsHashMap.put(FaceEmotionInterruptParameters.STRING_TTS_SPEED, message.get
                        (FaceEmotionInterruptParameters.STRING_TTS_SPEED));
                message.remove(FaceEmotionInterruptParameters.STRING_TTS_TEXT);
                message.remove(FaceEmotionInterruptParameters.STRING_TTS_PITCH);
                message.remove(FaceEmotionInterruptParameters.STRING_TTS_SPEED);
                
                Logs.showTrace("[MainApplication] tts: " + ttsHashMap.get(FaceEmotionInterruptParameters
                        .STRING_TTS_TEXT));
            }
            if (message.containsKey(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME))
            {
                imgHashMap = new HashMap<>();
                imgHashMap.put(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME, message.get
                        (FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME));
                message.remove(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME);
            }
            mFaceEmotionEventListener.onFaceEmotionResult(message, ttsHashMap, imgHashMap, null);
            
        }
    }
    
    private void handleMessageFaceEmotionMessage(Message msg)
    {
        HashMap<String, String> emotionHashMap = (HashMap<String, String>) msg.obj;
        Logs.showTrace("[MainApplication] emotionHashMap:" + emotionHashMap);
        
        try
        {
            if (!emotionHashMap.get(EmotionParameters.STRING_EXPRESSION_ATTENTION).equals("-1"))
            {
                HashMap<String, String> trackFaceEmotionData = new HashMap<>();
                trackFaceEmotionData.put("Source", "1");
                trackFaceEmotionData.put("Description", "data from face emotion recognition SDK");
                JSONObject emotionJsonObj = new JSONObject(emotionHashMap);
                trackFaceEmotionData.put("Value", emotionJsonObj.toString());
                
                //debug using
                Logs.showTrace("[MainApplication] send tracker Data: " + trackFaceEmotionData);
                //sendToTracker(trackFaceEmotionData);
            }
        }
        catch (Exception e)
        {
            Logs.showError("[MainApplication] send tracker while ERROR:" + e.toString());
        }
        
        if (null != mFaceEmotionEventListener)
        {
            if (msg.arg2 == EmotionParameters.METHOD_EMOTION_DETECT)
            {
                mFaceEmotionInterruptHandler.setEmotionEventData((HashMap<String, String>) msg.obj);
            }
            
        }
    }
    
    /**
     * 處理來自 InterruptLogicHandler 的事件
     */
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
                String trigger_result = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
                Logs.showTrace("[MainApplication] handleInterruptLogicMessage() " + "trigger_result = " +
                        trigger_result);
                
                switch (trigger_result)
                {
                    case "握手":
                        mCockpitSensorEventListener.onShakeHands(null);
                        break;
                    case "拍手":
                        mCockpitSensorEventListener.onClapHands(null);
                        break;
                    case "擠壓":
                        mCockpitSensorEventListener.onPinchCheeks(null);
                        break;
                    case "拍頭":
                        mCockpitSensorEventListener.onPatHead(null);
                        break;
                    case "RFID":
                        // TODO remove or change this quick and dirty code
                        String reading = message.get(InterruptLogicParameters.JSON_STRING_TAG);
                        mCockpitSensorEventListener.onScannedRfid(null, reading);
                        break;
                    default:
                        Logs.showTrace("[MainApplication] handleInterruptLogicMessage() unknown " +
                                "trigger_result: " + trigger_result);
                }
                break;
            default:
                Logs.showTrace("[MainApplication] handleInterruptLogicMessage() unknown msg.arg2: " + msg
                        .arg2);
        }
    }
    
    /**
     * 處理來自 Tracker 的事件
     */
    private void handleTrackerMessage(Message msg)
    {
        Logs.showTrace("[MainApplication] handleTrackerMessage()");
        
        int result = msg.arg1;
        int from = msg.arg2;
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        Logs.showTrace("[MainApplication] handleTrackerMessage() " + "Result: " + result + " From: " + from
                + " Message: " + message);
    }
    
    /**
     * 處理來自 CockpitService 的事件
     */
    private void handleCockpitServiceMessage(Message msg)
    {
        if (msg.arg1 == CockpitService.EVENT_DATA_TEXT)
        {
            // plain text from cockpit
            String data = (String) msg.obj;
            Logs.showTrace("[MainApplication] handleCockpitServiceMessage() onData(), data=`" + data + "`");
            
            if (null != mInterruptLogicHandler)
            {
                mInterruptLogicHandler.setDeviceEventData(data);
                mInterruptLogicHandler.startEventDataAnalysis();
            }
            
            return;
        }
        else if (msg.arg1 == CockpitService.EVENT_DATA_FILM_MAKING)
        {
            // film making commands from cockpit
            JSONObject j = (JSONObject) msg.obj;
            
            if (null != mCockpitFilmMakingEventListener)
            {
                try
                {
                    String action = j.getString("action");
                    String text = j.getString("text");
                    Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " + "film making " +
                            "action" + " = `" + action + "`, text = `" + text + "`");
                    
                    if (action.equals("tts"))
                    {
                        String language = j.getString("language");
                        mCockpitFilmMakingEventListener.onTTS(null, text, language);
                    }
                    else if (action.equals("showFaceImage"))
                    {
                        mCockpitFilmMakingEventListener.onEmotionImage(null, text);
                    }
                    else
                    {
                        Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " + "film making "
                                + "unknown action = `" + action);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            
            return;
        }
        
        if (null == mCockpitConnectionEventListener)
        {
            return;
        }
        
        // connection events
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
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() unhandled msg.arg1: " + msg
                        .arg1);
        }
    }
    
    /**
     * 處理來自 TextToSpeechHandler 的事件
     */
    private void handleTTSMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case ResponseCode.ERR_SUCCESS:
                analysisTTSResponse((HashMap<String, String>) msg.obj);
                break;
            case ResponseCode.ERR_NOT_INIT:
                Logs.showError("TTS service method called before initialization");
                break;
            case ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION:
                //deal with not found Google TTS Exception
                //mTtsHandler.downloadTTS();
                
                //deal with ACCESSIBILITY page can not open Exception
                //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(intent, 0);
                if (null != mTtsEventListener)
                {
                    String message = ((HashMap<String, String>) msg.obj).get("message");
                    mTtsEventListener.onInitFailed(msg.arg1, message);
                }
                break;
            case ResponseCode.ERR_UNKNOWN:
                Logs.showError("[MainApplication] handleTTSMessage() unknown error occured");
                if (null != mTtsEventListener)
                {
                    String message = ((HashMap<String, String>) msg.obj).get("message");
                    mTtsEventListener.onInitFailed(msg.arg1, message);
                }
                break;
            default:
                Logs.showError("[MainApplication] handleTTSMessage() unknown msg.arg2: " + msg.arg2);
        }
        
    }
    
    /**
     * 處理來自 TextToSpeechHandler 的事件
     */
    private void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            String utteranceId = message.get("TextID");
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            boolean textStatusStart = message.get("TextStatus").equals("START");
            
            if (textStatusDone)
            {
                if (null != mTtsEventListener)
                {
                    mTtsEventListener.onUtteranceDone(utteranceId);
                }
            }
            else if (textStatusStart)
            {
                if (null != mTtsEventListener)
                {
                    mTtsEventListener.onUtteranceStart(utteranceId);
                }
            }
        }
        else if (message.get("message").equals("init success"))
        {
            if (null != mTtsEventListener)
            {
                mTtsEventListener.onInitSuccess();
            }
        }
    }
}
