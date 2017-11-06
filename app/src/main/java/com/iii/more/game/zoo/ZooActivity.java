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

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1.
 */

public class ZooActivity extends Activity
{
    private final int SCEN_INDEX_START = 0; // 等待動物園圖案的RFID
    private final int SCEN_INDEX_ANIMAL_RFID = 1; // 取得動物園圖案的RFID
    
    private RobotHead robotHead = null;
    private int mnScenarizeIndex;
    private Timer timer = null;
    
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
        timer = new Timer(true);
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
    
    public void Scenarize(int nIndex)
    {
        mnScenarizeIndex = nIndex;
        switch (nIndex)
        {
            case SCEN_INDEX_START:
                // 遊戲開始
                robotHead.playTTS("嗨! 你好 來玩遊戲吧");
                // 開始等動物園RFID
                timer.schedule(new TimerZooRFID(), 3000, 0);
                break;
            case SCEN_INDEX_ANIMAL_RFID:
                robotHead.setFace(R.drawable.octobo16);
                robotHead.playTTS("哈囉，孩仔！今天我們一起去動物園玩！牽著我的手，出發囉！");
                break;
        }
        
    }
    
    public class TimerZooRFID extends TimerTask
    {
        public void run()
        {
            if (SCEN_INDEX_ANIMAL_RFID > mnScenarizeIndex)
            {
                Scenarize(SCEN_INDEX_ANIMAL_RFID);
            }
        }
    }
    
    ;
    
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG.MSG_INIT_TTS:
                    Scenarize(SCEN_INDEX_START);
                    break;
            }
        }
    };
}
