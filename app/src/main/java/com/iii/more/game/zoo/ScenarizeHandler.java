package com.iii.more.game.zoo;

import android.content.Context;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;


/**
 * Created by Jugo on 2017/12/6
 */

public class ScenarizeHandler
{
    public static enum FRONT
    {
        FACE, OBJECT
    }
    
    private Context theContext = null;
    
    public ScenarizeHandler(Context context)
    {
        theContext = context;
    }
    
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        try
        {
            scenarize.clear();
            
            setScenarize(scenarize, SCEN.SCEN_INDEX_START, SCEN.SCEN_INDEX_ANIMAL_RFID, true,
                false, R.drawable.octobo16, R.drawable.zoo_map, "octobo16.png", ImageView
                    .ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_CROP, FRONT.FACE, "嗨! 你好 "
                    + "來玩遊戲吧");
            
            setScenarize(scenarize, SCEN.SCEN_INDEX_ANIMAL_RFID, SCEN.SCEN_INDEX_HOLD_HAND, true,
                true, R.drawable.noeye, R.drawable.zoo_map, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_CROP, FRONT.OBJECT, "哈囉，" + GLOBAL
                    .ChildName + "今天我們一起去動物園玩！牽著我的手，出發囉！");
        }
        catch (Exception es)
        {
            Logs.showError("[ScenarizeHandler] createScenarize Exception:" + es.getMessage());
        }
        
    }
    
    private void setScenarize(SparseArray<JSONObject> scenarize, int index, int next, boolean
        face_show, boolean object_show, int face_id, int object_id, String face_image, ImageView
        .ScaleType scaleType, ImageView.ScaleType scaleTypeObj, ScenarizeHandler.FRONT front,
        String tts_text)
    {
        try
        {
            scenarize.put(index, new JSONObject().put("index", index).put
                ("next", next).put("face_show", face_show).put("object_show", object_show).put
                ("face_id", face_id).put("object_id", object_id).put("face_image", face_image)
                .put("face_scale_type", scaleType).put("object_scale_type", scaleTypeObj).put
                    ("front", front).put("tts_text", tts_text));
        }
        catch (Exception e)
        {
            Logs.showError("[ScenarizeHandler] setScenarize Exception:" + e.getMessage());
        }
    }
}