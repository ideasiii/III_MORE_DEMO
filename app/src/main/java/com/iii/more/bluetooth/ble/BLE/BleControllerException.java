package com.iii.more.bluetooth.ble.BLE;

import android.util.Log;

/**
 * Created by jack on 5/11/17.
 */

public class BleControllerException extends Exception
{
    private String errorMessage = "";
    
    public BleControllerException(String msg)
    {
        super(msg);
        errorMessage = msg;
    }
    
    @Override
    public String getMessage()
    {
        Log.d("MESSAGE",errorMessage);
        return errorMessage;
    }
}
