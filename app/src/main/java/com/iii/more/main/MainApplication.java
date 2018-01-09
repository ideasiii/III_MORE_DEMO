package com.iii.more.main;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.cyberon.utility.CReaderPlayer;
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
import sdk.ideas.tool.speech.tts.TextToSpeechHandler;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by joe on 2017/10/30
 */

public class MainApplication extends Application
{
    private static final byte CURRENT_USING_TTS_GOOGLE = 1;
    private static final byte CURRENT_USING_TTS_CYBERON_KID_FEMALE = 2;
    private static final byte CURRENT_USING_TTS_CYBERON_KID_MALE = 3;
    
    private final SelfHandler mSelfHandler = new SelfHandler(this);
    
    private CockpitService mCockpitService;
    private InternetCockpitService mInternetCockpitService;
    private OtgCockpitService mOtgCockpitService;
    private CockpitListenerBridge mCockpitListenerBridge = new CockpitListenerBridge(this);
    
    private TextToSpeechHandler mGoogleTtsHandler = new TextToSpeechHandler(this);
    private CReaderAdapter mCyberonTtsAdapter_KidMale; // 賽微 TTS: 女性
    private CReaderAdapter mCyberonTtsAdapter_KidFemale; // 賽微 TTS: 男性
    
    private TTSEventListenerBridge mTTSEventListenerBridge = new TTSEventListenerBridge(this);
    // 目前正在使用的 TTS 語音的編號
    private byte mCurrentUsingTts = CURRENT_USING_TTS_CYBERON_KID_FEMALE;
    
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
     * An alias of (MainApplication)context.getApplicationContext()
     *
     * @param context Context having access to getApplicationContext()
     * @return MainApplication instance
     */
    public static MainApplication getApp(Context context)
    {
        return (MainApplication) context.getApplicationContext();
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
    public void initTTS()
    {
        mGoogleTtsHandler.setHandler(mSelfHandler);
        mGoogleTtsHandler.init();
        
        final MainApplication self = getApp(this);
        final String cReaderDataRoot = getFilesDir().getAbsolutePath() + "/cyberon/CReader";
        
        new Thread()
        {
            @Override
            public void run()
            {
                CyberonAssetsExtractor.extract(getAssets(), getFilesDir().getAbsolutePath());
                
                mCyberonTtsAdapter_KidMale = new CReaderAdapter(self, cReaderDataRoot);
                mCyberonTtsAdapter_KidMale.setHandler(mSelfHandler);
                mCyberonTtsAdapter_KidMale.setVoiceName(CReaderPlayer.VoiceNameConstant
                    .TRADITIONAL_CHINESE_KID_MALE_VOICE_NAME);
                mCyberonTtsAdapter_KidMale.setSpeechRate(85);
                mCyberonTtsAdapter_KidMale.setVolume(500);
                mCyberonTtsAdapter_KidMale.init();
                
                mCyberonTtsAdapter_KidFemale = new CReaderAdapter(self, cReaderDataRoot);
                mCyberonTtsAdapter_KidFemale.setHandler(mSelfHandler);
                mCyberonTtsAdapter_KidFemale.setVoiceName(CReaderPlayer.VoiceNameConstant
                    .TRADITIONAL_CHINESE_KID_FEMALE_VOICE_NAME);
                mCyberonTtsAdapter_KidFemale.setPitch(75);
                mCyberonTtsAdapter_KidFemale.setSpeechRate(80);
                mCyberonTtsAdapter_KidFemale.setVolume(500);
                mCyberonTtsAdapter_KidFemale.init();
            }
        }.start();
    }
    
    /**
     * 設定 TTS pitch & speech rate
     */
    public void setTTSPitch(float pitch, float rate)
    {
        int cyberonMappedPitch = (int) ((pitch - 0.5) * 120) + 70;
        int cyberonMappedSpeed = (int) ((rate - 0.5) * 60) + 70;
        
        switch (mCurrentUsingTts)
        {
            case CURRENT_USING_TTS_GOOGLE:
                mGoogleTtsHandler.setPitch(pitch);
                mGoogleTtsHandler.setSpeechRate(rate);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setPitch(cyberonMappedPitch);
                mCyberonTtsAdapter_KidFemale.setSpeechRate(cyberonMappedSpeed);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setPitch(cyberonMappedPitch);
                mCyberonTtsAdapter_KidMale.setSpeechRate(cyberonMappedSpeed);
                break;
            default:
                Logs.showTrace("Unknown mCurrentUsingTts: " + mCurrentUsingTts);
        }
    }
    
    /**
     * 設定 TTS 的輸出語言
     */
    public void setTTSLanguage(Locale language)
    {
        switch (mCurrentUsingTts)
        {
            case CURRENT_USING_TTS_GOOGLE:
                mGoogleTtsHandler.setLocale(language);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setLanguage(language);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setLanguage(language);
                break;
            default:
                Logs.showTrace("Unknown mCurrentUsingTts: " + mCurrentUsingTts);
        }
    }
    
    /**
     * 取得 TTS 的輸出語言
     */
    public Locale getTTSLanguage()
    {
        switch (mCurrentUsingTts)
        {
            case CURRENT_USING_TTS_GOOGLE:
                return mGoogleTtsHandler.getLocale();
            case CURRENT_USING_TTS_CYBERON_KID_FEMALE:
                return mCyberonTtsAdapter_KidFemale.getLanguage();
            case CURRENT_USING_TTS_CYBERON_KID_MALE:
                return mCyberonTtsAdapter_KidMale.getLanguage();
            default:
                Logs.showTrace("Unknown mCurrentUsingTts: " + mCurrentUsingTts);
                return null;
        }
    }
    
    /**
     * 將 text 轉為語音輸出
     */
    public void playTTS(String text, String textId)
    {
        switch (mCurrentUsingTts)
        {
            case CURRENT_USING_TTS_GOOGLE:
                mGoogleTtsHandler.textToSpeech(text, textId);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.speak(text, textId);
                break;
            case CURRENT_USING_TTS_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.speak(text, textId);
                break;
            default:
                Logs.showTrace("Unknown mCurrentUsingTts: " + mCurrentUsingTts);
        }
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
        mGoogleTtsHandler.stop();
        mCyberonTtsAdapter_KidMale.stop();
        mCyberonTtsAdapter_KidFemale.stop();
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
    
    /**
     * 初始化布偶裝置連結
     */
    private void initCockpit()
    {
        getInternetCockpitFriendlyName();
        
        mCockpitListenerBridge.setEventDelegate(new CockpitListenerBridge.TellMeWhatToDo()
        {
            @Override
            public void onFaceEmotionDetected(String emotionName)
            {
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " + "simulates when face "
                    + "emotion is detected, emotionName = `" + emotionName + "`");
                
                HashMap<String, String> faceEmotionEvent = MagicBook.cookFaceEmotionDetectedEvent
                    (mFaceEmotionInterruptHandler, emotionName);
                
                if (null != faceEmotionEvent)
                {
                    Message m = mSelfHandler.obtainMessage(FaceEmotionInterruptParameters
                        .CLASS_FACE_EMOTION_INTERRUPT, 0, 0, faceEmotionEvent);
                    mSelfHandler.sendMessage(m);
                }
            }
            
            @Override
            public void onSetParameter(String action)
            {
                switch (action)
                {
                    case "switchTtsEngine":
                        mCurrentUsingTts = (byte) ((mCurrentUsingTts % 3) + 1);
                        Logs.showTrace("switchTtsEngine, mCurrentUsingTts = " + mCurrentUsingTts);
                        break;
                    default:
                        Logs.showTrace("mCockpitListenerBridge " + "onSetParameter() unknown action = `" +
                            action);
                }
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
                message.remove(FaceEmotionInterruptParameters.STRING_EMOTION_NAME);
            }
            
            mFaceEmotionEventListener.onFaceEmotionResult(emotionNameHashMap, ttsHashMap, imgHashMap,
                message);
            
        }
    }
    
    private void handleMessageFaceEmotionMessage(Message msg)
    {
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
                    
                    //debug using
                    //Logs.showTrace("[MainApplication] send tracker Data: " + trackFaceEmotionData);
                    sendToTracker(trackFaceEmotionData);
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
        if (null != mFaceEmotionEventListener)
        {
            mFaceEmotionInterruptHandler.setEmotionEventData((HashMap<String, String>) msg.obj);
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
}
