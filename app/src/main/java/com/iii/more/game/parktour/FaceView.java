package com.iii.more.game.parktour;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Jugo on 2018/2/2
 */

public class FaceView extends RelativeLayout
{
    public FaceView(Context context)
    {
        super(context);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    void init(Context context)
    {
    
    }
}
