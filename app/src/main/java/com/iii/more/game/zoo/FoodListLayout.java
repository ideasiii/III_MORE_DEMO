package com.iii.more.game.zoo;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.util.AttributeSet;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Jugo on 2017/12/9
 */

public class FoodListLayout extends ViewPagerLayout
{
    private Handler handlerScenarize = null;
    private Context theContext = null;
    
    private enum FOOD_ITEM
    {
        冰淇淋(R.drawable.food_cream_eat), 甜甜圈(R.drawable.food_donut_eat), 漢堡(R.drawable
        .food_hamb_eat), 棒棒糖(R.drawable.food_lollipops_eat), 蛋糕(R.drawable.food_cake_eat);
        private int value;
        
        FOOD_ITEM(int value)
        {
            this.value = value;
        }
        
        public int getValue()
        {
            return this.value;
        }
    }
    
    public FoodListLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public FoodListLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public FoodListLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public FoodListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    public FoodListLayout(Context context, Handler handler)
    {
        super(context);
        handlerScenarize = handler;
        init(context);
    }
    
    void init(Context context)
    {
        theContext = context;
        for (FOOD_ITEM aid : FOOD_ITEM.values())
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(aid.getValue());
            imageView.setTag(aid.getValue());
            imageView.setOnClickListener(onClickListener);
            addPage(imageView, aid.name());
        }
        start();
    }
    
    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Message message = new Message();
            message.what = SCEN.SCEN_INDEX_FOOD_EAT;
            message.obj = view.getTag();
            handlerScenarize.sendMessage(message);
        }
    };
}
