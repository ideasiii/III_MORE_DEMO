package com.iii.more.game.parktour;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iii.more.game.module.Utility;
import com.iii.more.main.R;

/**
 * Created by Jugo on 2018/2/2
 */

public class FaceView extends RelativeLayout
{
    private ImageView mimgFace = null;
    private Context theContext = null;
    
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
    
    private void init(Context context)
    {
        theContext = context;
        mimgFace = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        mimgFace.setLayoutParams(layoutParams);
        addView(mimgFace);
    }
    
    public void loadImage(final int nResId)
    {
        Utility.loadImage(theContext, nResId, mimgFace);
    }
    
    
}
