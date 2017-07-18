package com.iii.more.animate;

/**
 * Created by joe on 2017/6/29.
 */

public class AnimationParameters
{
    public static final int DEFAULT_DURING = 1200; //1.2 s
    
    public static final float DEFAULT_INTERPOLATOR_CYCLE_Parameter = 0.5f;
    
    public static final int INTERPOLATOR_LINEAR = 0;
    public static final int INTERPOLATOR_ACCELERATE_DECELERATE = 1;
    public static final int INTERPOLATOR_ACCELERATE = 2;
    public static final int INTERPOLATOR_CYCLE = 3;
    public static final int INTERPOLATOR_DECELERATE = 4;
    
    public static final int TYPE_CANCEL = 0;
    public static final int TYPE_NOT_CHANGE = -1;
    
    public static final String STRING_JSON_KEY_DURATION = "duration";
    public static final String STRING_JSON_KEY_REPEAT = "repeat";
    public static final String STRING_JSON_KEY_INTERPOLATE = "interpolate";
    public static final String STRING_JSON_KEY_TYPE = "type";
    
    public static final int TYPE_MAX = 63;
    
}
