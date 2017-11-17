package com.iii.more.main;

/**
 * Created by joe on 2017/4/12.
 */

public abstract class Parameters
{
    public static final boolean OOBE_DEBUG_ENABLE = true;
    
    public static final String ID_CHILD_NAME = "child_name";
    public static final String ID_ROBOT_NAME = "edubot_name";
    public static final String STRING_DEFAULT_ROBOT_NAME = "章魚寶";
    
    
    public static final String TASK_COMPOSER_DATA = "taskcomposerdata";
    
    public static final String CMP_HOST_IP = "175.98.119.121";
    public static final int CMP_HOST_PORT = 2310;
    
    public static final String DEFAULT_DEVICE_ID = "module_01";
    public static final String DMP_HOST_IP = "203.66.168.239";
    public static final int DMP_HOST_PORT = 5377;
    
    public static final String INTERNET_COCKPIT_SERVER_ADDRESS = "ws://smabuild.sytes.net:21098/controlled";
    
    public static final String TRACKER_APP_ID = "1510555888386";
    
    // for HTC10 1e-10f;  //for sampo 1e-25f;
    public static final float MEDIA_PLAYED_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//// for HTC10 1e-15f;
    public static final float DEFAULT_SPHINX_THRESHOLD = 1e-13f;//1e-20f;//1e-11f;
    
    public static final String IDEAS_SPHINX_KEY_WORD = "hi ideas";
    
    public static final int MESSAGE_END_WELCOME_LAYOUT = 1232;
    
    public static final String ALERT_DIALOG_CONNECTING_DEVICE = "c4b008ba-7b21-404e-9e44-dd3605489226";
    public static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN_ERROR = "c4b008ba-8b2f-404e-9e44-dd0605486446";
    public static final String ALERT_DIALOG_ENTER_BLE_READ_PEN_ID = "c4b008ba-8b2f-404e-9e44-dd0605486226";
    public static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN = "c4b008ca-8b2f-404e-9e44-dd0605482229";
    
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
