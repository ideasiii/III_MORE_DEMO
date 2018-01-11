package com.iii.more.game.zoo;

import android.os.Handler;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

/**
 * Created by Jugo on 2017/12/6
 */

public class ScenarizeHandler extends ScenarizeBase
{
    private ScenarizeCar scenarizeCar = null;
    private ScenarizeMrt scenarizeMrt = null;
    private ScenarizeBus scenarizeBus = null;
    private ScenarizeZoo scenarizeZoo = null;
    private ScenarizeFood scenarizeFood = null;
    private ScenarizeStart scenarizeStart = null;
    private ScenarizeEnd scenarizeEnd = null;
    
    
    public ScenarizeHandler(Handler handler)
    {
        super(handler);
        scenarizeBus = new ScenarizeBus(handler);
        scenarizeCar = new ScenarizeCar(handler);
        scenarizeMrt = new ScenarizeMrt(handler);
        scenarizeZoo = new ScenarizeZoo(handler);
        scenarizeFood = new ScenarizeFood(handler);
        scenarizeStart = new ScenarizeStart(handler);
        scenarizeEnd = new ScenarizeEnd(handler);
    }
    
    public void setHandler(Handler handler)
    {
        scenarizeBus.setHandler(handler);
        scenarizeCar.setHandler(handler);
        scenarizeMrt.setHandler(handler);
        scenarizeZoo.setHandler(handler);
        scenarizeFood.setHandler(handler);
        scenarizeStart.setHandler(handler);
        scenarizeEnd.setHandler(handler);
    }
    
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        scenarize.clear();
        
        // 情境開始
        scenarizeStart.createScenarize(scenarize);
        // 吃食物
        scenarizeFood.createScenarize(scenarize);
        // 動物園
        scenarizeZoo.createScenarize(scenarize);
        // 坐公車
        scenarizeBus.createScenarize(scenarize);
        // 坐捷運
        scenarizeMrt.createScenarize(scenarize);
        // 坐車子
        scenarizeCar.createScenarize(scenarize);
        // 情境結束
        scenarizeEnd.createScenarize(scenarize);
        
    }
}