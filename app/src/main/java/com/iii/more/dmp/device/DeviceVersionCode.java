package com.iii.more.dmp.device;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by joe on 2017/8/15.
 */

public class DeviceVersionCode
{
    private static final String DEVICE_USING = "Device_using";
    private static final String DEVICE_VERSION_CODE = "versioncode";
    private static final String DEFAULT_VERSION_CODE = "0";
    
    public static String getDeiceVersionCode(Context mContext)
    {
        String versionCode = getKey(mContext, DEVICE_VERSION_CODE);
        if (null == versionCode)
        {
            setDeiceVersionCode(mContext, DEFAULT_VERSION_CODE);
            return DEFAULT_VERSION_CODE;
        }
        else
        {
            return versionCode;
        }
    }
    
    public static void setDeiceVersionCode(Context mContext, String versionCode)
    {
        if (null != versionCode)
        {
            saveKey(mContext, DEVICE_VERSION_CODE, versionCode);
        }
        
    }
    
    
    private static void saveKey(Context mContext, String key, String versionCode)
    {
        SharedPreferences.Editor editor = mContext.getSharedPreferences(DEVICE_USING, Context.MODE_PRIVATE).edit();
        editor.putString(key, versionCode);
        editor.apply();
        
    }
    
    private static String getKey(Context mContext, String key)
    {
        SharedPreferences prefs = mContext.getSharedPreferences(DEVICE_USING, Context.MODE_PRIVATE);
        return prefs.getString(key, null);
    }
    
    
}
