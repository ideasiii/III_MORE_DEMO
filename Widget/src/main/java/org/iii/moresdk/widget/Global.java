package org.iii.moresdk.widget;

import android.content.Context;
import android.view.View;

public abstract class Global
{
    public static int getResourceId(Context context, String name, String defType)
    {
        return context.getResources().getIdentifier(name, defType, context.getPackageName());
    }
    
    public static int ScaleSize(Context context, int nSize)
    {
        float fScale = new Device(context).getScaleSize();
        return (int) Math.floor(nSize * fScale);
    }
    
    public static int measureWidth(int measureSpec)
    {
        int nResult = 0;
        int nSpecMode = View.MeasureSpec.getMode(measureSpec);
        int nSpecSize = View.MeasureSpec.getSize(measureSpec);
        
        if (View.MeasureSpec.AT_MOST == nSpecMode) // when layout set wrap_content
        {
            nResult = nSpecSize;
        }
        else if (View.MeasureSpec.EXACTLY == nSpecMode) // when layout set match_parent or
        // exactly size
        {
            nResult = nSpecSize;
        }
        
        return nResult;
    }
    
    public static int measureHeight(int measureSpec)
    {
        int nResult = 0;
        int nSpecMode = View.MeasureSpec.getMode(measureSpec);
        int nSpecSize = View.MeasureSpec.getSize(measureSpec);
        
        if (View.MeasureSpec.AT_MOST == nSpecMode) // when layout set wrap_content
        {
            nResult = nSpecSize;
        }
        else if (View.MeasureSpec.EXACTLY == nSpecMode) // when layout set match_parent or
        // exactly size
        {
            nResult = nSpecSize;
        }
        
        return nResult;
    }
}
