package com.iii.more.oobe;

import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.iii.more.oobe.logic.OobeLogicHandler;
import com.iii.more.oobe.logic.OobeLogicParameters;
import com.iii.more.oobe.view.OobeDisplayHandler;

/**
 * Created by joe on 2017/10/27.
 */

public class OobeActivity extends AppCompatActivity
{
    private OobeLogicHandler mOobeLogicHandler = null;
    private OobeDisplayHandler mOobeDisplayHandler = null;
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            handleMessages(msg);
        }
    };
    
    public void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case OobeLogicParameters.CLASS_OOBE_LOGIC:
                break;
            
            default:
                
                break;
            
        }
    }
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        
    }
    
    
}
