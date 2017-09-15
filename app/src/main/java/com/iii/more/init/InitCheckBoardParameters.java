package com.iii.more.init;

/**
 * Created by joe on 2017/6/20.
 */

public class InitCheckBoardParameters
{
    public static final int CLASS_INIT = 9744;
    public static final int METHOD_INIT = 0;
    
    public static final int CHECK_TIME = 1000;
    
    public static final int METHOD_READ_PEN = 1;
    public static final int METHOD_DEVICE_SERVER = 2;
    
    
    public static final int STATE_READ_PEN_CONNECT = 1;
    public static final int STATE_READ_PEN_UNKNOWN = 0;
    public static final int STATE_READ_PEN_DISCONNECT = -1;
    
    public static final int STATE_DEVICE_SERVER_INIT_SUCCESS = 1;
    public static final int STATE_DEVICE_SERVER_INIT_UNKNOWN = 0;
    public static final int STATE_DEVICE_SERVER_INIT_FAIL = -1;
    
}
