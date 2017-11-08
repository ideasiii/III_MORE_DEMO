package com.iii.more.cockpit;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

import com.iii.more.main.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

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

    private boolean mRemoteServerConnected;
    private ServerConnectionThread serverConnectionThread = null;
    private String mDeviceId;

    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "onCreate()");
        super.onCreate();

        mRemoteServerConnected = false;
        serviceConnected = true;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();

        serviceConnected = false;

        if (serverConnectionThread != null)
        {
            serverConnectionThread.client.close();
        }
    }

    public static boolean isServiceConnected()
    {
        return serviceConnected;
    }

    public void setDeviceId(String id) {
        if (mRemoteServerConnected) {
            Log.w(LOG_TAG, "Someone tried to set device ID after connected to server");
            return;
        }

        mDeviceId = id;
    }

    @Override
    public void connect()
    {
        Log.d(LOG_TAG, "connect()");

        serverConnectionThread = new ServerConnectionThread();
        serverConnectionThread.start();
    }

    private class ServerConnectionThread extends Thread
    {
        private static final int SERVER_MESSAGE_TYPE_SET_DEVICE_ID = 0;
        private static final int SERVER_MESSAGE_TYPE_COMMAND_TEXT = 1;

        // 拍片用，這種類型的指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出
        private static final int SERVER_MESSAGE_TYPE_COMMAND_FILM_MAKING = 2;

        private static final String SERVER_MESSAGE_KEY_TYPE = "type";
        private static final String SERVER_MESSAGE_KEY_TEXT = "text";

        private WebSocketClient client;
        private boolean registered = false;

        @Override
        public void run()
        {
            connectToServer();
        }

        private void connectToServer()
        {
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

            client = new WebSocketClient(uri, new Draft_6455())
            {
                @Override
                public void onOpen(ServerHandshake handshakedata)
                {
                    Log.d(LOG_TAG, "WebSocket onOpen, handshakedata status message = "
                            + handshakedata.getHttpStatusMessage());
                    mRemoteServerConnected = true;

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_PERMISSION_GRANTED, 0).sendToTarget();
                    }

                    registerDeviceId(mDeviceId);
                }

                @Override
                public void onMessage(String message)
                {
                    Log.d(LOG_TAG, "WebSocket onMessage, message = `" + message + "`");

                    if (mHandler != null)
                    {
                        processServerPushMessage(message);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote)
                {
                    Log.d(LOG_TAG, "WebSocket onClose, reason = " + reason);
                    mRemoteServerConnected = false;

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DISCONNECTED, 0).sendToTarget();
                    }
                }

                @Override
                public void onError(Exception ex)
                {
                    Log.d(LOG_TAG, "WebSocket onError, Exception = " + ex.toString() + ", message = " + ex.getMessage());
                    mRemoteServerConnected = false;

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DISCONNECTED, 0).sendToTarget();
                    }
                }
            };

            client.connect();
        }

        /**
         * 向伺服器註冊我們的 device ID，伺服器之後就會推送相關的訊息過來
         */
        private void registerDeviceId(String deviceId)
        {
            Log.d(LOG_TAG, "WebSocket registering device ID `" + deviceId + "`");

            String registerMessage = "{\"type\":"
                    + SERVER_MESSAGE_TYPE_SET_DEVICE_ID
                    + ",\"text\":\"" + deviceId + "\"}";
            client.send(registerMessage);
        }

        /**
         * 處理伺服器推送過來的訊息，並嘗試廣播至 app 內其他組件
         */
        private void processServerPushMessage(String message)
        {
            JSONObject json = getJsonObject(message);
            if (json == null)
            {
                return;
            }

            try
            {
                boolean success = json.getBoolean("success");
                if (success && !registered)
                {
                    // registration of device ID OK, ready for receiving commands
                    registered = true;

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_READY, 0).sendToTarget();
                    }

                    return;
                }
            }
            catch (JSONException e)
            {
                // maybe not a registration response, continue trying
            }

            int commandType;
            try
            {
                commandType = json.getInt(SERVER_MESSAGE_KEY_TYPE);
                Log.d(LOG_TAG, "Server pushed a message with type " + commandType);
            }
            catch (JSONException e)
            {
                Log.w(LOG_TAG, "Got malformed JSON (missing int field `type`)");
                e.printStackTrace();
                return;
            }

            switch (commandType)
            {
                case SERVER_MESSAGE_TYPE_COMMAND_TEXT:
                    String textCommandFromServer = stripTextFromCommand(json);
                    if (textCommandFromServer != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DATA_TEXT, 0, textCommandFromServer).sendToTarget();
                    }
                    break;
                case SERVER_MESSAGE_TYPE_COMMAND_FILM_MAKING:
                    JSONObject jsonCommandFromServer = stripJsonFromCommand(json);
                    if (jsonCommandFromServer != null)
                    {
                        mHandler.obtainMessage(CockpitService.MSG_WHAT, EVENT_DATA_FILM_MAKING, 0, jsonCommandFromServer).sendToTarget();
                    }
                    break;
                default:
                    Log.w(LOG_TAG, "Got unknown type of command (" + commandType + ")");
            }

        }

        private JSONObject getJsonObject(String src)
        {
            try
            {
                return new JSONObject(src);
            }
            catch (JSONException e)
            {
                Log.w(LOG_TAG, "Got garbage from server, body = `" + src + "`");
                e.printStackTrace();
                return null;
            }
        }

        private String stripTextFromCommand(JSONObject json)
        {
            try
            {
                return json.getString(SERVER_MESSAGE_KEY_TEXT);
            }
            catch (JSONException e)
            {
                Log.w(LOG_TAG, "Got malformed JSON (missing string field `text`)");
                e.printStackTrace();
                return null;
            }
        }

        private JSONObject stripJsonFromCommand(JSONObject json)
        {
            String jsonString;
            try
            {
                jsonString = json.getString(SERVER_MESSAGE_KEY_TEXT);
            }
            catch (JSONException e)
            {
                Log.w(LOG_TAG, SERVER_MESSAGE_KEY_TEXT + " does not exist in command from server `text`)");
                return null;
            }

            try
            {
                return new JSONObject(jsonString);
            }
            catch (JSONException e)
            {
                Log.w(LOG_TAG, "String in `text` cannot be parsed to JSONObject");
                return null;
            }
        }
    }
}