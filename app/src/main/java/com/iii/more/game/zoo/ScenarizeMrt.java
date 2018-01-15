package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeMrt extends ScenarizeBase
{
    public ScenarizeMrt(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_TRAFFIC_MRT,
            SCEN.SCEN_INDEX_TRAFFIC_CARD_MRT,
            NEXT_TRIGER.RFID,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.mrt,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "坐捷運時要記得使用悠遊卡付錢喔!!這樣才是好寶寶! 請刷悠遊卡。");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_TRAFFIC_CARD_MRT,
            SCEN.SCEN_INDEX_MRT_MAP,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.mrt,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "逼，，逼");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_MRT_MAP,
            SCEN.SCEN_INDEX_MRT_EMOTION_RESP,
            NEXT_TRIGER.UI_TOUCH_UP,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.mrt,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            GLOBAL.ChildName + "請你幫忙畫出坐車的路線圖");
        try
        {
            getScenarize(SCEN.SCEN_INDEX_MRT_MAP).put("emotion", true);
        }
        catch(Exception e)
        {
            Logs.showError("[ScenarizeBus] Exception: " + e.getMessage());
        }
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_MRT_EMOTION_RESP,
            SCEN.SCEN_INDEX_MRT_RUN,
            NEXT_TRIGER.TTS_TEXT,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.bus,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_MRT_RUN,
            SCEN.SCEN_INDEX_ZOO_DOOR,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.mrt_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "坐好，我們要出發囉");
    }
    
    
}
