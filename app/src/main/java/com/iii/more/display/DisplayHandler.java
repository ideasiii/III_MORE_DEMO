package com.iii.more.display;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii.more.animate.AnimationHandler;
import com.iii.more.main.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

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
    
    
    public DisplayHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mAnimationHandler = new AnimationHandler(mContext);
        mAnimationHandler.setImageView(mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID));
        mDisplayQueue = new ArrayDeque<DisplayElement>();
        mDisplayHandler = new Handler();
        mDisplayRunnable = new DisplayRunnable();
    }
    
    public void resetAllDisplayViews()
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
                ((ImageView) mHashMapViews.get(key)).setImageResource(0);
            }
        }
    }
    
    public boolean setDisplayJson(JSONObject displayJson)
    {
        try
        {
            if (displayJson.has("enable") && displayJson.getInt("enable") == 1)
            {
                if (displayJson.has("show"))
                {
                    this.mDisplayJsonArray = sortJsonArray(displayJson.getJSONArray("show"));
                    if (this.mDisplayJsonArray == null)
                    {
                        return false;
                    }
                    
                    convertJsonToQueue();
                    
                    return true;
                }
                
                
            }
        }
        catch (JSONException e)
        {
            Logs.showTrace(e.toString());
        }
        return false;
    }
    
    private JSONArray sortJsonArray(JSONArray roughArray)
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
                            return (a.getInt("time") - b.getInt("time"));
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        return 1;
                    }
                });
            }
            for (int i = 0; i < roughArray.length(); i++)
            {
                sortedJsonArray.put(jsonList.get(i));
            }
            
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
                time = mDisplayJsonArray.getJSONObject(i + 1).getInt("time") - mDisplayJsonArray.getJSONObject(i).getInt("time");
            }
            DisplayElement tmp = new DisplayElement(
                    mDisplayJsonArray.getJSONObject(i).getInt("time"),
                    time,
                    mDisplayJsonArray.getJSONObject(i).getString("host") + "/" + mDisplayJsonArray.getJSONObject(i).getString("file"),
                    Color.parseColor(mDisplayJsonArray.getJSONObject(i).getString("color")),
                    mDisplayJsonArray.getJSONObject(i).getString("description"),
                    mDisplayJsonArray.getJSONObject(i).getJSONObject("animation"),
                    mDisplayJsonArray.getJSONObject(i).getJSONObject("text"));
            
            mDisplayQueue.add(tmp);
            
            //for debugging using
            tmp.print();
        }
        
        
    }
    
    public void resetDisplayJson()
    {
        this.mDisplayJson = null;
    }
    
    public void startDisplay()
    {
        DisplayElement tmp = mDisplayQueue.poll();
        mDisplayHandler.postDelayed(mDisplayRunnable, tmp.nextTime);
    }
    
    private void toDo()
    {
        if (null != mDisplayQueue)
        {
            DisplayElement display = mDisplayQueue.poll();
            if (null != display)
            {
                //set next time
                if (display.nextTime != -1)
                {
                    mDisplayHandler.postDelayed(mDisplayRunnable, display.nextTime);
                }
            }
            
            mAnimationHandler.setAnimateJsonBehavior(display.animation);
            mAnimationHandler.startAnimate();
            
            
            
            
            
            
        }
    }
    
    
    class DisplayRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            toDo();
        }
    }
    
    
}
