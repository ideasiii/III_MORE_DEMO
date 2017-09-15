package com.iii.more.dmp;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;

import android.os.Handler;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/22.
 */

public class DMPHandler extends BaseHandler
{
    private Socket mSocket = null;
    private boolean mIsConnect = false;
    private Thread mReceiveThread = null;
    
    private String mDeviceID = "";
    private String mVersionCode = "";
    private String mUuid = "";
    
    private String mStrIP = "";
    private int mPort = 0;
    private boolean mIsInit = false;
    
    public DMPHandler(Context context)
    {
        super(context);
    }
    
    
    public void setIPAndPort(String strIP, int nPort)
    {
        mStrIP = strIP;
        mPort = nPort;
        mIsInit = true;
    }
    
    public void setBindData(@NonNull String deviceID, @NonNull String uuid, @NonNull String versionCode)
    {
        this.mDeviceID = deviceID;
        this.mUuid = uuid;
        this.mVersionCode = versionCode;
        
    }
    
    public void sendBindData()
    {
        if (mIsConnect)
        {
            
            Thread thread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    
                    JSONObject data = new JSONObject();
                    try
                    {
                        data.put(DMPParameters.JSON_STRING_DEVICE_ID, mDeviceID);
                        data.put(DMPParameters.JSON_STRING_UUID, mUuid);
                        data.put(DMPParameters.JSON_STRING_VERSION, mVersionCode);
                        int status = DMPController.dmpSend(DMPParameters.REQUEST_BIND, data.toString(), new DMPParameters.DMP_PACKET(), mSocket);
                        errorCodeHandle(status);
                        
                    }
                    catch (JSONException e)
                    {
                        Logs.showError("[DMPHandler] sendBindData ERROR" + e.toString());
                    }
                    catch (Exception e)
                    {
                        Logs.showError("[DMPHandler] sendBindData ERROR" + e.toString());
                    }
                    
                }
            });
            thread.start();
            
        }
        else
        {
            Logs.showError("[DMPHandler] is not connect!!");
        }
    }
    
    private void errorCodeHandle(int status)
    {
        
        switch (status)
        {
            case DMPParameters.STATUS_ERR_EXCEPTION:
                
                break;
            case DMPParameters.STATUS_ERR_INVALID_PARAM:
                break;
            case DMPParameters.STATUS_ERR_IO_EXCEPTION:
                break;
            case DMPParameters.STATUS_ERR_PACKET_CONVERT:
                break;
            case DMPParameters.STATUS_ERR_PACKET_LENGTH:
                
                break;
            case DMPParameters.STATUS_ERR_SOCKET_INVALID:
                break;
        }
        if (status != DMPParameters.STATUS_SUCCESS)
        {
            HashMap<String, String> message = new HashMap<>();
            Logs.showError("[DMPHandler] getErrorMessage: " + String.valueOf(status));
            callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DMPParameters.CLASS_DMP, DMPParameters.METHOD_INIT, message);
        }
        
        
    }
    
    
    public void startReceiveThread(int flag)
    {
        Logs.showTrace("[DMPHandler] start to startReceiveThread");
        //a thread to receive message
        Logs.showTrace("[DMPHandler] start to stopReceiveThread");
        stopReceiveThread();
        if (mIsInit)
        {
            if (null == mReceiveThread)
            {
                Logs.showTrace("[DMPHandler] start to create ReceiveRunnable");
                mReceiveThread = new Thread(new ReceiveRunnable(flag));
                mReceiveThread.start();
            }
        }
        else
        {
            //callback init first
            Logs.showError("[DMPHandler] init first");
        }
        
    }
    
    public void stopReceiveThread()
    {
        if (null != mReceiveThread)
        {
            if (mReceiveThread.isAlive())
            {
                mReceiveThread.interrupt();
            }
            mReceiveThread = null;
        }
    }
    
    public void startEnquireLinkThread()
    {
        //a thread to send link
        if (mIsConnect)
        {
            
        }
        else
        {
            //callback init first
            
            
        }
    }
    
    
    private class ReceiveRunnable implements Runnable
    {
        private int flag = 0;
        
        public ReceiveRunnable(int flag)
        {
            this.flag = flag;
        }
        
        @Override
        public void run()
        {
            
            try
            {
                mSocket = new Socket();
                
                mSocket.setKeepAlive(true);
                mSocket.setSoTimeout(0);
                mSocket.connect(new InetSocketAddress(mStrIP, mPort));
                mIsConnect = true;
                HashMap<String, String> mess = new HashMap<>();
                callBackMessage(ResponseCode.ERR_SUCCESS, DMPParameters.CLASS_DMP, flag, mess);
            }
            catch (IOException e)
            {
                Logs.showError("[DMPHandler] some error while connect server ERROR:" + e.toString());
                HashMap<String, String> mess = new HashMap<>();
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DMPParameters.CLASS_DMP, flag, mess);
                return;
            }
            try
            {
                boolean anyERROR = false;
                Logs.showTrace("");
                
                boolean test = false;
                
                while (!Thread.currentThread().isInterrupted())
                {
                    
                    DMPParameters.DMP_PACKET receivePacket = new DMPParameters.DMP_PACKET();
                    int status = DMPController.dmpReceive(receivePacket, mSocket);
                    if (status == DMPParameters.STATUS_SUCCESS)
                    {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", receivePacket.dmpBody);
                        
                        switch (receivePacket.dmpHeader.command_id)
                        {
                            case DMPParameters.RESPONSE_BIND:
                                callBackMessage(ResponseCode.ERR_SUCCESS, DMPParameters.CLASS_DMP,
                                        DMPParameters.METHOD_BIND, message);
                                
                                break;
                            case DMPParameters.REQUEST_EXPRESSION_PUSH:
                                
                                // {"current_expression":"OCTOBO_Expressions-07.png","device_id":"dhbf0qX7T9","imei":"98bf8da6-2d82-401d-9823-e276c1076501","id":1,"rfid_card":"A","status":0}
                                callBackMessage(ResponseCode.ERR_SUCCESS, DMPParameters.CLASS_DMP,
                                        DMPParameters.METHOD_EXPRESSION, message);
                                
                                //send response to server
                                JSONObject data = new JSONObject();
                                try
                                {
                                    data.put(DMPParameters.JSON_STRING_DEVICE_ID, mDeviceID);
                                    data.put(DMPParameters.JSON_STRING_UUID, mUuid);
                                    data.put(DMPParameters.JSON_STRING_MESSAGE, "Processing Successes");
                                }
                                catch (JSONException e)
                                {
                                    Logs.showError(e.toString());
                                }
                                
                                DMPController.dmpSend(DMPParameters.RESPONSE_EXPRESSION_PUSH,
                                        data.toString(), new DMPParameters.DMP_PACKET(), mSocket);
                                
                                break;
                            case DMPParameters.REQUEST_ENQUIRE_LINK:
                                //##################### test code start #####################
                                if (test)
                                {
                                    test = false;
                                    Looper.prepare();
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            String testString = "{\"current_expression\":\"OCTOBO_Expressions-07.png\",\"device_id\":\"dhbf0qX7T9\",\"imei\":\"98bf8da6-2d82-401d-9823-e276c1076501\",\"id\":1,\"rfid_card\":\"A\",\"status\":0}";
                                            HashMap<String, String> messageTest = new HashMap<String, String>();
                                            messageTest.put("message", testString);
                                            callBackMessage(ResponseCode.ERR_SUCCESS, DMPParameters.CLASS_DMP,
                                                    DMPParameters.METHOD_EXPRESSION, messageTest);
                                        }
                                        
                                        
                                    }, 15000);
                                    Looper.loop();
                                    
                                }
                                //##################### test code end #####################
                                
                                DMPController.dmpSend(DMPParameters.RESPONSE_ENQUIRE_LINK,
                                        "", new DMPParameters.DMP_PACKET(), mSocket);
                                
                                break;
                            
                            case DMPParameters.RESPONSE_ENQUIRE_LINK:
                                break;
                            
                            
                        }
                    }
                    else if (status == DMPParameters.STATUS_ERR_IO_EXCEPTION
                            || status == DMPParameters.STATUS_ERR_SOCKET_INVALID)
                    {
                        anyERROR = true;
                        Logs.showError("[DMPHandler] Broken Socket IO Exception!");
                        //callback to reconnect it
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "Broken Socket IO Exception!");
                        callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DMPParameters.CLASS_DMP,
                                DMPParameters.METHOD_BIND, message);
                        break;
                        
                    }
                    else
                    {
                        Logs.showError("[DMPHandler] ERROR code:" + String.valueOf(status));
                    }
                    
                    receivePacket = null;
                    
                }
                if (null != mSocket && !mSocket.isClosed())
                {
                    Logs.showTrace("[DMPHandler] Close Socket");
                    mSocket.close();
                    
                }
            }
            catch (IOException e)
            {
                Logs.showError("[DMPHandler] " + e.toString());
            }
        }
    }
    
    private class EnquireLinkRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            
        }
    }
    
    
}
