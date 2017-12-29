package com.iii.more.clock;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.iii.more.clock.logic.ClockLogicParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.main.R;

import com.iii.more.clock.display.ClockDisplayHandler;
import com.iii.more.clock.logic.ClockLogicHandler;
import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.main.Parameters;
import com.iii.more.main.TTSParameters;
import com.iii.more.screen.view.display.DisplayParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/12/26
 */

public class ClockActivity extends AppCompatActivity
{
    //Jugo server connect
    private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    
    private ClockDisplayHandler mClockDisplayHandler = null;
    private ClockLogicHandler mClockLogicHandler = null;
    
    private String clockParameters = "";
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[ClockActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String
                .valueOf(msg.what) + " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        clockParameters = intent.getExtras().getString(Parameters.CLOCK_INTENT_DATA, "");
        if (clockParameters.isEmpty())
        {
            finish();
        }
        else
        {
            init();
        }
        
    }
    
    @Override
    protected void onStart()
    {
        Logs.showTrace("[ClockActivity] onStart");
        mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
            SemanticWordCMPParameters.TYPE_REQUEST_STORY, clockParameters);
        super.onStart();
    }
    
    private void init()
    {
        setContentView(R.layout.clock);
        
        CMPHandler.setIPAndPort(Parameters.CMP_HOST_IP, Parameters.CMP_HOST_PORT);
        mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
        mSemanticWordCMPHandler.setHandler(mHandler);
        
        mClockDisplayHandler = new ClockDisplayHandler(this);
        mClockDisplayHandler.init();
        
        mClockLogicHandler = new ClockLogicHandler(this);
        
    }
    
    
    private void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case CMPParameters.CLASS_CMP_SEMANTIC_WORD:
                handleMessageSWCMP(msg);
                break;
            case DisplayParameters.CLASS_DISPLAY:
                handleMessageDisplay(msg);
                break;
            case ClockLogicParameters.CLASS_CLOCK_LOGIC:
                handleMessageClockLogic(msg);
                break;
            
        }
    }
    
    private void handleMessageClockLogic(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case ClockLogicParameters.METHOD_MEDIA_STREAM:
                    if (message.get("state").equals("complete"))
                    {
                        //jump to MainActivity
                        Logs.showTrace("[ClockActivity] now jump to MainActivity");
                    }
                    
                    break;
                case ClockLogicParameters.METHOD_TTS:
                    if (message.get("message").equals(TTSParameters.ID_SERVICE_UNKNOWN) || message.get
                        ("message").equals(TTSParameters.ID_SERVICE_IO_EXCEPTION))
                    {
                        //something happened , and jump to MainActivity
                        Logs.showError("[ClockActivity] something happened!");
                        
                    }
                    break;
            }
        }
        
    }
    
    private void handleMessageSWCMP(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("Get Response from CMP_SEMANTIC_WORD");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            if (message.containsKey("message"))
            {
                analysisSemanticWord(message.get("message"));
            }
            else
            {
                mClockLogicHandler.onError(TTSParameters.ID_SERVICE_UNKNOWN);
            }
        }
        else
        {
            //異常例外處理
            Logs.showError("[ClockActivity] ERROR while sending message to CMP Controller");
            
            //call logicHandler onERROR
            mClockLogicHandler.onError(TTSParameters.ID_SERVICE_UNKNOWN);
        }
    }
    
    
    public void analysisSemanticWord(String data)
    {
        try
        {
            JSONObject responseData = new JSONObject(data);
            
            if (responseData.has("display"))
            {
                Logs.showTrace("[ClockActivity] display Data:" + responseData.getJSONObject("display")
                    .toString());
                if (responseData.getJSONObject("display").length() != 0)
                {
                    mClockDisplayHandler.resetAllDisplayViews();
                    mClockDisplayHandler.setDisplayJson(responseData.getJSONObject("display"));
                    mClockDisplayHandler.startDisplay();
                }
                else
                {
                    Logs.showError("[ClockActivity] No Display Data!!");
                }
            }
            
            if (responseData.has("activity"))
            {
                Logs.showTrace("[ClockActivity] activity Data:" + responseData.getJSONObject("activity")
                    .toString());
                if (responseData.getJSONObject("activity").length() != 0)
                {
                    mClockLogicHandler.setActivityJson(responseData.getJSONObject("activity"));
                    mClockLogicHandler.startActivity();
                }
                else
                {
                    Logs.showError("[ClockActivity] No Activity Data!!");
                }
                
            }
            else
            {
                Logs.showError("[ClockActivity] No Activity Data!!");
                
            }
        }
        catch (JSONException e)
        {
            mClockLogicHandler.onError(TTSParameters.ID_SERVICE_IO_EXCEPTION);
            Logs.showError("[ClockActivity] analysisSemanticWord Exception:" + e.toString());
        }
    }
    
    
    private void handleMessageDisplay(Message msg)
    {
        switch (msg.arg2)
        {
            case DisplayParameters.METHOD_CLICK:
                Logs.showTrace("[ClockActivity] Screen get On Click!");
                
                break;
            default:
                break;
            
        }
    }
    
    @Override
    protected void onDestroy()
    {
        mClockLogicHandler.endAll();
        mClockDisplayHandler.killAll();
        super.onDestroy();
    }
}
