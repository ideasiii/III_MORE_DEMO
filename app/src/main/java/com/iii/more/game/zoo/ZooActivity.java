package com.iii.more.game.zoo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Message;
import android.speech.tts.UtteranceProgressListener;
import android.support.annotation.Nullable;

import com.iii.more.game.module.MSG;
import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.CockpitSensorEventListener;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;

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
import java.util.Timer;
import java.util.TimerTask;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
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
    private final int SCEN_INDEX_GAME_OVER = 666;
    private final int SCEN_INDEX_FINISH = 999;
    
    private MainApplication application = null;
    private RobotHead robotHead = null;
    private TrackerHandler trackerHandler = null;
    private Timer timer = null;
    private int mnScenarizeIndex;
    private VoiceRecognition mVoiceRecognition = null;
    private String mstrFavAnimal = "";
    private ImageView ivHambergur = null;
    private ImageView ivIceCream = null;
    private ImageView ivDonuts = null;
    private ImageView ivMan = null;
    private LinearLayout linearFood = null;
    private int mnDroppedX = 0;
    
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        Logs.showTrace("onCreate");
        super.onCreate(savedInstanceState);
        Utility.fullScreenNoBar(this);
        robotHead = new RobotHead(this);
        setContentView(robotHead);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
            public void onDroped(View view, int nX, int nY)
            {
                if (null != timer)
                {
                    timer.cancel();
                }
                mnDroppedX = nX;
                handler.sendEmptyMessage(SCEN_INDEX_DROP_CUSTOM);
                Logs.showTrace("onDropped view: " + view.getTag() + "x: " + String.valueOf(mnDroppedX));
            }
        });
        application = (MainApplication) getApplication();
        
        // 註冊 Sensor 感應 From Application
        application.setCockpitSensorEventListener(cockpitSensorEventListener);
        robotHead.setObjectImg(R.drawable.busy, ImageView.ScaleType.CENTER_INSIDE);
        robotHead.showObjectImg(true);
        
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
        
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams((int) Utility.convertDpToPixel(250, this), (int) Utility.convertDpToPixel(250, this));
        ivHambergur.setLayoutParams(layoutParams1);
        ivDonuts.setLayoutParams(layoutParams1);
        ivIceCream.setLayoutParams(layoutParams1);
        
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(300, 650);
        layoutParams2.setMargins(200, 700, 0, 0);
        ivMan.setLayoutParams(layoutParams2);
        ivMan.setOnTouchListener(dropTouchListener);
        
        
        linearFood = new LinearLayout(this);
        linearFood.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
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
        
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(handlerSpeech);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
    }
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        if (null != timer)
        {
            timer.cancel();
        }
        robotHead.stop();
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
        HashMap<String, String> track = new HashMap<String, String>();
        int nFace = 0;
        
        switch (nIndex)
        {
            case SCEN_INDEX_START:
                
                robotHead.showFaceImg(true);
                robotHead.showObjectImg(false);
                // 遊戲開始
                nFace = R.drawable.octobo16;
                strFaceImg = "octobo16.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                strTTS = "嗨! 你好 來玩遊戲吧";
                robotHead.setPitch(1.5f, 1.5f);
                break;
            case SCEN_INDEX_ANIMAL_RFID:
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                strTTS = "哈囉，" + strName + "今天我們一起去動物園玩！牽著我的手，出發囉！";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.zoo_map, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.bringObjImgtoFront();
                robotHead.setPitch(1.3f, 0.9f);
                break;
            case SCEN_INDEX_HOLD_HAND:
                strTTS = "抓緊喔！今天，你想要坐什麼交通工具去呢？";
                nFace = R.drawable.octobo13;
                strFaceImg = "octobo13.png";
                robotHead.bringFaceImgtoFront();
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.2f, 0.8f);
                break;
            case SCEN_INDEX_TRAFFIC_BUS: // 孩子選擇搭公車
                strTTS = "請刷悠遊卡";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.bus, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
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
                robotHead.bringObjImgtoFront();
                robotHead.setImgObjectTouch(true);
                nFace = R.drawable.businside;
                strFaceImg = "businside.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setObjectImg(R.drawable.man, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.3f, 0.8f);
                robotHead.addView(ivMan);
                break;
            case SCEN_INDEX_DROP_CUSTOM:    // 孩子直接用手指在畫面上拉乘客到座位上，完成
                robotHead.setImgObjectTouch(false);
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
                robotHead.setPitch(1.4f, 0.85f);
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE:
                strTTS = "快一點呀，，公車要開囉！";
                robotHead.setPitch(0.75f, 0.6f);
                break;
            case SCEN_INDEX_DROP_CUSTOM_IDLE2:
                strTTS = "啊啊 ，，來不及了，，公車開動囉！";
                robotHead.setPitch(2f, 2f);
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
                robotHead.setPitch(0.4f, 2f);
                break;
            case SCEN_INDEX_ZOO_DOOR:       // 顯示出動物園的大門
                //strTTS = "到囉，，讓我們一起來參觀台灣動物區的動物";
                strTTS = "到囉，，讓我們一起來參觀動物吧";
                nFace = R.drawable.zoodoor;
                strFaceImg = "zoodoor.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.setPitch(1.5f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_MONKEY:
                strTTS = "看，是猴子";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.monkey, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.2f, 1.6f);
                break;
            case SCEN_INDEX_BANANA:
                strTTS = "猴子 最愛吃香蕉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.2f, 1.6f);
                break;
            case SCEN_INDEX_BANANA_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana_non, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.2f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_BEAR:
                strTTS = "快看，是台灣黑熊";
                //    robotHead.setObjectImg(R.drawable.bear, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(1.5f, 0.8f);
                break;
            case SCEN_INDEX_ANIMAL_SWAN:
                strTTS = "好美喔，，是天鵝";
                //    robotHead.setObjectImg(R.drawable.swan, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(0.7f, 0.8f);
                break;
            case SCEN_INDEX_ANIMAL_LION:
                strTTS = "是獅子";
                //    robotHead.setObjectImg(R.drawable.lion, ImageView.ScaleType.FIT_XY);
                robotHead.setPitch(1.5f, 0.8f);
                break;
            case SCEN_INDEX_NOT_FEAR:
                strTTS = strName + "，不要怕，我陪你";
                nFace = R.drawable.octobo21;
                strFaceImg = "octobo21.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(false);
                robotHead.setPitch(1f, 0.7f);
                break;
            case SCEN_INDEX_FOOD_MENU:
                strTTS = strName + "我們來吃東西休息一下吧！選你想吃得食物吧";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(false);
                robotHead.setPitch(1.4f, 0.9f);
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
                robotHead.setPitch(1.5f, 0.9f);
                break;
            case SCEN_INDEX_EATED_HAMBERB:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.burger_non, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setPitch(1.6f, 0.5f);
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
                robotHead.setPitch(1.5f, 0.9f);
                break;
            case SCEN_INDEX_EATED_DNUTE:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.donut_non, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setPitch(1.6f, 0.5f);
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
                robotHead.setPitch(1.5f, 0.9f);
                break;
            case SCEN_INDEX_EATED_ICECREAME:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.icecream_non, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setPitch(1.6f, 0.5f);
                break;
            case SCEN_INDEX_ZOO_DOOR2:
                finish();
                strTTS = "讓我們一起再來參觀非洲動物區的動物喔";
                nFace = R.drawable.zoodoor;
                strFaceImg = "zoodoor.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.bringFaceImgtoFront();
                robotHead.setPitch(1.5f, 1.6f);
                break;
            case SCEN_INDEX_ANIMAL_ELEPHONE:
                strTTS = "快看，，是大象";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.elephone2, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_VEGETABLE:
                strTTS = "大象最喜歡吃草跟樹葉!!";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_VEGETABLE_NON:
                strTTS = "啊嗯嗯嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable_non, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_LEMUR:
                strTTS = "是狐猴";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.lemur, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_APPLE:
                strTTS = "蘋果是狐猴最愛吃的食物";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                robotHead.setPitch(1.8f, 0.7f);
                break;
            case SCEN_INDEX_APPLE_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple_non, ImageView.ScaleType.FIT_XY);
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
                nFace = R.drawable.octobo31;
                strFaceImg = "octobo31.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(0.8f, 1f);
                break;
            case SCEN_INDEX_FAV_ANIMAL_SPEECH:
                mVoiceRecognition.startListen();
                break;
            case SCEN_INDEX_GAME_OVER:
                strTTS = "再見囉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setPitch(0.9f, 1.2f);
                robotHead.setObjectImg(R.drawable.zoodoor, ImageView.ScaleType.FIT_XY);
                
                if (mstrFavAnimal.contains("大象"))
                {
                    robotHead.setObjectImg(R.drawable.elephone2, ImageView.ScaleType.CENTER_CROP);
                }
                
                if (mstrFavAnimal.contains("猩猩") || mstrFavAnimal.contains("星星"))
                {
                    robotHead.setObjectImg(R.drawable.kong, ImageView.ScaleType.CENTER_CROP);
                }
                
                if (mstrFavAnimal.contains("獅子"))
                {
                    //      robotHead.setObjectImg(R.drawable.lion, ImageView.ScaleType.CENTER_CROP);
                }
                
                if (mstrFavAnimal.contains("天鵝"))
                {
                    //      robotHead.setObjectImg(R.drawable.swan, ImageView.ScaleType.CENTER_CROP);
                }
                
                if (mstrFavAnimal.contains("台灣黑熊") || mstrFavAnimal.contains("熊") || mstrFavAnimal.contains("黑熊"))
                {
                    //      robotHead.setObjectImg(R.drawable.bear, ImageView.ScaleType.CENTER_CROP);
                }
                
                if (mstrFavAnimal.contains("台灣獼猴") || mstrFavAnimal.contains("獼猴") || mstrFavAnimal.contains("猴子"))
                {
                    robotHead.setObjectImg(R.drawable.monkey, ImageView.ScaleType.CENTER_CROP);
                }
                robotHead.showObjectImg(true);
                break;
            case SCEN_INDEX_FINISH:
                finish();
                break;
            default:
                return;
        }
        robotHead.setPitch(0.95f, 1.0f);
        robotHead.playTTS(strTTS, String.valueOf(nIndex));
        trackerHandler.setRobotFace(strFaceImg);
        
        track.put("Scenarize", String.valueOf(nIndex));
        track.put("TTS", strTTS);
        track.put("Face", String.valueOf(nFace));
        track.put("Sensor", "");
        application.sendToTracker(track);
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
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_TRAFFIC_BUS), 6000); // 假設選了公車
                    break;
                case SCEN_INDEX_TRAFFIC_BUS:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_TRAFFIC_CARD), 6000); // 孩子將悠遊卡RFID放上盤子
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
                    //timer = new Timer(true);
                    // TODO hack, extend delay
                    //timer.schedule(new ScenarizeTimer(SCEN_INDEX_DROP_CUSTOM_IDLE), 3000);
                    //timer.schedule(new ScenarizeTimer(SCEN_INDEX_DROP_CUSTOM_IDLE), 6000);
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
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_BANANA), 2000);
                    break;
                case SCEN_INDEX_BANANA:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_BANANA_NON), 1000);
                    break;
                case SCEN_INDEX_BANANA_NON:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_ELEPHONE), 2000);
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
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EAT_HAMBERB), 6000);
                    break;
                case SCEN_INDEX_EAT_HAMBERB:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EATED_HAMBERB), 1000);
                    break;
                case SCEN_INDEX_EATED_HAMBERB:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_GAME_OVER), 1000);
                    break;
                case SCEN_INDEX_EAT_DNUTE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EATED_DNUTE), 1000);
                    break;
                case SCEN_INDEX_EATED_DNUTE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_GAME_OVER), 1000);
                    break;
                case SCEN_INDEX_EAT_ICECREAME:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_EATED_ICECREAME), 1000);
                    break;
                case SCEN_INDEX_EATED_ICECREAME:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_GAME_OVER), 1000);
                    break;
                case SCEN_INDEX_ZOO_DOOR2:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_ANIMAL_ELEPHONE), 1000);
                    break;
                case SCEN_INDEX_ANIMAL_ELEPHONE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_VEGETABLE), 1000);
                    break;
                case SCEN_INDEX_VEGETABLE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_VEGETABLE_NON), 2000);
                    break;
                case SCEN_INDEX_VEGETABLE_NON:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_LEMUR), 1000);
                    break;
                case SCEN_INDEX_LEMUR:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_APPLE), 1000);
                    break;
                case SCEN_INDEX_APPLE:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_APPLE_NON), 2000);
                    break;
                case SCEN_INDEX_APPLE_NON:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_FOOD_MENU), 1000);
                    break;
                case SCEN_INDEX_ANIMAL_KONG:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_FAV_ANIMAL), 1000);
                    break;
                case SCEN_INDEX_FAV_ANIMAL:
                    handler.sendEmptyMessage(SCEN_INDEX_FAV_ANIMAL_SPEECH);
                    break;
                case SCEN_INDEX_GAME_OVER:
                    timer = new Timer(true);
                    timer.schedule(new ScenarizeTimer(SCEN_INDEX_FINISH), 2000);
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
            Logs.showTrace("onScannedRfid Result:" + scannedResult);
            switch (mnScenarizeIndex)
            {
                case SCEN_INDEX_START:
                    mnScenarizeIndex = -1;
                    timer.cancel();
                    handler.sendEmptyMessage(SCEN_INDEX_ANIMAL_RFID);
                    break;
                case SCEN_INDEX_HOLD_HAND:
                    mnScenarizeIndex = -1;
                    timer.cancel();
                    handler.sendEmptyMessage(SCEN_INDEX_TRAFFIC_BUS);
                    break;
                case SCEN_INDEX_TRAFFIC_BUS:
                    mnScenarizeIndex = -1;
                    timer.cancel();
                    handler.sendEmptyMessage(SCEN_INDEX_TRAFFIC_CARD);
                    break;
            }
        }
    };
    
    private Handler handlerSpeech = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
            {
                mVoiceRecognition.stopListen();
                Logs.showTrace("[LogicHandler] Get voice Text: " + message.get("message"));
                mstrFavAnimal = (String) message.get("message");
                handler.sendEmptyMessage(SCEN_INDEX_GAME_OVER);
            }
            else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                //startListen first handle
            }
            else
            {
                Logs.showTrace("get ERROR message: " + message.get("message"));
                mVoiceRecognition.stopListen();
                handler.sendEmptyMessage(SCEN_INDEX_GAME_OVER);
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
                    if (null != timer)
                    {
                        timer.cancel();
                    }
                    handler.sendEmptyMessage(SCEN_INDEX_EAT_HAMBERB);
                    return true;
                }
                if (0 == strTag.compareTo("DNUTE"))
                {
                    if (null != timer)
                    {
                        timer.cancel();
                    }
                    handler.sendEmptyMessage(SCEN_INDEX_EAT_DNUTE);
                    return true;
                }
                if (0 == strTag.compareTo("ICECREAM"))
                {
                    if (null != timer)
                    {
                        timer.cancel();
                    }
                    handler.sendEmptyMessage(SCEN_INDEX_EAT_ICECREAME);
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
                Logs.showTrace("onTouch UP view: " + view.getTag() + "x: " + String.valueOf(view.getX()));
                return false;
            }
            else
            {
                return false;
            }
        }
    };
}
