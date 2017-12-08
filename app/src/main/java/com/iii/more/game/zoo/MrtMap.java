package com.iii.more.game.zoo;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iii.more.game.module.FingerPaintView;
import com.iii.more.main.R;

/**
 * Created by joe on 2017/11/29.
 */

public class MrtMap extends RelativeLayout
{
    private ImageView imgMap = null;
    private FingerPaintView fingerPaintView = null;
    
    public MrtMap(Context context)
    {
        super(context);
        init(context);
    }
    
    public MrtMap(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public MrtMap(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public MrtMap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    @SuppressLint("ResourceAsColor")
    void init(Context context)
    {
        setBackgroundColor(Color.TRANSPARENT);
        imgMap = new ImageView(context);
        
        RelativeLayout.LayoutParams layoutParamsMap = new RelativeLayout.LayoutParams(ViewGroup
            .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        
        RelativeLayout.LayoutParams layoutParamsTrain = new RelativeLayout.LayoutParams(200, 200);
        
        imgMap.setLayoutParams(layoutParamsMap);
        
        imgMap.setImageResource(R.drawable.mrt_map);
        imgMap.setBackgroundColor(Color.TRANSPARENT);
        
        imgMap.setScaleType(ImageView.ScaleType.FIT_XY);
    
        fingerPaintView = new FingerPaintView(context);
        fingerPaintView.setLayoutParams(layoutParamsMap);
        fingerPaintView.setBackgroundResource(R.drawable.mrt_map);
        
        addView(fingerPaintView);
    }
}
