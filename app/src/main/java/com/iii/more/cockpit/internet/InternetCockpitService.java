package com.iii.more.cockpit.internet;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iii.more.cockpit.CockpitService;
import com.iii.more.cockpit.InServiceEventHandler;

import org.json.JSONObject;

/**
 * 透過網路連結的訓練用虛擬駕駛艙
 * This service waits for push message from server via WebSocket.
 * Object must register a device ID before connect to server, after registration
 * server will push message sent from host.
 */
public class InternetCockpitService extends CockpitService
{
    private static final String LOG_TAG = "InternetCockpitService";


    private static boolean serviceSpawned = false;

    private ServerConnection mServerConnection;
    private URI mUri;
    private String mDeviceId;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");

        serviceSpawned = true;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");

        if (mServerConnection != null)
        {
            mServerConnection.close();
        }

        serviceSpawned = false;
        super.onDestroy();
    }

    public static boolean isServiceSpawned()
    {
        return serviceSpawned;
    }

    @Override
    public boolean _instance_IsServiceSpawned()
    {
        return serviceSpawned;
    }

    public void setServerAddress(String address)
    {
        try
        {
            mUri = new URI(address);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_PROTOCOL_NOT_SUPPORTED, 0).sendToTarget();
            }
        }
    }

    /** call before calling connect() */
    public void setDeviceId(String id)
    {
        if (mServerConnection != null && mServerConnection.isOpen())
        {
            Log.w(LOG_TAG, "Someone tried to set device ID after connected to server");
            return;
        }

        mDeviceId = id;
    }

    @Override
    public void connect()
    {
        Log.d(LOG_TAG, "connect()");

        if (mServerConnection != null)
        {
            Log.d(LOG_TAG, "connect() mServerConnection != null");
            return;
        }

        if (mUri == null)
        {
            return;
        }

        mReconnectOnDisconnect = true;
        mServerConnection = new ServerConnection(mUri, mDeviceId, mServerConnectionEventListener);
        mServerConnection.connect();
    }

    @Override
    public void disconnect()
    {
        Log.d(LOG_TAG, "disconnect()");

        if (mServerConnection == null)
        {
            Log.d(LOG_TAG, "disconnect() mServerConnection == null");
            return;
        }

        mReconnectOnDisconnect = false;
        mServerConnection.close();
    }

    private ServerConnection.EventListener mServerConnectionEventListener = new ServerConnection.EventListener()
    {
        @Override
        public void onPermissionGranted()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_PERMISSION_GRANTED, 0).sendToTarget();
            }
        }

        @Override
        public void onDisconnected()
        {
            if (mServerConnection != null)
            {
                mServerConnection.close();
                mServerConnection = null;
            }

            if (serviceSpawned && mReconnectOnDisconnect)
            {
                Message delayMsg = mInServiceEventHandler.obtainMessage(InServiceEventHandler.IN_SERVICE_EVENT_NEED_RECONNECT);
                mInServiceEventHandler.sendMessageDelayed(delayMsg, 1000);
            }

            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DISCONNECTED, 0).sendToTarget();
            }
        }

        @Override
        public void onReady()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_READY, 0).sendToTarget();
            }
        }

        @Override
        public void onProtocolNotSupported()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_PROTOCOL_NOT_SUPPORTED, 0).sendToTarget();
            }
        }

        @Override
        public void onDataText(String text)
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DATA_TEXT, 0, text).sendToTarget();
            }
        }

        @Override
        public void onDataFilmMaking(JSONObject json)
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DATA_FILM_MAKING, 0, json).sendToTarget();
            }
        }
    };
}
