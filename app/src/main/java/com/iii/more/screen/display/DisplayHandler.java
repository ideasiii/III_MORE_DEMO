package com.iii.more.screen.display;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.iii.more.animate.AnimationHandler;
import com.iii.more.screen.view.ViewHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/7/11.
 */

public class DisplayHandler extends BaseHandler
{
    private JSONArray mDisplayJsonArray = null;
    private AnimationHandler mAnimationHandler = null;
    private HashMap<Integer, View> mHashMapViews = null;
    private Handler mDisplayHandler = null;
    private DisplayRunnable mDisplayRunnable = null;
    private ArrayDeque<DisplayElement> mDisplayQueue = null;
    RequestListener mRequestListener = new RequestListener<String, GlideDrawable>()
    {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource)
        {
            Logs.showError("[RequestListener] Exception: " + e.toString() + " Model: " + model + " isFirstResource: " + String.valueOf(isFirstResource));
            
            ((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID)).setImageResource(0);
            return false;
        }
        
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
        {
            Logs.showTrace("[RequestListener] Model: " + model + " isFromMemoryCache: " + String.valueOf(isFromMemoryCache) + " isFirstResource: " + String.valueOf(isFirstResource));
            return false;
        }
    };
    
    public DisplayHandler(@NonNull Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mAnimationHandler = new AnimationHandler(mContext);
        mAnimationHandler.setView(mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID));
        mDisplayQueue = new ArrayDeque<>();
        mDisplayHandler = new Handler(Looper.getMainLooper());
        mDisplayRunnable = new DisplayRunnable();
    }
    
    public void resetAllDisplayViews()
    {
        if (null != mDisplayHandler && null != mAnimationHandler && null != mHashMapViews)
        {
            mDisplayHandler.removeCallbacks(mDisplayRunnable);
            
            //cancel animation
            mAnimationHandler.animateCancel();
            
            //clear view to default statue
            for (int key : mHashMapViews.keySet())
            {
                if (mHashMapViews.get(key) instanceof TextView)
                {
                    ((TextView) mHashMapViews.get(key)).setText("");
                }
                else if (mHashMapViews.get(key) instanceof ImageView)
                {
                    //((ImageView) mHashMapViews.get(key)).setImageResource(0);
                    Glide.with(mContext)
                            .load("")
                            .listener(mRequestListener)
                            .into(new GlideDrawableImageViewTarget(((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID))));
                }
                else if (mHashMapViews.get(key) instanceof RelativeLayout)
                {
                    ViewHandler.setBackgroundColor(DisplayParameters.DEFAULT_BACKGROUND_COLOR,
                            mHashMapViews.get(key));
                }
            }
        }
        else
        {
            Logs.showError("[DisplayHandler] something ERROR in resetAllDisplayViews method");
        }
    }
    
    public void setDisplayView(@NonNull HashMap<Integer, View> hashMapViews)
    {
        this.mHashMapViews = hashMapViews;
    }
    
    public boolean setDisplayJson(@NonNull JSONObject displayJson)
    {
        Logs.showTrace("[DisplayHandler] in setDisplayJson!!");
        Logs.showTrace("[DisplayHandler] " + displayJson.toString());
        try
        {
            if (displayJson.has(DisplayParameters.STRING_JSON_KEY_ENABLE) && displayJson.getInt(DisplayParameters.STRING_JSON_KEY_ENABLE) == 1)
            {
                if (displayJson.has(DisplayParameters.STRING_JSON_KEY_SHOW))
                {
                    this.mDisplayJsonArray = sortJsonArray(displayJson.getJSONArray(DisplayParameters.STRING_JSON_KEY_SHOW));
                    if (this.mDisplayJsonArray == null)
                    {
                        return false;
                    }
                    
                    convertJsonToQueue();
                    
                    return true;
                }
            }
            else
            {
                //not show
                this.resetAllDisplayViews();
                return true;
            }
            
            
        }
        catch (JSONException e)
        {
            Logs.showTrace(e.toString());
        }
        return false;
    }
    
    private JSONArray sortJsonArray(@NonNull JSONArray roughArray)
    {
        try
        {
            JSONArray sortedJsonArray = new JSONArray();
            List<JSONObject> jsonList = new ArrayList<JSONObject>();
            
            for (int i = 0; i < roughArray.length(); i++)
            {
                jsonList.add(roughArray.getJSONObject(i));
            }
            if (roughArray.length() != 1)
            {
                Collections.sort(jsonList, new Comparator<JSONObject>()
                {
                    public int compare(JSONObject a, JSONObject b)
                    {
                        try
                        {
                            return (a.getInt(DisplayParameters.STRING_JSON_KEY_TIME) - b.getInt(DisplayParameters.STRING_JSON_KEY_TIME));
                        }
                        catch (JSONException e)
                        {
                            Logs.showError("[DisplayHandler] Sort Display Queue ERROR" + e.toString());
                        }
                        return 1;
                    }
                });
            }
            for (int i = 0; i < roughArray.length(); i++)
            {
                sortedJsonArray.put(jsonList.get(i));
            }
            
            jsonList.clear();
            jsonList = null;
            
            return sortedJsonArray;
        }
        catch (JSONException e)
        {
            Logs.showError(e.toString());
        }
        return null;
    }
    
    private void convertJsonToQueue() throws JSONException
    {
        mDisplayQueue.clear();
        for (int i = 0; i < mDisplayJsonArray.length(); i++)
        {
            int time = -1;
            if (i + 1 != mDisplayJsonArray.length())
            {
                time = mDisplayJsonArray.getJSONObject(i + 1).getInt(DisplayParameters.STRING_JSON_KEY_TIME) - mDisplayJsonArray.getJSONObject(i).getInt(DisplayParameters.STRING_JSON_KEY_TIME);
            }
            DisplayElement tmp = new DisplayElement(
                    mDisplayJsonArray.getJSONObject(i).getInt(DisplayParameters.STRING_JSON_KEY_TIME),
                    time,
                    mDisplayJsonArray.getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_HOST) + "/" + mDisplayJsonArray.getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_FILE),
                    Color.parseColor(mDisplayJsonArray.getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_COLOR)),
                    mDisplayJsonArray.getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_DESCRIPTION),
                    mDisplayJsonArray.getJSONObject(i).getJSONObject(DisplayParameters.STRING_JSON_KEY_ANIMATION),
                    mDisplayJsonArray.getJSONObject(i).getJSONObject(DisplayParameters.STRING_JSON_KEY_TEXT));
            
            mDisplayQueue.add(tmp);
            
            //for debugging using
            tmp.print();
        }
        
        
    }
    
    public void resetDisplayData()
    {
        this.mDisplayJsonArray = null;
        this.mDisplayQueue.clear();
        
    }
    
    public void startDisplay()
    {
        toDo();
    }
    
    private void toDo()
    {
        if (null != mDisplayQueue)
        {
            final DisplayElement display = mDisplayQueue.poll();
            if (null != display)
            {
                //set next time
                if (display.nextTime != -1)
                {
                    mDisplayHandler.postDelayed(mDisplayRunnable, display.nextTime);
                }
                
                
                mAnimationHandler.setAnimateJsonBehavior(display.animation);
                mAnimationHandler.startAnimate();
                
                ViewHandler.setBackgroundColor(display.backgroundColor, mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID));
                
                Glide.with(mContext)
                        .load(display.imageURL)
                        .listener(mRequestListener)
                        .into(new GlideDrawableImageViewTarget(((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID))));
                
                
            }
            
            
        }
    }
    
    
    private class DisplayRunnable implements Runnable
    {
        @Override
        public void run()
        {
            toDo();
        }
    }
    
    
}
