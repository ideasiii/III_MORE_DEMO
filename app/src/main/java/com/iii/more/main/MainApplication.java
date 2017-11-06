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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private final List<CockpitConnectionEventListener> mCockpitConnectionEventListeners = new CopyOnWriteArrayList<>();
    private final List<CockpitSensorEventListener> mCockpitSensorEventListeners = new CopyOnWriteArrayList<>();

    @Override
    public void onCreate()
    {
        super.onCreate();

        bootCockpitService();
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

                    for (CockpitConnectionEventListener l : mCockpitConnectionEventListeners)
                    {
                        l.onData(null, data);
                    }
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
    
}
