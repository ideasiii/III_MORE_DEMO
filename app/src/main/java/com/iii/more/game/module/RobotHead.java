package com.iii.more.game.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;

import com.iii.more.main.R;

/**
 * Created by jugo on 2017/11/1.
 */

public class RobotHead extends RelativeLayout
{
    private ImageView imgFace = null;
    
    public RobotHead(Context context)
    {
        super(context);
        init(context);
    }
    
    public RobotHead(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public RobotHead(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public RobotHead(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    
    private void init(Context context)
    {
        setBackgroundResource(R.color.default_app_color);
        imgFace = new ImageView(context);
        imgFace.setScaleType(ImageView.ScaleType.FIT_XY);
        //imgFace.setAdjustViewBounds(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(-300, 60, -300, -60);
        imgFace.setLayoutParams(layoutParams);
        imgFace.setImageResource(R.drawable.default_image);
        
        
        addView(imgFace);
    }
}
