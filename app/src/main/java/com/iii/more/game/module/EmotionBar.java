package com.iii.more.game.module;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iii.more.main.R;

import org.iii.moresdk.widget.ProgressView;

/**
 * Created by Jugo on 2018/2/8
 */

public class EmotionBar extends LinearLayout
{
    private Context theContext = null;
    private RelativeLayout rlFaceLayout = null;
    private ImageView imgFaceLeft = null;
    private ImageView imgFaceRight = null;
    private ProgressView progressView = null;
    
    public EmotionBar(Context context)
    {
        super(context);
        init(context);
    }
    
    public EmotionBar(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public EmotionBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public EmotionBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        theContext = context;
        this.setOrientation(VERTICAL);
        ViewGroup.LayoutParams lpMain = new ViewGroup.LayoutParams(ViewGroup.LayoutParams
            .MATCH_PARENT, 400);
        setLayoutParams(lpMain);
        setPadding(50, 50, 50, 50);
        setBackgroundColor(Color.WHITE);
        setGravity(Gravity.TOP);
        
        rlFaceLayout = new RelativeLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams
            .MATCH_PARENT, 100);
        rlFaceLayout.setLayoutParams(layoutParams);
        
        imgFaceLeft = new ImageView(context);
        imgFaceRight = new ImageView(context);
        RelativeLayout.LayoutParams lpFaceLeft = new RelativeLayout.LayoutParams(100, 100);
        RelativeLayout.LayoutParams lpFaceRight = new RelativeLayout.LayoutParams(100, 100);
        lpFaceLeft.addRule(RelativeLayout.ALIGN_PARENT_START);
        lpFaceRight.addRule(RelativeLayout.ALIGN_PARENT_END);
        imgFaceLeft.setLayoutParams(lpFaceLeft);
        imgFaceRight.setLayoutParams(lpFaceRight);
        imgFaceLeft.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFaceRight.setScaleType(ImageView.ScaleType.FIT_XY);
        // imgFaceLeft.setImageResource(R.drawable.iii_face_joy);
        //imgFaceRight.setImageResource(R.drawable.iii_face_angry);
        
        rlFaceLayout.addView(imgFaceLeft);
        rlFaceLayout.addView(imgFaceRight);
        addView(rlFaceLayout);
        
        progressView = new ProgressView(context);
        progressView.setLayoutParams(layoutParams);
        addView(progressView);
    }
    
    public void setPosition(final int nPosition)
    {
        progressView.setPosition(nPosition);
    }
    
    public void setIcon(int nResIdLeft, int nResIdRight)
    {
        imgFaceLeft.setImageResource(nResIdLeft);
        imgFaceRight.setImageResource(nResIdRight);
    }
    
    public void setColor(final int nFR, final int nFG, final int nFB, final int nBR, final int
        nBG, final int nBB)
    {
        progressView.setFrontColor(nFR, nFG, nFB);
        progressView.setBackColor(nBR, nBG, nBB);
    }
}
