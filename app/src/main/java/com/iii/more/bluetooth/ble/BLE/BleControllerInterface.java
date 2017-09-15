package com.iii.more.bluetooth.ble.BLE;

import android.bluetooth.BluetoothDevice;

/**
 * Created by jack on 5/11/17.
 */

public interface BleControllerInterface
{
    void onDeviceFound(BluetoothDevice device);
    
    void onScanFinished();
    
    void onConnected();
    
    void onDisconnected();
    
    void onReady();
    
    void onReceive(String string);
}
