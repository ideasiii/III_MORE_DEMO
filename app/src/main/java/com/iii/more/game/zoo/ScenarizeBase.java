package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/14
 */

public abstract class ScenarizeBase extends ScenarizeDefine
{
    private SparseArray<JSONObject> theScenarize = null;
    private Handler handlerScenarize = null;
    public static SensorEventHandler sensorEventHandler = null;
    
    public ScenarizeBase(Handler handler)
    {
        handlerScenarize = handler;
        if (null == sensorEventHandler)
        {
            sensorEventHandler = new SensorEventHandler(handler);
        }
    }
    
    public void setHandler(Handler handler)
    {
        handlerScenarize = handler;
        sensorEventHandler.setHandler(handler);
    }
    
    public void goScenarize(final int nScenarizeIndex)
    {
        handlerScenarize.sendEmptyMessage(nScenarizeIndex);
    }
    
    public abstract void createScenarize(SparseArray<JSONObject> scenarize);
    
    protected void setScenarize(SparseArray<JSONObject> scenarize, int index, int next,
        ScenarizeHandler.NEXT_TRIGER nextTriger, boolean face_show, boolean object_show, int
        face_id, int object_id, String face_image, ImageView.ScaleType scaleType, ImageView
        .ScaleType scaleTypeObj, ScenarizeHandler.FRONT front, String tts_text)
    {
        try
        {
            scenarize.put(index, new JSONObject().put("index", index).put("next", next).put
                ("face_show", face_show).put("object_show", object_show).put("face_id", face_id)
                .put("object_id", object_id).put("face_image", face_image).put("face_scale_type",
                    scaleType).put("object_scale_type", scaleTypeObj).put("front", front).put
                    ("tts_text", tts_text).put("next_triger", nextTriger).put("com/iii/more/emotion", false));
            
            sensorEventHandler.addSensorEvent(index, next, nextTriger);
            theScenarize = scenarize;
        }
        catch (Exception e)
        {
            Logs.showError("[ScenarizeHandler] setScenarize Exception:" + e.getMessage());
        }
    }
    
    protected JSONObject getScenarize(final int nIndex)
    {
        return theScenarize.get(nIndex);
    }
}
