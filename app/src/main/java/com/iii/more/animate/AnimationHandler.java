package com.iii.more.animate;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.GridLayoutAnimationController;
import android.view.animation.LinearInterpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/6/30.
 */

public class AnimationHandler extends BaseHandler
{
    private Techniques mTechniques = null;
    private YoYo.YoYoString rope = null;
    private View mView = null;
    private JSONObject mAnimateBehavior = null;
    
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
    
    public void setView(View view)
    {
        mView = view;
    }
    
    
    public void setAnimateJsonBehavior(JSONObject jsonBehavior)
    {
        Logs.showTrace("[AnimationHandler]Animate Json:" + jsonBehavior.toString());
        
        mAnimateBehavior = jsonBehavior;
    }
    
    public boolean startAnimate()
    {
        return animateChange();
    }
    
    private boolean animateChange()
    {
        if (null != mAnimateBehavior)
        {
            try
            {
                int animateType = mAnimateBehavior.getInt(AnimationParameters.STRING_JSON_KEY_TYPE);
                
                if (animateType == AnimationParameters.TYPE_NOT_CHANGE)
                {
                    return true;
                    
                }
                else if (animateType == AnimationParameters.TYPE_CANCEL)
                {
                    animateCancel();
                }
                else
                {
                    animateCancel();
                    
                    
                    animateType -= 1;
                    if (animateType >= 0 && animateType < AnimationParameters.TYPE_MAX)
                    {
                        mTechniques = Techniques.values()[animateType];
                        mTechniques.getAnimator();
                        if (null != mTechniques && null != mView)
                        {
                            YoYo.AnimationComposer mAnimationComposer = YoYo.with(mTechniques);
                            
                            if (mAnimateBehavior.has(AnimationParameters.STRING_JSON_KEY_DURATION))
                            {
                                mAnimationComposer.duration(mAnimateBehavior.getInt(AnimationParameters.STRING_JSON_KEY_DURATION));
                            }
                            else
                            {
                                mAnimationComposer.duration(AnimationParameters.DEFAULT_DURING);
                            }
                            if (mAnimateBehavior.has(AnimationParameters.STRING_JSON_KEY_REPEAT))
                            {
                                if (mAnimateBehavior.getInt(AnimationParameters.STRING_JSON_KEY_REPEAT) == 1)
                                {
                                    mAnimationComposer.repeat(YoYo.INFINITE);
                                }
                            }
                            else
                            {
                                //default just show once
                            }
                            if (mAnimateBehavior.has(AnimationParameters.STRING_JSON_KEY_INTERPOLATE))
                            {
                                switch (mAnimateBehavior.getInt(AnimationParameters.STRING_JSON_KEY_INTERPOLATE))
                                {
                                    case AnimationParameters.INTERPOLATOR_LINEAR:
                                        mAnimationComposer.interpolate(new LinearInterpolator());
                                        break;
                                    
                                    case AnimationParameters.INTERPOLATOR_ACCELERATE_DECELERATE:
                                        mAnimationComposer.interpolate(new AccelerateDecelerateInterpolator());
                                        break;
                                    
                                    case AnimationParameters.INTERPOLATOR_ACCELERATE:
                                        mAnimationComposer.interpolate(new AccelerateInterpolator());
                                        break;
                                    
                                    case AnimationParameters.INTERPOLATOR_CYCLE:
                                        mAnimationComposer.interpolate(new CycleInterpolator(
                                                AnimationParameters.DEFAULT_INTERPOLATOR_CYCLE_Parameter));
                                        break;
                                    
                                    case AnimationParameters.INTERPOLATOR_DECELERATE:
                                        mAnimationComposer.interpolate(new DecelerateInterpolator());
                                        break;
                                    default:
                                        mAnimationComposer.interpolate(new LinearInterpolator());
                                        break;
                                }
                                
                            }
                            
                            
                            rope = mAnimationComposer
                                    .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                                    .withListener(myAnimatorListener)
                                    .playOn(mView);
                            
                            return true;
                        }
                        else
                        {
                            //index access invalid
                            Logs.showError("[AnimationHandler] Animate Type not Support");
                        }
                    }
                    else
                    {
                        //index access invalid
                        Logs.showError("[AnimationHandler] Animate Type not Support");
                    }
                }
                
            }
            
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
        }
        return false;
    }
    
    public void animateCancel()
    {
        if (null != rope)
        {
            if (rope.isRunning())
            {
                Logs.showTrace("[AnimationHandler] stop animate!");
                rope.stop();
                rope = null;
            }
        }
    }
    
    
}
