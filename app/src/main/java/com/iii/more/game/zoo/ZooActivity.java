package com.iii.more.game.zoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;

import com.iii.more.game.module.MSG;
import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.Utility;
import com.iii.more.main.CockpitSensorEventListener;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1.
 */

public class ZooActivity extends Activity
{
    private final int SCEN_INDEX_START = 100; // 等待動物園圖案的RFID
    private final int SCEN_INDEX_ANIMAL_RFID = 101; // 取得動物園圖案的RFID
    private final int SCEN_INDEX_HOLD_HAND = 102; // 孩子抓住章魚寶的手
    private final int SCEN_INDEX_TRAFFIC_BUS = 103; // 孩子選擇搭公車
    private final int SCEN_INDEX_TRAFFIC_MRT = 104; // 孩子選擇搭捷運
    private final int SCEN_INDEX_TRAFFIC_CAR = 105; // 孩子選擇坐汽車
    
    MainApplication application = null;
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
        application = (MainApplication) getApplication();
        
        // 註冊 Sensor 感應 From Application
        application.addCockpitSensorEventListener(cockpitSensorEventListener);
        
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
        String strTTS = "";
        String strName = application.getName(Parameters.ID_CHILD_NAME);
        
        switch (nIndex)
        {
            case SCEN_INDEX_START:
                // 遊戲開始
                robotHead.playTTS("嗨! 你好 來玩遊戲吧", String.valueOf(SCEN_INDEX_START));
                
                break;
            case SCEN_INDEX_ANIMAL_RFID:
                strTTS = "哈囉，" + strName + "今天我們一起去動物園玩！牽著我的手，出發囉！";
                robotHead.setFace(R.drawable.octobo16);
                robotHead.playTTS(strTTS, String.valueOf(SCEN_INDEX_ANIMAL_RFID));
                
                break;
            case SCEN_INDEX_HOLD_HAND:
                strTTS = "抓緊喔！今天，你想要坐什麼交通工具去呢？";
                robotHead.setFace(R.drawable.octobo13);
                robotHead.playTTS(strTTS, String.valueOf(SCEN_INDEX_HOLD_HAND));
                break;
            case SCEN_INDEX_TRAFFIC_BUS: // 孩子選擇搭公車
                strTTS = "請刷悠遊卡";
                robotHead.setFace(R.drawable.octobo13);
                robotHead.playTTS(strTTS, String.valueOf(SCEN_INDEX_TRAFFIC_BUS));
                break;
        }
        
    }
    
    private UtteranceProgressListener utteranceProgressListener = new UtteranceProgressListener()
    {
        @Override
        public void onStart(String s)
        {
            Logs.showTrace("TTS onStart: " + s);
        }
        
        @Override
        public void onDone(String s)
        {
            Logs.showTrace("TTS onDone: " + s);
            int nId;
            
            nId = Integer.valueOf(s);
            switch (nId)
            {
                case SCEN_INDEX_START:
                    // 開始等動物園RFID
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_RFID), 3000);
                    break;
                case SCEN_INDEX_ANIMAL_RFID:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_HOLD_HAND), 3000);
                    break;
                case SCEN_INDEX_HOLD_HAND: // 孩子挑選交通工具RFID
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_TRAFFIC_BUS), 3000); // 假設選了公車
                    break;
            }
        }
        
        @Override
        public void onError(String s)
        {
            Logs.showTrace("TTS onError: " + s);
        }
    };
    
    
    public class ScenarizeTimer extends TimerTask
    {
        private int mnType;
        
        ScenarizeTimer(int nType)
        {
            mnType = nType;
        }
        
        public void run()
        {
            switch (mnType)
            {
                case SCEN_INDEX_ANIMAL_RFID:
                case SCEN_INDEX_HOLD_HAND:
                case SCEN_INDEX_TRAFFIC_BUS:
                    handler.sendEmptyMessage(mnType);
                    break;
            }
        }
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
                    // 註冊 TTS 執行狀態
                    robotHead.SetOnUtteranceProgressListener(utteranceProgressListener);
                    Scenarize(SCEN_INDEX_START);
                    break;
                case SCEN_INDEX_ANIMAL_RFID:
                case SCEN_INDEX_HOLD_HAND:
                case SCEN_INDEX_TRAFFIC_BUS:
                    Scenarize(msg.what);
                    break;
            }
        }
    };
    
    private CockpitSensorEventListener cockpitSensorEventListener = new CockpitSensorEventListener()
    {
        
        @Override
        public void onShakeHands(Object sender)
        {
        
        }
        
        @Override
        public void onClapHands(Object sender)
        {
        
        }
        
        @Override
        public void onPinchCheeks(Object sender)
        {
        
        }
        
        @Override
        public void onPatHead(Object sender)
        {
        
        }
        
        @Override
        public void onScannedRfid(Object sensor, String scannedResult)
        {
        
        }
    };
}
