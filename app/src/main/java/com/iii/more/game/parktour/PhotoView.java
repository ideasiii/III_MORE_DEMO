package com.iii.more.game.parktour;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.iii.more.game.module.CameraPreview;
import com.iii.more.main.R;

import org.iii.moresdk.widget.Global;
import org.iii.moresdk.widget.Logs;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jugo on 2018/2/22
 */

public class PhotoView extends RelativeLayout
{
    private Context theContext;
    private CameraPreview cameraPreview = null;
    private ImageView imgMask = null;
    
    private OnCameraOpened onCameraOpenedListener = null;
    
    public static interface OnCameraOpened
    {
        public void opened();
    }
    
    public void setOnCameraOpenedListener(OnCameraOpened listener)
    {
        onCameraOpenedListener = listener;
    }
    
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
        theContext = context;
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
        
        cameraPreview.setOnCameraOpenedListener(new CameraPreview.OnCameraOpened()
        {
            @Override
            public void opened()
            {
                if (null != onCameraOpenedListener)
                {
                    onCameraOpenedListener.opened();
                }
            }
        });
    }
    
    public void start(Activity activity)
    {
        cameraPreview.onResume(activity);
    }
    
    public void picture()
    {
        //width: 1200 height: 1824
        cameraPreview.takePicture();
    }
    
    public void stop()
    {
        cameraPreview.onPause();
    }
    
    public String setFrame(final int nIndex)
    {
        switch (nIndex)
        {
            case Scenarize.SCEN_END_PHOTO_BEAR:
                imgMask.setImageResource(R.drawable.iii_zoo_bear_frame);
                return "黑熊";
            case Scenarize.SCEN_END_PHOTO_LION:
                imgMask.setImageResource(R.drawable.iii_zoo_lion_frame);
                return "獅子";
            case Scenarize.SCEN_END_PHOTO_LEOPARD:
                imgMask.setImageResource(R.drawable.iii_zoo_leopard_frame);
                return "花豹";
            case Scenarize.SCEN_END_PHOTO_MONKEY:
                imgMask.setImageResource(R.drawable.iii_zoo_monkey_frame);
                return "猴子";
        }
        return "動物";
    }
}
