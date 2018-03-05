package com.iii.more.power;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by joe on 2018/3/1
 */

public class PowerSaving
{
    private static PowerManager pm = null;
    private static PowerManager.WakeLock mWakeLock = null;
    
    public static boolean setOnScreenAlwaysOn(Context context)
    {
        pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (null != pm)
        {
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "[PowerSaving] full weak lock");
            
            if (null != mWakeLock)
            {
                mWakeLock.acquire();
                return true;
            }
        }
        return false;
    }
    
    public static void releaseScreenAlwaysOn(Context context)
    {
        if (null != mWakeLock)
        {
            mWakeLock.release();
        }
    }
    
    
}

