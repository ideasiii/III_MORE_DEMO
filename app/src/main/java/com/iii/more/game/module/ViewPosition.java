package com.iii.more.game.module;

import android.view.ViewGroup;

/**
 * Created by Jugo on 2017/12/25
 */

public class ViewPosition
{
    public float x;
    public float y;
    public int width;
    public int height;
    
    public ViewPosition()
    {
        x = 0f;
        y = 0f;
        width = ViewGroup.LayoutParams.MATCH_PARENT;
        height = ViewGroup.LayoutParams.MATCH_PARENT;
    }
    
    public ViewPosition(final float fX, final float fY, final int nWidth, final int nHeight)
    {
        x = fX;
        y = fY;
        width = nWidth;
        height = nHeight;
    }
}
