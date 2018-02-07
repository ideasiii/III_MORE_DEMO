package org.iii.moresdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class IndicatorView extends View
{
    
    private Paint mPaint = null;
    private int mnCount = 1;
    private int mnSpacing = 0;
    private int mnPosition = -1;
    
    public IndicatorView(Context context)
    {
        super(context);
        init(context);
    }
    
    public IndicatorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context)
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mnSpacing = Global.ScaleSize(context, 7);
    }
    
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(Global.measureWidth(widthMeasureSpec), Global.measureHeight
                (heightMeasureSpec));
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        if (0 >= mnCount)
        {
            return;
        }
        int nCenterX = (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) / 2;
        int height = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
        float radius = height / 2; // 圓半徑
        int nSpacing = height + mnSpacing;
        
        int nRemind = mnCount % 2;
        int nHelfCount = mnCount / 2;
        int nFirstCircleX = 0;
        
        if (0 < nRemind) // 奇數
        {
            nFirstCircleX = (int) (nCenterX - (nHelfCount * nSpacing));
        }
        else
        { // 偶數
            nFirstCircleX = (int) (nCenterX - (nHelfCount * nSpacing) + (mnSpacing / 2) + radius);
        }
        
        for (int i = 0; i < mnCount; ++i)
        {
            if (i == mnPosition)
            {
                mPaint.setColor(Color.DKGRAY);
            }
            else
            {
                mPaint.setColor(Color.LTGRAY);
            }
            canvas.drawCircle(nFirstCircleX, radius, radius, mPaint);
            nFirstCircleX += nSpacing;
        }
    }
    
    public void setCount(int nCount)
    {
        mnCount = nCount;
        this.invalidate();
    }
    
    public void setPosition(int nPosition)
    {
        mnPosition = nPosition;
        this.invalidate();
    }
}
