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
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.iii.more.animate.AnimationHandler;
import com.iii.more.main.R;
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
    
    private HashMap<String, Integer> imageOctoboHashMap = null;
    
    private HashMap<String, Integer> imageStarFishHashMap = null;
    
    RequestListener mRequestListener = new RequestListener<Integer, GlideDrawable>()
    {
        @Override
        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource)
        {
            Logs.showError("[RequestListener] Exception: " + e.toString() + " Model: " + model + " isFirstResource: " + String.valueOf(isFirstResource));
            
            ((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID)).setImageResource(0);
            return false;
        }
        
        @Override
        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
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
        mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID).setOnClickListener(this);
        mDisplayQueue = new ArrayDeque<>();
        mDisplayHandler = new Handler(Looper.getMainLooper());
        mDisplayRunnable = new DisplayRunnable();
        
        createMappingTable();
        
        
    }
    
    private void createMappingTable()
    {
        imageOctoboHashMap = new HashMap<>();
        imageOctoboHashMap.put("OCTOBO_Expressions-01.png", R.drawable.octobo01);
        imageOctoboHashMap.put("OCTOBO_Expressions-02.png", R.drawable.octobo02);
        imageOctoboHashMap.put("OCTOBO_Expressions-03.png", R.drawable.octobo03);
        imageOctoboHashMap.put("OCTOBO_Expressions-04.png", R.drawable.octobo04);
        imageOctoboHashMap.put("OCTOBO_Expressions-05.png", R.drawable.octobo05);
        imageOctoboHashMap.put("OCTOBO_Expressions-06.png", R.drawable.octobo06);
        imageOctoboHashMap.put("OCTOBO_Expressions-07.png", R.drawable.octobo07);
        imageOctoboHashMap.put("OCTOBO_Expressions-08.png", R.drawable.octobo08);
        imageOctoboHashMap.put("OCTOBO_Expressions-09.png", R.drawable.octobo09);
        imageOctoboHashMap.put("OCTOBO_Expressions-10.png", R.drawable.octobo10);
        imageOctoboHashMap.put("OCTOBO_Expressions-11.png", R.drawable.octobo11);
        imageOctoboHashMap.put("OCTOBO_Expressions-12.png", R.drawable.octobo12);
        imageOctoboHashMap.put("OCTOBO_Expressions-13.png", R.drawable.octobo13);
        imageOctoboHashMap.put("OCTOBO_Expressions-14.png", R.drawable.octobo14);
        imageOctoboHashMap.put("OCTOBO_Expressions-15.png", R.drawable.octobo15);
        imageOctoboHashMap.put("OCTOBO_Expressions-16.png", R.drawable.octobo16);
        imageOctoboHashMap.put("OCTOBO_Expressions-17.png", R.drawable.octobo17);
        imageOctoboHashMap.put("OCTOBO_Expressions-18.png", R.drawable.octobo18);
        imageOctoboHashMap.put("OCTOBO_Expressions-19.png", R.drawable.octobo19);
        imageOctoboHashMap.put("OCTOBO_Expressions-20.png", R.drawable.octobo20);
        imageOctoboHashMap.put("OCTOBO_Expressions-21.png", R.drawable.octobo21);
        imageOctoboHashMap.put("OCTOBO_Expressions-22.png", R.drawable.octobo22);
        imageOctoboHashMap.put("OCTOBO_Expressions-23.png", R.drawable.octobo23);
        imageOctoboHashMap.put("OCTOBO_Expressions-24.png", R.drawable.octobo24);
        imageOctoboHashMap.put("OCTOBO_Expressions-25.png", R.drawable.octobo25);
        imageOctoboHashMap.put("OCTOBO_Expressions-26.png", R.drawable.octobo26);
        imageOctoboHashMap.put("OCTOBO_Expressions-27.png", R.drawable.octobo27);
        imageOctoboHashMap.put("OCTOBO_Expressions-28.png", R.drawable.octobo28);
        imageOctoboHashMap.put("OCTOBO_Expressions-29.png", R.drawable.octobo29);
        imageOctoboHashMap.put("OCTOBO_Expressions-30.png", R.drawable.octobo30);
        imageOctoboHashMap.put("OCTOBO_Expressions-31.png", R.drawable.octobo31);
        imageOctoboHashMap.put("OCTOBO_Expressions-32.png", R.drawable.octobo32);
        imageOctoboHashMap.put("OCTOBO_Expressions-33.png", R.drawable.octobo33);
        imageOctoboHashMap.put("OCTOBO_Expressions-34.png", R.drawable.octobo34);
        imageOctoboHashMap.put("OCTOBO_Expressions-35.png", R.drawable.octobo35);
        imageOctoboHashMap.put("OCTOBO_Expressions-36.png", R.drawable.octobo36);
        imageOctoboHashMap.put("OCTOBO_Expressions-37.png", R.drawable.octobo37);
        imageOctoboHashMap.put("OCTOBO_Expressions-38.png", R.drawable.octobo38);
        imageOctoboHashMap.put("OCTOBO_Expressions-39.png", R.drawable.octobo39);
        imageOctoboHashMap.put("OCTOBO_Expressions-40.png", R.drawable.octobo40);
        
        
        imageStarFishHashMap = new HashMap<>();
        imageStarFishHashMap.put("starfish01.png", R.drawable.starfish01);
        imageStarFishHashMap.put("starfish02.png", R.drawable.starfish02);
        imageStarFishHashMap.put("starfish03.png", R.drawable.starfish03);
        imageStarFishHashMap.put("starfish04.png", R.drawable.starfish04);
        imageStarFishHashMap.put("starfish05.png", R.drawable.starfish05);
        imageStarFishHashMap.put("starfish06.png", R.drawable.starfish06);
        imageStarFishHashMap.put("starfish07.png", R.drawable.starfish07);
        imageStarFishHashMap.put("starfish08.png", R.drawable.starfish08);
        imageStarFishHashMap.put("starfish09.png", R.drawable.starfish09);
        imageStarFishHashMap.put("starfish10.png", R.drawable.starfish10);
        
        imageStarFishHashMap.put("starfish11.png", R.drawable.starfish11);
        imageStarFishHashMap.put("starfish12.png", R.drawable.starfish12);
        imageStarFishHashMap.put("starfish13.png", R.drawable.starfish13);
        imageStarFishHashMap.put("starfish14.png", R.drawable.starfish14);
        imageStarFishHashMap.put("starfish15.png", R.drawable.starfish15);
        imageStarFishHashMap.put("starfish16.png", R.drawable.starfish16);
        imageStarFishHashMap.put("starfish17.png", R.drawable.starfish17);
        imageStarFishHashMap.put("starfish18.png", R.drawable.starfish18);
        imageStarFishHashMap.put("starfish19.png", R.drawable.starfish19);
        imageStarFishHashMap.put("starfish20.png", R.drawable.starfish20);
        
        imageStarFishHashMap.put("starfish21.png", R.drawable.starfish21);
        imageStarFishHashMap.put("starfish22.png", R.drawable.starfish22);
        imageStarFishHashMap.put("starfish23.png", R.drawable.starfish23);
        imageStarFishHashMap.put("starfish24.png", R.drawable.starfish24);
        imageStarFishHashMap.put("starfish25.png", R.drawable.starfish25);
        imageStarFishHashMap.put("starfish26.png", R.drawable.starfish26);
        imageStarFishHashMap.put("starfish27.png", R.drawable.starfish27);
        imageStarFishHashMap.put("starfish28.png", R.drawable.starfish28);
        imageStarFishHashMap.put("starfish29.png", R.drawable.starfish29);
        imageStarFishHashMap.put("starfish30.png", R.drawable.starfish30);
        
        imageStarFishHashMap.put("starfish31.png", R.drawable.starfish31);
        imageStarFishHashMap.put("starfish32.png", R.drawable.starfish32);
        imageStarFishHashMap.put("starfish33.png", R.drawable.starfish33);
        imageStarFishHashMap.put("starfish34.png", R.drawable.starfish34);
        imageStarFishHashMap.put("starfish35.png", R.drawable.starfish35);
        imageStarFishHashMap.put("starfish36.png", R.drawable.starfish36);
        imageStarFishHashMap.put("starfish37.png", R.drawable.starfish37);
        imageStarFishHashMap.put("starfish38.png", R.drawable.starfish38);
        imageStarFishHashMap.put("starfish39.png", R.drawable.starfish39);
        imageStarFishHashMap.put("starfish40.png", R.drawable.starfish40);
        
        
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
        
        Logs.showTrace("[DisplayHandler] theLastDisplayElement.timeDuring: " +
                String.valueOf(theLastDisplayElement.timeDuring));
        
        DisplayElement tmp = mDisplayQueue.poll();
        theLastDisplayElement.nextTime = tmp.timeDuring - seconds;
        
        if (theLastDisplayElement.nextTime < -1)
        {
            theLastDisplayElement.nextTime = -1;
        }
        
        Logs.showTrace("[DisplayHandler]####theLastDisplayElement#####");
        theLastDisplayElement.print();
        
        
        mSaveDisplayQueue.add(theLastDisplayElement);
        mSaveDisplayQueue.add(tmp);
        
        Logs.showTrace("[DisplayHandler] total mDisplayQueue Data size:" + String.valueOf(mDisplayQueue.size()));
        while (true)
        {
            DisplayElement tmp2 = mDisplayQueue.poll();
            if (null == tmp)
            {
                break;
            }
            else
            {
                mSaveDisplayQueue.add(tmp2);
                tmp2.print();
            }
        }
        Logs.showTrace("[DisplayHandler] total mSaveDisplayQueue Data size:" + String.valueOf(mSaveDisplayQueue.size()));
        
        
    }
    
    public void resumeDisplaying()
    {
        mDisplayQueue = mSaveDisplayQueue.clone();
        startDisplay();
        
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
                ((ImageView) mHashMapViews.get(key)).setImageResource(id);
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
                    ((ImageView) mHashMapViews.get(key)).setImageResource(R.drawable.default_image);
                    // Glide.with(mContext)
                    //        .load("")
                    //        .listener(mRequestListener)
                    //        .into(new GlideDrawableImageViewTarget(((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID))));
                }
                else if (mHashMapViews.get(key) instanceof RelativeLayout)
                {
                    
                    ViewHandler.setBackgroundColor(mContext.getResources().getColor(DisplayParameters.DEFAULT_BACKGROUND_COLOR),
                            mHashMapViews.get(key));
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
        Logs.showTrace("[DisplayHandler] total Data size:" + String.valueOf(mDisplayQueue.size()));
        
        
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
            DisplayElement display = mDisplayQueue.poll();
            theLastDisplayElement = display;
            
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
                
                Integer drawableNum = 0;
                
                if (display.imageURL.contains("OCTOBO_Expressions-"))
                {
                    drawableNum = convertOctoboImageURLToDrawable(display.imageURL);
                }
                else
                {
                    if (mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID) instanceof RelativeLayout)
                    {
                        ViewHandler.setBackgroundColor(mContext.getResources().getColor(R.color.starfish_background),
                                mHashMapViews.get(DisplayParameters.RELATIVE_LAYOUT_ID));
                    }
                    drawableNum = convertStarFishImageURLToDrawable(display.imageURL);
                }
                if (drawableNum != 0)
                {
                    Glide.with(mContext)
                            .load(drawableNum)
                            //.load(display.imageURL)
                            .listener(mRequestListener)
                            .into(new GlideDrawableImageViewTarget(((ImageView) mHashMapViews.get(DisplayParameters.IMAGE_VIEW_ID))));
                }
                
                
            }
            
            
        }
    }
    
    private Integer convertOctoboImageURLToDrawable(String url)
    {
        for (String key : imageOctoboHashMap.keySet())
        {
            if (url.contains(key))
            {
                return imageOctoboHashMap.get(key);
            }
        }
        return 0;
        
        
    }
    
    private Integer convertStarFishImageURLToDrawable(String url)
    {
        for (String key : imageStarFishHashMap.keySet())
        {
            if (url.contains(key))
            {
                return imageStarFishHashMap.get(key);
            }
            
        }
        return 0;
        
        
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
            
            callBackMessage(ResponseCode.ERR_SUCCESS, DisplayParameters.CLASS_DISPLAY,
                    DisplayParameters.METHOD_CLICK, message);
            
            
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
