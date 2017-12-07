package com.iii.more.game.module;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by Jugo on 2017/12/7
 */

public class ViewPagerLayout extends RelativeLayout
{
    private Context theContext = null;
    private ViewPager viewPager = null;
    private ViewPagerAdapter viewPagerAdapter = null;
    
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
        viewPager = new ViewPager(context);
        LayoutParams layoutParamsViewPager = new LayoutParams(ViewGroup.LayoutParams
            .MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(layoutParamsViewPager);
        viewPager.setBackgroundColor(Color.TRANSPARENT);
        addView(viewPager);
        viewPagerAdapter = new ViewPagerAdapter();
    }
    
    public void start()
    {
        viewPager.setAdapter(viewPagerAdapter);
    }
    
    public int addPage(View view, String strTitle)
    {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
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
        
        public int addPage(View view, String strTitle)
        {
            Page page = new Page();
            page.view = view;
            page.strTitle = strTitle;
            Pages.put(Pages.size(), page);
            return (Pages.size() - 1);
        }
        
        public void removePage(int position)
        {
            if (isValid(position) && (Pages.indexOfKey(position) < 0))
            {
                Pages.removeAt(position);
            }
        }
        
        public boolean isValid(int position)
        {
            return !(0 > position || getCount() <= position);
        }
    }
}
