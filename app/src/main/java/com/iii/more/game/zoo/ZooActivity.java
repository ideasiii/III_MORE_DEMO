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

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import sdk.ideas.common.Logs;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

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
    private final int SCEN_INDEX_ANIMAL_MONKEY = 113;
    private final int SCEN_INDEX_ANIMAL_BEAR = 114;
    private final int SCEN_INDEX_ANIMAL_SWAN = 115;
    private final int SCEN_INDEX_ANIMAL_LION = 116;
    private final int SCEN_INDEX_NOT_FEAR = 117;
    private final int SCEN_INDEX_FOOD_MENU = 118;
    private final int SCEN_INDEX_EAT_HAMBERB = 119;
    private final int SCEN_INDEX_EATED_HAMBERB = 120;
    private final int SCEN_INDEX_ZOO_DOOR2 = 121;
    private final int SCEN_INDEX_ANIMAL_ELEPHONE = 122;
    private final int SCEN_INDEX_ANIMAL_KONG = 123;
    private final int SCEN_INDEX_FAV_ANIMAL = 124;
    private final int SCEN_INDEX_FAV_ANIMAL_SPEECH = 125;
    
    MainApplication application = null;
    private RobotHead robotHead = null;
    private Timer timer = null;
    private int mnScenarizeIndex;
    private VoiceRecognition mVoiceRecognition = null;
    
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
        robotHead.setObjectImg(R.drawable.busy, ImageView.ScaleType.CENTER_INSIDE);
        robotHead.showObjectImg(true);
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(handlerSpeech);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
    }
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        robotHead.stop();
        if (null != mVoiceRecognition)
        {
            mVoiceRecognition.stopListen();
        }
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
                robotHead.showObjectImg(false);
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
                robotHead.setObjectImg(R.drawable.bus, ImageView.ScaleType.CENTER_INSIDE);
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
                robotHead.setImgObjectTouch(true);
                robotHead.setFace(R.drawable.businsidebk, ImageView.ScaleType.FIT_XY);
                robotHead.setObjectImg(R.drawable.elephone, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 0.8f);
                break;
            case SCEN_INDEX_DROP_CUSTOM:    // 孩子直接用手指在畫面上拉乘客到座位上，完成
                robotHead.setImgObjectTouch(false);
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
                strTTS = "啊啊 ，，來不及了，，公車開動囉！";
                robotHead.setPitch(2f, 2f);
                break;
            case SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                strTTS = "噗噗噗噗噗噗噗噗噗噗";
                robotHead.setObjectImg(R.drawable.bus, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(false);
                robotHead.setPitch(0.4f, 2f);
                break;
            case SCEN_INDEX_ZOO_DOOR:       // 顯示出動物園的大門
                strTTS = "噹噹噹噹，，到囉，，讓我們一起來參觀台灣動物區的動物喔";
                robotHead.setFace(R.drawable.zoodoor, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_MONKEY:
                strTTS = "哇! 台灣獼猴耶";
                robotHead.setObjectImg(R.drawable.monkey, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(1.8f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_BEAR:
                strTTS = "吼! 快看，是台灣黑熊";
                robotHead.setObjectImg(R.drawable.bear, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(1.5f, 0.8f);
                break;
            case SCEN_INDEX_ANIMAL_SWAN:
                strTTS = "哇! 好美喔，，是天鵝";
                robotHead.setObjectImg(R.drawable.swan, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(0.7f, 0.8f);
                break;
            case SCEN_INDEX_ANIMAL_LION:
                strTTS = "啊!!啊，，是獅子";
                robotHead.setObjectImg(R.drawable.lion, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(2.5f, 0.8f);
                break;
            case SCEN_INDEX_NOT_FEAR:
                strTTS = strName + "，不要怕，我陪你";
                robotHead.setFace(R.drawable.octobo21, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(false);
                robotHead.setPitch(1f, 0.7f);
                break;
            case SCEN_INDEX_FOOD_MENU:
                strTTS = strName + "我們來吃東西休息一下吧！";
                robotHead.setFace(R.drawable.foodmenu, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(1.4f, 0.9f);
                break;
            case SCEN_INDEX_EAT_HAMBERB:
                strTTS = "來吃漢堡囉！";
                robotHead.setFace(R.drawable.octobo37, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.hamburger1, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(2f, 0.9f);
                break;
            case SCEN_INDEX_EATED_HAMBERB:
                strTTS = "嗯 好吃！";
                robotHead.setFace(R.drawable.octobo25, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.hamburger2, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setPitch(2f, 0.5f);
                break;
            case SCEN_INDEX_ZOO_DOOR2:
                strTTS = "讓我們一起再來參觀非洲動物區的動物喔";
                robotHead.setFace(R.drawable.zoodoor2, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_ELEPHONE:
                strTTS = "快看，，是大象，，大象最喜歡吃草跟樹葉喔!!";
                robotHead.setObjectImg(R.drawable.elephone2, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_ANIMAL_KONG:
                strTTS = "哈哈，，是猩猩，，猩猩最喜歡吃香蕉喔!!";
                robotHead.setObjectImg(R.drawable.kong, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(0.9f, 1.5f);
                break;
            case SCEN_INDEX_FAV_ANIMAL:
                strTTS = "今天，真好玩，請告訴我，你最喜歡什麼動物呢";
                robotHead.showObjectImg(false);
                robotHead.setFace(R.drawable.octobo31, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(0.8f, 1f);
                break;
            case SCEN_INDEX_FAV_ANIMAL_SPEECH:
                mVoiceRecognition.startListen();
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
                    handler.sendEmptyMessage(SCEN_INDEX_ANIMAL_MONKEY);
                    break;
                case SCEN_INDEX_ANIMAL_MONKEY:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_BEAR), 2000);
                    break;
                case SCEN_INDEX_ANIMAL_BEAR:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_SWAN), 2000);
                    break;
                case SCEN_INDEX_ANIMAL_SWAN:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_LION), 2000);
                    break;
                case SCEN_INDEX_ANIMAL_LION:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_NOT_FEAR), 2000);
                    break;
                case SCEN_INDEX_NOT_FEAR:
                    handler.sendEmptyMessage(SCEN_INDEX_FOOD_MENU);
                    break;
                case SCEN_INDEX_FOOD_MENU:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EAT_HAMBERB), 2000);
                    break;
                case SCEN_INDEX_EAT_HAMBERB:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EATED_HAMBERB), 1000);
                    break;
                case SCEN_INDEX_EATED_HAMBERB:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ZOO_DOOR2), 1000);
                    break;
                case SCEN_INDEX_ZOO_DOOR2:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_ELEPHONE), 1000);
                    break;
                case SCEN_INDEX_ANIMAL_ELEPHONE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_KONG), 1000);
                    break;
                case SCEN_INDEX_ANIMAL_KONG:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_FAV_ANIMAL), 1000);
                    break;
                case SCEN_INDEX_FAV_ANIMAL:
                    handler.sendEmptyMessage(SCEN_INDEX_FAV_ANIMAL_SPEECH);
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
    
    private Handler handlerSpeech = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            mVoiceRecognition.stopListen();
            
        }
    };
}
