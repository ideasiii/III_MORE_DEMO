package com.iii.more.cockpit.internet;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

import com.iii.more.cockpit.CockpitService;
import com.iii.more.main.Parameters;

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

    private static boolean serviceConnected = false;

    private ServerConnection serverConnection = null;
    private String mDeviceId;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");

        serviceConnected = true;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");

        if (serverConnection != null)
        {
            serverConnection.close();
        }

        serviceConnected = false;
        super.onDestroy();
    }

    public static boolean isServiceConnected()
    {
        return serviceConnected;
    }

    public void setDeviceId(String id) {
        if (serverConnection != null && serverConnection.isOpen()) {
            Log.w(LOG_TAG, "Someone tried to set device ID after connected to server");
            return;
        }

        mDeviceId = id;
    }

    @Override
    public void connect()
    {
        Log.d(LOG_TAG, "connect()");

        if (serverConnection != null && serverConnection.isOpen())
        {
            Log.d(LOG_TAG, "connect() already connected");
            return;
        }

        URI uri;
        try
        {
            uri = new URI(Parameters.INTERNET_COCKPIT_SERVER_ADDRESS);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            if (mHandler != null)
            {
                mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_PROTOCOL_NOT_SUPPORTED, 0).sendToTarget();
            }
            return;
        }

        serverConnection = new ServerConnection(uri, mDeviceId, new ServerConnection.EventListener()
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
                if (mHandler != null)
                {
                    mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DISCONNECTED, 0).sendToTarget();
                    serverConnection.close();
                    serverConnection = null;
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
        });

        serverConnection.connect();
    }
}