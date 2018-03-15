package com.iii.more.main;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
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
import com.iii.more.interrupt.logic.InterruptLogicParameters;
import com.iii.more.main.listeners.CockpitConnectionEventListener;
import com.iii.more.main.listeners.CockpitFilmMakingEventListener;
import com.iii.more.main.listeners.CockpitSensorEventListener;
import com.iii.more.main.listeners.FaceEmotionEventListener;
import com.iii.more.main.listeners.TTSEventListener;
import com.iii.more.main.secret.MagicBook;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.Logs;
import sdk.ideas.tracker.Tracker;
import sdk.ideas.common.CtrlType;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by joe on 2017/10/30
 */

public class MainApplication extends Application
{
    private final SelfHandler mSelfHandler = new SelfHandler(this);
    
    private CockpitService mCockpitService;
    private InternetCockpitService mInternetCockpitService;
    private OtgCockpitService mOtgCockpitService;
    private CockpitListenerBridge mCockpitListenerBridge = new CockpitListenerBridge(this);
    
    private TTSVoicePool mTTSVoicePool = new TTSVoicePool(this, TTSVoicePool.TTS_VOICE_CYBERON_KID_FEMALE);
    private TTSEventListenerBridge mTTSEventListenerBridge = new TTSEventListenerBridge(this);
    
    private FaceEmotionEventListener mFaceEmotionEventListener = null;
    private FaceEmotionInterruptHandler mFaceEmotionInterruptHandler = new FaceEmotionInterruptHandler(this);
    private EmotionHandler mEmotionHandler = null;
    private static boolean isFaceEmotionStart = false;
    
    private Tracker mTracker = new Tracker(this);
    private SoundEffectsPool mSoundEffectsPool = new SoundEffectsPool(this);
    
    // 方便讓遙控器控制端辨識的名稱
    private String mInternetCockpitFriendlyName;
    
    public MainApplication()
    {
    }
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        initCockpit();
        // initInterruptLogic();
        // initFaceEmotionInterrupt();
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
    
    public void setCockpitConnectionListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitConnectionEventListener()");
        mCockpitListenerBridge.setConnectionListener(l);
    }
    
    public void setCockpitSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] setSensorEventListener()");
        mCockpitListenerBridge.setSensorEventListener(l);
    }
    
    public void setCockpitFilmMakingEventListener(CockpitFilmMakingEventListener l)
    {
        Logs.showTrace("[MainApplication] setFilmMakingEventListener()");
        mCockpitListenerBridge.setFilmMakingEventListener(l);
    }
    
    public void setFaceEmotionEventListener(FaceEmotionEventListener l)
    {
        Logs.showTrace("[MainApplication] setEmotionEventListener()");
        mFaceEmotionEventListener = l;
    }
    
    public void setTTSEventListener(TTSEventListener l)
    {
        mTTSEventListenerBridge.setEventListener(l);
    }
    
    public void startFaceEmotion()
    {
        if (null == mEmotionHandler)
        {
            mEmotionHandler = new EmotionHandler(this);
            mEmotionHandler.setHandler(mSelfHandler);
            mEmotionHandler.init();
        }
        
        if (!isFaceEmotionStart)
        {
            mEmotionHandler.start();
            isFaceEmotionStart = true;
            Logs.showTrace("[MainApplication] startFaceEmotion Success");
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
                Logs.showTrace("[MainApplication] stopFaceEmotion Success");
            }
        }
    }
    
    /**
     *
     * @return
     * STRING_EMOTION_NA : 沒有臉見人
     * STRING_EMOTION_NEUTRAL : 情緒不知
     * 其他參數請看 EmotionParameters.java
     */
    public String getNowEmotionStateName()
    {
        if (null != mFaceEmotionInterruptHandler)
        {
            return mFaceEmotionInterruptHandler.getNowEmotionStateName();
        }
        return EmotionParameters.STRING_EMOTION_NA;
        
    }
    
    public void startTracker()
    {
        mTracker.setHandler(mSelfHandler);
        mTracker.startTracker(Parameters.TRACKER_APP_ID);
    }
    
    public void sendToTracker(HashMap<String, String> data)
    {
        mTracker.track(data);
    }
    
    public void sendToTrackerWithObjectMap(HashMap<String, Object> data)
    {
        mTracker.trackWithObjectMap(data);
    }
    
    /**
     * 初始化 TTS 服務
     */
    private void initTTS()
    {
        mTTSVoicePool.setHandler(mSelfHandler);
        mTTSVoicePool.init();
    }
    
    /**
     * 設定 TTS pitch & speech rate
     */
    public void setTTSPitch(float pitch, float rate)
    {
        mTTSVoicePool.setPitch(pitch, rate);
    }
    
    public void setTTSPitchCyberonScaling(int pitch, int rate)
    {
        mTTSVoicePool.setPitchCyberonScaling(pitch, rate);
    }
    
    /**
     * 設定 TTS 的輸出語言
     */
    public void setTTSLanguage(Locale language)
    {
        mTTSVoicePool.setLanguage(language);
    }
    
    /**
     * 取得 TTS 的輸出語言
     */
    public Locale getTTSLanguage()
    {
        return mTTSVoicePool.getLanguage();
    }
    
    /**
     * 將 text 轉為語音輸出
     */
    public void playTTS(String text, String textId)
    {
        mTTSVoicePool.speak(text, textId);
    }
    
    /**
     * 設定好 TTS pitch & speech rate 後再將 text 轉為語音輸出
     */
    public void playTTS(String text, String textId, float pitch, float rate)
    {
        setTTSPitch(pitch, rate);
        playTTS(text, textId);
    }
    
    /**
     * 停止正在進行的 TTS
     */
    public void stopTTS()
    {
        mTTSVoicePool.stop();
    }
    
    public void setVoice(byte voice)
    {
        //mTTSVoicePool.setVoice((byte) (voice % (TTSVoicePool.TTS_VOICE_SIZE+ 1)));
        mTTSVoicePool.setVoice(voice);
    }
    
    public void replaySoundEffect(final int resId)
    {
        mSoundEffectsPool.replay(resId);
    }
    
    /**
     * 取得要傳送給 InternetCockpit 伺服器的識別名稱
     */
    private void getInternetCockpitFriendlyName()
    {
        try
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mInternetCockpitFriendlyName = bluetoothAdapter.getName();
        }
        catch (Exception e)
        {
            Logs.showError("Cannot get bluetooth device name");
            mInternetCockpitFriendlyName = "N/A";
        }
        
        Logs.showTrace("use " + mInternetCockpitFriendlyName + " as friendly name in InternetCockpitService");
    }
    
    private String getTopActivityName()
    {
        final Activity top = MagicBook.getActivity();
        if (top == null)
        {
            return "N/A";
        }
        
        return top.getLocalClassName();
    }
    
    /**
     * 初始化布偶裝置連結
     */
    private void initCockpit()
    {
        getInternetCockpitFriendlyName();
        
        mCockpitListenerBridge.setEventDelegate(new CockpitListenerBridge.TellMeWhatToDo()
        {
            @Override
            public void onFaceEmotionDetected(String emotionName, int score)
            {
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " + "simulates when face "
                    + "emotion is detected, emotionName = `" + emotionName + "`, score: " + score);
                
                HashMap<String, String> faceEmotionEvent = MagicBook.cookFaceEmotionDetectedEvent
                    (mFaceEmotionInterruptHandler, emotionName, score);
                
                if (null != faceEmotionEvent)
                {
                    Message m = mSelfHandler.obtainMessage(FaceEmotionInterruptParameters
                        .CLASS_FACE_EMOTION_INTERRUPT, 0, 0, faceEmotionEvent);
                    mSelfHandler.sendMessage(m);
                }
            }
            
            @Override
            public void onSetParameter(String action, String text)
            {
                switch (action)
                {
                    case "switchTtsEngine":
                        mTTSVoicePool.setVoice((byte) ((mTTSVoicePool.getActiveVoice() % TTSVoicePool
                            .TTS_VOICE_SIZE) + 1));
                        break;
                    case "setTtsPitchCyberonScaling":
                        int pitch;
                        try
                        {
                            pitch = Integer.valueOf(text);
                        }
                        catch (NumberFormatException nfe)
                        {
                            pitch = 100;
                        }
                        mTTSVoicePool.setPitchCyberonScaling(pitch);
                        break;
                    case "setTtsSpeechRateCyberonScaling":
                        int rate;
                        try
                        {
                            rate = Integer.valueOf(text);
                        }
                        catch (NumberFormatException nfe)
                        {
                            rate = 100;
                        }
                        mTTSVoicePool.setSpeechRateCyberonScaling(rate);
                        break;
                    default:
                        Logs.showTrace("mCockpitListenerBridge " + "onSetParameter() unknown action = `" +
                            action);
                }
            }
            
            @Override
            public void onJumpActivity(String from, String to)
            {
                MagicBook.jumpToActivity(from, to);
            }
            
            @Override
            public String onRequestTopActivityName()
            {
                return getTopActivityName();
            }
        });
        
        CockpitService.startThenBindService(this, InternetCockpitService.class, mCockpitServiceConnection,
            null);
        CockpitService.startThenBindService(this, OtgCockpitService.class, mCockpitServiceConnection, null);
    }
    
    /**
     * 初始化 FaceEmotionInterruptHandler 處理 face emotion 邏輯的部分
     */
    public void initFaceEmotionInterrupt()
    {
        String interruptEmotionBehaviorDataArrayInput;
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
        mFaceEmotionInterruptHandler.setHandler(mSelfHandler);
    }
    
    /**
     * 初始化 InterruptLogicHandler 處理 sensor 邏輯的部分
     */
    public void initInterruptLogic()
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
        
        mCockpitListenerBridge.sensorInterruptLogicHandler.refillInterruptRules
            (interruptLogicBehaviorDataArrayInput);
        mCockpitListenerBridge.sensorInterruptLogicHandler.setHandler(mSelfHandler);
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
                mInternetCockpitService.setFriendlyName(mInternetCockpitFriendlyName);
                mInternetCockpitService.setServerAddress(Parameters.INTERNET_COCKPIT_SERVER_ADDRESS);
            }
            else if (mCockpitService instanceof OtgCockpitService)
            {
                mOtgCockpitService = (OtgCockpitService) mCockpitService;
            }
            
            mCockpitService.setHandler(mSelfHandler);
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
    
    private static final class SelfHandler extends Handler
    {
        private final WeakReference<MainApplication> mWeakSelf;
        
        private SelfHandler(MainApplication app)
        {
            mWeakSelf = new WeakReference<>(app);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            MainApplication app = mWeakSelf.get();
            if (app == null)
            {
                Logs.showTrace("[MainApplication] [SelfHandler] WeakReference is null");
                return;
            }
            
            switch (msg.what)
            {
                case CockpitService.MSG_WHAT:
                case InterruptLogicParameters.CLASS_INTERRUPT_LOGIC:
                    app.mCockpitListenerBridge.handleMessage(msg);
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
                    app.mTTSEventListenerBridge.handleTTSMessage(msg);
                    break;
                case CReaderAdapter.MSG_WHAT:
                    app.mTTSEventListenerBridge.handleCReaderMessage(msg);
                    break;
                default:
                    Logs.showTrace("[MainApplication] [SelfHandler] unhandled msg.what: " + msg.what);
            }
        }
    }
    
    private void handleMessageFaceEmotionInterruptMessage(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        Logs.showTrace("[MainApplication] FaceEmotionInterruptMessage: " + message);
        
        if (null != mFaceEmotionEventListener)
        {
            HashMap<String, String> ttsHashMap = null;
            HashMap<String, String> imgHashMap = null;
            HashMap<String, String> emotionNameHashMap = null;
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
            if (message.containsKey(FaceEmotionInterruptParameters.STRING_EMOTION_NAME))
            {
                emotionNameHashMap = new HashMap<>();
                emotionNameHashMap.put(FaceEmotionInterruptParameters.STRING_EMOTION_NAME, message.get
                    (FaceEmotionInterruptParameters.STRING_EMOTION_NAME));
                emotionNameHashMap.put(FaceEmotionInterruptParameters.STRING_EMOTION_VALUE, message.get
                    (FaceEmotionInterruptParameters.STRING_EMOTION_VALUE));
                
                message.remove(FaceEmotionInterruptParameters.STRING_EMOTION_NAME);
                message.remove(FaceEmotionInterruptParameters.STRING_EMOTION_VALUE);
            }
            
            mFaceEmotionEventListener.onFaceEmotionResult(emotionNameHashMap, ttsHashMap, imgHashMap,
                message);
            
        }
    }
    
    private void handleMessageFaceEmotionMessage(Message msg)
    {
        Activity topActivity = MagicBook.getActivity();
        
        if (msg.arg2 == EmotionParameters.METHOD_EMOTION_DETECT)
        {
            HashMap<String, String> emotionHashMap = (HashMap<String, String>) msg.obj;
            try
            {
                if (!emotionHashMap.get(EmotionParameters.STRING_EXPRESSION_ATTENTION).equals("-1"))
                {
                    HashMap<String, String> trackFaceEmotionData = new HashMap<>();
                    trackFaceEmotionData.put("Source", "1");
                    trackFaceEmotionData.put("Description", "data from face emotion recognition SDK");
                    JSONObject emotionJsonObj = new JSONObject(emotionHashMap);
                    trackFaceEmotionData.put("Value", emotionJsonObj.toString());
                    
                    if (topActivity != null)
                    {
                        trackFaceEmotionData.put("TopActivity", topActivity.getLocalClassName());
                    }
                    
                    //debug using
                    //Logs.showTrace("[MainApplication] send tracker Data: " + trackFaceEmotionData);
                    sendToTracker(trackFaceEmotionData);
                }
                
                //temperate add in
                if (null != mFaceEmotionEventListener)
                {
                    mFaceEmotionInterruptHandler.setEmotionEventData((HashMap<String, String>) msg.obj);
                }
            }
            catch (Exception e)
            {
                Logs.showError("[MainApplication] send tracker while ERROR:" + e.toString());
            }
        }
        else if (msg.arg2 == EmotionParameters.METHOD_FACE_DETECT)
        {
            if (null != mFaceEmotionEventListener)
            {
                mFaceEmotionEventListener.onFaceDetectResult(true);
            }
        }
        else if (msg.arg2 == EmotionParameters.METHOD_NO_FACE_DETECT)
        {
            if (null != mFaceEmotionEventListener)
            {
                mFaceEmotionEventListener.onFaceDetectResult(false);
            }
        }
        // temperate mark
        /*
        if (null != mFaceEmotionEventListener)
        {
            mFaceEmotionInterruptHandler.setEmotionEventData((HashMap<String, String>) msg.obj);
        }*/
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
        
        Logs.showTrace("[MainApplication] handleTrackerMessage() " + "Result: " + result + " From: " + from + " Message: " + message);
    }
}
