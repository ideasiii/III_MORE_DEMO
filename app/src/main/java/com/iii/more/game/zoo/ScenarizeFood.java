package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
 */

/**
 * 腳本: 我們來吃東西休息一下吧！選一個吧!
 
 Ø   今天你想吃什麼呢? 點一下你想吃的東西喔!
 
 Ø   哇!!每一種都好好吃的樣子喔~~你要選哪一個呢?
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
            R.drawable.p_o_noeye,
            R.drawable.food_store,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "我們來吃東西休息一下吧！選一個吧");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_FOOD_CHOICE,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.UI_CLICK,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.food_store,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "今天你想吃什麼呢? 點一下你想吃的東西喔");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_FOOD_EAT,
            SCEN.SCEN_INDEX_GAME_OVER,
            NEXT_TRIGER.TTS_TEXT,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.food_store,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            ",,,,,,,,,,,,啊,,嗯,,嗯,,嗯  嗯 ,,好吃！,,,,,,,,,,,,我們下次再來玩");
    }
}
