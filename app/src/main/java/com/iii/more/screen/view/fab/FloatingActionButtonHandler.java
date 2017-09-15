package com.iii.more.screen.view.fab;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.iii.more.main.R;
import com.scalified.fab.ActionButton;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/10.
 */

public class FloatingActionButtonHandler extends BaseHandler implements View.OnClickListener, View.OnTouchListener
{
    private ActionButton mActionButton = null;
    
    public FloatingActionButtonHandler(Context context)
    {
        super(context);
    }
    
    public void setID(@IdRes int id)
    {
        mActionButton = (ActionButton) ((AppCompatActivity) mContext).findViewById(id);
    }
    
    public void init(int imageID, float imageSize, @NonNull ActionButton.Animations showAnimation,
            @NonNull ActionButton.Animations hideAnimation)
    {
        if (null != mActionButton)
        {
            mActionButton.setImageResource(imageID);
            mActionButton.setImageSize(imageSize);
            mActionButton.setShowAnimation(showAnimation);
            mActionButton.setHideAnimation(hideAnimation);
            mActionButton.setOnClickListener(this);
        }
        
    }
    
    @Override
    public void onClick(View v)
    {
        HashMap<String, String> message = new HashMap<>();
        //   message.put("FAB_X", String.valueOf((v.getLeft() + v.getRight()) / 2));
        //  message.put("FAB_Y", String.valueOf((v.getTop() + v.getBottom()) / 2));
        message.put("message", "success");
        message.put("onClick", String.valueOf(v.getId()));
        callBackMessage(ResponseCode.ERR_SUCCESS, FloatingActionButtonParameters.CLASS_FAB,
                FloatingActionButtonParameters.METHOD_ON_CLICK, message);
        
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            if (v.getId() == R.id.fab_btn)
            {
                Log.d("onFabClick", "FAB Action DOWN");
            }
            Log.d("onFabClick", "Action DOWN");
        }
        return false;
    }
    
    public void hide()
    {
        if (null != mActionButton)
        {
            mActionButton.hide();
        }
    }
    
    public void show()
    {
        if (null != mActionButton)
        {
            mActionButton.show();
        }
    }
    
    public int getFabX()
    {
        return ((mActionButton.getLeft() + mActionButton.getRight()) / 2);
    }
    
    public int getFabY()
    {
        return ((mActionButton.getTop() + mActionButton.getBottom()) / 2);
        
    }
    
    public float getFabRadius()
    {
        return (1f * mActionButton.getWidth() / 2f);
    }
}
