package com.iii.more.oobe.view;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.iii.more.screen.view.display.DisplayHandler;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

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
    
    
    public SurfaceHolder setMediaDisplayView(int id)
    {
        mSurfaceView = (SurfaceView) ((Activity) (mContext)).findViewById(id);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        
        return mSurfaceHolder;
    }
    
    
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder)
    {
        Logs.showTrace("[OobeDisplayHandler] surfaceCreated");
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
