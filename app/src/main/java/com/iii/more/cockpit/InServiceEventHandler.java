package com.iii.more.cockpit;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * 處理只會在 service 內流轉的事件的 handler
 */
public class InServiceEventHandler extends Handler
{
    public static final int IN_SERVICE_EVENT_NEED_RECONNECT = 13248;

    private final WeakReference<CockpitService> mWeakService;
    private final String mLogTag;
    InServiceEventHandler(CockpitService s, String logTag)
    {
        mWeakService = new WeakReference<>(s);
        mLogTag = logTag;
    }

    @Override
    public void handleMessage(Message msg)
    {
        CockpitService service = mWeakService.get();
        if (service == null || !service._instance_IsServiceSpawned())
        {
            Log.d(mLogTag, "[InServiceEventHandler] service == null || !serviceSpawned");
            return;
        }

        if (msg.what == IN_SERVICE_EVENT_NEED_RECONNECT && service.isReconnectOnDisconnect())
        {
            Log.d(mLogTag, "Reconnecting to server");
            service.connect();
        }
    }
}
