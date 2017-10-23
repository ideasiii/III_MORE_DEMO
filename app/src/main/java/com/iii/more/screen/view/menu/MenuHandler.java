package com.iii.more.screen.view.menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.iii.more.animate.AnimatorUtils;
import com.iii.more.main.R;
import com.iii.more.screen.ClipRevealFrame;
import com.ogaclejapan.arclayout.ArcLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Device;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/8/10.
 */

public class MenuHandler extends BaseHandler implements View.OnClickListener
{
    private View rootLayout = null;
    private ClipRevealFrame menuLayout = null;
    private ArcLayout arcLayout = null;
    private View centerItem = null;
    
    public MenuHandler(Context context)
    {
        super(context);
    }
    
    public void setIDs(@IdRes int rootLayoutID, @IdRes int menuLayoutID
            , @IdRes int arcLayoutID, @IdRes int centerItemID)
    {
        //Device mDevice = new Device(mContext);
        // Logs.showTrace("####### getScaleSize:" + String.valueOf(mDevice.getScaleSize()));
        // Nexus 7   ########### W:1200  H:1824 dpi:320 ####### getScaleSize:2.0
        // HTC 10    ########### W:1440  H:2560 dpi:640 ####### getScaleSize:4.0
        // zefone 5  ########### W:720  H:1280 dpi:320 ####### getScaleSize:2.0
        // note 3    ########### W:1080  H:1920 dpi:480 ####### getScaleSize:3.0
        
        
        rootLayout = ((AppCompatActivity) mContext).findViewById(rootLayoutID);
        
        menuLayout = (ClipRevealFrame) ((AppCompatActivity) mContext).findViewById(menuLayoutID);
        arcLayout = (ArcLayout) ((AppCompatActivity) mContext).findViewById(arcLayoutID);
        centerItem = ((AppCompatActivity) mContext).findViewById(centerItemID);
        
        centerItem.setOnClickListener(this);
        
        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++)
        {
            arcLayout.getChildAt(i).setOnClickListener(this);
            
        }
        
    }
    
    
    @Override
    public void onClick(View v)
    {
        if (v instanceof Button)
        {
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "success");
            message.put("onClick", String.valueOf(v.getId()));
            callBackMessage(ResponseCode.ERR_SUCCESS, MenuParameters.CLASS_MENU, MenuParameters.METHOD_CLICK, message);
            
        }
    }
    
    public void showMenu(int cx, int cy, float startRadius)
    {
        float endRadius = (float) Math.hypot(
                Math.max(cy, rootLayout.getWidth() - cx),
                Math.max(cy, rootLayout.getHeight() - cy));
        showMenu(cx, cy, startRadius, endRadius);
    }
    
    private void showMenu(int cx, int cy, float startRadius, float endRadius)
    {
        menuLayout.setVisibility(View.VISIBLE);
        
        List<Animator> animList = new ArrayList<>();
        
        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(MenuParameters.MENU_REVEAL_DURING);
        
        animList.add(revealAnim);
        animList.add(createShowItemAnimator(centerItem));
        
        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++)
        {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }
        
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
    }
    
    public void hideMenu(int cx, int cy, float endRadius)
    {
        float startRadius = (float) Math.hypot(
                Math.max(cx, rootLayout.getWidth() - cx),
                Math.max(cx, rootLayout.getHeight() - cx));
        hideMenu(cx, cy, startRadius, endRadius);
    }
    
    private void hideMenu(int cx, int cy, float startRadius, float endRadius)
    {
        List<Animator> animList = new ArrayList<>();
        
        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--)
        {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }
        
        animList.add(createHideItemAnimator(centerItem));
        
        Animator revealAnim = createCircularReveal(menuLayout, cx, cy, startRadius, endRadius);
        revealAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        revealAnim.setDuration(MenuParameters.MENU_REVEAL_DURING);
        revealAnim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        
        animList.add(revealAnim);
        
        AnimatorSet animSet = new AnimatorSet();
        animSet.playSequentially(animList);
        animSet.start();
        
    }
    
    private Animator createShowItemAnimator(View item)
    {
        float dx = centerItem.getX() - item.getX();
        float dy = centerItem.getY() - item.getY();
        
        item.setScaleX(0f);
        item.setScaleY(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);
        
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(0f, 1f),
                AnimatorUtils.scaleY(0f, 1f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );
        
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(MenuParameters.MENU_ITEM_DURING);
        return anim;
    }
    
    private Animator createHideItemAnimator(final View item)
    {
        final float dx = centerItem.getX() - item.getX();
        final float dy = centerItem.getY() - item.getY();
        
        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.scaleX(1f, 0f),
                AnimatorUtils.scaleY(1f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );
        
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });
        anim.setDuration(MenuParameters.MENU_ITEM_DURING);
        return anim;
    }
    
    private Animator createCircularReveal(final ClipRevealFrame view, int x, int y, float startRadius,
            float endRadius)
    {
        final Animator reveal;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            reveal = ViewAnimationUtils.createCircularReveal(view, x, y, startRadius, endRadius);
        }
        else
        {
            view.setClipOutLines(true);
            view.setClipCenter(x, y);
            reveal = ObjectAnimator.ofFloat(view, "ClipRadius", startRadius, endRadius);
            reveal.addListener(new Animator.AnimatorListener()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    
                }
                
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    view.setClipOutLines(false);
                }
                
                @Override
                public void onAnimationCancel(Animator animation)
                {
                    
                }
                
                @Override
                public void onAnimationRepeat(Animator animation)
                {
                    
                }
            });
        }
        return reveal;
    }
    
    
    private int currentOrientation()
    {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            //code for portrait mode
        }
        else
        {
            //code for landscape mode
        }
        
        return 0;
    }
}
