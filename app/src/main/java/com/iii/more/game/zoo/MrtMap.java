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

import android.os.Handler;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/29
 */

public class MrtMap extends RelativeLayout
{
    private Handler handlerScenarize = null;
    private ImageView imgMap = null;
    private FingerPaintView fingerPaintView = null;
    private final float ZOO_X = 500;
    private final float ZOO_Y = 500;
    
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
    
    public MrtMap(Context context, Handler handler)
    {
        super(context);
        handlerScenarize = handler;
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
        fingerPaintView.setOnTouchLister(onTouchLister);
        addView(fingerPaintView);
    }
    
    private FingerPaintView.OnTouchLister onTouchLister = new FingerPaintView.OnTouchLister()
    {
        @Override
        public void onTouch(FingerPaintView.Emotion emotion, float nX, float nY)
        {
            switch (emotion)
            {
                case Touch_up:
                    if (ZOO_X <= nX && ZOO_Y <= nY)
                    {
                        Logs.showTrace("[MrtMap] onTouch ZOO OK");
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_CHOICE_ZOO);
                    }
                    else
                    {
                        fingerPaintView.clear();
                    }
                    break;
                case Touch_move:
                    break;
                case Touch_down:
                    break;
            }
        }
    };
}
