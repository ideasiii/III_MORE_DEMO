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
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/1.
 */

public class ZooActivity extends Activity
{
    private final int SCEN_INDEX_START = 100;           // 等待動物園圖案的RFID
    private final int SCEN_INDEX_ANIMAL_RFID = 101;     // 取得動物園圖案的RFID
    private final int SCEN_INDEX_HOLD_HAND = 102;       // 孩子抓住章魚寶的手
    private final int SCEN_INDEX_TRAFFIC_BUS = 103;     // 孩子選擇搭公車
    private final int SCEN_INDEX_TRAFFIC_MRT = 104;     // 孩子選擇搭捷運
    private final int SCEN_INDEX_TRAFFIC_CAR = 105;     // 孩子選擇坐汽車
    private final int SCEN_INDEX_TRAFFIC_CARD = 106;    // 孩子將悠遊卡RFID放上盤子
    private final int SCEN_INDEX_BUS_INSIDE = 107;      // 章魚寶眼睛螢幕畫面轉成公車內部
    private final int SCEN_INDEX_DROP_CUSTOM = 108;     // 孩子直接用手指在畫面上拉乘客到座位上
    private final int SCEN_INDEX_DROP_CUSTOM_IDLE = 109;     // 孩子很久未在畫面上拉乘客到座位上
    private final int SCEN_INDEX_DROP_CUSTOM_IDLE2 = 110;     // 孩子很久未在畫面上拉乘客到座位上part2
    private final int SCEN_INDEX_BUS_DRIVE = 111;
    private final int SCEN_INDEX_ZOO_DOOR = 112;
    
    MainApplication application = null;
    private RobotHead robotHead = null;
    private Timer timer = null;
    private int mnScenarizeIndex;
    
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
                handler.sendEmptyMessage(nWhat);
            }
        });
        robotHead.setOnDroppedListener(new RobotHead.OnDroppedListener()
        {
            @Override
            public void onDroped(View view)
            {
                handler.sendEmptyMessage(SCEN_INDEX_DROP_CUSTOM);
            }
        });
        application = (MainApplication) getApplication();
        
        // 註冊 Sensor 感應 From Application
        application.setCockpitSensorEventListener(cockpitSensorEventListener);
        
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
                robotHead.showFaceImg(true);
                // 遊戲開始
                robotHead.setFace(R.drawable.octobo16, ImageView.ScaleType.CENTER_CROP);
                strTTS = "嗨! 你好 來玩遊戲吧";
                robotHead.setPitch(1.5f, 1.5f);
                break;
            case SCEN_INDEX_ANIMAL_RFID:
                strTTS = "哈囉，" + strName + "今天我們一起去動物園玩！牽著我的手，出發囉！";
                robotHead.setFace(R.drawable.octobo16, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.5f, 0.9f);
                break;
            case SCEN_INDEX_HOLD_HAND:
                strTTS = "抓緊喔！今天，你想要坐什麼交通工具去呢？";
                robotHead.setFace(R.drawable.octobo13, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.2f, 0.8f);
                break;
            case SCEN_INDEX_TRAFFIC_BUS: // 孩子選擇搭公車
                strTTS = "請刷悠遊卡";
                robotHead.setFace(R.drawable.octobo14, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.bus);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(false);
                robotHead.setPitch(1.1f, 0.8f);
                break;
            case SCEN_INDEX_TRAFFIC_MRT:
                break;
            case SCEN_INDEX_TRAFFIC_CAR:
                break;
            case SCEN_INDEX_TRAFFIC_CARD:   // 孩子將悠遊卡RFID放上盤子
                strTTS = "逼，，逼";
                robotHead.setPitch(2f, 2f);
                break;
            case SCEN_INDEX_BUS_INSIDE:     // 章魚寶眼睛螢幕畫面轉成公車內部
                strTTS = strName + "，，請你幫忙讓大家都有座位坐";
                robotHead.setFace(R.drawable.businsidebk, ImageView.ScaleType.FIT_XY);
                robotHead.setObjectImg(R.drawable.elephone);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 0.8f);
                break;
            case SCEN_INDEX_DROP_CUSTOM:    // 孩子直接用手指在畫面上拉乘客到座位上，完成
                strTTS = "好棒！!!我們出發囉！";
                robotHead.setFace(R.drawable.busdropped, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(false);
                robotHead.setPitch(1.7f, 0.85f);
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE:
                strTTS = "喂！!!喂!!! 快一點呀，，公車要開囉！";
                robotHead.setPitch(0.75f, 0.6f);
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE2:
                strTTS = "啊啊 ，，來不及了，，站好喔!! 公車開動囉！";
                robotHead.setPitch(2f, 2f);
                break;
            case SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                strTTS = "噗噗噗噗噗噗噗噗噗噗";
                //robotHead.setFace(R.drawable.octobo14, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.bus);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(false);
                robotHead.setPitch(0.4f, 2f);
                break;
            case SCEN_INDEX_ZOO_DOOR:       // 顯示出動物園的大門
                strTTS = "噹噹噹噹，，到囉";
                robotHead.setFace(R.drawable.zoodoor, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 1.6f);
                break;
            default:
                return;
        }
        robotHead.playTTS(strTTS, String.valueOf(nIndex));
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
            
            switch (Integer.valueOf(s))
            {
                case SCEN_INDEX_START:
                    // 開始等動物園RFID
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_RFID), 2000);
                    break;
                case SCEN_INDEX_ANIMAL_RFID:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_HOLD_HAND), 2000);
                    break;
                case SCEN_INDEX_HOLD_HAND: // 孩子挑選交通工具RFID
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_TRAFFIC_BUS), 3000); // 假設選了公車
                    break;
                case SCEN_INDEX_TRAFFIC_BUS:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_TRAFFIC_CARD), 3000); // 孩子將悠遊卡RFID放上盤子
                    break;
                case SCEN_INDEX_TRAFFIC_MRT:
                    break;
                case SCEN_INDEX_TRAFFIC_CAR:
                    break;
                case SCEN_INDEX_TRAFFIC_CARD:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_BUS_INSIDE), 1000);
                    break;
                case SCEN_INDEX_BUS_INSIDE:     // 等待拉人去座位
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_DROP_CUSTOM_IDLE), 3000);
                    break;
                case SCEN_INDEX_DROP_CUSTOM_IDLE: // 等待拉人去座位 第二次
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_DROP_CUSTOM_IDLE2), 3000);
                    break;
                case SCEN_INDEX_DROP_CUSTOM:    // 好棒！!!我們出發囉！
                    handler.sendEmptyMessage(SCEN_INDEX_BUS_DRIVE);
                    break;
                case SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                    handler.sendEmptyMessage(SCEN_INDEX_ZOO_DOOR);
                    break;
                case SCEN_INDEX_ZOO_DOOR:
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
            handler.sendEmptyMessage(mnType);
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
                default:
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
            Logs.showTrace("onShakeHands");
        }
        
        @Override
        public void onClapHands(Object sender)
        {
            Logs.showTrace("onClapHands");
        }
        
        @Override
        public void onPinchCheeks(Object sender)
        {
            Logs.showTrace("onPinchCheeks");
        }
        
        @Override
        public void onPatHead(Object sender)
        {
            Logs.showTrace("onPatHead");
        }
        
        @Override
        public void onScannedRfid(Object sensor, String scannedResult)
        {
            Logs.showTrace("onScannedRfid");
        }
    };
}
