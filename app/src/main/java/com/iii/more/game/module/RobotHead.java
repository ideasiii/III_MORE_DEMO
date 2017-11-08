package com.iii.more.game.module;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.speech.tts.UtteranceProgressListener;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;

import com.iii.more.main.R;

import java.util.Locale;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1.
 */

public class RobotHead extends RelativeLayout
{
    public static final int INIT_TTS = 0;
    
    TtsHandler ttsHandler = null;
    private ImageView imgFace = null;
    private ImageView imgObject = null;
    private OnInitedListener onInitedListener = null;
    private OnDroppedListener onDroppedListener = null;
    private boolean mbObjImgTouch = false;
    
    public static interface OnInitedListener
    {
        public void onInited(int nWhat);
    }
    
    public static interface OnDroppedListener
    {
        public void onDroped(View view);
    }
    
    public void setOnInitedListener(OnInitedListener listener)
    {
        onInitedListener = listener;
    }
    
    public void setOnDroppedListener(OnDroppedListener listener)
    {
        onDroppedListener = listener;
    }
    
    public RobotHead(Context context)
    {
        super(context);
        init(context);
    }
    
    public void SetOnUtteranceProgressListener(UtteranceProgressListener utteranceProgressListener)
    {
        if (null != ttsHandler && ttsHandler.isValid())
        {
            ttsHandler.SetOnUtteranceProgressListener(utteranceProgressListener);
        }
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
        
        LayoutParams layoutParamsP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.setLayoutParams(layoutParamsP);
        
        fLeft = Utility.convertDpToPixel(-120, context);
        fTop = Utility.convertDpToPixel(60, context);
        fRight = Utility.convertDpToPixel(-120, context);
        fBottom = Utility.convertDpToPixel(-60, context);
        
        setBackgroundResource(R.color.default_app_color);
        imgFace = new ImageView(context);
        imgFace.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFace.setAdjustViewBounds(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        // layoutParams.setMargins((int) fLeft, (int) fTop, (int) fRight, (int) fBottom);
        imgFace.setLayoutParams(layoutParams);
        imgFace.setImageResource(R.drawable.default_image);
        //imgFace.setPadding(-50, 50, -50, -50);
        addView(imgFace);
        imgFace.setOnDragListener(ImgFaceDragListener);
        showFaceImg(false);
        
        imgObject = new ImageView(context);
        imgObject.setTag("Object Image");
        imgObject.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgObject.setAdjustViewBounds(true);
        LayoutParams layoutParams1 = new LayoutParams((int) Utility.convertDpToPixel(300, context), (int) Utility.convertDpToPixel(300, context));
        layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
        imgObject.setLayoutParams(layoutParams1);
        imgObject.setBackgroundColor(Color.TRANSPARENT);
        addView(imgObject);
        showObjectImg(false);
        // imgObject.setOnTouchListener(ImgObjTouchListener);
        
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
    
    public void setFace(int nResId, ImageView.ScaleType is)
    {
        imgFace.setImageResource(nResId);
        imgFace.setScaleType(is);
    }
    
    public void setObjectImg(int nResId, ImageView.ScaleType is)
    {
        imgObject.setImageResource(nResId);
        imgObject.setScaleType(is);
    }
    
    public void showObjectImg(boolean bShow)
    {
        if (bShow)
        {
            imgObject.setVisibility(View.VISIBLE);
        }
        else
        {
            imgObject.setVisibility(View.GONE);
        }
    }
    
    public void showFaceImg(boolean bShow)
    {
        if (bShow)
        {
            imgFace.setVisibility(View.VISIBLE);
        }
        else
        {
            imgFace.setVisibility(View.GONE);
        }
    }
    
    public void playTTS(String strWord, String strUtteranceld)
    {
        ttsHandler.speack(strWord, strUtteranceld);
    }
    
    public void setPitch(float fpitch, float frate)
    {
        ttsHandler.setPitch(fpitch, frate);
    }
    
    public void setBackground(int nColor)
    {
        setBackgroundResource(nColor);
    }
    
    public void setImgObjectTouch(boolean bEnable)
    {
        if (bEnable)
        {
            imgObject.setOnTouchListener(ImgObjTouchListener);
        }
        else
        {
            imgObject.setOnTouchListener(null);
        }
    }
    
    private OnTouchListener ImgObjTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                ClipData.Item item = new ClipData.Item((CharSequence) imgObject.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(imgObject.getTag().toString(), mimeTypes, item);
                DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                Logs.showTrace("ACTION_UP");
                return false;
            }
            else
            {
                return false;
            }
        }
    };
    
    private OnDragListener ImgFaceDragListener = new OnDragListener()
    {
        @Override
        public boolean onDrag(View v, DragEvent event)
        {
            int action = event.getAction();
            switch (event.getAction())
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    Logs.showTrace("Dropped!!!!!!!!!!!!!");
                    if (null != onDroppedListener)
                    {
                        onDroppedListener.onDroped(v);
                    }
                    setBackgroundResource(R.color.default_app_color);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    };
}
