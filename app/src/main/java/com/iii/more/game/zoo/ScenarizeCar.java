package com.iii.more.game.zoo;

import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

import android.os.Handler;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeCar extends ScenarizeBase
{
    public ScenarizeCar(Handler handler)
    {
        super(handler);
    }
    
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_TRAFFIC_CAR,
            SCEN.SCEN_INDEX_CAR_START,
            NEXT_TRIGER.RFID,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.car,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "用鑰匙發動吧。");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_START,
            SCEN.SCEN_INDEX_CAR_RUN,
            NEXT_TRIGER.SENSOR_ALL,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.car,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "握著我的手轉一下，要發動囉");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_RUN,
            SCEN.SCEN_INDEX_CAR_OUTSIDE,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.car_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT,
            "噗噗噗噗噗噗噗 你看窗外有好多交通工具");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_OUTSIDE,
            SCEN.SCEN_INDEX_CAR_FIX,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.car_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_FIX,
            SCEN.SCEN_INDEX_CAR_EMOTION_RESP,
            NEXT_TRIGER.UI_DRAG_DROP,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.car_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT,
            "車子故障了，請幫忙修補輪胎");
        try
        {
            getScenarize(SCEN.SCEN_INDEX_CAR_FIX).put("emotion", true);
        }
        catch(Exception e)
        {
            Logs.showError("[ScenarizeBus] Exception: " + e.getMessage());
        }
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_EMOTION_RESP,
            SCEN.SCEN_INDEX_CAR_FIX_SUCCESS,
            NEXT_TRIGER.TTS_TEXT,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.bus,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT,
            "");
        
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CAR_FIX_SUCCESS,
            SCEN.SCEN_INDEX_ZOO_DOOR,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.car_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "太好了，車子修好了，謝謝你的幫忙，我們出發吧");
    }
}
