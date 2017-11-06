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
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import sdk.ideas.common.Logs;
import sdk.ideas.common.OnCallbackResult;

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
    private final List<CockpitConnectionEventListener> mCockpitConnectionEventListeners = new CopyOnWriteArrayList<>();
    private final List<CockpitSensorEventListener> mCockpitSensorEventListeners = new CopyOnWriteArrayList<>();

    private InterruptLogicHandler mInterruptLogicHandler = null;

    @Override
    public void onCreate()
    {
        super.onCreate();

        bootCockpitService();
        initInterruptLogic();
    }

    /** 取得章魚 or 使用者的名字 */
    public String getName(String id)
    {
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        return prefs.getString(id, null);
    }

    /** 設定章魚 or 使用者的名字 */
    public void setName(String id, String name)
    {
        SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(id, name);
        editor.apply();
    }

    public void addCockpitConnectionEventListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] addCockpitConnectionEventListener()");
        mCockpitConnectionEventListeners.add(l);
    }

    public void removeCockpitConnectionEventListener(CockpitConnectionEventListener l)
    {
        Logs.showTrace("[MainApplication] removeCockpitConnectionEventListener()");
        mCockpitConnectionEventListeners.remove(l);
    }

    public void addCockpitSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] addCockpitSensorEventListener()");
        mCockpitSensorEventListeners.add(l);
    }

    public void removeCockpitSensorEventListener(CockpitSensorEventListener l)
    {
        Logs.showTrace("[MainApplication] removeCockpitSensorEventListener()");
        mCockpitSensorEventListeners.remove(l);
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
        mInterruptLogicHandler = new InterruptLogicHandler(this);

        try
        {
            SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
            String message = prefs.getString(Parameters.TASK_COMPOSER_DATA, "ssss");
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

        mInterruptLogicHandler.setHandler(mInterruptLogicHandlerResultHandler);
        mInterruptLogicHandler.setOnCallbackResultListener(new OnCallbackResult()
                                                           {
                                                               @Override
                                                               public void onCallbackResult(int i, int i1, int i2, HashMap<String, String> hashMap)
                                                               {
                                                                   Logs.showTrace("[MainApplication] initInterruptLogic() onCallbackResult()");
                                                               }
                                                           }
        );
        Logs.showTrace("[MainApplication] initInterruptLogic() OK");
    }

    /** CockpitService 的 ServiceConnection */
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
                        .setDeviceId(SemanticDeviceID.getDeiceID(getApplicationContext()));
            }

            mCockpitService.setHandler(mCockpitServiceHandler);
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

    /** 處理來自 CockpitService 的事件 */
    private final Handler mCockpitServiceHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case CockpitService.MSG_DATA_IN:
                    String data = (String) msg.obj;
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onData(), data=`" + data + "`");

                    if (mInterruptLogicHandler != null)
                    {
                        mInterruptLogicHandler.setDeviceEventData(data);
                        mInterruptLogicHandler.startEventDataAnalysis();
                    }

                    /*for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onData(null, data);
                    }*/
                    break;
                case CockpitService.MSG_NO_DEVICE:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onNoDevice()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onNoDevice(null);
                    }
                    break;
                case CockpitService.MSG_READY:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onReady()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onReady(null);
                    }
                    break;
                case CockpitService.MSG_PROTOCOL_NOT_SUPPORTED:
                case CockpitService.MSG_CDC_DRIVER_NOT_WORKING:
                case CockpitService.MSG_USB_DEVICE_NOT_WORKING:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onProtocolNotSupported()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onProtocolNotSupported(null);
                    }
                    break;
                case CockpitService.MSG_PERMISSION_GRANTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onPermissionGranted()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onPermissionGranted(null);
                    }
                    break;
                case CockpitService.MSG_PERMISSION_NOT_GRANTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onPermissionNotGranted()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onPermissionNotGranted(null);
                    }
                    break;
                case CockpitService.MSG_DISCONNECTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onDisconnected()");

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onDisconnected(null);
                    }
                    break;
                default:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler got unhandled msg.what: " + msg.what);
            }
        }
    };

    private final Handler mInterruptLogicHandlerResultHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] handleMessage()");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;

            switch (msg.arg2)
            {
                case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                    String trigger_result = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);

                    switch (trigger_result)
                    {
                        case "握手":
                            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] shake hands");
                            for (CockpitSensorEventListener l : mCockpitSensorEventListeners)
                            {
                                l.onShakeHands(null);
                            }
                            break;
                        case "拍手":
                            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] clap hands");
                            for (CockpitSensorEventListener l : mCockpitSensorEventListeners)
                            {
                                l.onClapHands(null);
                            }
                            break;
                        case "擠壓":
                            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] pinch cheeks");
                            for (CockpitSensorEventListener l : mCockpitSensorEventListeners)
                            {
                                l.onPinchCheeks(null);
                            }
                            break;
                        case "拍頭":
                            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] pat head");
                            for (CockpitSensorEventListener l : mCockpitSensorEventListeners)
                            {
                                l.onPatHead(null);
                            }
                            break;
                        default:
                            Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] handleMessage() unknown trigger_result: " + trigger_result);
                    }
                default:
                    Logs.showTrace("[MainApplication] [mInterruptLogicHandlerResultHandler] handleMessage() unknown msg.arg2: " + msg.arg2);
            }
        }
    };

    private static final String INTERRUPT_LOGIC_BEHAVIOR_DATA_ARRAY_FALLBACK_INPUT = "[{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":1,\"action\":1,\"tag\":\"SHAKE_HANDS\",\"trigger\":\"OCTOBO_Expressions-35.png\",\"value\":\"1\",\"desc\":\"握手\"},{\"sensors\":[\"f1\",\"f2\",\"C\",\"D\"],\"trigger_rule\":2,\"action\":2,\"tag\":\"CLAP_HANDS\",\"trigger\":\"OCTOBO_Expressions-24.png\",\"value\":\"1\",\"desc\":\"拍手\"},{\"sensors\":[\"FSR1\",\"FSR2\"],\"trigger_rule\":2,\"action\":3,\"tag\":\"EXTRUSION\",\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"擠壓\"},{\"sensors\":[\"X\",\"Y\",\"Z\"],\"trigger_rule\":1,\"action\":4,\"tag\":\"SHAKE\",\"trigger\":\"OCTOBO_Expressions-38.png\",\"value\":\"1\",\"desc\":\"搖晃\"},{\"sensors\":[\"H\"],\"trigger_rule\":1,\"action\":5,\"tag\":\"TURN_ON_THE_LIGHT\",\"trigger\":\"ON\",\"value\":\"1\",\"desc\":\"開燈\"},{\"sensors\":[\"FSR1\",\"FSR2\"],\"trigger_rule\":1,\"action\":6,\"tag\":\"PAT_HEAD\",\"trigger\":\"OCTOBO_Expressions-01.png\",\"value\":\"1\",\"desc\":\"拍頭\"}]";
}
