package com.iii.more.game.zoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;

import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.listeners.CockpitSensorEventListener;
import com.iii.more.main.listeners.FaceEmotionEventListener;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;
import com.iii.more.main.listeners.TTSEventListener;

import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

/**
 * Created by jugo on 2017/11/1
 */

public class ZooActivity extends Activity implements FaceEmotionEventListener
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
    private final int SCEN_INDEX_DROP_CUSTOM_IDLE2 = 110;     //
    // 孩子很久未在畫面上拉乘客到座位上part2
    private final int SCEN_INDEX_BUS_DRIVE = 111;
    private final int SCEN_INDEX_ZOO_DOOR = 112;
    private final int SCEN_INDEX_ANIMAL_MONKEY = 113;
    private final int SCEN_INDEX_FOOD_MENU = 118;
    private final int SCEN_INDEX_EAT_HAMBERB = 119;
    private final int SCEN_INDEX_EATED_HAMBERB = 120;
    private final int SCEN_INDEX_ANIMAL_ELEPHONE = 122;
    private final int SCEN_INDEX_ANIMAL_KONG = 123;
    private final int SCEN_INDEX_FAV_ANIMAL = 124;
    private final int SCEN_INDEX_FAV_ANIMAL_SPEECH = 125;
    private final int SCEN_INDEX_BANANA = 126;
    private final int SCEN_INDEX_BANANA_NON = 127;
    private final int SCEN_INDEX_VEGETABLE = 128;
    private final int SCEN_INDEX_VEGETABLE_NON = 129;
    private final int SCEN_INDEX_LEMUR = 130;
    private final int SCEN_INDEX_APPLE = 131;
    private final int SCEN_INDEX_APPLE_NON = 132;
    private final int SCEN_INDEX_EAT_DNUTE = 133;
    private final int SCEN_INDEX_EAT_ICECREAME = 134;
    private final int SCEN_INDEX_EATED_DNUTE = 135;
    private final int SCEN_INDEX_EATED_ICECREAME = 136;
    private final int SCEN_INDEX_MRT_MAP = 137;
    private final int SCEN_INDEX_FACE_EMONTION = 777;
    private final int SCEN_INDEX_GAME_OVER = 666;
    private final int SCEN_INDEX_FINISH = 999;
    
    private MainApplication application = null;
    private RobotHead robotHead = null;
    public static TrackerHandler trackerHandler = null;
    private int mnScenarizeIndex;
    private VoiceRecognition mVoiceRecognition = null;
    public ImageView ivHambergur = null;
    public ImageView ivIceCream = null;
    public ImageView ivDonuts = null;
    private ImageView ivMan = null;
    private LinearLayout linearFood = null;
    private int mnDroppedX = 0;
    private int mnTraffic = 0;
    private MrtMap mrtMap = null;
    
    //// {TTS_SPEED=1.0, TTS_PITCH=1.0, TTS_TEXT=你笑得好開心喔！什麼事情這麼好笑？}
    // {IMG_FILE_NAME=OCTOBO_Expressions-31.png}
    private class CEmotion
    {
        String strEmotion;
        String strTTS_SPEED;
        String strTTS_PITCH;
        String strTTS_TEXT;
        String strIMG_FILE_NAME;
        
        public CEmotion()
        {
            clear();
        }
        
        void clear()
        {
            strEmotion = null;
            strIMG_FILE_NAME = null;
            strTTS_PITCH = null;
            strTTS_SPEED = null;
            strTTS_TEXT = null;
        }
    }
    
    private CEmotion stEmotion;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        Logs.showTrace("onCreate");
        super.onCreate(savedInstanceState);
        Utility.fullScreenNoBar(this);
        robotHead = new RobotHead(this);
        setContentView(robotHead);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        robotHead.setOnDroppedListener(new RobotHead.OnDroppedListener()
        {
            @Override
            public void onDropped(View view, int nX, int nY)
            {
                handlerScenarize.removeMessages(SCEN_INDEX_BUS_INSIDE);
                handlerScenarize.removeMessages(SCEN_INDEX_DROP_CUSTOM);
                mnDroppedX = nX;
                handlerScenarize.sendEmptyMessage(SCEN_INDEX_DROP_CUSTOM);
                Logs.showTrace("onDropped view: " + view.getTag() + "x: " + String.valueOf
                    (mnDroppedX));
            }
        });
        application = (MainApplication) getApplication();
        
        // 註冊 Sensor 感應 From Application
        application.setCockpitSensorEventListener(cockpitSensorEventListener);
        robotHead.setObjectImg(R.drawable.busy, ImageView.ScaleType.CENTER_INSIDE);
        robotHead.showObjectImg(true);
        // 註冊TTS Listener
        application.setTTSEventListener(ttsEventListener);
        
        ivHambergur = new ImageView(this);
        ivDonuts = new ImageView(this);
        ivIceCream = new ImageView(this);
        ivMan = new ImageView(this);
        
        ivHambergur.setTag("BURGER");
        ivDonuts.setTag("DNUTE");
        ivIceCream.setTag("ICECREAM");
        ivMan.setTag("MAN");
        
        ivHambergur.setImageResource(R.drawable.burger);
        ivDonuts.setImageResource(R.drawable.donut);
        ivIceCream.setImageResource(R.drawable.icecream);
        ivMan.setImageResource(R.drawable.man);
        ivMan.setScaleType(ImageView.ScaleType.FIT_XY);
        
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams((int) Utility
            .convertDpToPixel(250, this), (int) Utility.convertDpToPixel(250, this));
        ivHambergur.setLayoutParams(layoutParams1);
        ivDonuts.setLayoutParams(layoutParams1);
        ivIceCream.setLayoutParams(layoutParams1);
        
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(300, 650);
        layoutParams2.setMargins(200, 700, 0, 0);
        ivMan.setLayoutParams(layoutParams2);
        ivMan.setOnTouchListener(dropTouchListener);
        
        
        linearFood = new LinearLayout(this);
        linearFood.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
            .MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        linearFood.setOrientation(LinearLayout.VERTICAL);
        linearFood.setGravity(Gravity.CENTER_HORIZONTAL);
        linearFood.addView(ivHambergur);
        linearFood.addView(ivDonuts);
        linearFood.addView(ivIceCream);
        
        ivHambergur.setOnTouchListener(onTouchListener);
        ivDonuts.setOnTouchListener(onTouchListener);
        ivIceCream.setOnTouchListener(onTouchListener);
        
        trackerHandler = new TrackerHandler(this);
        trackerHandler.setSource("0");
        trackerHandler.setActivity("game");
        trackerHandler.setDescription("Edubot Zoo Game");
        
        stEmotion = new CEmotion();
        
        mrtMap = new MrtMap(this);
        RelativeLayout.LayoutParams layoutParamsMrtMap = new RelativeLayout.LayoutParams(1000,
            1000);
        layoutParamsMrtMap.setMargins((int) 0, (int) 200, (int) 0, (int) 0);
        layoutParamsMrtMap.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mrtMap.setLayoutParams(layoutParamsMrtMap);
        
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
        application.setFaceEmotionEventListener(this);
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(handlerSpeech);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        Scenarize(SCEN_INDEX_START);
    }
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        
        if (null != mVoiceRecognition)
        {
            mVoiceRecognition.stopListen();
        }
        super.onStop();
        finish();
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
        String strFaceImg = "";
        int nFace;
        
        if (null != stEmotion.strEmotion)
        {
            nIndex = SCEN_INDEX_FACE_EMONTION;
        }
        switch (nIndex)
        {
            case SCEN_INDEX_START: // 遊戲開始
                robotHead.showFaceImg(true);
                robotHead.showObjectImg(false);
                nFace = R.drawable.octobo16;
                strFaceImg = "octobo16.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                strTTS = "嗨! 你好 來玩遊戲吧";
                // robotHead.setPitch(1.5f, 1.5f);
                break;
            case SCEN_INDEX_ANIMAL_RFID:
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                strTTS = "哈囉，" + strName + "今天我們一起去動物園玩！牽著我的手，出發囉！";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.zoo_map, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.bringObjImgtoFront();
                // robotHead.setPitch(1.3f, 0.9f);
                break;
            case SCEN_INDEX_HOLD_HAND:
                strTTS = "抓緊喔！今天，你想要坐什麼交通工具去呢？";
                nFace = R.drawable.octobo13;
                strFaceImg = "octobo13.png";
                robotHead.bringFaceImgtoFront();
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN_INDEX_TRAFFIC_BUS: // 孩子選擇搭公車
                mnTraffic = SCEN_INDEX_TRAFFIC_BUS;
                strTTS = "請刷悠遊卡";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.bus, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN_INDEX_TRAFFIC_MRT:
                mnTraffic = SCEN_INDEX_TRAFFIC_MRT;
                strTTS = "請刷悠遊卡";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.mrt_train, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN_INDEX_TRAFFIC_CAR:
                mnTraffic = SCEN_INDEX_TRAFFIC_CAR;
                break;
            case SCEN_INDEX_TRAFFIC_CARD:   // 孩子將悠遊卡RFID放上盤子
                strTTS = "逼，，逼";
                break;
            case SCEN_INDEX_BUS_INSIDE:     // 章魚寶眼睛螢幕畫面轉成公車內部
                strTTS = strName + "，，請你幫忙讓大家都有座位坐";
                robotHead.bringObjImgtoFront();
                nFace = R.drawable.businside;
                strFaceImg = "businside.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setObjectImg(R.drawable.man, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.addView(ivMan);
                break;
            case SCEN_INDEX_DROP_CUSTOM:    // 孩子直接用手指在畫面上拉乘客到座位上，完成
                robotHead.removeView(ivMan);
                strTTS = "好棒！!!我們出發囉！";
                robotHead.showObjectImg(false);
                Logs.showTrace("dropped X = " + String.valueOf(mnDroppedX));
                if (550 < mnDroppedX)
                {
                    nFace = R.drawable.businside_right;
                    strFaceImg = "businside_right.png";
                }
                else
                {
                    nFace = R.drawable.businside_left;
                    strFaceImg = "businside_left.png";
                }
                
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE:
                strTTS = "快一點呀，，公車要開囉！";
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE2:
                strTTS = "啊啊 ，，來不及了，，公車開動囉！";
                break;
            case SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                strTTS = "噗噗噗噗噗噗噗噗噗噗";
                robotHead.bringFaceImgtoFront();
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.busmoving, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN_INDEX_MRT_MAP:
                robotHead.showObjectImg(false);
                nFace = R.drawable.noeye;
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.addView(mrtMap);
                break;
            case SCEN_INDEX_ZOO_DOOR:       // 顯示出動物園的大門
                strTTS = "到囉，，讓我們一起來參觀動物吧";
                nFace = R.drawable.zoodoor;
                strFaceImg = "zoodoor.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN_INDEX_ANIMAL_MONKEY:
                strTTS = "看，是猴子";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.monkey, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN_INDEX_BANANA:
                strTTS = "猴子 最愛吃香蕉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN_INDEX_BANANA_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana_non, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN_INDEX_FOOD_MENU:
                strTTS = strName + "我們來吃東西休息一下吧！選你想吃得食物吧";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(false);
                robotHead.addView(linearFood);
                break;
            case SCEN_INDEX_EAT_HAMBERB:
                robotHead.removeView(linearFood);
                strTTS = "來吃漢堡囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.burger, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN_INDEX_EATED_HAMBERB:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.burger_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN_INDEX_EAT_DNUTE:
                robotHead.removeView(linearFood);
                strTTS = "來吃甜甜圈囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.donut, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN_INDEX_EATED_DNUTE:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.donut_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN_INDEX_EAT_ICECREAME:
                robotHead.removeView(linearFood);
                strTTS = "來吃冰淇淋囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.icecream, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN_INDEX_EATED_ICECREAME:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.icecream_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN_INDEX_ANIMAL_ELEPHONE:
                strTTS = "快看，，是大象";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.elephone2, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_VEGETABLE:
                strTTS = "大象最喜歡吃草跟樹葉!!";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_VEGETABLE_NON:
                strTTS = "啊嗯嗯嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable_non, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_LEMUR:
                strTTS = "是狐猴";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.lemur, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_APPLE:
                strTTS = "蘋果是狐猴最愛吃的食物";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_APPLE_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple_non, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_ANIMAL_KONG:
                strTTS = "哈哈，，是猩猩，，猩猩最喜歡吃香蕉喔!!";
                robotHead.setObjectImg(R.drawable.kong, ImageView.ScaleType.FIT_XY);
                break;
            case SCEN_INDEX_FAV_ANIMAL:
                strTTS = "今天，真好玩，請告訴我，你最喜歡什麼動物呢";
                robotHead.showObjectImg(false);
                nFace = R.drawable.octobo31;
                strFaceImg = "octobo31.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN_INDEX_FAV_ANIMAL_SPEECH:
                // mVoiceRecognition.startListen();
                break;
            case SCEN_INDEX_GAME_OVER:
                strTTS = "再見囉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.zoodoor, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_FINISH:
                finish();
                break;
            case SCEN_INDEX_FACE_EMONTION:
                stEmotion.clear();
                strTTS = stEmotion.strTTS_TEXT;
                break;
            default:
                return;
        }
        application.setTTSPitch(1.0f, 1.0f);
        application.playTTS(strTTS, String.valueOf(nIndex));
        
        // 傳送Tracker Data
        trackerHandler.setRobotFace(strFaceImg).setSensor("", "").setScene(String.valueOf
            (mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", strTTS, "1", "1", "").send();
    }
    
    @SuppressLint("HandlerLeak")
    private final Handler handlerScenarize = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Scenarize(msg.what);
        }
    };
    
    private CockpitSensorEventListener cockpitSensorEventListener = new CockpitSensorEventListener()
    {
        // "Type":"clap_hand|shake_hand|pat_hat|squeeze|rfid"
        
        @Override
        public void onShakeHands(Object sender)
        {
            Logs.showTrace("onShakeHands");
            trackerHandler.setRobotFace("").setSensor("shake_hand", "1").setScene(String.valueOf
                (mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "").send();
        }
        
        @Override
        public void onClapHands(Object sender)
        {
            Logs.showTrace("onClapHands");
            trackerHandler.setRobotFace("").setSensor("clap_hand", "1").setScene(String.valueOf
                (mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "").send();
        }
        
        @Override
        public void onPinchCheeks(Object sender)
        {
            // 捏臉頰
            Logs.showTrace("onPinchCheeks");
            trackerHandler.setRobotFace("").setSensor("pinch_cheeks", "1").setScene(String
                .valueOf(mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "")
                .send();
        }
        
        @Override
        public void onPatHead(Object sender)
        {
            Logs.showTrace("onPatHead");
            trackerHandler.setRobotFace("").setSensor("pat_hat", "1").setScene(String.valueOf
                (mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "").send();
        }
        
        @Override
        public void onScannedRfid(Object sensor, String scannedResult)
        {
            Logs.showTrace("onScannedRfid Result:" + scannedResult);
            trackerHandler.setRobotFace("").setSensor("rfid", scannedResult).setScene(String
                .valueOf(mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "")
                .send();
            
            switch (mnScenarizeIndex)
            {
                case SCEN_INDEX_START:
                    mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN_INDEX_ANIMAL_RFID);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_ANIMAL_RFID);
                    break;
                case SCEN_INDEX_HOLD_HAND:
                    mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN_INDEX_TRAFFIC_BUS);
                    if (0 == scannedResult.compareTo("1"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_BUS);
                    }
                    else if (0 == scannedResult.compareTo("2"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_MRT);
                    }
                    else if (0 == scannedResult.compareTo("3"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_CAR);
                    }
                    else
                    {
                        handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_BUS);
                    }
                    break;
                case SCEN_INDEX_TRAFFIC_BUS:
                    mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN_INDEX_TRAFFIC_CARD);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_CARD);
                    break;
                case SCEN_INDEX_TRAFFIC_MRT:
                    mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN_INDEX_TRAFFIC_CARD);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_TRAFFIC_CARD);
                    break;
            }
        }
    };
    
    @SuppressLint("HandlerLeak")
    private Handler handlerSpeech = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg
                .what) + " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode
                .METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
            {
                mVoiceRecognition.stopListen();
                Logs.showTrace("[LogicHandler] Get voice Text: " + message.get("message"));
                //mstrFavAnimal = (String) message.get("message");
                handlerScenarize.sendEmptyMessage(SCEN_INDEX_GAME_OVER);
            }
            else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                //startListen first handle
                Logs.showTrace("Google Speech Success");
            }
            else
            {
                Logs.showTrace("get ERROR message: " + message.get("message"));
                mVoiceRecognition.stopListen();
                handlerScenarize.sendEmptyMessage(SCEN_INDEX_GAME_OVER);
            }
        }
    };
    
    private View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                String strTag = (String) view.getTag();
                Logs.showTrace("onTouch down view tag: " + strTag);
                if (0 == strTag.compareTo("BURGER"))
                {
                    handlerScenarize.removeMessages(SCEN_INDEX_EAT_HAMBERB);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_EAT_HAMBERB);
                    return true;
                }
                if (0 == strTag.compareTo("DNUTE"))
                {
                    handlerScenarize.removeMessages(SCEN_INDEX_EAT_DNUTE);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_EAT_DNUTE);
                    return true;
                }
                if (0 == strTag.compareTo("ICECREAM"))
                {
                    handlerScenarize.removeMessages(SCEN_INDEX_EAT_ICECREAME);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_EAT_ICECREAME);
                    return true;
                }
            }
            return false;
        }
    };
    
    private View.OnTouchListener dropTouchListener = new View.OnTouchListener()
    {
        
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
            {
                ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
                ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                view.setVisibility(View.INVISIBLE);
                return true;
            }
            else if (motionEvent.getAction() == MotionEvent.ACTION_UP)
            {
                Logs.showTrace("ACTION_UP");
                Logs.showTrace("onTouch UP view: " + view.getTag() + "x: " + String.valueOf(view
                    .getX()));
                return false;
            }
            else
            {
                return false;
            }
        }
    };
    
    private TTSEventListener ttsEventListener = new TTSEventListener()
    {
        
        @Override
        public void onInitSuccess()
        {
        
        }
        
        @Override
        public void onInitFailed(int status, String message)
        {
        
        }
        
        @Override
        public void onUtteranceStart(String utteranceId)
        {
        
        }
        
        @Override
        public void onUtteranceDone(String utteranceId)
        {
            switch (Integer.valueOf(utteranceId))
            {
                case SCEN_INDEX_START:
                    // handlerScenarize.sendEmptyMessage(SCEN_INDEX_ANIMAL_RFID);
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_MRT_MAP); // 測試
                    break;
                case SCEN_INDEX_ANIMAL_RFID:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_HOLD_HAND, 2000);
                    break;
                case SCEN_INDEX_HOLD_HAND: // 孩子挑選交通工具RFID
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_TRAFFIC_BUS, 6000);
                    break;
                case SCEN_INDEX_TRAFFIC_BUS: // 孩子將悠遊卡RFID放上盤子
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_TRAFFIC_CARD, 6000);
                    break;
                case SCEN_INDEX_TRAFFIC_MRT: // 孩子將悠遊卡RFID放上盤子
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_TRAFFIC_CARD, 6000);
                    break;
                case SCEN_INDEX_TRAFFIC_CAR:
                    break;
                case SCEN_INDEX_TRAFFIC_CARD:
                    switch (mnTraffic)
                    {
                        case SCEN_INDEX_TRAFFIC_BUS:
                            handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_BUS_INSIDE, 1000);
                            break;
                        case SCEN_INDEX_TRAFFIC_MRT:
                            handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_MRT_MAP, 1000);
                            break;
                    }
                    
                    break;
                case SCEN_INDEX_BUS_INSIDE:     // 等待拉人去座位
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_DROP_CUSTOM, 6000);
                    break;
                case SCEN_INDEX_DROP_CUSTOM_IDLE: // 等待拉人去座位 第二次
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_DROP_CUSTOM_IDLE2, 3000);
                    break;
                case SCEN_INDEX_DROP_CUSTOM:    // 好棒！!!我們出發囉！
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_BUS_DRIVE);
                    break;
                case SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_ZOO_DOOR);
                    break;
                case SCEN_INDEX_ZOO_DOOR:
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_ANIMAL_MONKEY);
                    break;
                case SCEN_INDEX_ANIMAL_MONKEY:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_BANANA, 2000);
                    break;
                case SCEN_INDEX_BANANA:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_BANANA_NON, 1000);
                    break;
                case SCEN_INDEX_BANANA_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_ANIMAL_ELEPHONE, 2000);
                    break;
                case SCEN_INDEX_FOOD_MENU:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_EAT_HAMBERB, 6000);
                    break;
                case SCEN_INDEX_EAT_HAMBERB:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_EATED_HAMBERB, 1000);
                    break;
                case SCEN_INDEX_EAT_DNUTE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_EATED_DNUTE, 1000);
                    break;
                case SCEN_INDEX_EAT_ICECREAME:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_EATED_ICECREAME, 1000);
                    break;
                case SCEN_INDEX_EATED_HAMBERB:
                case SCEN_INDEX_EATED_DNUTE:
                case SCEN_INDEX_EATED_ICECREAME:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_GAME_OVER, 1000);
                    break;
                case SCEN_INDEX_ANIMAL_ELEPHONE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_VEGETABLE, 1000);
                    break;
                case SCEN_INDEX_VEGETABLE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_VEGETABLE_NON, 2000);
                    break;
                case SCEN_INDEX_VEGETABLE_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_LEMUR, 1000);
                    break;
                case SCEN_INDEX_LEMUR:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_APPLE, 1000);
                    break;
                case SCEN_INDEX_APPLE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_APPLE_NON, 2000);
                    break;
                case SCEN_INDEX_APPLE_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_FOOD_MENU, 1000);
                    break;
                case SCEN_INDEX_ANIMAL_KONG:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_FAV_ANIMAL, 1000);
                    break;
                case SCEN_INDEX_FAV_ANIMAL:
                    handlerScenarize.sendEmptyMessage(SCEN_INDEX_FAV_ANIMAL_SPEECH);
                    break;
                case SCEN_INDEX_GAME_OVER:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN_INDEX_FINISH, 2000);
                    break;
                case SCEN_INDEX_FACE_EMONTION:
                    handlerScenarize.sendEmptyMessageDelayed(mnScenarizeIndex, 100);
                    break;
            }
        }
    };
    
    @Override
    public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String,
        String> ttsEmotionData, HashMap<String, String> imageEmotionData, Object extendData)
    {
        stEmotion.clear();
        
        if (null != faceEmotionData)
        {
            stEmotion.strEmotion = faceEmotionData.get("EMOTION_NAME");
        }
        
        if (null != ttsEmotionData)
        {
            stEmotion.strTTS_SPEED = ttsEmotionData.get("TTS_SPEED");
            stEmotion.strTTS_PITCH = ttsEmotionData.get("TTS_PITCH");
            stEmotion.strTTS_TEXT = ttsEmotionData.get("TTS_TEXT");
        }
        
        if (null != imageEmotionData)
        {
            stEmotion.strIMG_FILE_NAME = imageEmotionData.get("IMG_FILE_NAME");
        }
        
        Logs.showTrace("[ZooActivity] onFaceEmotionResult EMOTION_NAME:" + stEmotion.strEmotion +
            " TTS_SPEED:" + stEmotion.strTTS_SPEED + " TTS_PITCH:" + stEmotion.strTTS_PITCH + " "
            + "TTS_TEXT:" + stEmotion.strTTS_TEXT + " IMG_FILE_NAME:" + stEmotion.strIMG_FILE_NAME);
        
        handlerScenarize.sendEmptyMessage(SCEN_INDEX_FACE_EMONTION);
    }
    
    @Override
    public void onFaceDetectResult(boolean isDetectFace)
    {
    
    }
}
