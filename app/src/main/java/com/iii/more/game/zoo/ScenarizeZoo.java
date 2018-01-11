package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeZoo extends ScenarizeBase
{
    public ScenarizeZoo(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_DOOR,
            SCEN.SCEN_INDEX_CHOICE_ZOO,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT,
            "到囉，，讓我們一起來參觀動物吧");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_CHOICE_ZOO,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.UI_CLICK,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT,
            "請選擇你要參觀的動物區");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_TAIWAN,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "歡迎參觀台灣動物區");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_BIRD,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "歡迎參觀鳥園");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_RAIN,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "歡迎參觀熱帶雨林動物區");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_CUT,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "歡迎參觀可愛動物區");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ZOO_AFFICA,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.SLIDE_END,
            true,
            false,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "歡迎參觀非洲動物區");
    }
}
