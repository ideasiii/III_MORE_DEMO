package com.iii.more.main;

/**
 * Created by joe on 2017/4/12.
 */

public abstract class Parameters
{
    
    public static final String CMP_HOST_IP = "175.98.119.121";
    public static final int CMP_HOST_PORT = 2310;
    
    public static final String DEFAULT_DEVICE_ID = "module_01";
    public static final String DMP_HOST_IP = "203.66.168.239";
    public static final int DMP_HOST_PORT = 5377;
    
    // for HTC10 1e-10f;  //for sampo 1e-25f;
    public static final float MEDIA_PLAYED_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//// for HTC10 1e-15f;
    public static final float DEFAULT_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//1e-11f;
    
    public static final String IDEAS_SPHINX_KEY_WORD = "hi ideas";
    
    public static final int MESSAGE_END_WELCOME_LAYOUT = 1232;
    
    public static final String ALERT_DIALOG_WRITE_PERMISSION = "b173de1a-7666-4dfd-8f96-7e8bae023636";
    public static final String ALERT_DIALOG_CONNECTING_DEVICE = "c4b008ba-8b2f-404e-9e44-dd0605486441";
    public static final String ALERT_DIALOG_CONNECTING_DEVICE_BLUETOOTH = "c4b008ba-8b2f-404e-9e44-dd0605486446";
    public static final String ALERT_DIALOG_ENTER_DEVICE_ID = "c4b008ba-8b2f-404e-9e44-dd0605486226";
    public static final String ALERT_DIALOG_CONFIRM_CONNECT_DEVICE = "c4b008ca-8b2f-404e-9e44-dd0605482229";
    
    public static final int MODE_NOT_CONNECT_DEVICE = -1;
    public static final int MODE_CONNECT_DEVICE = 1;
    public static final int MODE_UNKNOWN_DEVICE = 0;
    private static int modeFlag = MODE_UNKNOWN_DEVICE;
    
    public static void setModeFlag(int flag)
    {
        modeFlag = flag;
    }
    
    public static int getModeFlag()
    {
        return modeFlag;
    }
    
    
    
    
    
}
