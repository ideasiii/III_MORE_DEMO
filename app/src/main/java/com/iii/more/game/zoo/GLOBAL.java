package com.iii.more.game.zoo;

import android.util.SparseArray;

import org.json.JSONObject;

/**
 * Created by jugo on 2017/12/5
 */

public abstract class GLOBAL
{
    public static class Scenarize
    {
        public int ScenarizeIndex;
        public int ScenarizeNext;
        
        public Scenarize()
        {
            ScenarizeIndex = -1;
            ScenarizeNext = -1;
        }
    }
    
    public static Scenarize scenarizeCurr = new Scenarize();
 //   public static int mnScenarizeIndex;
    public static SparseArray<JSONObject> scenarize = new SparseArray<JSONObject>();
    public static String ChildName;
    public static int mnDroppedX = 0;
}
