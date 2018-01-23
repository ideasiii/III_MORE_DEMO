package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/14
 */

public class ScenarizeStart extends ScenarizeBase
{
    public ScenarizeStart(Handler handler)
    {
        super(handler);
    }
    
    @Override
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_START,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.RFID,
            true,
            false,
            R.drawable.g_o_speak,
            R.drawable.zoo,
            "g_o_speak.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_CROP,
            FRONT.FACE, "嗨! 你好 來玩遊戲吧");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_ANIMAL_RFID,
            SCEN.SCEN_INDEX_HOLD_HAND,
            NEXT_TRIGER.SENSOR_ALL,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.zoo,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_INSIDE,
            FRONT.OBJECT, "哈囉，" + GLOBAL.ChildName + "今天我們一起去動物園玩！牽著我的手，出發囉");
    
        setScenarize(scenarize,
            SCEN.SCEN_INDEX_HOLD_HAND,
            SCEN.SCEN_INDEX_NO_ACTION,
            NEXT_TRIGER.RFID,
            true,
            true,
            R.drawable.p_o_noeye,
            R.drawable.traffic,
            "p_o_noeye.png",
            ImageView.ScaleType.CENTER_CROP,
            ImageView.ScaleType.CENTER_CROP,
            FRONT.OBJECT,
            "抓緊喔！今天，你想要坐什麼交通工具去呢");
    }
}
