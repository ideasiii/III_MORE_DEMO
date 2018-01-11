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

/**
 * 腳本: 坐公車時要記得使用悠遊卡付錢喔!!這樣才是好寶寶! 請刷悠遊卡。
 
 Ø   來，把悠遊卡放到我的盤子上就可以囉!
 
 Ø   剛剛悠遊卡沒有刷成功，這樣沒辦法坐公車耶!我們再試一次好嗎?
 */

/**
 * 腳本: 請你幫忙讓大家都有座位坐。要把人放到位置上喔~
 
 Ø   公車快要開囉~~還有人沒有位子坐，這樣很危險喔!!
 
 Ø   小朋友再檢查一次喔!!要記得讓大家都有位子坐喔!
 */
public class ScenarizeBus extends ScenarizeBase
{
    public ScenarizeBus(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_TRAFFIC_BUS,
            SCEN.SCEN_INDEX_TRAFFIC_CARD_BUS,
            NEXT_TRIGER.RFID,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.bus,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "坐公車時要記得使用悠遊卡付錢喔!!這樣才是好寶寶! 請刷悠遊卡");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_TRAFFIC_CARD_BUS,
            SCEN.SCEN_INDEX_BUS_INSIDE,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.bus,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE, "逼，，逼");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_BUS_INSIDE,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.UI_DRAG_DROP,
            true,
            false,
            R.drawable.businside,
            R.drawable.bus,
            "businside.png",
            ImageView.ScaleType.CENTER_INSIDE,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "請你幫忙讓大家都有座位坐。要把人放到位置上喔");
    try
    {
        getScenarize(SCEN.SCEN_INDEX_BUS_INSIDE).put("emotion", true);
    }
    catch(Exception e)
    {
        Logs.showError("[ScenarizeBus] Exception: " + e.getMessage());
    }
        
        setScenarize(scenarize,
        SCEN.SCEN_INDEX_DROP_CUSTOM,
        SCEN.SCEN_INDEX_BUS_EMOTION_RESP,
        NEXT_TRIGER.TTS_TEXT,
        true,
        false,
        R.drawable.businside_right,
        R.drawable.bus,
        "businside_right.png",
        ImageView.ScaleType.CENTER_INSIDE,
        ImageView.ScaleType.CENTER_INSIDE,
        FRONT.FACE,
        "好棒！!!我們出發囉！");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_BUS_EMOTION_RESP,
            SCEN.SCEN_INDEX_BUS_DRIVE,
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
            SCEN.SCEN_INDEX_BUS_DRIVE,
            SCEN.SCEN_INDEX_ZOO_DOOR,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.bus_run,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "噗噗噗噗噗噗噗噗噗噗");
    }
}
