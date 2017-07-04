package com.iii.more.animate;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/6/30.
 */

public class AnimationHandler extends BaseHandler
{
    private Techniques mTechniques = null;
    private YoYo.YoYoString rope;
    private View mImageView = null;
    private int animateDuring = AnimationParameters.ANIMATE_DEFAULT_DURING;
    
    Animator.AnimatorListener myAnimatorListener = new Animator.AnimatorListener()
    {
        
        @Override
        public void onAnimationStart(Animator animation)
        {
            Logs.showTrace("[AnimationHandler] onAnimationStart" + animation.getClass().toString());
        }
        
        @Override
        public void onAnimationEnd(Animator animation)
        {
            Logs.showTrace("[AnimationHandler] onAnimationEnd" + animation.getClass().toString());
        }
        
        @Override
        public void onAnimationCancel(Animator animation)
        {
            Logs.showTrace("[AnimationHandler] onAnimationCancel" + animation.getClass().toString());
        }
        
        @Override
        public void onAnimationRepeat(Animator animation)
        {
            Logs.showTrace("[AnimationHandler] onAnimationRepeat" + animation.getClass().toString());
            
        }
    };
    
    
    public AnimationHandler(Context context)
    {
        super(context);
    }
    
    public void setImageView(View imageView)
    {
        mImageView = imageView;
    }
    
    public void setAnimateDuring(int animateDuring)
    {
        this.animateDuring = animateDuring;
        
    }
    
    public void animateChange(int animateType)
    {
        animateCancel();
        if (animateType != -1)
        {
            try
            {
                mTechniques = Techniques.values()[animateType];
                mTechniques.getAnimator();
                if (null != mTechniques)
                {
                    rope = YoYo.with(mTechniques)
                            .duration(1200)
                            .repeat(YoYo.INFINITE)
                            .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                            .interpolate(new AccelerateDecelerateInterpolator())
                            .withListener(myAnimatorListener)
                            .playOn(mImageView);
                    
                    
                }
                else
                {
                    //index access invalid
                    
                }
            }
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
            
            
        }
        
    }
    
    public void animateCancel()
    {
        if (null != rope)
        {
            if (rope.isRunning())
            {
                Logs.showTrace("[AnimationHandler] stop animate!");
                rope.stop();
            }
        }
    }
    
    
}
