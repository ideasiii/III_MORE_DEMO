package com.iii.more.game.module;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iii.more.main.R;


/**
 * Created by Jugo on 2017/12/7
 */

public class ViewPagerLayout extends RelativeLayout
{
    private Context theContext = null;
    private ViewPager viewPager = null;
    private ViewPagerAdapter viewPagerAdapter = null;
    private boolean mbPagingEnable = true;
    private ImageView imgForward = null;
    private ImageView imgBack = null;
    
    
    public ViewPagerLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public ViewPagerLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public ViewPagerLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public ViewPagerLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        setBackgroundColor(Color.TRANSPARENT);
        theContext = context;
        
        RelativeLayout.LayoutParams layoutParamsViewPager = new RelativeLayout.LayoutParams
            (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParamsViewPager.setMargins(30, 30, 30, 30);
        viewPager = new ViewPager(context);
        viewPager.setLayoutParams(layoutParamsViewPager);
        viewPager.setBackgroundColor(Color.TRANSPARENT);
        addView(viewPager);
        
        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setOnTouchListener(onTouchListener);
        
        RelativeLayout.LayoutParams layoutParamsForward = new RelativeLayout.LayoutParams(80, 200);
        layoutParamsForward.addRule(ALIGN_PARENT_END);
        layoutParamsForward.addRule(CENTER_VERTICAL);
        imgForward = new ImageView(context);
        imgForward.setAlpha(0.5f);
        imgForward.setImageResource(R.drawable.arrow_forward);
        imgForward.setLayoutParams(layoutParamsForward);
        imgForward.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imgForward);
        
        RelativeLayout.LayoutParams layoutParamsBack = new RelativeLayout.LayoutParams(80, 200);
        layoutParamsBack.addRule(ALIGN_PARENT_START);
        layoutParamsBack.addRule(CENTER_VERTICAL);
        imgBack = new ImageView(context);
        imgBack.setAlpha(0.5f);
        imgBack.setImageResource(R.drawable.arrow_back);
        imgBack.setLayoutParams(layoutParamsBack);
        imgBack.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imgBack);
        
    }
    
    private OnTouchListener onTouchListener = new OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            return !mbPagingEnable;
        }
    };
    
    public void start()
    {
        viewPager.setAdapter(viewPagerAdapter);
    }
    
    public int addPage(View view, String strTitle)
    {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        if (null == viewPagerAdapter)
        {
            viewPagerAdapter = new ViewPagerAdapter();
            viewPager.setAdapter(viewPagerAdapter);
        }
        return viewPagerAdapter.addPage(view, strTitle);
    }
    
    public View getPage(int position)
    {
        return viewPagerAdapter.getView(position);
    }
    
    public void showPage(int position)
    {
        if (!viewPagerAdapter.isValid(position))
        {
            return;
        }
        viewPager.setCurrentItem(position, true);
    }
    
    public void removePage(int position)
    {
        viewPagerAdapter.removePage(position);
    }
    
    public void setPagingEnable(boolean bEnable)
    {
        mbPagingEnable = bEnable;
    }
    
    public int getCurrentPage()
    {
        return viewPager.getCurrentItem();
    }
    
    public int getPageCount()
    {
        return viewPagerAdapter.getCount();
    }
    
    public String getTitle(int position)
    {
        return viewPagerAdapter.getTitle(position);
    }
    
    public void clear()
    {
        viewPagerAdapter.clear();
    }
    
    public void showArrow(boolean bShow)
    {
        if (bShow)
        {
            imgForward.setVisibility(VISIBLE);
            imgBack.setVisibility(VISIBLE);
        }
        else
        {
            imgForward.setVisibility(INVISIBLE);
            imgBack.setVisibility(INVISIBLE);
        }
    }
    
    private class ViewPagerAdapter extends PagerAdapter
    {
        
        private class Page
        {
            public View view = null;
            public String strTitle = null;
        }
        
        private SparseArray<Page> Pages = null;
        
        public ViewPagerAdapter()
        {
            Pages = new SparseArray<Page>();
        }
        
        @Override
        public int getCount()
        {
            return Pages.size();
        }
        
        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return (view == object);
        }
        
        @Override
        public int getItemPosition(Object object)
        {
            return super.getItemPosition(object);
        }
        
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(Pages.get(position).view);
        }
        
        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            ((ViewPager) container).addView(Pages.get(position).view, 0);
            return getView(position);
        }
        
        public View getView(int position)
        {
            return Pages.get(position).view;
        }
        
        public String getTitle(int position)
        {
            return Pages.get(position).strTitle;
        }
        
        public int addPage(View view, String strTitle)
        {
            if (null == Pages)
            {
                Pages = new SparseArray<Page>();
            }
            Page page = new Page();
            page.view = view;
            page.strTitle = strTitle;
            Pages.put(Pages.size(), page);
            return (Pages.size() - 1);
        }
        
        public int addPage(View view, String strTitle, int position)
        {
            if (null == Pages)
            {
                Pages = new SparseArray<Page>();
            }
            Page page = new Page();
            page.view = view;
            page.strTitle = strTitle;
            Pages.put(position, page);
            return position;
        }
        
        
        public void removePage(int position)
        {
            if (isValid(position) && (Pages.indexOfKey(position) < 0))
            {
                Pages.removeAt(position);
            }
        }
        
        public void clear()
        {
            Pages.clear();
        }
        
        public boolean isValid(int position)
        {
            return !(0 > position || getCount() <= position);
        }
    }
}
