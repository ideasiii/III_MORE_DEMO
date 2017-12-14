package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
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
            R.drawable.noeye,
            R.drawable.bus,
            "noeye.png",
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
            R.drawable.noeye,
            R.drawable.bus,
            "noeye.png",
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
            "請你幫忙讓大家都有座位坐");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_DROP_CUSTOM,
            SCEN.SCEN_INDEX_BUS_DRIVE,
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
            SCEN.SCEN_INDEX_BUS_DRIVE,
            SCEN.SCEN_INDEX_ZOO_DOOR,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.noeye,
            R.drawable.bus_run,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "噗噗噗噗噗噗噗噗噗噗");
    }
}
