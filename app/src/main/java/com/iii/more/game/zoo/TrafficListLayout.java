package com.iii.more.game.zoo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

/**
 * Created by Jugo on 2017/12/12
 */

public class TrafficListLayout extends ViewPagerLayout
{
    private Handler handlerScenarize = null;
    
    //汽車、摩托車、救護車、挖土機
    private enum TRAFFIC_ITEM
    {
        汽車(R.drawable.car_car),
        摩托車(R.drawable.bird_gugu),
        救護車(R.drawable.bird_parrot),
        挖土機(R.drawable.bird_pigeon);
        private int value;
        
        TRAFFIC_ITEM(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    public TrafficListLayout(Context context)
    {
        super(context);
    }
    
    public TrafficListLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    
    public TrafficListLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }
    
    public TrafficListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    
    public TrafficListLayout(Context context, Handler handler)
    {
        super(context);
        handlerScenarize = handler;
        init(context);
        setPagingEnable(false);
        showArrow(false);
    }
    
    void init(Context context)
    {
        for (TRAFFIC_ITEM aid : TRAFFIC_ITEM.values())
        {
            ImageView imageView = new ImageView(context);
            Glide.with(context).load(aid.getValue()).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
            addPage(imageView, aid.name());
        }
        start();
    }
}
