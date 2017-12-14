package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeFood extends ScenarizeBase
{
    public ScenarizeFood(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_FOOD_STORE,
            SCEN.SCEN_INDEX_FOOD_CHOICE,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.noeye,
            R.drawable.food_store,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "我們來吃東西休息一下吧！");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_FOOD_CHOICE,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.UI_CLICK,
            true,
            false,
            R.drawable.noeye,
            R.drawable.food_store,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "請選擇你要吃的食物");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_FOOD_EAT,
            SCEN.SCEN_INDEX_GAME_OVER,
            NEXT_TRIGER.TTS_TEXT,
            true,
            false,
            R.drawable.noeye,
            R.drawable.food_store,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            ",,,,,,,,,,,,啊,,嗯,,嗯,,嗯  嗯 ,,好吃！,,,,,,,,,,,,我們下次再來玩");
    }
}
