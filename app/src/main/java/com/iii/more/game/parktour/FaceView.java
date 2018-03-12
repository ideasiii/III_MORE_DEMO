package com.iii.more.game.parktour;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.R;
import com.iii.more.main.TTSVoicePool;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2018/2/2
 */

public class FaceView extends RelativeLayout
{
    private MainApplication application = null;
    private ImageView mimgMouth = null;
    private ImageView mimgFace = null;
    private Context theContext = null;
    
    public FaceView(Context context)
    {
        super(context);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    public FaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    
    private void init(Context context)
    {
        application = (MainApplication) context.getApplicationContext();
        setBackgroundColor(Color.rgb(108, 147, 213));
        theContext = context;
        mimgFace = new ImageView(context);
        mimgMouth = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mimgFace.setLayoutParams(layoutParams);
        mimgMouth.setLayoutParams(layoutParams);
        addView(mimgFace);
        addView(mimgMouth);
        mimgMouth.setVisibility(View.GONE);
        setOnLongClickListener(new OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View view)
            {
                Logs.showTrace("[FaceView] onLongClick");
                showConfig(theContext);
                return false;
            }
        });
    }
    
    public void loadImage(final int nResId)
    {
        setBackgroundResource(nResId);
        Utility.loadImage(theContext, nResId, mimgFace);
    }
    
    public void showMouth(boolean bShow)
    {
        if (bShow)
        {
            mimgMouth.setVisibility(View.VISIBLE);
        }
        else
        {
            mimgMouth.setVisibility(View.GONE);
        }
    }
    
    public void loadImageMouth(final int nResId)
    {
        mimgMouth.setImageResource(nResId);
    }
    
    private void showConfig(Context context)
    {
        Dialog dialog = new Dialog(context, R.style.MyDialog);
        dialog.setContentView(R.layout.parktour_config);
        ImageButton ibMan = dialog.findViewById(R.id.imageButtonVoiceMan);
        final LinearLayout llVoiceGirl = dialog.findViewById(R.id.llVoiceGirl);
        final LinearLayout llVoiceMan = dialog.findViewById(R.id.llVoiceMan);
        ibMan.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llVoiceGirl.setBackgroundResource(R.color.Trans_Black);
                llVoiceMan.setBackgroundResource(R.color.default_app_color);
                application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_MALE);
            }
        });
        
        ImageButton ibGirl = dialog.findViewById(R.id.imageButtonVoiceGirl);
        ibGirl.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llVoiceGirl.setBackgroundResource(R.color.default_app_color);
                llVoiceMan.setBackgroundResource(R.color.Trans_Black);
                application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_FEMALE);
            }
        });
        dialog.show();
    }
}
