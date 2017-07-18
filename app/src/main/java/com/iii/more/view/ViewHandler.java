package com.iii.more.view;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by joe on 2017/7/4.
 */

public class ViewHandler
{
    public static void setBackgroundColor(@ColorInt int color, View view)
    {
        View root = view.getRootView();
        root.setBackgroundColor(color);
        view.setBackgroundColor(color);
    }
    
    public static void setBackgroundColor(@NonNull Color color, View view)
    {
        setBackgroundColor(color, view);
        
    }
    
    
}
