package com.iii.more.screen.view.display;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iii.more.animate.AnimationHandler;
import com.iii.more.main.R;
import com.iii.more.main.Tools;
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
import java.util.NoSuchElementException;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/7/11.
 */

public class DisplayHandler extends BaseHandler implements View.OnClickListener, View.OnTouchListener
{
    private JSONArray mDisplayJsonArray = null;
    private AnimationHandler mAnimationHandler = null;
    private HashMap<Integer, View> mHashMapViews = null;
    private Handler mDisplayHandler = null;
    private DisplayRunnable mDisplayRunnable = null;
    private ArrayDeque<DisplayElement> mDisplayQueue = null;
    private ArrayDeque<DisplayElement> mSaveDisplayQueue = null;
    private DisplayElement theLastDisplayElement = null;
    private DisplayElement theFinalDisplayElement = null;
    
    public DisplayHandler(@NonNull Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mAnimationHandler = new AnimationHandler(mContext);
        mAnimationHandler.setView(mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID));
        mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID).setOnClickListener(this);
        mDisplayQueue = new ArrayDeque<>();
        mDisplayHandler = new Handler(Looper.getMainLooper());
        mDisplayRunnable = new DisplayRunnable();
    }
    
    public void pauseDisplaying(int seconds)
    {
        Logs.showTrace("[DisplayHandler] pauseDisplaying in: " + String.valueOf(seconds));
        //停止Runnable
        killAll();
        
        if (null == mSaveDisplayQueue)
        {
            mSaveDisplayQueue = new ArrayDeque<>();
        }
        else
        {
            mSaveDisplayQueue.clear();
        }
        
        if (null == theLastDisplayElement)
        {
            Logs.showError("[DisplayHandler] theLastDisplayElement is null");
        }
        else
        {
            Logs.showTrace("[DisplayHandler] theLastDisplayElement.timeDuring: " + String.valueOf
                (theLastDisplayElement.timeDuring));
            
            Logs.showTrace("[DisplayHandler] mDisplayQueue size: " + String.valueOf(mDisplayQueue.size()));
            DisplayElement tmp = mDisplayQueue.poll();
            if (null != tmp)
            {
                theLastDisplayElement.nextTime = tmp.timeDuring - seconds;
                
                if (theLastDisplayElement.nextTime < -1)
                {
                    theLastDisplayElement.nextTime = -1;
                }
                
                Logs.showTrace("[DisplayHandler]####theLastDisplayElement#####");
                theLastDisplayElement.print();
                
                
                mSaveDisplayQueue.add(theLastDisplayElement);
                mSaveDisplayQueue.add(tmp);
                
                Logs.showTrace("[DisplayHandler] total mDisplayQueue Data size:" + String.valueOf
                    (mDisplayQueue.size()));
                while (true)
                {
                    DisplayElement tmp2 = mDisplayQueue.poll();
                    if (null == tmp2)
                    {
                        break;
                    }
                    else
                    {
                        mSaveDisplayQueue.add(tmp2);
                        tmp2.print();
                    }
                }
                Logs.showTrace("[DisplayHandler] total mSaveDisplayQueue Data size:" + String.valueOf
                    (mSaveDisplayQueue.size()));
            }
            else
            {
                Logs.showError("[DisplayHandler] tmp is null!");
                Logs.showError("[DisplayHandler] get the final element" );
               
                mSaveDisplayQueue.add(theFinalDisplayElement);
            }
        }
        
    }
    
    public void resumeDisplaying()
    {
        if (null != mSaveDisplayQueue)
        {
            mDisplayQueue = mSaveDisplayQueue.clone();
            startDisplay();
        }
    }
    
    protected void setImageViewState(boolean enable)
    {
        for (int key : mHashMapViews.keySet())
        {
            if (mHashMapViews.get(key) instanceof ImageView)
            {
                if (enable)
                {
                    mHashMapViews.get(key).setVisibility(View.VISIBLE);
                }
                else
                {
                    mHashMapViews.get(key).setVisibility(View.GONE);
                }
                break;
            }
        }
    }
    
    public void setImageViewImageFromDrawable(int id)
    {
        for (int key : mHashMapViews.keySet())
        {
            if (mHashMapViews.get(key) instanceof ImageView)
            {
                Glide.with(mContext).load(id).apply(RequestOptions.diskCacheStrategyOf
                    (DiskCacheStrategy.NONE)).into((ImageView) mHashMapViews.get(key));
                break;
            }
        }
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
                    ((ImageView) mHashMapViews.get(key)).setImageResource(R.drawable.p_o_normal);
                    // Glide.with(mContext)
                    //        .load("")
                    //        .listener(mRequestListener)
                    //        .into(new GlideDrawableImageViewTarget(((ImageView) mHashMapViews.get
                    // (DisplayParameters.IMAGE_VIEW_ID))));
                }
                else if (mHashMapViews.get(key) instanceof RelativeLayout)
                {
                    
                    ViewHandler.setBackgroundColor(mContext.getResources().getColor(DisplayParameters
                        .DEFAULT_BACKGROUND_COLOR), mHashMapViews.get(key));
                }
            }
        }
        else
        {
            Logs.showError("[DisplayHandler] something ERROR in resetAllDisplayViews method");
        }
    }
    
    public void killAll()
    {
        if (null != mDisplayHandler && null != mAnimationHandler)
        {
            mDisplayHandler.removeCallbacks(mDisplayRunnable);
            
            mAnimationHandler.animateCancel();
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
            if (displayJson.has(DisplayParameters.STRING_JSON_KEY_ENABLE) && displayJson.getInt
                (DisplayParameters.STRING_JSON_KEY_ENABLE) == 1)
            {
                if (displayJson.has(DisplayParameters.STRING_JSON_KEY_SHOW))
                {
                    this.mDisplayJsonArray = sortJsonArray(displayJson.getJSONArray(DisplayParameters
                        .STRING_JSON_KEY_SHOW));
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
                resetDisplayData();
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
                            return (a.getInt(DisplayParameters.STRING_JSON_KEY_TIME) - b.getInt
                                (DisplayParameters.STRING_JSON_KEY_TIME));
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
                time = mDisplayJsonArray.getJSONObject(i + 1).getInt(DisplayParameters
                    .STRING_JSON_KEY_TIME) - mDisplayJsonArray.getJSONObject(i).getInt(DisplayParameters
                    .STRING_JSON_KEY_TIME);
            }
            DisplayElement tmp = new DisplayElement(mDisplayJsonArray.getJSONObject(i).getInt
                (DisplayParameters.STRING_JSON_KEY_TIME), time, mDisplayJsonArray.getJSONObject(i)
                .getString(DisplayParameters.STRING_JSON_KEY_HOST) + "/" + mDisplayJsonArray.getJSONObject
                (i).getString(DisplayParameters.STRING_JSON_KEY_FILE), Color.parseColor(mDisplayJsonArray
                .getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_COLOR)), mDisplayJsonArray
                .getJSONObject(i).getString(DisplayParameters.STRING_JSON_KEY_DESCRIPTION),
                mDisplayJsonArray.getJSONObject(i).getJSONObject(DisplayParameters
                    .STRING_JSON_KEY_ANIMATION), mDisplayJsonArray.getJSONObject(i).getJSONObject
                (DisplayParameters.STRING_JSON_KEY_TEXT));
            
            mDisplayQueue.add(tmp);
            
            //for debugging using
            tmp.print();
        }
        Logs.showTrace("[DisplayHandler] total Data size:" + String.valueOf(mDisplayQueue.size()));
        
        
    }
    
    public void resetDisplayData()
    {
        this.mDisplayJsonArray = null;
        this.mDisplayQueue.clear();
        
    }
    
    public void startDisplay()
    {
        theFinalDisplayElement = null;
        toDo();
    }
    
    private void toDo()
    {
        if (null != mDisplayQueue)
        {
            Logs.showTrace("[DisplayHandler]in toDo function total mDisplayQueue Data size:" + String.valueOf
                (mDisplayQueue.size()));
            
            DisplayElement display = mDisplayQueue.poll();
            theLastDisplayElement = display;
            
            if (null != display)
            {
                //set next time
                Logs.showTrace("[DisplayHandler]###set next time");
                if (display.nextTime != -1)
                {
                    mDisplayHandler.postDelayed(mDisplayRunnable, display.nextTime);
                }
                else
                {
                    theFinalDisplayElement = display;
                }
                
                mAnimationHandler.setAnimateJsonBehavior(display.animation);
                mAnimationHandler.startAnimate();
                
                ViewHandler.setBackgroundColor(display.backgroundColor, mHashMapViews.get(DisplayParameters
                    .RELATIVE_LAYOUT_ID));
                
                int drawableId;
                
                if (display.imageURL.contains("OCTOBO_Expressions-"))
                {
                    drawableId = convertOctoboImageURLToDrawableID(display.imageURL);
                }
                else
                {
                    if (mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID) instanceof RelativeLayout)
                    {
                        ViewHandler.setBackgroundColor(mContext.getResources().getColor(R.color
                            .starfish_background), mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID));
                    }
                    drawableId = convertStarFishImageURLToDrawableID(display.imageURL);
                }
                if (drawableId != 0)
                {
                   
                    Glide.with(mContext).load(R.drawable.g_o_speak).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID));
                    
                    
                }
            }
        }
    }
    
    private int convertOctoboImageURLToDrawableID(@NonNull String url)
    {
        String filename = Tools.stripFilenameFromUrl(url);
        Logs.showTrace("convertOctoboImageURLToDrawableID() url = " + url + ", filename = " + filename);
        
        return getDrawableIDFromFileName(filename);
    }
    
    private int convertStarFishImageURLToDrawableID(@NonNull String url)
    {
        String filename = Tools.stripFilenameFromUrl(url);
        Logs.showTrace("convertStarFishImageURLToDrawableID() url = " + url + ", filename = " + filename);
        
        return getDrawableIDFromFileName(filename);
    }
    
    public int getDrawableIDFromFileName(@NonNull String filename)
    {
        if (filename.startsWith("OCTOBO_Expressions-"))
        {
            filename = "octobo" + filename.substring(filename.lastIndexOf('-') + 1, filename.length());
        }
        
        try
        {
            filename = Tools.removeFileExtension(filename);
            Logs.showTrace("getDrawableIDFromFileName() filename = " + filename);
            return Tools.getDrawableId(filename);
        }
        catch (NoSuchElementException e)
        {
            return 0;
        }
    }
    
    @Override
    public void onClick(View v)
    {
        if (v instanceof RelativeLayout)
        {
            Logs.showTrace("[DisplayHandler] you click screen");
            
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "onClick");
            message.put("id", String.valueOf(v.getId()));
            
            callBackMessage(ResponseCode.ERR_SUCCESS, DisplayParameters.CLASS_DISPLAY, DisplayParameters
                .METHOD_CLICK, message);
        }
    }
    
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        return false;
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
