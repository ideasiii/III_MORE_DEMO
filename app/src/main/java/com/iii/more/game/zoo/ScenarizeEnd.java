package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeEnd extends ScenarizeBase
{
    public ScenarizeEnd(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_GAME_OVER,
            SCEN.SCEN_INDEX_FINISH,
            NEXT_TRIGER.TTS_TEXT,
            true,
            true,
            R.drawable.noeye,
            R.drawable.zoo,
            "noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.FACE,
            "掰掰囉");
    }
}
