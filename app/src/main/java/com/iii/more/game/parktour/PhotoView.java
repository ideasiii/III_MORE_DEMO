package com.iii.more.game.parktour;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.iii.more.game.module.CameraPreview;

/**
 * Created by Jugo on 2018/2/22
 */

public class PhotoView extends RelativeLayout
{
    private CameraPreview cameraPreview = null;
    
    public PhotoView(Context context)
    {
        super(context);
        init(context);
    }
    
    public PhotoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public PhotoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //this.setLayoutParams(layoutParams);
        
        if (null == cameraPreview)
        {
            cameraPreview = new CameraPreview(context);
        }
        
        cameraPreview.setLayoutParams(layoutParams);
        addView(cameraPreview);
    }
    
    public void start(Activity activity)
    {
        cameraPreview.onResume(activity);
    }
}
