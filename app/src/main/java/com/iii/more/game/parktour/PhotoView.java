package com.iii.more.game.parktour;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iii.more.game.module.CameraPreview;
import com.iii.more.main.R;

/**
 * Created by Jugo on 2018/2/22
 */

public class PhotoView extends RelativeLayout
{
    private CameraPreview cameraPreview = null;
    private ImageView imgMask = null;
    
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
        setBackgroundColor(Color.rgb(108, 147, 213));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (null == cameraPreview)
        {
            cameraPreview = new CameraPreview(context);
        }
        
        cameraPreview.setLayoutParams(layoutParams);
        addView(cameraPreview);
        
        imgMask = new ImageView(context);
        imgMask.setScaleType(ImageView.ScaleType.FIT_XY);
        imgMask.setImageResource(R.drawable.iii_photo_frame);
        imgMask.setLayoutParams(layoutParams);
        addView(imgMask);
    }
    
    public void start(Activity activity)
    {
        cameraPreview.onResume(activity);
    }
    
    public void picture()
    {
        cameraPreview.takePicture();
    }
    
    public void stop()
    {
        cameraPreview.onPause();
    }
}
