package com.iii.more.cockpit;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * 駕駛艙的抽象通訊服務類別。雖然叫做駕駛艙，但目前並不能操作任何物件，只能被動接收各種事件。
 */
public abstract class CockpitService extends Service
{
    // Handler.handleMessage() 的 msg.what 可能的值
    public static final int MSG_DATA_IN = 0;
    public static final int MSG_NO_DEVICE = 1;
    public static final int MSG_READY = 2;
    public static final int MSG_PROTOCOL_NOT_SUPPORTED = 3;
    public static final int MSG_PERMISSION_GRANTED = 4;
    public static final int MSG_PERMISSION_NOT_GRANTED = 5;
    public static final int MSG_DISCONNECTED = 6;
    public static final int MSG_CDC_DRIVER_NOT_WORKING = 27;
    public static final int MSG_USB_DEVICE_NOT_WORKING = 28;

    // 拍片用，這種類型的指令將跳過 interrupt logic 判斷，直接影響 app 的視覺、聽覺輸出
    public static final int MSG_FILM_MAKING = 30;

    private static final String LOG_TAG = "CockpitService";

    protected IBinder mBinder = new CockpitBinder();
    protected Context mContext = this;
    protected Handler mHandler;

    @Override
    public void onCreate()
    {
        Log.d(LOG_TAG, "onCreate()");
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
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    /** 開始嘗試與駕駛艙連結 */
    public abstract void connect();

    /**
     * 設定 handler。應該只有 MainApplication 會使用此方法，其他物件應該使用 MainApplication.addCockpitEventListener()
     * 向 MainApplication 註冊 CockpitEventListener 以監聽駕駛艙的事件。
     */
    public void setHandler(Handler mHandler)
    {
        this.mHandler = mHandler;
    }

    public class CockpitBinder extends Binder {
        public CockpitService getService() {
            return CockpitService.this;
        }
    }

    /**
     * Starts service (if not started yet) and then binds context to the service.
     */
    public static void startThenBindService(Context context, Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        java.lang.reflect.Method method;
        boolean isServiceConnected;

        try {
            method = service.getMethod("isServiceConnected");
            Boolean ret = (Boolean) method.invoke(null);
            isServiceConnected = ret;
            Log.i(LOG_TAG, "startService(): " + service.getSimpleName()
                    + ".isServiceConnected() = " + isServiceConnected);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return;
        }

        if (!isServiceConnected) {
            Intent startService = new Intent(context, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }

            Log.d(LOG_TAG, "startService(): start " + service.getSimpleName());
            context.startService(startService);
        }

        Intent bindingIntent = new Intent(context, service);
        context.bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
}
