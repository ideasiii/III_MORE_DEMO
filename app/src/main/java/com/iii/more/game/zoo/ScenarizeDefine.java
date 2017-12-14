package com.iii.more.game.zoo;

/**
 * Created by Jugo on 2017/12/14
 */

public abstract class ScenarizeDefine
{
    public static enum FRONT
    {
        FACE, OBJECT
    }
    
    public static enum NEXT_TRIGER
    {
        NO_TRIGER, TTS_TEXT, HAND_SHAKE, HAND_CLAP, PINCH_CHEEK, PAT_HEAD, SENSOR_ALL, RFID,
        UI_DRAG_DROP, UI_CLICK, UI_TOUCH_DOWN, UI_TOUCH_MOVE, UI_TOUCH_UP, SLIDE_END
    }
}
