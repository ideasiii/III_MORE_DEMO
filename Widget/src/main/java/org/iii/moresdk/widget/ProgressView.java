package org.iii.moresdk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Jugo on 2018/2/6
 */

public class ProgressView extends View
{
    private Paint paint = null;
    private RectF oval = null;              // 弧形
    private int mnMax = 100;                // MAX Position
    private int mnMin = 0;                  // MIN Position
    private int mnCurrentPosition = 50;     // Current Position
    
    private int mnFrontR = 252;
    private int mnFrontG = 42;
    private int mnFrontB = 29;
    
    // 253, 183, 175
    private int mnBackR = 253;
    private int mnBackG = 183;
    private int mnBackB = 175;
    
    public ProgressView(Context context)
    {
        super(context);
        init(context);
    }
    
    public ProgressView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int
        defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        paint = new Paint();
        paint.setAntiAlias(true);
        oval = new RectF();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(Global.measureWidth(widthMeasureSpec), Global.measureHeight
            (heightMeasureSpec));
        Logs.showTrace("[ProgressBar] onMeasure width: " + Global.measureWidth(widthMeasureSpec)
            + " height: " + Global.measureHeight(heightMeasureSpec));
    }
    
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        
        // calculate the left and right values
        float right = this.getWidth() - this.getPaddingRight();
        float left = right - this.getWidth() + this.getPaddingLeft();
        float height = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();
        float radius = height / 2; // 圓半徑
        
        //====== 圖背景條 ======//
        oval.set(left, 0, right, height);
        paint.setColor(Color.rgb(mnBackR, mnBackG, mnBackB));
        canvas.drawRoundRect(oval, radius, radius, paint);
        
        //====== 圖前景條 ======//
        oval.set(left, 0, (right / (mnMax - mnMin)) * mnCurrentPosition, height);
        paint.setColor(Color.rgb(mnFrontR, mnFrontG, mnFrontB));
        canvas.drawRoundRect(oval, radius, radius, paint);
    }
    
    public void setPosition(final int nPosition)
    {
        mnCurrentPosition = nPosition;
        this.invalidate();
    }
    
    public void setPositionLevel(final int nMin, final int nMax)
    {
        mnMin = nMin;
        mnMax = nMax;
    }
    
    public void setFrontColor(final int nR, final int nG, final int nB)
    {
        mnFrontR = nR;
        mnFrontG = nG;
        mnFrontB = nB;
    }
    
    public void setBackColor(final int nR, final int nG, final int nB)
    {
        mnBackR = nR;
        mnBackG = nG;
        mnBackB = nB;
    }
}
