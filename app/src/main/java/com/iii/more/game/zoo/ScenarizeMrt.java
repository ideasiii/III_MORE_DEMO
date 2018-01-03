package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

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
            R.drawable.noeye,
            R.drawable.mrt,
            "noeye.png",
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
            R.drawable.noeye,
            R.drawable.mrt,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "逼，，逼");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_MRT_MAP,
            SCEN.SCEN_INDEX_EMOTION_RESP,
            NEXT_TRIGER.UI_TOUCH_UP,
            true,
            false,
            R.drawable.noeye,
            R.drawable.mrt,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            GLOBAL.ChildName + "請你幫忙畫出坐車的路線圖");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_EMOTION_RESP,
            SCEN.SCEN_INDEX_CHOICE_ZOO,
            NEXT_TRIGER.TTS_TEXT,
            true,
            false,
            R.drawable.noeye,
            R.drawable.bus,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "");
    }
}
