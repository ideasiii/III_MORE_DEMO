package com.iii.more.game.parktour;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.R;
import com.iii.more.main.TTSVoicePool;

import android.os.Handler;

import sdk.ideas.common.Logs;

/**
 * Created by Jugo on 2018/2/2
 */

public class FaceView extends RelativeLayout
{
    private Dialog dialog = null;
    private MainApplication application = null;
    private ImageView mimgMouth = null;
    private ImageView mimgFace = null;
    private ImageView mimgSpeak = null;
    private Context theContext = null;
    private static int msnVocal = Config.VOICE_DEFAULT_VOCAL;
    private static int msnPitch = Config.VOICE_DEFAULT_PITCH;
    private static int msnRate = Config.VOICE_DEFAULT_RATE;
    private static Handler pHandler = null;
    
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
        mimgSpeak = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams layoutParamsSpeak = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParamsSpeak.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mimgFace.setLayoutParams(layoutParams);
        mimgMouth.setLayoutParams(layoutParams);
        mimgSpeak.setLayoutParams(layoutParamsSpeak);
        Utility.loadImage(theContext, R.drawable.iii_talkmouth, mimgSpeak);
        addView(mimgFace);
        addView(mimgMouth);
        addView(mimgSpeak);
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
    
    public void setHandler(Handler handler)
    {
        pHandler = handler;
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
            mimgMouth.setVisibility(View.INVISIBLE);
        }
    }
    
    public void showSpeak(boolean bShow)
    {
        if (bShow)
        {
            mimgSpeak.setVisibility(View.VISIBLE);
        }
        else
        {
            mimgSpeak.setVisibility(View.INVISIBLE);
        }
    }
    
    public void loadImageMouth(final int nResId)
    {
        mimgMouth.setImageResource(nResId);
    }
    
    private void showConfig(Context context)
    {
        dialog = new Dialog(context, R.style.MyDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.parktour_config);
        
        ImageButton ibMan = dialog.findViewById(R.id.imageButtonVoiceMan);
        ImageButton ibGirl = dialog.findViewById(R.id.imageButtonVoiceGirl);
        SeekBar seekBarPitch = dialog.findViewById(R.id.SeekBarPitch);
        SeekBar seekBarRate = dialog.findViewById(R.id.SeekBarRate);
        Button buttonOverScen = dialog.findViewById(R.id.buttonOverScen);
        final TextView textViewPitch = dialog.findViewById(R.id.textViewPitch);
        final TextView textViewRate = dialog.findViewById(R.id.textViewRate);
        final LinearLayout llVoiceGirl = dialog.findViewById(R.id.llVoiceGirl);
        final LinearLayout llVoiceMan = dialog.findViewById(R.id.llVoiceMan);
        
        seekBarPitch.setProgress(msnPitch);
        seekBarRate.setProgress(msnRate);
        textViewPitch.setText("Pitch : " + Integer.toString(msnPitch));
        textViewRate.setText("Rate : " + Integer.toString(msnRate));
        
        if (TTSVoicePool.TTS_VOICE_CYBERON_KID_MALE == msnVocal)
        {
            llVoiceGirl.setBackgroundResource(R.color.Trans_Black);
            llVoiceMan.setBackgroundResource(R.color.default_app_color);
            application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_MALE);
        }
        else
        {
            llVoiceGirl.setBackgroundResource(R.color.default_app_color);
            llVoiceMan.setBackgroundResource(R.color.Trans_Black);
            application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_FEMALE);
        }
        
        
        ibMan.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llVoiceGirl.setBackgroundResource(R.color.Trans_Black);
                llVoiceMan.setBackgroundResource(R.color.default_app_color);
                application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_MALE);
                msnVocal = TTSVoicePool.TTS_VOICE_CYBERON_KID_MALE;
            }
        });
        
        ibGirl.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                llVoiceGirl.setBackgroundResource(R.color.default_app_color);
                llVoiceMan.setBackgroundResource(R.color.Trans_Black);
                application.setVoice(TTSVoicePool.TTS_VOICE_CYBERON_KID_FEMALE);
                msnVocal = TTSVoicePool.TTS_VOICE_CYBERON_KID_FEMALE;
            }
        });
        
        seekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                textViewPitch.setText("Pitch : " + Integer.toString(progress));
                msnPitch = progress;
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                application.setTTSPitchCyberonScaling(msnPitch, msnRate);
            }
        });
        
        seekBarRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b)
            {
                textViewRate.setText("Rate : " + Integer.toString(progress));
                msnRate = progress;
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
            
            }
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                application.setTTSPitchCyberonScaling(msnPitch, msnRate);
            }
        });
        
        buttonOverScen.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dialog.dismiss();
                pHandler.sendEmptyMessage(Scenarize.SCEN_OVER);
            }
        });
        dialog.show();
    }
}
