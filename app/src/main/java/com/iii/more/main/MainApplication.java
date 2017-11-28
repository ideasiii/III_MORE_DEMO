package com.iii.more.main;

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
    private final SelfHandler mSelfHandler = new SelfHandler(this);

    private CockpitService mCockpitService;
    private InternetCockpitService mInternetCockpitService;
    private OtgCockpitService mOtgCockpitService;

    private Tracker mTracker = new Tracker(this);
    private TextToSpeechHandler mTtsHandler = new TextToSpeechHandler(this);
    private CockpitListenerBridge mCockpitListenerBridge = new CockpitListenerBridge(this);

    private FaceEmotionEventListener mFaceEmotionEventListener = null;
    private TTSEventListener mTtsEventListener;

    // this logic handler does not handle emotion logic

    private FaceEmotionInterruptHandler mFaceEmotionInterruptHandler = new FaceEmotionInterruptHandler(this);
    private EmotionHandler mEmotionHandler = null;
    private static boolean isFaceEmotionStart = false;

    // 方便讓遙控器控制端辨識的名稱
    private String mInternetCockpitServiceFriendlyName;

    public MainApplication()
    {
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        initCockpit();
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

    public void setCockpitConnectionListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitConnectionEventListener()");
        mCockpitListenerBridge.cockpitConnectionEventListener = l;
    }

    public void setCockpitSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitSensorEventListener()");
        mCockpitListenerBridge.cockpitSensorEventListener = l;
    }

    public void setCockpitFilmMakingEventListener(CockpitFilmMakingEventListener l)
    {
        Logs.showTrace("[MainApplication] setCockpitFilmMakingEventListener()");
        mCockpitListenerBridge.cockpitFilmMakingEventListener = l;
    }

    public void setFaceEmotionEventListener(FaceEmotionEventListener l)
    {
        Logs.showTrace("[MainApplication] setEmotionEventListener()");
        mFaceEmotionEventListener = l;
    }

    public void setTTSEventListener(TTSEventListener l)
    {
        Logs.showTrace("[MainApplication] setTTSEventListener()");
        for (StackTraceElement ste: Thread.currentThread().getStackTrace())
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

    /**
     * 初始化 TTS 服務
     */
    public void initTTS()
    {
        mTtsHandler.setHandler(mSelfHandler);
        mTtsHandler.init();
    }

    /**
     * 設定 TTS pitch & speech rate
     */
    public void setTTSPitch(float pitch, float rate)
    {
        mTtsHandler.setPitch(pitch);
        mTtsHandler.setSpeechRate(rate);
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
        Logs.showTrace("[MainApplication] stopTTS()");
        mTtsHandler.stop();
    }

    /**
     * 取得要傳送給 InternetCockpit 伺服器的識別名稱
     */
    private void getInternetCockpitFriendlyName()
    {
        try
        {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mInternetCockpitServiceFriendlyName = bluetoothAdapter.getName();
        }
        catch(Exception e)
        {
            Logs.showError("Cannot get bluetooth device name");
            mInternetCockpitServiceFriendlyName = "N/A";
        }

        Logs.showTrace("use " + mInternetCockpitServiceFriendlyName + " as friendly name in InternetCockpitService");
    }

    /**
     * 初始化駕駛艙連結
     */
    private void initCockpit()
    {
        getInternetCockpitFriendlyName();

        mCockpitListenerBridge.setEventDelegate(new CockpitListenerBridge.TellMeWhatToDo() {
            @Override
            public void onFaceEmotionDetected(String emotionName) {
                // face emotion simulation commands from cockpit
                Logs.showTrace("[MainApplication] handleCockpitServiceMessage() " +
                        "simulates when face emotion is detected, emotionName = `" + emotionName + "`");

                HashMap<String, String> faceEmotionEvent =
                        MagicBook.cookFaceEmotionDetectedEvent(mFaceEmotionInterruptHandler, emotionName);

                if (null != faceEmotionEvent)
                {
                    Message m = mSelfHandler.obtainMessage(FaceEmotionInterruptParameters.CLASS_FACE_EMOTION_INTERRUPT,
                            0, 0, faceEmotionEvent);
                    mSelfHandler.sendMessage(m);
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
        mFaceEmotionInterruptHandler.setHandler(mSelfHandler);
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

        mCockpitListenerBridge.sensorInterruptLogicHandler.setInterruptLogicBehaviorDataArray(interruptLogicBehaviorDataArrayInput);
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
                mInternetCockpitService.setFriendlyName(mInternetCockpitServiceFriendlyName);
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
                    app.handleTTSMessage(msg);
                    break;
                default:
                    Logs.showTrace("[MainApplication] [SelfHandler] unhandled msg.what: " + msg.what);
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

        Logs.showTrace("[MainApplication] handleTrackerMessage() " +
                "Result: " + result + " From: " + from + " Message: " + message);
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
