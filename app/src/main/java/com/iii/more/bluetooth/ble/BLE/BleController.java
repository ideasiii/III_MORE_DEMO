package com.iii.more.bluetooth.ble.BLE;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import sdk.ideas.common.Logs;

/**
 * Created by jack on 5/11/17.
 */

public class BleController
{
    
    private static BleController mController;
    private static Context mContext;
    
    private BluetoothAdapter mBtAdapter;
    private BroadcastReceiver mReceiver;
    private static String deviceName = "";
    
    private BluetoothGattCharacteristic mCharacteristic;
    private BluetoothGatt mGatt;
    private static BleControllerInterface mInterface;
    
    public static BleController getController(Context context, final String DEVICE_NAME, final BleControllerInterface bleControllerInterface)
            throws BleControllerException
    {
        
        if (DEVICE_NAME == null || context == null)
        {
            throw new BleControllerException("device name, context, or activity cannot be null");
        }
        
        if (mController == null)
        {
            mController = new BleController();
        }
        
        mContext = context;
        mInterface = bleControllerInterface;
        deviceName = DEVICE_NAME;
        
        
        // try to turn on bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            // Device does not support Bluetooth
            Logs.showError("[BleController] bluetooth does not support");
        }
        else
        {
            if (!mBluetoothAdapter.isEnabled())
            {
                Logs.showError("[BleController] bluetooth is disable");
                throw new BleControllerException("bluetooth is disable");
            }
            else
            {
                Logs.showTrace("[BleController] bluetooth is enable");
            }
        }
        
        return mController;
    }
    
    public void scanForDevices()
    {
        
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        
        mReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.v(getClass().getName(), "on received " + intent.getAction());
                
                if (intent.getAction() == BluetoothDevice.ACTION_FOUND)
                {
                    
                    String name = BluetoothDevice.EXTRA_DEVICE;
                    BluetoothDevice device = intent.getParcelableExtra(name);
                    
                    if (device.getName() != null && device.getName().equals(deviceName))
                    {
                        mInterface.onDeviceFound(device);
                    }
                    
                }
                else if (intent.getAction() == BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                {
                    mInterface.onScanFinished();
                }
            }
        };
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        
        mContext.registerReceiver(mReceiver, filter);
        
        if (mBtAdapter.isDiscovering())
        {
            mBtAdapter.cancelDiscovery();
        }
        
        mBtAdapter.startDiscovery();
        
    }
    
    public void stopScan()
    {
        mBtAdapter.cancelDiscovery();
    }
    
    public void connectToDevice(BluetoothDevice device)
    {
        mBtAdapter.cancelDiscovery();
        mContext.unregisterReceiver(mReceiver);
        
        device.connectGatt(mContext, false, new BluetoothGattCallback()
        {
            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status)
            {
                super.onServicesDiscovered(gatt, status);
                
                Log.v(getClass().getName(), "onServicesDiscovered " + gatt.getServices().size());
                
                for (int i = 0; i < gatt.getServices().size(); i++)
                {
                    Log.v(getClass().getName(), gatt.getServices().get(i).getUuid().toString());
                    
                    if (i == 2)
                    {
                        mCharacteristic = gatt.getServices().get(i).getCharacteristics().get(0);
                        
                        mGatt.setCharacteristicNotification(mCharacteristic, true);
                        
                        BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptors().get(0);
                        
                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                        
                        gatt.writeDescriptor(descriptor);
                        
                        mInterface.onReady();
                    }
                }
            }
            
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
            {
                super.onConnectionStateChange(gatt, status, newState);
                
                if (newState == BluetoothProfile.STATE_CONNECTED)
                {
                    Log.v(getClass().getName(), "ble connected");
                    
                    mGatt = gatt;
                    mGatt.discoverServices();
                    
                    mInterface.onConnected();
                }
                else if (newState == BluetoothProfile.STATE_DISCONNECTED)
                {
                    Log.v(getClass().getName(), "ble disconnected");
                    
                    mGatt = null;
                    
                    mInterface.onDisconnected();
                }
            }
            
            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic)
            {
                super.onCharacteristicChanged(gatt, characteristic);
                
                byte[] msg = characteristic.getValue();
                
                mInterface.onReceive("0x" + toHex(msg));
            }
        });
    }
    
    
    public static String toHex(byte[] b)
    {
        String mHex = "";
        for (int i = 0; i < b.length; i++)
        {
            mHex = mHex + "" + "0123456789ABCDEF".charAt(0xf & b[i] >> 4) + "0123456789ABCDEF".charAt(b[i] & 0xf);
        }
        return mHex;
    }
    
    
    public void disconnectToDevice()
    {
        mBtAdapter.cancelDiscovery();
        mContext.unregisterReceiver(mReceiver);
    }
    
    
    
    public void send(String string)
    {
        if (mGatt != null)
        {
            byte[] byteValue = string.getBytes();
            mCharacteristic.setValue(byteValue);
            mGatt.writeCharacteristic(mCharacteristic);
        }
    }
    
    
}
