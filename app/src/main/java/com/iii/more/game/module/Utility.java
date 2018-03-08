package com.iii.more.game.module;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;


public abstract class Utility
{
    public static float convertPixelToDp(float px, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    
    public static float convertDpToPixel(float dp, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
    
    public static void fullScreenNoBar(Activity activity)
    {
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
            .SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
            .SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
            .SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        
        // This work only for android 4.4+
        
        
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        
        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = activity.getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View
            .OnSystemUiVisibilityChangeListener()
        {
            
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }
    
    public static int getResourceId(Context context, String name, String defType)
    {
        return context.getResources().getIdentifier(name, defType, context.getPackageName());
    }
    
    public static void loadImage(Context context, int nResId, ImageView imageView)
    {
        Glide.with(context).load(nResId).apply(RequestOptions.diskCacheStrategyOf
            (DiskCacheStrategy.NONE)).into(imageView);
    }
    
}
