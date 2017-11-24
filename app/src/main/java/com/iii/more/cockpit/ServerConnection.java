package com.iii.more.cockpit;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * 與伺服器連線的 WebSocket client
 */
class ServerConnection extends WebSocketClient
{
    private static final String LOG_TAG = "InternetCockpitClient";

    private static final int PING_INTERVAL = 15000;

    private static final int SERVER_MESSAGE_ACTION_TYPE_SIGN_IN = 0;
    private static final int SERVER_MESSAGE_ACTION_TYPE_PAPER = 1;

    // 只是文字，這種類型的指令內的文字會被當作 OTG 裝置傳出的字串
    private static final int SERVER_PAPER_TYPE_COMMAND_TEXT = 0;

    // 拍片用，這種類型的指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出
    private static final int SERVER_PAPER_TYPE_COMMAND_FILM_MAKING = 1;

    private static final int API_VERSION = 1;

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
    public void connect()
    {
        if (uri != null && uri.getScheme().equals("wss"))
        {
            Log.d(LOG_TAG, "connect() URI scheme is wss, performing additional setup");
            try
            {
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, null, null);
                SSLSocketFactory factory = sslContext.getSocketFactory();
                setSocket(factory.createSocket());
            }
            catch (NoSuchAlgorithmException | KeyManagementException | IOException e)
            {
                e.printStackTrace();
                if (mEventListener != null)
                {
                    mEventListener.onProtocolNotSupported();
                }
                return;
            }
        }

        super.connect();
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

        registerDeviceId();
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
    private void registerDeviceId()
    {
        Log.d(LOG_TAG, "Registering with device ID `" + mDeviceId + "`");

        try
        {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", mDeviceId);
            jsonBody.put("apiVersion", API_VERSION);

            JSONObject jsonRoot = new JSONObject();
            jsonRoot.put("action", SERVER_MESSAGE_ACTION_TYPE_SIGN_IN);
            jsonRoot.put("body", jsonBody);

            String registerMessage = jsonRoot.toString();
            send(registerMessage);
        }
        catch (JSONException e)
        {
            Log.d(LOG_TAG, "Cannot generate registration JSON: " + e.getMessage());
        }
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
            int action = json.getInt("action");
            switch (action)
            {
                case SERVER_MESSAGE_ACTION_TYPE_SIGN_IN:
                    handleSetIdResponse(json);
                    break;
                case SERVER_MESSAGE_ACTION_TYPE_PAPER:
                    handlePaperPush(json);
                    break;
                default:
                    Log.w(LOG_TAG, "Got unknown type of action (" + action + ")");
            }
        }
        catch (JSONException e)
        {
            Log.w(LOG_TAG, "Server pushed malformed message: " + e.getMessage());
        }
    }

    private void handleSetIdResponse(JSONObject root) throws JSONException
    {
        JSONObject actionBody = root.getJSONObject("body");
        boolean success = actionBody.getBoolean("success");

        if (success)
        {
            if (!registered)
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
                        while (true)
                        {
                            try
                            {
                                sendPing();
                                Thread.sleep(PING_INTERVAL);
                            }
                            catch (InterruptedException ie)
                            {
                                continue;
                            }
                            catch (Exception e)
                            {
                                return;
                            }
                        }
                    }
                }.start();

                Log.i(LOG_TAG, "Registration OK, message: " + actionBody.getString("message"));
            }
            else
            {
                Log.w(LOG_TAG, "Got duplicate registration response?");
            }
        }
        else
        {
            Log.w(LOG_TAG, "Registration failed, reason: " + actionBody.getString("message"));
        }
    }

    private void handlePaperPush(JSONObject root) throws JSONException
    {
        JSONObject actionBody = root.getJSONObject("body");
        int paperType = actionBody.getInt("paperType");
        JSONObject paperBody = actionBody.getJSONObject("paperBody");
        String text = paperBody.getString("text");

        switch (paperType)
        {
            case SERVER_PAPER_TYPE_COMMAND_TEXT:
                if (mEventListener != null && text != null)
                {
                    mEventListener.onDataText(text);
                }
                break;
            case SERVER_PAPER_TYPE_COMMAND_FILM_MAKING:
                JSONObject filmMakingJson = new JSONObject(text);
                if (mEventListener != null && filmMakingJson != null)
                {
                    mEventListener.onDataFilmMaking(filmMakingJson);
                }
                break;
            default:
                Log.w(LOG_TAG, "Got unknown paper type (" + paperType + ")");
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
