package com.iii.more.bluetooth.ble;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.iii.more.bluetooth.ble.BLE.BleController;
import com.iii.more.bluetooth.ble.BLE.BleControllerException;
import com.iii.more.bluetooth.ble.BLE.BleControllerInterface;

import java.util.Calendar;
import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/15.
 */

public class ReadPenBLEHandler extends BaseHandler
{
    private BleController controller = null;
    
    private boolean connected = false;
    
    private BleControllerInterface mBleControllerInterface = new BleControllerInterface()
    {
        @Override
        public void onDeviceFound(BluetoothDevice device)
        {
            Logs.showTrace("[ReadPenBLEHandler] OnDeviceFound");
            if (!connected)
            {
                controller.connectToDevice(device);
                connected = true;
            }
        }
        
        @Override
        public void onScanFinished()
        {
            Logs.showTrace("[ReadPenBLEHandler] OnScanFinished");
            if (!connected)
            {
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "ERROR While connect read pen");
                callBackMessage(ResponseCode.ERR_UNKNOWN, ReadPenBLEParameters.CLASS_ReadPenBLE, ReadPenBLEParameters.METHOD_CONNECT, message);
                disconnect();
            }
        }
        
        @Override
        public void onConnected()
        {
            Logs.showTrace("[ReadPenBLEHandler] OnConnected");
        }
        
        @Override
        public void onDisconnected()
        {
            Logs.showTrace("[ReadPenBLEHandler] OnDisconnected");
        }
        
        @Override
        public void onReady()
        {
            Logs.showTrace("[ReadPenBLEHandler] OnReady");
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "ble connect success and ready");
            callBackMessage(ResponseCode.ERR_SUCCESS, ReadPenBLEParameters.CLASS_ReadPenBLE,
                    ReadPenBLEParameters.METHOD_CONNECT, message);
            
        }
        
        @Override
        public void onReceive(final String string)
        {
            Logs.showTrace("[ReadPenBLEHandler] OnReceive: " + string);
            //    0x000001FE
            String data = string.replace("AA000000000064000000000000000000", "").replace("0x", "");
            Long dataNum = Long.parseLong(data, 16);
            
            HashMap<String, String> message = new HashMap<>();
            message.put("message", String.valueOf(dataNum));
            
            callBackMessage(ResponseCode.ERR_SUCCESS, ReadPenBLEParameters.CLASS_ReadPenBLE,
                    ReadPenBLEParameters.METHOD_RECEIVE, message);
        }
    };
    
    
    public ReadPenBLEHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        try
        {
            controller = BleController.getController(mContext, ReadPenBLEParameters.deviceName,
                    mBleControllerInterface);
            
            controller.scanForDevices();
            
        }
        catch (BleControllerException e)
        {
            Logs.showError("[ReadPenBLEHandler] BleControllerException: " + e.getMessage().toString());
            if (e.getMessage().toString().equals("bluetooth is disable"))
            {
                Logs.showError("[ReadPenBLEHandler] BleControllerException: " + e.toString());
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "please open bluetooth!");
                
                
                callBackMessage(ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER, ReadPenBLEParameters.CLASS_ReadPenBLE,
                         ReadPenBLEParameters.METHOD_CONNECT, message);
            }
        }
    }
    
    public void disconnect()
    {
        controller.disconnectToDevice();
    }
    
    
}
