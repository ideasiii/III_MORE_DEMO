package com.iii.more.game.zoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.iii.more.game.module.MSG;
import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.Utility;
import com.iii.more.main.R;

import android.os.Handler;

import java.util.logging.LogRecord;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1.
 */

public class ZooActivity extends Activity
{
    private RobotHead robotHead = null;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        Logs.showTrace("onCreate");
        super.onCreate(savedInstanceState);
        Utility.fullScreenNoBar(this);
        robotHead = new RobotHead(this);
        setContentView(robotHead);
        robotHead.setOnInitedListener(new RobotHead.OnInitedListener()
        {
            @Override
            public void onInited(int nWhat)
            {
                Message msg = new Message();
                msg.what = nWhat;
                handler.sendMessage(msg);
            }
        });
        
        
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            Utility.fullScreenNoBar(this);
        }
    }
    
    @Override
    protected void onStart()
    {
        Logs.showTrace("onStart");
        super.onStart();
        robotHead.start();
    }
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        robotHead.stop();
        super.onStop();
    }
    
    @Override
    protected void onPause()
    {
        Logs.showTrace("onPause");
        super.onPause();
    }
    
    @Override
    protected void onDestroy()
    {
        Logs.showTrace("onDestroy");
        super.onDestroy();
    }
    
    public void startScenarize()
    {
        robotHead.playTTS("嗨! 你好 來玩遊戲吧,,,,請你先將動物園圖案的標章放在我前面的盤子上喔");
    }
    
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG.MSG_INIT_TTS:
                    startScenarize();
                    break;
            }
        }
    };
}
