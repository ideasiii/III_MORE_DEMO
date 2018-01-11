package com.iii.more.game.module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ImageView;

import com.iii.more.main.R;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1
 */

public class RobotHead extends RelativeLayout
{
    private static int mCurX = 0;
    private static int mCurY = 0;
    private static ImageView imgFace = null;
    private static ImageView imgObject = null;
    private OnDroppedListener onDroppedListener = null;
    
    public interface OnDroppedListener
    {
        void onDropped(View view, int nX, int nY);
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
        LayoutParams layoutParamsP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        this.setPadding(0, 170, 0, 0);
        this.setLayoutParams(layoutParamsP);
        setBackgroundColor(Color.rgb(160, 201, 236)); //A0C9EC
        
        setBackgroundResource(R.color.default_app_color);
        imgFace = new ImageView(context);
        imgFace.setScaleType(ImageView.ScaleType.FIT_XY);
        imgFace.setAdjustViewBounds(false);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        imgFace.setLayoutParams(layoutParams);
        imgFace.setImageResource(R.drawable.default_image);
        imgFace.setOnDragListener(ImgFaceDragListener);
        showFaceImg(false);
        
        imgObject = new ImageView(context);
        imgObject.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgObject.setAdjustViewBounds(true);
        LayoutParams layoutParams1 = new LayoutParams((int) Utility.convertDpToPixel(400,
            context), (int) Utility.convertDpToPixel(400, context));
        layoutParams1.setMargins((int) 0, (int) 300, (int) 0, (int) 0);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        imgObject.setLayoutParams(layoutParams1);
        imgObject.setBackgroundColor(Color.TRANSPARENT);
        showObjectImg(false);
        
        addView(imgObject);
        addView(imgFace);
        Logs.showTrace("[zoo] RobotHead init ");
    }
    
    public ImageView getImgObject()
    {
        return imgObject;
    }
    
    public void setFace(Activity activity, int nResId, ImageView.ScaleType is)
    {
        // imgFace.setImageResource(nResId);
        imgFace.setScaleType(is);
        Utility.loadImage(activity, nResId, imgFace);
        Logs.showTrace("[zoo] RobotHead set Face image:" + nResId);
    }
    
    public void setObjectImg(Activity activity, int nResId, ImageView.ScaleType is)
    {
        // imgObject.setImageResource(nResId);
        imgObject.setScaleType(is);
        Utility.loadImage(activity, nResId, imgObject);
        Logs.showTrace("[zoo] RobotHead set object image:" + nResId);
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
    
    public void setBackground(int nColor)
    {
        setBackgroundResource(nColor);
    }
    
    
    private OnDragListener ImgFaceDragListener = new OnDragListener()
    {
        @Override
        public boolean onDrag(View v, DragEvent event)
        {
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
                        onDroppedListener.onDropped(v, mCurX, mCurY);
                    }
                    setBackgroundResource(R.color.default_app_color);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    mCurX = (int) event.getX();
                    mCurY = (int) event.getY();
                    Logs.showTrace("drop x = " + String.valueOf(mCurX));
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    
    public void bringObjImgtoFront()
    {
        imgObject.bringToFront();
    }
    
    public void bringFaceImgtoFront()
    {
        imgFace.bringToFront();
    }
    
}
