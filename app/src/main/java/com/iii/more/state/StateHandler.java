package com.iii.more.state;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.iii.more.screen.brightness.BrightnessUtils;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/7/21.
 */

public class StateHandler extends BaseHandler
{
    private Handler mStateHandler = null;
    private BrightnessChangeRunnable mBrightnessChangeRunnable = null;
    
    public StateHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mStateHandler = new Handler();
        mBrightnessChangeRunnable = new BrightnessChangeRunnable();
    }
    
    public void cancelStateRunnable()
    {
        try
        {
            mStateHandler.removeCallbacks(mBrightnessChangeRunnable);
            toDo(BrightnessUtils.MAX_BRIGHTNESS);
        }
        catch (Exception e)
        {
            Logs.showError("[StateHandler] " + e.toString());
        }
    }
    
    public void startWaitState()
    {
        Logs.showTrace("[StateHandler] start to wait State!");
        mStateHandler.postDelayed(mBrightnessChangeRunnable, StateParameters.WAIT_TIME);
    }
    
    private class BrightnessChangeRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            toDo(BrightnessUtils.MIN_BRIGHTNESS);
        }
    }
    
    private void toDo(int brightness)
    {
        
        //set brightness
        BrightnessUtils.setBrightness((Activity) mContext, brightness);
    }
    
    
}
