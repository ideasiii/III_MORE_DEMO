package com.iii.more.game.zoo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/8
 */

public class ZooTaiwanLayout extends ViewPagerLayout
{
    private Handler handlerScenarize = null;
    private boolean mbRepeat = false;
    private final int MSG_SLIDE_SHOW = 1;
    private int mnSecond = 3000;
    
    private enum ANIMAL_ID
    {
        台灣獼猴(1), 台灣黑熊(2);
        //台灣獼猴(1), 台灣黑熊(2), 梅花鹿(3),石虎(4);
        private int value;
        
        private ANIMAL_ID(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private class Animal
    {
        public ImageView image;
        public String name;
        public int id;
        
        public Animal(Context context)
        {
            image = new ImageView(context);
        }
    }
    
    SparseArray<Animal> listAnimal = null;
    
    
    public ZooTaiwanLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public ZooTaiwanLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ZooTaiwanLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public ZooTaiwanLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        stopSlideShow();
        super.finalize();
    }
    
    public void setHandler(Handler handler)
    {
        handlerScenarize = handler;
    }
    
    private void init(Context context)
    {
        setPagingEnable(false);
        listAnimal = new SparseArray<Animal>();
        for (ANIMAL_ID aid : ANIMAL_ID.values())
        {
            Animal animal = new Animal(context);
            animal.id = aid.getValue();
            animal.name = aid.name();
            int nr = R.drawable.zoo_taiwan;
            switch (animal.id)
            {
                case 1:
                    nr = R.drawable.taiwan_monkey;
                    break;
                case 2:
                    nr = R.drawable.taiwan_bear;
                    break;
            }
            
            Glide.with(context).load(R.drawable.taiwan_monkey).asGif().diskCacheStrategy
                (DiskCacheStrategy.SOURCE).into(animal.image);
            addPage(animal.image, animal.name);
        }
        start();
    }
    
    public void startSlideShow(int nSecond, boolean bRepeat)
    {
        mnSecond = nSecond * 1000;
        mbRepeat = bRepeat;
        handler.sendEmptyMessageDelayed(MSG_SLIDE_SHOW, mnSecond);
    }
    
    public void stopSlideShow()
    {
        mnSecond = 3000;
        mbRepeat = false;
        handler.removeMessages(MSG_SLIDE_SHOW);
    }
    
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (MSG_SLIDE_SHOW == msg.what)
            {
                int nPage = getCurrentPage();
                int nCount = getPageCount();
                Logs.showTrace("[ZooTaiwanLayout] handleMessage shlid show page:" + nPage + " " +
                    "count:" + nCount);
                if ((nPage + 1) >= nCount)
                {
                    nPage = 0;
                }
                else
                {
                    ++nPage;
                }
                showPage(nPage);
                if (mbRepeat)
                {
                    sendEmptyMessageDelayed(MSG_SLIDE_SHOW, mnSecond);
                }
            }
        }
    };
}
