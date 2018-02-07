package com.iii.more.cockpit;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 透過網路連結的訓練用虛擬駕駛艙
 * This service waits for push message from server via WebSocket.
 * Object must register a device ID before connect to server, after registration
 * server will push message sent from host.
 */
public class InternetCockpitService extends CockpitService
{
    /**
     * msg.arg1 in Handler.onMessage() to distinguish us
     * from other subclass of CockpitService......
     */
    public static final int MSG_ARG1 = 1369753497;

    private static final String LOG_TAG = "InternetCockpitService";

    /** paper 內容的類型 */
    private static class PaperType
    {
        /** 只是文字，這類指令內的文字會被當作 OTG 裝置傳出的字串 */
        private static final int TEXT = 0;

        /** 拍片用，這類指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出 */
        private static final int FILM_MAKING = 1;

        /** 拯救臉部肌肉，模擬偵測到臉部表情 */
        private static final int FACE_EMOTION_DETECTED = 2;

        /** 修改設定參數 */
        private static final int PARAMETERS = 3;

        /** 從某個 Activity 跳到另一個 Activity */
        private static final int JUMP_ACTIVITY = 30;
    }

    private static boolean serviceSpawned = false;

    private ServerConnection mServerConnection;
    private URI mServerUri;
    private String mDeviceId;
    private String mFriendlyName = "";

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
    boolean _instance_IsServiceSpawned()
    {
        return serviceSpawned;
    }

    public void setServerAddress(String address)
    {
        try
        {
            mServerUri = new URI(address);
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PROTOCOL_NOT_SUPPORTED).sendToTarget();
            }
        }
    }

    /** call this method before connect() */
    public void setDeviceId(String id)
    {
        if (mServerConnection != null && mServerConnection.isOpen())
        {
            Log.w(LOG_TAG, "Someone tried to set device ID after connected to server");
            return;
        }

        mDeviceId = id;
    }

    public void setFriendlyName(String name) {
        mFriendlyName = name;
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

        if (mServerUri == null)
        {
            Log.d(LOG_TAG, "connect() mServerUri == null");
            return;
        }

        mReconnectOnDisconnect = true;
        mServerConnection = new ServerConnection(mServerUri, mDeviceId, mFriendlyName, mServerConnectionEventListener);
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

        try
        {
            mServerConnection.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            // ignore any error when closing connection
        }

        mServerConnection = null;
    }

    private ServerConnection.EventListener mServerConnectionEventListener = new ServerConnection.EventListener()
    {
        @Override
        public void onPermissionGranted()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PERMISSION_GRANTED).sendToTarget();
            }
        }

        @Override
        public void onDisconnected()
        {
            if (mServerConnection != null)
            {
                try
                {
                    mServerConnection.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    // ignore any error when closing connection
                }

                mServerConnection = null;
            }

            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DISCONNECTED).sendToTarget();
            }

            scheduleReconnect();
        }

        @Override
        public void onReady()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_READY).sendToTarget();
            }
        }

        @Override
        public void onProtocolNotSupported()
        {
            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PROTOCOL_NOT_SUPPORTED).sendToTarget();
            }
        }

        @Override
        public void onPaper(int type, String text)
        {
            if (mHandler == null)
            {
                return;
            }

            switch (type)
            {
                case PaperType.TEXT:
                    mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DATA_TEXT, text).sendToTarget();
                    break;
                case PaperType.FILM_MAKING:
                    try
                    {
                        JSONObject json = new JSONObject(text);
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DATA_FILM_MAKING, json).sendToTarget();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case PaperType.FACE_EMOTION_DETECTED:
                    mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DATA_FACE_EMOTION, text).sendToTarget();
                    break;
                case PaperType.PARAMETERS:
                    try
                    {
                        JSONObject json = new JSONObject(text);
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DATA_PARAMETERS, json).sendToTarget();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                case PaperType.JUMP_ACTIVITY:
                    try
                    {
                        JSONObject json = new JSONObject(text);
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_JUMP_ACTIVITY, json).sendToTarget();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    break;
                default:
                    Log.w(LOG_TAG, "Drop paper with unknown type " + type);
            }
        }
    };
}
