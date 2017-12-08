package com.iii.more.game.zoo;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.iii.more.game.module.ViewPagerLayout;
import com.iii.more.main.R;

/**
 * Created by Jugo on 2017/12/7
 */

public class ZooAreaLayout extends ViewPagerLayout
{
    private Handler handlerScenarize = null;
    
    private static enum ZOO
    {
        TAIWAN, BIRD, RAIN, CUT, AFFICA
    }
    
    private ImageView imgZooTaiwan = null;
    private ImageView imgZooBird = null;
    private ImageView imgZoorain = null;
    private ImageView imgZoocut = null;
    private ImageView imgZooAffica = null;
    
    public ZooAreaLayout(Context context, Handler handler)
    {
        super(context);
        init(context);
        handlerScenarize = handler;
    }
    
    public ZooAreaLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ZooAreaLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public ZooAreaLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    public void init(Context context)
    {
        imgZooTaiwan = new ImageView(context);
        imgZooBird = new ImageView(context);
        imgZoorain = new ImageView(context);
        imgZoocut = new ImageView(context);
        imgZooAffica = new ImageView(context);
        
        imgZooTaiwan.setImageResource(R.drawable.zoo_taiwan);
        imgZooBird.setImageResource(R.drawable.zoo_bird);
        imgZoorain.setImageResource(R.drawable.zoo_rain);
        imgZoocut.setImageResource(R.drawable.zoo_cut);
        imgZooAffica.setImageResource(R.drawable.zoo_affrica);
        
        imgZooTaiwan.setTag(ZOO.TAIWAN);
        imgZooBird.setTag(ZOO.BIRD);
        imgZoocut.setTag(ZOO.CUT);
        imgZoorain.setTag(ZOO.RAIN);
        imgZooAffica.setTag(ZOO.AFFICA);
        
        imgZooTaiwan.setOnClickListener(onClickListener);
        imgZooBird.setOnClickListener(onClickListener);
        imgZoocut.setOnClickListener(onClickListener);
        imgZoorain.setOnClickListener(onClickListener);
        imgZooAffica.setOnClickListener(onClickListener);
        
        addPage(imgZooTaiwan, "台灣動物區");
        addPage(imgZooBird, "鳥園");
        addPage(imgZooAffica, "非洲動物區");
        addPage(imgZoocut, "可愛動物區");
        addPage(imgZoorain, "熱帶雨林動物區");
        
        start();
    }
    
    private OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ZOO tagZoo = (ZOO) view.getTag();
            switch (tagZoo)
            {
                case TAIWAN:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_TAIWAN);
                    break;
                case BIRD:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_BIRD);
                    break;
                case RAIN:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_RAIN);
                    break;
                case CUT:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_CUT);
                    break;
                case AFFICA:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_AFFICA);
                    break;
            }
        }
    };
}
