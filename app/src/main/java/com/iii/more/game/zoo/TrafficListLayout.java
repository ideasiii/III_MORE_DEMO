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
import com.bumptech.glide.request.RequestOptions;
import com.iii.more.game.module.Utility;
import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/12
 */

public class TrafficListLayout extends ViewPagerLayout
{
    private Handler handlerScenarize = null;
    private int mnNext = -1;
    
    private enum TRAFFIC_ITEM
    {
        汽車(R.drawable.car_car),
        摩托車(R.drawable.car_motor),
        救護車(R.drawable.car_emblance),
        挖土機(R.drawable.car_exmal);
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
    
    public void setNextScenarize(final int nNext)
    {
        mnNext = nNext;
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
//            Glide.with(context).load(aid.getValue()).apply(RequestOptions.diskCacheStrategyOf
//                (DiskCacheStrategy.NONE)).into(imageView);
            Utility.loadImage(context,aid.getValue(),imageView);
            addPage(imageView, aid.name());
        }
        start();
        
        setOnSlideShowListener(onSlideShowListener);
    }
    
    private OnSlideShowListener onSlideShowListener = new OnSlideShowListener()
    {
        @Override
        public void onShow(int nPage, SLIDE_STATUS slideStatus)
        {
            if (slideStatus == SLIDE_STATUS.END)
            {
                handlerScenarize.sendEmptyMessage(mnNext);
                return;
            }
            
            Logs.showTrace("[zoo] TrafficListLayout page change:" + nPage + " Title:" +
                TrafficListLayout.this.getTitle(nPage) + " SLIDE_STATUS:" + slideStatus);
            
            Message message = new Message();
            message.what = SCEN.MSG_TTS_PLAY;
            
            switch (slideStatus)
            {
                case START:
                case RUN:
                    message.obj = TrafficListLayout.this.getTitle(nPage);
                    handlerScenarize.sendMessage(message);
                    break;
            }
        }
    };
}
