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
    private List<CockpitEventListener> mCockpitEventListeners = new CopyOnWriteArrayList<>();

    public void addCockpitEventListener(CockpitEventListener l)
    {
        Logs.showTrace("[MainApplication] addCockpitEventListener()");
        mCockpitEventListeners.add(l);
    }

    public void removeCockpitEventListener(CockpitEventListener l)
    {
        Logs.showTrace("[MainApplication] removeCockpitEventListener()");
        mCockpitEventListeners.remove(l);
    }

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

    // this was in Activity.onResume()
    public void bootCockpitService()
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
    private Handler mCockpitServiceHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case CockpitService.MSG_DATA_IN:
                    String data = (String) msg.obj;
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onData(), data=`" + data + "`");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onData(null, data);
                    }
                    break;
                case CockpitService.MSG_NO_DEVICE:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onNoDevice()");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onNoDevice(null);
                    }
                    break;
                case CockpitService.MSG_READY:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onReady()");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onReady(null);
                    }
                    break;
                case CockpitService.MSG_PROTOCOL_NOT_SUPPORTED:
                case CockpitService.MSG_CDC_DRIVER_NOT_WORKING:
                case CockpitService.MSG_USB_DEVICE_NOT_WORKING:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onProtocolNotSupported()");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onProtocolNotSupported(null);
                    }
                    break;
                case CockpitService.MSG_PERMISSION_GRANTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onPermissionGranted()");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onPermissionGranted(null);
                    }
                    break;
                case CockpitService.MSG_PERMISSION_NOT_GRANTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onPermissionNotGranted()");

                    for (CockpitEventListener l : mCockpitEventListeners)
                    {
                        l.onPermissionNotGranted(null);
                    }
                    break;
                case CockpitService.MSG_DISCONNECTED:
                    Logs.showTrace("[MainApplication] mCockpitServiceHandler onDisconnected()");

                    for (CockpitEventListener l : mCockpitEventListeners)
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
