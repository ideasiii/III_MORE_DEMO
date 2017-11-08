package com.iii.more.cockpit.internet;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

/**
 * 與伺服器連線的 WebSocket client
 */
class ServerConnection extends WebSocketClient
{
    private static final String LOG_TAG = "InternetCockpitClient";

    private static final int PING_INTERVAL = 3000;

    private static final int SERVER_MESSAGE_TYPE_SET_DEVICE_ID = 0;
    private static final int SERVER_MESSAGE_TYPE_COMMAND_TEXT = 1;

    // 拍片用，這種類型的指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出
    private static final int SERVER_MESSAGE_TYPE_COMMAND_FILM_MAKING = 2;

    private static final String SERVER_MESSAGE_KEY_TYPE = "type";
    private static final String SERVER_MESSAGE_KEY_TEXT = "text";

    private final String mDeviceId;
    private final EventListener mEventListener;
    private boolean registered = false;

    public ServerConnection(URI uri, String deviceId, EventListener l)
    {
        super(uri, new Draft_6455());

        mDeviceId = deviceId;
        mEventListener = l;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        Log.d(LOG_TAG, "WebSocket onOpen, handshakedata status message = "
                + handshakedata.getHttpStatusMessage());

        if (mEventListener != null)
        {
            mEventListener.onPermissionGranted();
        }

        registerDeviceId(mDeviceId);
    }

    @Override
    public void onMessage(String message)
    {
        Log.d(LOG_TAG, "WebSocket onMessage, message = `" + message + "`");
        processServerPushMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        Log.d(LOG_TAG, "WebSocket onClose, reason = " + reason);

        if (mEventListener != null)
        {
            mEventListener.onDisconnected();
        }
    }

    @Override
    public void onError(Exception ex)
    {
        Log.d(LOG_TAG, "WebSocket onError, Exception = " + ex.toString() + ", message = " + ex.getMessage());

        if (mEventListener != null)
        {
            mEventListener.onDisconnected();
        }
    }

    /**
     * 向伺服器註冊我們的 device ID，伺服器之後就會推送相關的訊息過來
     */
    private void registerDeviceId(String deviceId)
    {
        Log.d(LOG_TAG, "Registering device ID `" + deviceId + "`");

        String registerMessage = "{\"type\":"
                + SERVER_MESSAGE_TYPE_SET_DEVICE_ID
                + ",\"text\":\"" + deviceId + "\"}";
        send(registerMessage);
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

                if (mEventListener != null)
                {
                    mEventListener.onReady();
                }

                new Thread()
                {
                    @Override
                    public void run()
                    {
                        while(true)
                        {
                            try
                            {
                                sendPing();
                                Thread.sleep(PING_INTERVAL);
                            }
                            catch(Exception e)
                            {
                                return;
                            }
                        }

                    }
                }.start();


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
                if (mEventListener != null && textCommandFromServer != null)
                {
                    mEventListener.onDataText(textCommandFromServer);
                }

                break;
            case SERVER_MESSAGE_TYPE_COMMAND_FILM_MAKING:
                JSONObject jsonCommandFromServer = stripJsonFromCommand(json);
                if (mEventListener != null && jsonCommandFromServer != null)
                {
                    mEventListener.onDataFilmMaking(jsonCommandFromServer);
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

    interface EventListener
    {
        /** 連上 server，尚未註冊 device ID */
        void onPermissionGranted();

        /** 使用者不允許裝置的使用時的回呼 */
        //void onPermissionNotGranted();

        /** 找不到可用的裝置時的回呼，通常發生在應用程式剛啟動而裝置未插入時 */
        //void onNoDevice();

        /** 斷線時的回呼 */
        void onDisconnected();

        /** 已註冊 device ID，準備接收資料時的回呼 */
        void onReady();

        /** 不支援此種裝置連接方式時的回呼 (應該很少發生吧?) */
        void onProtocolNotSupported();

        void onDataText(String text);

        void onDataFilmMaking(JSONObject json);
    }
}
