package com.iii.more.emotion.interrupt;

import android.content.Context;
import android.support.annotation.NonNull;


import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;

/**
 * Created by joe on 2017/11/13.
 */

public class FaceEmotionInterruptHandler extends BaseHandler
{
    private HashMap<String, String> mFaceData = null;
    
    private boolean isRecordMode = false;
    
    public FaceEmotionInterruptHandler(Context context)
    {
        super(context);
    }
    
    public void setFaceEventData(@NonNull HashMap<String, String> faceEmotionHashData)
    {
    
    
    }
    
    public void setFaceEmotionBehavior(@NonNull JSONObject faceEmotionRule)
    {
    
    }
    
    public void startEventAnalyze()
    {
    
    }
    
    
}
