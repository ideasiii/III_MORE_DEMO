package com.iii.more.cockpit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 駕駛艙的抽象通訊服務類別。雖然叫做駕駛艙，但目前並不能操作任何物件，只能被動接收各種事件。
 */
public abstract class CockpitService extends Service
{
    private static final String LOG_TAG = "CockpitService";

    /** msg.what in Handler.onMessage() */
    public static final int MSG_WHAT = 2147483647;

    // Handler.handleMessage() 的 msg.what 可能的值
    public static final int EVENT_DATA_TEXT = 0;
    public static final int EVENT_NO_DEVICE = 1;
    public static final int EVENT_READY = 2;
    public static final int EVENT_PROTOCOL_NOT_SUPPORTED = 3;
    public static final int EVENT_PERMISSION_GRANTED = 4;
    public static final int EVENT_PERMISSION_NOT_GRANTED = 5;
    public static final int EVENT_DISCONNECTED = 6;
    public static final int EVENT_CDC_DRIVER_NOT_WORKING = 27;
    public static final int EVENT_USB_DEVICE_NOT_WORKING = 28;

    // 拍片用，這種類型的指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出
    public static final int EVENT_DATA_FILM_MAKING = 30;

    // 拯救臉部肌肉，模擬偵測到臉部表情
    public static final int EVENT_DATA_FACE_EMOTION= 31;

    // 修改設定參數
    public static final int EVENT_DATA_PARAMETERS = 32;

    // 從某個 Activity 跳到另一個 Activity
    public static final int EVENT_JUMP_ACTIVITY = 42;

    // 重新連線事件的傳遞延遲時間 (重新連線間隔)
    private static final int RECONNECT_EVENT_DELAY = 1000;

    // 是否在斷線時嘗試重新連線
    protected volatile boolean mReconnectOnDisconnect = true;

    protected InServiceEventHandler mInServiceEventHandler;

    protected IBinder mBinder = new Binder();
    protected Context mContext = this;
    protected Handler mHandler;

    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "onCreate()");

        mInServiceEventHandler = new InServiceEventHandler(this, LOG_TAG);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOG_TAG, "onStartCommand()");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    /**
     * 開始嘗試與駕駛艙連結
     */
    public abstract void connect();

    /**
     * 解除與駕駛艙的連結，且不嘗試重新連線。
     * PS. 不呼叫此方法前，當連線成功後遭遇連線中斷時會「重複」嘗試重新連線。
     */
    public abstract void disconnect();

    /**
     * same as isServiceSpawned(), only used by InServiceEventHandler
     */
    abstract boolean _instance_IsServiceSpawned();

    /**
     * 設定 handler。應該只有 MainApplication 會使用此方法，
     * 其他物件應該使用 MainApplication.setCockpitConnectionEventListener() 或 MainApplication.setCockpitSensorEventListener()
     * 向 MainApplication 註冊 CockpitEventListener 以監聽駕駛艙的事件。
     */
    public void setHandler(Handler h)
    {
        mHandler = h;
    }

    public boolean isReconnectOnDisconnect()
    {
        return mReconnectOnDisconnect;
    }

    protected void scheduleReconnect()
    {
        if (_instance_IsServiceSpawned() && mReconnectOnDisconnect)
        {
            Message delayMsg = mInServiceEventHandler.obtainMessage(InServiceEventHandler.EVENT_NEED_RECONNECT);
            mInServiceEventHandler.sendMessageDelayed(delayMsg, RECONNECT_EVENT_DELAY);
        }
    }

    public class Binder extends android.os.Binder
    {
        public CockpitService getService()
        {
            return CockpitService.this;
        }
    }

    /**
     * Starts service (if not started yet) and then binds context to the service.
     */
    public static void startThenBindService(Context context, Class<? extends CockpitService> service, ServiceConnection serviceConnection, Bundle extras)
    {
        java.lang.reflect.Method method;
        boolean isServiceSpawned;

        try
        {
            method = service.getMethod("isServiceSpawned");
            isServiceSpawned = (Boolean) method.invoke(null);
            Log.i(LOG_TAG, "startService(): " + service.getSimpleName()
                    + ".isServiceSpawned() = " + isServiceSpawned);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
        {
            e.printStackTrace();
            return;
        }

        if (!isServiceSpawned)
        {
            Intent startService = new Intent(context, service);
            if (extras != null && !extras.isEmpty())
            {
                Set<String> keys = extras.keySet();
                for (String key : keys)
                {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }

            Log.d(LOG_TAG, "startService(): start " + service.getSimpleName());
            context.startService(startService);
        }

        Intent bindingIntent = new Intent(context, service);
        context.bindService(bindingIntent, serviceConnection, BIND_AUTO_CREATE);
    }
}
