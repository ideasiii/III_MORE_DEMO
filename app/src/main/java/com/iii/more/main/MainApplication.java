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
import com.iii.more.cockpit.internet.InternetCockpitService;
import com.iii.more.emotion.EmotionHandler;
import com.iii.more.emotion.EmotionParameters;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import sdk.ideas.common.Logs;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by joe on 2017/10/30.
 */


/*###
     mean pending to write
*/

public class MainApplication extends Application
{
    private CockpitService mCockpitService;
    private CockpitConnectionEventListener mCockpitConnectionEventListener;
    private CockpitSensorEventListener mCockpitSensorEventListener;
    private CockpitFilmMakingEventListener mCockpitFilmMakingEventListener;
    private FaceEmotionEventListener mFaceEmotionEventListener = null;
    
    // this logic handler does not handle emotion logic
    private InterruptLogicHandler mInterruptLogicHandler = new InterruptLogicHandler(this);
    
    private final MainHandler mMainHandler = new MainHandler(this);
    
    private EmotionHandler mEmotionHandler = null;
    private static boolean isFaceEmotionStart = false;
    
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        
        bootCockpitService();
        initInterruptLogic();
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
    
    public void startFaceEmotion()
    {
        if (null == mEmotionHandler)
        {
            mEmotionHandler = new EmotionHandler(this);
            mEmotionHandler.setHandler(mMainHandler);
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
    
    
    // this was in Activity.onResume()
    private void bootCockpitService()
    {
        // TODO invoke with one of service.class you want
        CockpitService.startThenBindService(this, InternetCockpitService.class,
                mCockpitServiceConnection, null);
    }
    
    private void initInterruptLogic()
    {
        try
        {
            SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
            String message = prefs.getString(Parameters.TASK_COMPOSER_DATA, "non-json text");
            JSONObject tmp = new JSONObject(message);
            
            if (tmp.has("rules"))
            {
                JSONObject rules = tmp.getJSONObject("rules");
                if (rules.has("action"))
                {
                    Logs.showTrace("[MainApplication] Use interrupt logic behavior data array input from SharedPreferences");
                    
                    mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(rules.getJSONArray("action").toString());
                }
                else
                {
                    Logs.showError("[MainApplication] SharedPreferences does not have interrupt logic behavior data array, use fallback input");
                    mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT);
                }
                /*if (rules.has("emotion"))
                {
                    mInterruptLogicHandler.setInterruptEmotionLogicBehaviorDataArray(rules.getJSONArray("emotion").toString());
                }*/
            }
        }
        catch (JSONException e)
        {
            Logs.showError("[MainApplication] cannot get interrupt logic behavior data array from SharedPreferences: " + e.toString());
            Logs.showError("[MainApplication] use fallback input");
            mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT);
        }
        
        mInterruptLogicHandler.setHandler(mMainHandler);
        Logs.showTrace("[MainApplication] initInterruptLogic() done");
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
            
            mCockpitService = ((CockpitService.CockpitBinder) service).getService();
            
            if (mCockpitService instanceof InternetCockpitService)
            {
                ((InternetCockpitService) mCockpitService)
                        .setDeviceId(SemanticDeviceID.getDeviceID(getApplicationContext()));
            }
            
            mCockpitService.setHandler(mMainHandler);
            mCockpitService.connect();
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Logs.showTrace("[MainApplication] onServiceDisconnected()");
            
            if (mCockpitService != null)
            {
                mCockpitService.setHandler(null);
                mCockpitService = null;
            }
        }
    };
    
    private static final class MainHandler extends Handler
    {
        private final WeakReference<MainApplication> mApp;
        
        public MainHandler(MainApplication app)
        {
            mApp = new WeakReference<>(app);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            MainApplication app = mApp.get();
            if (app == null)
            {
                Logs.showTrace("[MainApplication] [MainHandler] WeakReference is null");
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
                default:
                    Logs.showTrace("[MainApplication] [MainHandler] unhandled msg.what: " + msg.what);
            }
        }
    }
    
    ;
    
    private void handleMessageFaceEmotionMessage(Message msg)
    {
        if (null != mFaceEmotionEventListener)
        {
            mFaceEmotionEventListener.onFaceEmotionResult((HashMap<String, String>) msg.obj);
        }
    }
    
    /**
     * 接收 InterruptLogicHandler 事件的 handler
     */
    private void handleInterruptLogicMessage(Message msg)
    {
        if (mCockpitSensorEventListener == null)
        {
            return;
        }
        
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        switch (msg.arg2)
        {
            case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                String trigger_result = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
                Logs.showTrace("[MainApplication] [InterruptLogicHandlerResultHandler] " +
                        "trigger_result = " + trigger_result);
                
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
                    default:
                        Logs.showTrace("[MainApplication] [InterruptLogicHandlerResultHandler] unknown trigger_result: " + trigger_result);
                }
                break;
            default:
                Logs.showTrace("[MainApplication] [InterruptLogicHandlerResultHandler] unknown msg.arg2: " + msg.arg2);
        }
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
            Logs.showTrace("[MainApplication] [CockpitServiceHandler] onData(), data=`" + data + "`");
            
            if (mInterruptLogicHandler != null)
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
            
            if (mCockpitFilmMakingEventListener != null)
            {
                try
                {
                    String action = j.getString("action");
                    String text = j.getString("text");
                    Logs.showTrace("[MainApplication] [CockpitServiceHandler] onFilmMaking() " +
                            "action = `" + action + "`, text = `" + text + "`");
                    
                    if (action.equals("tts"))
                    {
                        String language = j.getString("language");
                        mCockpitFilmMakingEventListener.onTTS(null, text, language);
                    }
                    else if (action.equals("showFaceImage"))
                    {
                        mCockpitFilmMakingEventListener.onEmotionImage(null, text);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            
            return;
        }
        
        if (mCockpitConnectionEventListener == null)
        {
            return;
        }
        
        // connection events
        switch (msg.arg1)
        {
            case CockpitService.EVENT_NO_DEVICE:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onNoDevice()");
                mCockpitConnectionEventListener.onNoDevice(null);
                break;
            case CockpitService.EVENT_READY:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onReady()");
                mCockpitConnectionEventListener.onReady(null);
                break;
            case CockpitService.EVENT_PROTOCOL_NOT_SUPPORTED:
            case CockpitService.EVENT_CDC_DRIVER_NOT_WORKING:
            case CockpitService.EVENT_USB_DEVICE_NOT_WORKING:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onProtocolNotSupported()");
                mCockpitConnectionEventListener.onProtocolNotSupported(null);
                break;
            case CockpitService.EVENT_PERMISSION_GRANTED:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onPermissionGranted()");
                mCockpitConnectionEventListener.onPermissionGranted(null);
                break;
            case CockpitService.EVENT_PERMISSION_NOT_GRANTED:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onPermissionNotGranted()");
                mCockpitConnectionEventListener.onPermissionNotGranted(null);
                break;
            case CockpitService.EVENT_DISCONNECTED:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] onDisconnected()");
                mCockpitConnectionEventListener.onDisconnected(null);
                break;
            default:
                Logs.showTrace("[MainApplication] [CockpitServiceHandler] unhandled msg.arg1: " + msg.arg1);
        }
    }
    
    private static final String INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT = "[{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":1,\"action\":1,\"tag\":\"SHAKE_HANDS\",\"trigger\":\"OCTOBO_Expressions-35.png\",\"value\":\"1\",\"desc\":\"握手\"},{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":2,\"action\":2,\"tag\":\"CLAP_HANDS\",\"trigger\":\"OCTOBO_Expressions-24.png\",\"value\":\"1\",\"desc\":\"拍手\"},{\"sensors\":[\"FSR1\",\"FSR2\"],\"trigger_rule\":2,\"action\":3,\"tag\":\"EXTRUSION\",\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"擠壓\"},{\"sensors\":[\"X\",\"Y\",\"Z\"],\"trigger_rule\":1,\"action\":4,\"tag\":\"SHAKE\",\"trigger\":\"OCTOBO_Expressions-38.png\",\"value\":\"1\",\"desc\":\"搖晃\"},{\"sensors\":[\"H\"],\"trigger_rule\":1,\"action\":5,\"tag\":\"TURN_ON_THE_LIGHT\",\"trigger\":\"ON\",\"value\":\"1\",\"desc\":\"開燈\"},{\"sensors\":[\"FSR1\",\"FSR2\"],\"trigger_rule\":1,\"action\":6,\"tag\":\"PAT_HEAD\",\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"拍頭\"}]";
}
