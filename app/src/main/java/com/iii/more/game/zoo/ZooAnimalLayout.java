package com.iii.more.game.zoo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/8
 */

public class ZooAnimalLayout extends ViewPagerLayout
{
    private Context theContext = null;
    private Handler handlerScenarize = null;
    private boolean mbRepeat = false;
    private final int MSG_SLIDE_SHOW = 1;
    private int mnSecond = 3000;
    
    public static enum ANIMAL_AREA
    {
        台灣動物區(1),
        鳥園(2),
        非洲動物區(3),
        可愛動物區(4),
        熱帶雨林動物區(5);
        private int value;
        
        private ANIMAL_AREA(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private enum ANIMAL_BIRD
    {
        鳥園(0),
        貓頭鷹(1),
        老鷹(2),
        鴿子(3),
        鸚鵡(4);
        private int value;
        
        ANIMAL_BIRD(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private enum ANIMAL_TAIWAN
    {
        台灣動物區(0),
        台灣獼猴(1),
        台灣黑熊(2);
        private int value;
        
        ANIMAL_TAIWAN(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private enum ANIMAL_CUT
    {
        可愛動物區(0),
        牛(1),
        小狗(2),
        馬(3),
        小豬(4),
        綿羊(5);
        private int value;
        
        ANIMAL_CUT(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    
    private enum ANIMAL_RAIN
    {
        熱帶雨林動物區(0),
        鱷魚(1),
        花豹(2),
        孟加拉虎(3);
        private int value;
        
        ANIMAL_RAIN(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    private enum ANIMAL_AFFRICA
    {
        非洲動物區(0),
        大象(1),
        猩猩(2),
        獅子(3);
        private int value;
        
        ANIMAL_AFFRICA(int value)
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
    
    public ZooAnimalLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public ZooAnimalLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ZooAnimalLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public ZooAnimalLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
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
        theContext = context;
        setPagingEnable(false);
    }
    
    public void init(ANIMAL_AREA animal_area)
    {
        clear();
        switch (animal_area)
        {
            case 台灣動物區:
                for (ANIMAL_TAIWAN aid : ANIMAL_TAIWAN.values())
                {
                    Animal animal = new Animal(theContext);
                    animal.id = aid.getValue();
                    animal.name = aid.name();
                    int nr = R.drawable.zoo_taiwan;
                    switch (animal.id)
                    {
                        case 0:
                            nr = R.drawable.zoo_taiwan;
                            break;
                        case 1:
                            nr = R.drawable.taiwan_monkey;
                            break;
                        case 2:
                            nr = R.drawable.taiwan_bear;
                            break;
                    }
                    Glide.with(theContext).load(nr).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into(animal.image);
                    addPage(animal.image, animal.name);
                }
                break;
            case 鳥園:
                for (ANIMAL_BIRD aid : ANIMAL_BIRD.values())
                {
                    Animal animal = new Animal(theContext);
                    animal.id = aid.getValue();
                    animal.name = aid.name();
                    int nr = R.drawable.zoo_bird;
                    switch (animal.id)
                    {
                        case 0:
                            nr = R.drawable.zoo_bird;
                            break;
                        case 1:
                            nr = R.drawable.bird_gugu;
                            break;
                        case 2:
                            nr = R.drawable.bird_eagle;
                            break;
                        case 3:
                            nr = R.drawable.bird_pigeon;
                            break;
                        case 4:
                            nr = R.drawable.bird_parrot;
                            break;
                    }
                    Glide.with(theContext).load(nr).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into(animal.image);
                    addPage(animal.image, animal.name);
                }
                break;
            case 可愛動物區: //可愛動物區(0), 牛(1), 小狗(2), 馬(3), 小豬(4), 綿羊(5);
                for (ANIMAL_CUT aid : ANIMAL_CUT.values())
                {
                    Animal animal = new Animal(theContext);
                    animal.id = aid.getValue();
                    animal.name = aid.name();
                    int nr = R.drawable.zoo_cut;
                    switch (animal.id)
                    {
                        case 0:
                            nr = R.drawable.zoo_cut;
                            break;
                        case 1:
                            nr = R.drawable.cut_cow;
                            break;
                        case 2:
                            nr = R.drawable.cut_dog;
                            break;
                        case 3:
                            nr = R.drawable.cut_hourse;
                            break;
                        case 4:
                            nr = R.drawable.cut_pig;
                            break;
                        case 5:
                            nr = R.drawable.cut_sheep;
                            break;
                    }
                    Glide.with(theContext).load(nr).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into(animal.image);
                    addPage(animal.image, animal.name);
                }
                break;
            case 非洲動物區: // 非洲動物區(0), 大象(1), 猩猩(2), 獅子(3);
                for (ANIMAL_AFFRICA aid : ANIMAL_AFFRICA.values())
                {
                    Animal animal = new Animal(theContext);
                    animal.id = aid.getValue();
                    animal.name = aid.name();
                    int nr = R.drawable.zoo_affrica;
                    switch (animal.id)
                    {
                        case 0:
                            nr = R.drawable.zoo_affrica;
                            break;
                        case 1:
                            nr = R.drawable.affica_elephone;
                            break;
                        case 2:
                            nr = R.drawable.affica_kong;
                            break;
                        case 3:
                            nr = R.drawable.affica_lion;
                            break;
                    }
                    Glide.with(theContext).load(nr).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into(animal.image);
                    addPage(animal.image, animal.name);
                }
                break;
            case 熱帶雨林動物區: // 熱帶雨林動物區(0), 鱷魚(1), 花豹(2), 孟加拉虎(3);
                for (ANIMAL_RAIN aid : ANIMAL_RAIN.values())
                {
                    Animal animal = new Animal(theContext);
                    animal.id = aid.getValue();
                    animal.name = aid.name();
                    int nr = R.drawable.zoo_rain;
                    switch (animal.id)
                    {
                        case 0:
                            nr = R.drawable.zoo_rain;
                            break;
                        case 1:
                            nr = R.drawable.rain_coco;
                            break;
                        case 2:
                            nr = R.drawable.rain_guarge;
                            break;
                        case 3:
                            nr = R.drawable.rain_tiger;
                            break;
                    }
                    Glide.with(theContext).load(nr).apply(RequestOptions.diskCacheStrategyOf
                        (DiskCacheStrategy.NONE)).into(animal.image);
                    addPage(animal.image, animal.name);
                }
                break;
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
    
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            if (MSG_SLIDE_SHOW == msg.what)
            {
                int nPage = getCurrentPage();
                int nCount = getPageCount();
                Logs.showTrace("[ZooAnimalLayout] handleMessage slide show page:" + nPage + " " +
                    "count:" + nCount);
                Message message = new Message();
                message.what = SCEN.MSG_TTS_PLAY;
                
                if ((nPage + 1) >= nCount) // end
                {
                    if (mbRepeat)
                    {
                        showPage(0);
                        message.obj = getTitle(0);
                        handlerScenarize.sendMessage(message);
                        sendEmptyMessageDelayed(MSG_SLIDE_SHOW, mnSecond);
                    }
                    else
                    {
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ANIMAL_END);
                    }
                }
                else
                {
                    ++nPage;
                    showPage(nPage);
                    message.obj = getTitle(nPage);
                    handlerScenarize.sendMessage(message);
                    sendEmptyMessageDelayed(MSG_SLIDE_SHOW, mnSecond);
                }
            }
        }
    };
}
