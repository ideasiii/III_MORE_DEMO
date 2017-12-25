package com.iii.more.game.zoo;

import android.content.Context;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.iii.more.game.module.DragDropLayout;
import com.iii.more.game.module.ViewPosition;
import com.iii.more.main.R;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2017/12/25
 */

public class CarFixLayout extends DragDropLayout
{
    private Handler handlerScenarize = null;
    private ImageView DropTarget = null;
    private ImageView DragItemCircle = null;
    private ImageView DragItemTriangle = null;
    private ImageView DragItemCube = null;
    
    public CarFixLayout(Context context)
    {
        super(context);
        init(context);
    }
    
    public CarFixLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public CarFixLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public CarFixLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    void init(Context context)
    {
        DropTarget = new ImageView(context);
        DragItemCircle = new ImageView(context);
        DragItemTriangle = new ImageView(context);
        DragItemCube = new ImageView(context);
        
        DropTarget.setImageResource(R.drawable.fix_car_car);
        DropTarget.setScaleType(ImageView.ScaleType.FIT_XY);
        
        DragItemCircle.setImageResource(R.drawable.fix_car_circle);
        DragItemCircle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        DragItemCircle.setTag("wheel_circle");
        
        DragItemCube.setImageResource(R.drawable.fix_car_cube);
        DragItemCube.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        DragItemCube.setTag("wheel_cube");
        
        DragItemTriangle.setImageResource(R.drawable.fix_car_triangle);
        DragItemTriangle.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        DragItemTriangle.setTag("wheel_triangle");
        
        addDropTarget(DropTarget, new ViewPosition());
        addDragItem(DragItemCircle, new ViewPosition(60, 650, 150, 150));
        addDragItem(DragItemCube, new ViewPosition(350, 650, 150, 150));
        addDragItem(DragItemTriangle, new ViewPosition(620, 650, 150, 150));
        
        setOnDragDroppedListener(onDragDropped);
    }
    
    private OnDragDropped onDragDropped = new OnDragDropped()
    {
        @Override
        public void onDropped(View view, int x, int y)
        {
            String strTag = (String) view.getTag();
            Logs.showTrace("[zoo][CarFixLayout] onDropped View tag=" + view.getTag() + " x=" + x
                + " y=" + y);
            if (null != strTag)
            {
                if (0 == strTag.compareTo("wheel_circle"))
                {
                    Logs.showTrace("[CarFixLayout] Fix Car wheel circle");
                }
                
                if (0 == strTag.compareTo("wheel_cube"))
                {
                    Logs.showTrace("[CarFixLayout] Fix Car wheel cube");
                }
                
                if (0 == strTag.compareTo("wheel_triangle"))
                {
                    Logs.showTrace("[CarFixLayout] Fix Car wheel triangle");
                }
            }
        }
    };
}
