package com.iii.more.dmp.device;

import android.content.Context;

import com.iii.more.dmp.DMPHandler;
import com.iii.more.dmp.DMPParameters;
import com.iii.more.download.image.ImageDownloadHandler;
import com.iii.more.download.image.ImageDownloadParameters;
import com.iii.more.storage.StoragePath;

import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/15.
 */

public class DeviceDMPHandler extends BaseHandler
{
    private DMPHandler mDMPHandler = null;
    private ImageDownloadHandler mImageDownloadHandler = null;
    
    
    private Handler selfHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case DMPParameters.CLASS_DMP:
                    handleDMP(msg);
                    break;
                
                case ImageDownloadParameters.CLASS_IMAGE_DOWNLOAD:
                    handleImageDownload(msg);
                    break;
            }
        }
    };
    
    public DeviceDMPHandler(Context context)
    {
        super(context);
    }
    
    public void init(String ip, int port, String uuid)
    {
        mImageDownloadHandler = new ImageDownloadHandler(mContext);
        mImageDownloadHandler.setHandler(selfHandler);
        
        mDMPHandler = new DMPHandler(mContext);
        mDMPHandler.setHandler(selfHandler);
        mDMPHandler.setIPAndPort(ip, port);
        mDMPHandler.setBindData(DeviceDMPParameters.getDeviceID(), uuid,
                DeviceVersionCode.getDeiceVersionCode(mContext));
        mDMPHandler.startReceiveThread(DMPParameters.METHOD_INIT);
    }
    
    public void stopConnectedThread()
    {
        if (null != mDMPHandler)
        {
            mDMPHandler.stopReceiveThread();
        }
    }
    
    private void handleDMP(Message msg)
    {
        HashMap<String, String> message = new HashMap<>();
        // Logs.showTrace("[DeviceDMPHandler] get data:" + msg.obj);
        switch (msg.arg2)
        {
            case DMPParameters.METHOD_INIT:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    mDMPHandler.sendBindData();
                }
                else
                {
                    callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DeviceDMPParameters.CLASS_DMP_DEVICE,
                            DeviceDMPParameters.METHOD_INIT, message);
                }
                break;
            case DMPParameters.METHOD_REBIND:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    Logs.showTrace("[DeviceDMPHandler] rebind data");
                    mDMPHandler.sendBindData();
                }
                else
                {
                    selfHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Logs.showTrace("[DeviceDMPHandler] start to rebind server");
                            mDMPHandler.startReceiveThread(DMPParameters.METHOD_REBIND);
                        }
                    }, DeviceDMPParameters.RECONNECT_TIME);
                }
                break;
            case DMPParameters.METHOD_BIND:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    downloadImage(msg.obj);
                }
                else
                {
                    Logs.showError("[DeviceDMPHandler] something error while get bind data");
                    //rebind
                    selfHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Logs.showTrace("[DeviceDMPHandler] start to rebind server");
                            mDMPHandler.startReceiveThread(DMPParameters.METHOD_REBIND);
                        }
                    }, DeviceDMPParameters.RECONNECT_TIME);
                    
                }
                break;
            case DMPParameters.METHOD_EXPRESSION:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    try
                    {
                        // {"current_expression":"OCTOBO_Expressions-07.png","device_id":"dhbf0qX7T9","imei":"98bf8da6-2d82-401d-9823-e276c1076501","id":1,"rfid_card":"A","status":0}
                        JSONObject tmp = new JSONObject(((HashMap<String, String>) msg.obj).get("message"));
                        Logs.showTrace("[DeviceDMPHandler] get expression:" + tmp.toString());
                        if (tmp.has("current_expression"))
                        {
                            JSONObject animate = new JSONObject();
                            animate.put("type", 1);
                            animate.put("duration", 1000);
                            animate.put("repeat", 0);
                            animate.put("interpolate", 1);
                            //create display json
                            JSONObject data = new JSONObject();
                            data.put("time", 0);
                            data.put("host", "https://ryejuice.sytes.net/edubot/OCTOBO_Expressions/");
                            data.put("file", tmp.getString("current_expression"));
                            data.put("color", "#FFA0C9EC");
                            data.put("description", "快樂");
                            data.put("animation", animate);
                            data.put("text", new JSONObject());
                            JSONArray show = new JSONArray();
                            show.put(data);
                            
                            JSONObject display = new JSONObject();
                            display.put("enable", 1);
                            display.put("show", show);
                            
                            message.put("display", display.toString());
                        }
                        if (tmp.has("rfid_card"))
                        {
                            message.put("rfid_card", tmp.getString("rfid_card"));
                        }
                        Logs.showTrace("[DeviceDMPHandler] convert to display expression:" + message.get("display"));
                        callBackMessage(ResponseCode.ERR_SUCCESS, DeviceDMPParameters.CLASS_DMP_DEVICE,
                                DeviceDMPParameters.METHOD_DISPLAY, message);
                    }
                    catch (JSONException e)
                    {
                        Logs.showError("[DeviceDMPHandler] convert to json ERROR" + e.toString());
                    }
                    
                }
                
                break;
        }
        
    }
    
    private void handleImageDownload(Message msg)
    {
        HashMap<String, String> message = new HashMap<>();
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("[DeviceDMPHandler] update image successful!!");
            callBackMessage(ResponseCode.ERR_SUCCESS, DeviceDMPParameters.CLASS_DMP_DEVICE,
                    DeviceDMPParameters.METHOD_INIT, message);
        }
        else if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
        {
            callBackMessage(ResponseCode.ERR_IO_EXCEPTION, DeviceDMPParameters.CLASS_DMP_DEVICE,
                    DeviceDMPParameters.METHOD_INIT, message);
            
        }
    }
    
    private void downloadImage(Object obj)
    {
        HashMap<String, String> savePath = new HashMap<>();
        HashMap<String, String> message = (HashMap<String, String>) obj;
        try
        {
            JSONObject data = new JSONObject(message.get("message"));
            if (data.has("imgs"))
            {
                JSONArray imgURLs = data.getJSONArray("imgs");
                String savePartPath = StoragePath.getSavePath();
                Logs.showTrace("[DeviceDMPHandler] savePath: " + savePartPath);
                
                //set Version
                String version = imgURLs.getJSONObject(0).getString("version");
                
                DeviceVersionCode.setDeiceVersionCode(mContext, version);
                
                
                for (int i = 0; i < imgURLs.length(); i++)
                {
                    JSONObject tmp = imgURLs.getJSONObject(i);
                    Logs.showTrace("[DeviceDMPHandler] file name:" + getRealFileName(tmp.getString("file_name")));
                    savePath.put(tmp.getString("file_name"), savePartPath + "/" + getRealFileName(tmp.getString("file_name")));
                    
                    
                }
                
            }
            mImageDownloadHandler.init(savePath);
            
            // Logs.showTrace("[DeviceDMPHandler] get data" + data.toString());
        }
        catch (JSONException e)
        {
            Logs.showError("[DeviceDMPHandler] " + e.toString());
        }
        
        //  mImageDownloadHandler.init();
    }
    
    private String getRealFileName(String url)
    {
        String[] data = url.split("/");
        return data[data.length - 1];
    }
    
    
}
