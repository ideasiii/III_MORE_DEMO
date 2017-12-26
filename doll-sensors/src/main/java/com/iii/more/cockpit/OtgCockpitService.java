package com.iii.more.cockpit;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.felhr.usbserial.CDCSerialDevice;
import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 透過 USB OTG 連結的駕駛艙
 */
public class OtgCockpitService extends CockpitService
{
    /** used in Handler to distinguish us from other subclass of CockpitService...... */
    public static final int MSG_ARG1 = 621561778;

    private static final String LOG_TAG = "OtgCockpitService";

    private static final String BROADCAST_ACTION_USB_PERMISSION_RESULT = "com.iii.OtgCockpitService._internal.USB_PERMISSION";
    private static final String BROADCAST_ACTION_SYSTEM_USB_ATTACHED = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    private static final String BROADCAST_ACTION_SYSTEM_USB_DETACHED = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private static final int BAUD_RATE = 9600;
    private static final String RECEIVED_DATA_CHARSET_NAME = "UTF-8";

    private static boolean serviceSpawned = false;

    private UsbManager mUsbManager;
    private UsbDevice mUsbDevice;
    private UsbDeviceConnection mUsbDeviceConnection;
    private UsbSerialDevice mUsbSerialDevice;

    private boolean mSerialPortConnected;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(LOG_TAG, "onCreate()");

        mSerialPortConnected = false;
        serviceSpawned = true;
        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOG_TAG, "onDestroy()");

        unregisterIntentReceiver();
        serviceSpawned = false;
        super.onDestroy();
    }

    public static boolean isServiceSpawned()
    {
        return serviceSpawned;
    }

    @Override
    boolean _instance_IsServiceSpawned()
    {
        return serviceSpawned;
    }

    @Override
    public void connect()
    {
        Log.d(LOG_TAG, "connect()");

        if (!mSerialPortConnected)
        {
            registerIntentReceiver();
            findSerialPortDevice();
        }
    }

    @Override
    public void disconnect()
    {
        Log.d(LOG_TAG, "disconnect()");

        if (!mSerialPortConnected || mUsbSerialDevice == null)
        {
            Log.d(LOG_TAG, "!mSerialPortConnected || mUsbSerialDevice == null");
            return;
        }

        mReconnectOnDisconnect = false;
        mSerialPortConnected = false;
        mUsbSerialDevice.close();
    }

    private void findSerialPortDevice()
    {
        Log.d(LOG_TAG, "findSerialPortDevice()");

        // This snippet will try to open the first encountered USB device connected, excluding usb root hubs
        HashMap<String, UsbDevice> usbDevices = mUsbManager.getDeviceList();
        if (!usbDevices.isEmpty())
        {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet())
            {
                mUsbDevice = entry.getValue();
                int deviceVID = mUsbDevice.getVendorId();
                int devicePID = mUsbDevice.getProductId();

                if (deviceVID != 0x1d6b && devicePID != 0x0001 && devicePID != 0x0002 && devicePID != 0x0003)
                {
                    Log.d(LOG_TAG, "found device");
                    requestUserPermission();
                    keep = false;
                }
                else
                {
                    mUsbDeviceConnection = null;
                    mUsbDevice = null;
                }

                if (!keep)
                {
                    break;
                }
            }

            if (!keep)
            {
                // There is no USB devices connected (but usb host were listed)
                Log.d(LOG_TAG, "no USB devices connected (but usb host were listed)");

                if (mHandler != null)
                {
                    mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_NO_DEVICE).sendToTarget();
                }
            }
        }
        else
        {
            // There is no USB devices connected.
            Log.d(LOG_TAG, "no USB devices connected");

            if (mHandler != null)
            {
                mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_NO_DEVICE).sendToTarget();
            }
        }
    }

    private void registerIntentReceiver()
    {
        Log.d(LOG_TAG, "registerIntentReceiver()");

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION_USB_PERMISSION_RESULT);
        filter.addAction(BROADCAST_ACTION_SYSTEM_USB_DETACHED);
        filter.addAction(BROADCAST_ACTION_SYSTEM_USB_ATTACHED);

        registerReceiver(mUsbEventReceiver, filter);
    }

    private void unregisterIntentReceiver()
    {
        unregisterReceiver(mUsbEventReceiver);
    }

    /*
     * Request user permission. The response will be received in the BroadcastReceiver
     */
    private void requestUserPermission()
    {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(BROADCAST_ACTION_USB_PERMISSION_RESULT), 0);
        mUsbManager.requestPermission(mUsbDevice, pendingIntent);
    }

    /*
     *  Data received from serial port will be received here. Just populate onReceivedData with your code
     *  In this particular example. byte stream is converted to String and send to UI thread to
     *  be treated there.
     */
    private UsbSerialInterface.UsbReadCallback mUsbReadCallback = new UsbSerialInterface.UsbReadCallback()
    {
        @Override
        public void onReceivedData(byte[] data)
        {
            if (mHandler == null)
            {
                return;
            }

            try
            {
                String decoded = new String(data, RECEIVED_DATA_CHARSET_NAME);
                mHandler.obtainMessage(MSG_WHAT, EVENT_DATA_TEXT, 0, decoded).sendToTarget();
            }
            catch (UnsupportedEncodingException e)
            {
                Log.wtf(LOG_TAG, RECEIVED_DATA_CHARSET_NAME + " encoding is not supported on this mUsbDevice (!?)");
                e.printStackTrace();
            }
        }
    };

    /*
     * Different notifications from OS will be received here (USB attached, detached, permission responses...)
     * About BroadcastReceiver: http://developer.android.com/reference/android/content/BroadcastReceiver.html
     */
    private final BroadcastReceiver mUsbEventReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String intentAction = intent.getAction();
            if (!serviceSpawned || intentAction == null)
            {
                return;
            }

            if (intentAction.equals(BROADCAST_ACTION_USB_PERMISSION_RESULT))
            {
                Log.i(LOG_TAG, "USB permission event received");

                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) // User accepted our USB mUsbDeviceConnection. Try to open the mUsbDevice as a serial port
                {
                    Log.i(LOG_TAG, "User granted USB usage permission");

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PERMISSION_GRANTED).sendToTarget();
                    }

                    mUsbDeviceConnection = mUsbManager.openDevice(mUsbDevice);
                    mSerialPortConnected = true;
                    new Thread(new OpenPortRunnable()).run();
                }
                else // User not accepted our USB mUsbDeviceConnection. Send an Intent to the Main Activity
                {
                    Log.i(LOG_TAG, "User denied USB usage permission");

                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PERMISSION_NOT_GRANTED).sendToTarget();
                    }
                }
            }
            else if (intentAction.equals(BROADCAST_ACTION_SYSTEM_USB_ATTACHED))
            {
                Log.i(LOG_TAG, "Received 'USB is attached' event");

                if (!mSerialPortConnected)
                {
                    findSerialPortDevice(); // A USB mUsbDevice has been attached. Try to open it as a Serial port
                }
            }
            else if (intentAction.equals(BROADCAST_ACTION_SYSTEM_USB_DETACHED))
            {
                Log.i(LOG_TAG, "Received 'USB is detached' event");

                if (mHandler != null)
                {
                    mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_DISCONNECTED).sendToTarget();
                }

                mSerialPortConnected = false;
                mUsbSerialDevice.close();

                scheduleReconnect();
            }
        }
    };

    /*
     * A simple thread to open a serial port.
     * Although it should be a fast operation. moving usb operations away from UI thread is a good thing.
     */
    private class OpenPortRunnable implements Runnable
    {
        @Override
        public void run()
        {
            mUsbSerialDevice = UsbSerialDevice.createUsbSerialDevice(mUsbDevice, mUsbDeviceConnection);
            if (mUsbSerialDevice != null)
            {
                if (mUsbSerialDevice.open())
                {
                    mUsbSerialDevice.setBaudRate(BAUD_RATE);
                    mUsbSerialDevice.setDataBits(UsbSerialInterface.DATA_BITS_8);
                    mUsbSerialDevice.setStopBits(UsbSerialInterface.STOP_BITS_1);
                    mUsbSerialDevice.setParity(UsbSerialInterface.PARITY_NONE);
                    mUsbSerialDevice.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                    mUsbSerialDevice.read(mUsbReadCallback);

                    // Everything went as expected. Send an intent to MainActivity
                    Log.i(LOG_TAG, "USB serial port is ready");
                    if (mHandler != null)
                    {
                        mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_READY).sendToTarget();
                    }
                }
                else
                {
                    Log.i(LOG_TAG, "USB serial port could not be opened");

                    // Serial port could not be opened, maybe an I/O error or if CDC driver was chosen, it does not really fit
                    // Send an Intent to Main Activity
                    if (mUsbSerialDevice instanceof CDCSerialDevice)
                    {
                        if (mHandler != null)
                        {
                            mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_CDC_DRIVER_NOT_WORKING).sendToTarget();
                        }
                    }
                    else
                    {
                        if (mHandler != null)
                        {
                            mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_USB_DEVICE_NOT_WORKING).sendToTarget();
                        }
                    }
                }
            }
            else
            {
                Log.e(LOG_TAG, "USB OTG functionality is not supported on this device?");

                if (mHandler != null)
                {
                    mHandler.obtainMessage(MSG_WHAT, MSG_ARG1, EVENT_PROTOCOL_NOT_SUPPORTED).sendToTarget();
                }
            }
        }
    }
}
