package com.iii.more.game.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;

import com.iii.more.main.R;

import java.util.Locale;

/**
 * Created by jugo on 2017/11/1.
 */

public class RobotHead extends RelativeLayout
{
    public static final int INIT_TTS = 0;
    
    TtsHandler ttsHandler = null;
    private ImageView imgFace = null;
    private OnInitedListener onInitedListener = null;
    
    public static interface OnInitedListener
    {
        public void onInited(int nWhat);
    }
    
    public void setOnInitedListener(OnInitedListener listener)
    {
        onInitedListener = listener;
    }
    
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
        float fLeft, fTop, fRight, fBottom;
        
        fLeft = Utility.convertDpToPixel(-120, context);
        fTop = Utility.convertDpToPixel(60, context);
        fRight = Utility.convertDpToPixel(-120, context);
        fBottom = Utility.convertDpToPixel(-60, context);
        
        setBackgroundResource(R.color.default_app_color);
        imgFace = new ImageView(context);
        imgFace.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFace.setAdjustViewBounds(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins((int) fLeft, (int) fTop, (int) fRight, (int) fBottom);
        imgFace.setLayoutParams(layoutParams);
        imgFace.setImageResource(R.drawable.default_image);
        
        addView(imgFace);
        
        ttsHandler = new TtsHandler(context);
        ttsHandler.setOnTTSStartedListener(new TtsHandler.OnTTSStartedListener()
        {
            @Override
            public void OnStarted()
            {
                // TTS init Success.
                if (null != onInitedListener)
                {
                    onInitedListener.onInited(MSG.MSG_INIT_TTS);
                }
            }
        });
    }
    
    public void start()
    {
        ttsHandler.createTTS();
        ttsHandler.setLanguage(Locale.TAIWAN);
    }
    
    public void stop()
    {
        ttsHandler.release();
    }
    
    public void setFace(int nResId)
    {
        imgFace.setImageResource(nResId);
    }
    
    public void playTTS(String strWord)
    {
        ttsHandler.speack(strWord);
    }
}
