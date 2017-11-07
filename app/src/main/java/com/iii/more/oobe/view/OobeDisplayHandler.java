package com.iii.more.oobe.view;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.iii.more.screen.view.display.DisplayHandler;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/10/31.
 */

public class OobeDisplayHandler extends DisplayHandler implements SurfaceHolder.Callback
{
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    
    public OobeDisplayHandler(Context context)
    {
        super(context);
    }
    
    
    public void setMediaDisplayView(int id)
    {
        mSurfaceView = (SurfaceView) ((Activity) (mContext)).findViewById(id);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        
    }
    
    public SurfaceHolder getSurfaceHolder()
    {
        return mSurfaceHolder;
    }
    
    public void setVideoViewVisibility(boolean enable)
    {
        if (enable)
        {
            mSurfaceView.setVisibility(View.VISIBLE);
        }
        else
        {
            mSurfaceView.setVisibility(View.INVISIBLE);
        }
    }
    
    public void setImageViewVisibility(boolean enable)
    {
        
        super.setImageViewState(enable);
    }
    
    
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        Logs.showTrace("[OobeDisplayHandler] surfaceCreated");
        HashMap<String, String> message = new HashMap<>();
        message.put("message", "surfaceCreated");
        mSurfaceHolder = surfaceHolder;
        callBackMessage(ResponseCode.ERR_SUCCESS, OobeDisplayParameters.OOBE_DISPLAY_CLASS, OobeDisplayParameters.METHOD_SURFACE_CREATE, message);
        
        
    }
    
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2)
    {
        Logs.showTrace("[OobeDisplayHandler] surfaceChanged");
    }
    
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder)
    {
        Logs.showTrace("[OobeDisplayHandler] surfaceDestroyed");
    }
}
