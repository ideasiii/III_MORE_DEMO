package com.iii.more.oobe.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import com.iii.more.screen.view.display.DisplayHandler;

import android.os.Handler;

import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


/**
 * Created by joe on 2017/10/31.
 */

public class OobeDisplayHandler extends DisplayHandler implements View.OnTouchListener
{
    private final Handler isPressedHandler = new Handler();
    private boolean mBooleanIsPressed = false;
    private final Runnable isPressedRunnable = new Runnable()
    {
        public void run()
        {
            //call back
            Logs.showTrace("[OobeDisplayHandler] call back METHOD_ON_LONG_TOUCH");
            callBackMessage(ResponseCode.ERR_SUCCESS, OobeDisplayParameters.OOBE_DISPLAY_CLASS,
                OobeDisplayParameters.METHOD_ON_LONG_TOUCH, new HashMap<String, String>());
            
        }
    };
    
    public OobeDisplayHandler(Context context)
    {
        super(context);
    }
    
    @Override
    public void init()
    {
        super.init();
        super.getRootLayout().setOnTouchListener(this);
    }
    
    
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            Logs.showTrace("[OobeDisplayHandler] onTouch　ACTION_DOWN");
            if (!mBooleanIsPressed)
            {
                // Execute your Runnable after 5000 milliseconds = 5 seconds.
                isPressedHandler.postDelayed(isPressedRunnable, OobeDisplayParameters.ON_LONG_TOUCH_SECOND);
                mBooleanIsPressed = true;
            }
        }
        
        if (motionEvent.getAction() == MotionEvent.ACTION_UP)
        {
            Logs.showTrace("[OobeDisplayHandler] onTouch　ACTION_UP");
            view.performClick();
            if (mBooleanIsPressed)
            {
                mBooleanIsPressed = false;
                isPressedHandler.removeCallbacks(isPressedRunnable);
            }
        }
        return true;
    }
}
