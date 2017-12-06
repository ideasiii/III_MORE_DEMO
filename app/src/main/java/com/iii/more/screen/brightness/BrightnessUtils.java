package com.iii.more.screen.brightness;

import android.app.Activity;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by joe on 2017/7/19.
 */

public class BrightnessUtils
{
    private static final String TAG = BrightnessUtils.class.getSimpleName();
    public static final int MAX_BRIGHTNESS = 255;
    public static final int MIN_BRIGHTNESS = 0;
    
    
    /**
     * 判斷是否開起了自動亮度調節
     */
    public static boolean isAutoBrightness(Activity activity)
    {
        boolean automaticBrightness = false;
        try
        {
            automaticBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System
                    .SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        }
        catch (Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        return automaticBrightness;
    }
    
    /**
     * 獲取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity)
    {
        if (isAutoBrightness(activity))
        {
            return getAutoScreenBrightness(activity);
        }
        else
        
        {
            return getManualScreenBrightness(activity);
        }
    }
    
    /**
     * 獲取手動模式下的屏幕亮度
     */
    public static int getManualScreenBrightness(Activity activity)
    {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try
        {
            nowBrightnessValue = android.provider.Settings.System.getInt(resolver, Settings.System
                    .SCREEN_BRIGHTNESS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }
    
    /**
     * 獲取自動模式下的屏幕亮度
     */
    public static int getAutoScreenBrightness(Activity activity)
    {
        float nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try
        {
            nowBrightnessValue = android.provider.Settings.System.getFloat(resolver,
                    "screen_auto_brightness_adj"); //[-1,1],无法直接获取到Setting中的值，以字符串表示  Log.d(TAG,
            // "[ouyangyj] Original AutoBrightness Value:" + nowBrightnessValue);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        float tempBrightness = nowBrightnessValue + 1.0f; //[0,2]
        float fValue = (tempBrightness / 2.0f) * 225.0f;
        Log.d(TAG, "[ouyangyj] Converted Value: " + fValue);
        return (int) fValue;
    }
    
    /**
     * 設置亮度
     *
     * @param activity   is the Activity Param
     * @param brightness (0 = Min)<= brightness <= (Max = 255)
     */
    public static boolean setBrightness(Activity activity, int brightness)
    {
        if (brightness > MAX_BRIGHTNESS || brightness < MIN_BRIGHTNESS)
        {
            return false;
        }
        else
        {
            try
            {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
                Log.d(TAG, "set lp.screenBrightness == " + lp.screenBrightness);
                activity.getWindow().setAttributes(lp);
                return true;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                return false;
            }
        }
    }
    
    /**
     * 停止自動亮度調節
     */
    public static void stopAutoBrightness(Activity activity)
    {
        try
        {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * 開起亮度自動調節
     */
    public static void startAutoBrightness(Activity activity)
    {
        try
        {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    
}