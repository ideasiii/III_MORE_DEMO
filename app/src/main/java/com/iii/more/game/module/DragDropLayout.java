package com.iii.more.game.module;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/25
 */

public abstract class DragDropLayout extends RelativeLayout
{
    private int mCurX = 0;
    private int mCurY = 0;
    private SparseArray<OnDragDropped> listOnDragDroppedListener;
    
    //========== Listener interface ==================//
    public static interface OnDragDropped
    {
        public void onDropped(View view, int x, int y);
    }
    
    protected void setOnDragDroppedListener(OnDragDropped listener)
    {
        if (null != listener)
        {
            listOnDragDroppedListener.append(listOnDragDroppedListener.size(), listener);
        }
    }
    
    private void notifyDragDropped(View view, final int nX, final int nY)
    {
        for (int i = 0; i < listOnDragDroppedListener.size(); ++i)
        {
            listOnDragDroppedListener.get(i).onDropped(view, nX, nY);
        }
    }
    
    public DragDropLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public DragDropLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public DragDropLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public DragDropLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        listOnDragDroppedListener = new SparseArray<OnDragDropped>();
    }
    
    public void addDropTarget(View viewTarget, ViewPosition viewPosition)
    {
        if (null != viewTarget)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (viewPosition.width, viewPosition.height);
            viewTarget.setLayoutParams(layoutParams);
            viewTarget.setX(viewPosition.x);
            viewTarget.setY(viewPosition.y);
            viewTarget.setOnDragListener(onDragListener);
            addView(viewTarget);
        }
    }
    
    public void addDragItem(View viewItem, ViewPosition viewPosition)
    {
        if (null != viewItem)
        {
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams
                (viewPosition.width, viewPosition.height);
            viewItem.setLayoutParams(layoutParams);
            viewItem.setX(viewPosition.x);
            viewItem.setY(viewPosition.y);
            viewItem.setOnTouchListener(dropTouchListener);
            
            if (null == viewItem.getTag())
            {
                viewItem.setTag(String.valueOf(viewItem.getId()));
            }
            addView(viewItem);
        }
    }
    
    private OnDragListener onDragListener = new OnDragListener()
    {
        @Override
        public boolean onDrag(View v, DragEvent event)
        {
            switch (event.getAction())
            {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    notifyDragDropped((View) event.getLocalState(), mCurX, mCurY);
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    mCurX = (int) event.getX();
                    mCurY = (int) event.getY();
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    
    private View.OnTouchListener dropTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            
            return motionEvent.getAction() == MotionEvent.ACTION_UP;
            
        }
    };
}
