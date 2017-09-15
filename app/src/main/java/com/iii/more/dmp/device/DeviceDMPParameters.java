package com.iii.more.dmp.device;

/**
 * Created by joe on 2017/8/15.
 */

public class DeviceDMPParameters
{
    public static final int CLASS_DMP_DEVICE = 9234;
    
    public static final int METHOD_INIT = 0;
    public static final int METHOD_DISPLAY = 1;
    private static String device_id = "";
    public static final int RECONNECT_TIME = 10000;
    
    public static void setDeviceID(String device_id)
    {
        DeviceDMPParameters.device_id = device_id;
    }
    
    public static String getDeviceID()
    {
        return device_id;
    }
}
