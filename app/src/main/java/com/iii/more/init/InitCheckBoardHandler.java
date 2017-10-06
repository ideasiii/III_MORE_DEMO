package com.iii.more.init;

import android.content.Context;

import com.iii.more.main.Parameters;
import com.iii.more.screen.view.progressDialog.ProgressDialog;

import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/14.
 */

//do
//1. connect to 2 floor server BY CMP TCP/IP
//   1)init data, get download image list
//   2)check image file is in Local or not
//   3)start to download them
//2. connect to read pan BY Bluetooth Low Energy


public class InitCheckBoardHandler extends BaseHandler
{
    private ProgressDialog mProgressDialog = null;
    private int flag = 0;
    private int bleState = InitCheckBoardParameters.STATE_READ_PEN_UNKNOWN;
    private int deviceServerState = InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_UNKNOWN;
    private int bluetoothDeviceConnectState = InitCheckBoardParameters.STATE_DEVICE_UNKNOWN;
    
    public void setBLEState(int bleState)
    {
        this.bleState = bleState;
    }
    
    public void setDeviceServerState(int deviceServerState)
    {
        this.deviceServerState = deviceServerState;
    }
    
    public void setBluetoothDeviceState(int bluetoothDeviceState)
    {
        this.bluetoothDeviceConnectState = bluetoothDeviceState;
    }
    
    
    public int getBluetoothDeviceState()
    {
        return bluetoothDeviceConnectState;
    }
    
    public int getBLEState()
    {
        return bleState;
    }
    
    public int getDeviceServerState()
    {
        return deviceServerState;
    }
    
    private Handler mSelfHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            HashMap<String, String> message = new HashMap<>();
            
            mProgressDialog.dismiss();
            
            if (msg.what == ResponseCode.ERR_SUCCESS)
            {
                callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT,
                        InitCheckBoardParameters.METHOD_INIT, message);
            }
            else if (msg.what == ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER)
            {
                callBackMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER, InitCheckBoardParameters.CLASS_INIT,
                        InitCheckBoardParameters.METHOD_INIT, message);
            }
            else if (msg.what == ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND)
            {
                callBackMessage(ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND, InitCheckBoardParameters.CLASS_INIT,
                        InitCheckBoardParameters.METHOD_INIT, message);
            }
            else if (msg.what == ResponseCode.ERR_IO_EXCEPTION)
            {
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, InitCheckBoardParameters.CLASS_INIT,
                        InitCheckBoardParameters.METHOD_INIT, message);
            }
            else
            {
                callBackMessage(ResponseCode.ERR_UNKNOWN, InitCheckBoardParameters.CLASS_INIT,
                        InitCheckBoardParameters.METHOD_INIT, message);
            }
            
            
        }
    };
    
    public InitCheckBoardHandler(Context mContext)
    {
        super(mContext);
        
    }
    
    
    public void init(int flag)
    {
        this.flag = flag;
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.init();
        
        
    }
    
    public void startCheckInit()
    {
        if (null != mProgressDialog)
        {
            Logs.showTrace("[InitCheckBoardHandler] start to init data");
            mProgressDialog.show();
            
            switch (flag)
            {
                case Parameters.MODE_CONNECT_DEVICE:
                    
                    Thread tmp = new Thread(new InitCheckRunnable());
                    tmp.start();
                    
                    
                    //init device Socket Server
                  /*  HashMap<String, String> message = new HashMap<>();
                    message.put("message", "start to init Device Server");
                    callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT,
                            InitCheckBoardParameters.METHOD_DEVICE_SOCKET_SERVER, message);
                    
                    
                    //connect read pen
                    HashMap<String, String> message2 = new HashMap<>();
                    message2.put("message", "start to init read pen");
                    callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT,
                            InitCheckBoardParameters.METHOD_READ_PEN, message2);*/
                    
                    
                    //connect to Bluetooth Device
                    HashMap<String, String> message3 = new HashMap<>();
                    message3.put("message", "start to init bluetooth device");
                    callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT,
                            InitCheckBoardParameters.METHOD_BLUETOOTH_DEVICE, message3);
                    
                    
                    //connect to 2 floor Http Server
                    HashMap<String, String> message4 = new HashMap<>();
                    message4.put("message", "start to init http Server");
                    callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT,
                            InitCheckBoardParameters.METHOD_DEVICE_HTTP_SERVER, message4);
                    
                    
                    break;
                case Parameters.MODE_NOT_CONNECT_DEVICE:
                    mSelfHandler.sendEmptyMessageDelayed(ResponseCode.ERR_SUCCESS, 2000);
                    break;
                
                
            }
            
            
        }
        else
        {
            Logs.showError("[InitCheckBoardHandler] init first!");
        }
        
        
    }
    
    
    private class InitCheckRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    Logs.showTrace("[InitCheckBoard] check init again!");
                    Logs.showTrace("[InitCheckBoard] getBluetoothDeviceState():" + String.valueOf(getBluetoothDeviceState()));
                    Logs.showTrace("[InitCheckBoard] getDeviceServerState():" + String.valueOf(getDeviceServerState()));
                    if (getBluetoothDeviceState() == InitCheckBoardParameters.STATE_DEVICE_CONNECT &&
                            getDeviceServerState() == InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_SUCCESS)
                    {
                        mSelfHandler.sendEmptyMessage(ResponseCode.ERR_SUCCESS);
                        break;
                        
                    }
                    else if (getBluetoothDeviceState() == InitCheckBoardParameters.STATE_DEVICE_DISCONNECT)
                    {
                        mSelfHandler.sendEmptyMessage(ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND);
                        break;
                    }
                    
                    Thread.sleep(InitCheckBoardParameters.CHECK_TIME);
                }
            }
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
        }
    }
    
}
