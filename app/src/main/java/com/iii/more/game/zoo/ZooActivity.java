/**
 * 圖檔連結
 * https://docs.google.com/document/d/10m8E-TKE4rBdH_fNxfXXoUVe6wg6WdDRrrvTX2BbfDQ/edit
 */

package com.iii.more.game.zoo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.WeakHashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

/**
 * Created by jugo on 2017/11/1
 */

public class ZooActivity extends Activity
{
    private MainApplication application = null;
    private RobotHead robotHead = null;
    public static TrackerHandler trackerHandler = null;
    
    private VoiceRecognition mVoiceRecognition = null;
    private ImageView ivMan = null;
    private MrtMap mrtMap = null;
    private TTSEventHandler ttsEventHandler = null;
    private FaceEmotionEventHandler faceEmotionEventHandler = null;
    private ScenarizeHandler scenarizeHandler = null;
    private ZooAreaLayout zooAreaLayout = null;
    private ZooAnimalLayout zooAnimalLayout = null;
    private int mnZooAreaCount = 0;
    private int nTimeoutAnimal = 4;
    RelativeLayout.LayoutParams layoutParamsExView = null;
    private FoodListLayout foodListLayout = null;
    private static TrafficListLayout trafficListLayout = null;
    private ImageView imgvFoodEat = null;
    private CarFixLayout carFixLayout = null;
    
    
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
                GLOBAL.mnDroppedX = nX;
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_DROP_CUSTOM);
                //handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_BUS_EMOTION_RESP);
                Logs.showTrace("onDropped view: " + view.getTag() + "x: " + String.valueOf(GLOBAL
                    .mnDroppedX));
            }
        });
        application = (MainApplication) getApplication();
        
        ttsEventHandler = new TTSEventHandler(handlerScenarize);
        faceEmotionEventHandler = new FaceEmotionEventHandler(this, handlerScenarize);
        
        scenarizeHandler = new ScenarizeHandler(handlerScenarize);
        
        
        // 註冊 Sensor 感應 From Application
        application.setCockpitSensorEventListener(scenarizeHandler.sensorEventHandler
            .getSensorEventListener());
        // 註冊TTS Listener
        application.setTTSEventListener(ttsEventHandler.getTTSEventListener());
        
        robotHead.setFace(this, R.drawable.g_o_speak, ImageView.ScaleType.CENTER_CROP);
        
        ivMan = new ImageView(this);
        ivMan.setTag("MAN");
        ivMan.setImageResource(R.drawable.man);
        ivMan.setScaleType(ImageView.ScaleType.FIT_XY);
        
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams((int) Utility
            .convertDpToPixel(250, this), (int) Utility.convertDpToPixel(250, this));
        
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(300, 650);
        layoutParams2.setMargins(200, 700, 0, 0);
        ivMan.setLayoutParams(layoutParams2);
        ivMan.setOnTouchListener(dropTouchListener);
        
        trackerHandler = new TrackerHandler(this);
        trackerHandler.setSource("0");
        trackerHandler.setActivity("game");
        trackerHandler.setDescription("Edubot Zoo Game");
        
        layoutParamsExView = new RelativeLayout.LayoutParams(1200, 1200);
        //layoutParamsExView.setMargins((int) 0, (int) 0, (int) 0, (int) 0);
        layoutParamsExView.addRule(RelativeLayout.CENTER_HORIZONTAL);
        
        mrtMap = new MrtMap(this, handlerScenarize);
        RelativeLayout.LayoutParams layoutParamsMrtMap = new RelativeLayout.LayoutParams(800, 800);
        layoutParamsMrtMap.setMargins((int) 0, (int) 190, (int) 0, (int) 0);
        layoutParamsMrtMap.addRule(RelativeLayout.CENTER_HORIZONTAL);
        mrtMap.setLayoutParams(layoutParamsMrtMap);
        
        zooAreaLayout = new ZooAreaLayout(this, handlerScenarize);
        zooAreaLayout.setLayoutParams(layoutParamsExView);
        zooAreaLayout.showArrow(true);
        zooAreaLayout.showArrow(true);
        
        foodListLayout = new FoodListLayout(this, handlerScenarize);
        foodListLayout.setLayoutParams(layoutParamsExView);
        
        imgvFoodEat = new ImageView(this);
        imgvFoodEat.setLayoutParams(layoutParamsExView);
        
        trafficListLayout = new TrafficListLayout(this, handlerScenarize);
        trafficListLayout.setLayoutParams(layoutParamsExView);
        
        carFixLayout = new CarFixLayout(this, handlerScenarize);
        RelativeLayout.LayoutParams layoutParamsCarFix = new RelativeLayout.LayoutParams(800,
            1100);
        layoutParamsCarFix.setMargins((int) 0, (int) 80, (int) 0, (int) 0);
        layoutParamsCarFix.addRule(RelativeLayout.CENTER_HORIZONTAL);
        carFixLayout.setLayoutParams(layoutParamsCarFix);
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
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(handlerSpeech);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        GLOBAL.ChildName = application.getName(Parameters.ID_CHILD_NAME);
        mnZooAreaCount = 0;
        scenarizeHandler.createScenarize(GLOBAL.scenarize);
        scenarizeHandler.setHandler(handlerScenarize);
        robotHead.setFace(this, R.drawable.g_o_speak, ImageView.ScaleType.CENTER_CROP);
        Scenarize(SCEN.SCEN_INDEX_START, null);
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
    
    public void Scenarize(int nIndex, Object object)
    {
        Logs.showTrace("############################ Scenarize Index:" + nIndex +
            "############################");
        if (SCEN.SCEN_INDEX_FINISH == nIndex)
        {
            finish();
        }
        
        String strTTS = "";
        String strFaceImg = "";
    
        GLOBAL.scenarizeCurr.ScenarizeIndex = nIndex;
        
        if (SCEN.MSG_TTS_PLAY == nIndex)
        {
            application.setTTSPitch(1.0f, 1.0f);
            application.playTTS((String) object, String.valueOf(nIndex));
            return;
        }
        
        if (SCEN.SCEN_INDEX_ANIMAL_END == nIndex)
        {
            if (SCEN.MAX_ZOO_VISIT <= mnZooAreaCount)
            {
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_FOOD_STORE);
                return;
            }
            else
            {
                robotHead.removeView(zooAnimalLayout);
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_CHOICE_ZOO);
            }
        }
        
        
        if (GLOBAL.scenarize.indexOfKey(nIndex) < 0)
        {
            Logs.showError("[ZooActivity] Scenarize invalid Index:" + nIndex);
            return;
        }
    
        
        
        try
        {
            JSONObject jsonScenarize = GLOBAL.scenarize.get(nIndex);
            Logs.showTrace("[ZooActivity] Scenarize: " + jsonScenarize.toString());
            GLOBAL.scenarizeCurr.ScenarizeNext = jsonScenarize.getInt("next");
            robotHead.bringToFront();
            robotHead.showFaceImg(jsonScenarize.getBoolean("face_show"));
            robotHead.showObjectImg(jsonScenarize.getBoolean("object_show"));
            robotHead.setFace(this, jsonScenarize.getInt("face_id"), (ImageView.ScaleType)
                jsonScenarize.get("face_scale_type"));
            robotHead.setObjectImg(this, jsonScenarize.getInt("object_id"), (ImageView.ScaleType)
                jsonScenarize.get("object_scale_type"));
            strFaceImg = jsonScenarize.getString("face_image");
            
            if ((boolean) jsonScenarize.get("emotion"))
            {
                application.setFaceEmotionEventListener(faceEmotionEventHandler
                    .getFaceEmotionEventListener());
            }
            else
            {
                application.setFaceEmotionEventListener(null);
            }
            ScenarizeHandler.FRONT front = (ScenarizeHandler.FRONT) jsonScenarize.get("front");
            
            strTTS = jsonScenarize.getString("tts_text");
            
            if (SCEN.SCEN_INDEX_DROP_CUSTOM == nIndex)
            {
                robotHead.removeView(ivMan);
                if (550 < GLOBAL.mnDroppedX)
                {
                    robotHead.setFace(this, R.drawable.businside_right, (ImageView.ScaleType)
                        jsonScenarize.get("face_scale_type"));
                    strFaceImg = "businside_right.png";
                }
                else
                {
                    robotHead.setFace(this, R.drawable.businside_left, (ImageView.ScaleType)
                        jsonScenarize.get("face_scale_type"));
                    strFaceImg = "businside_left.png";
                }
            }
            
            if (SCEN.SCEN_INDEX_CHOICE_ZOO == nIndex)
            {
                if (0 < mnZooAreaCount)
                {
                    robotHead.removeView(zooAnimalLayout);
                    strTTS = "讓我們再來參觀其他動物區";
                }
                if (0 < robotHead.indexOfChild(mrtMap))
                {
                    robotHead.removeView(mrtMap);
                }
                robotHead.addView(zooAreaLayout);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_TAIWAN == nIndex)
            {
                ++mnZooAreaCount;
                robotHead.removeView(zooAreaLayout);
                zooAnimalLayout = new ZooAnimalLayout(this);
                zooAnimalLayout.showArrow(false);
                zooAnimalLayout.setHandler(handlerScenarize);
                zooAnimalLayout.setLayoutParams(layoutParamsExView);
                zooAnimalLayout.init(ZooAnimalLayout.ANIMAL_AREA.台灣動物區);
                robotHead.addView(zooAnimalLayout);
                zooAnimalLayout.startSlideShow(nTimeoutAnimal, false);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_BIRD == nIndex)
            {
                ++mnZooAreaCount;
                robotHead.removeView(zooAreaLayout);
                zooAnimalLayout = new ZooAnimalLayout(this);
                zooAnimalLayout.showArrow(false);
                zooAnimalLayout.setHandler(handlerScenarize);
                zooAnimalLayout.setLayoutParams(layoutParamsExView);
                zooAnimalLayout.init(ZooAnimalLayout.ANIMAL_AREA.鳥園);
                robotHead.addView(zooAnimalLayout);
                zooAnimalLayout.startSlideShow(nTimeoutAnimal, false);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_RAIN == nIndex)
            {
                ++mnZooAreaCount;
                robotHead.removeView(zooAreaLayout);
                zooAnimalLayout = new ZooAnimalLayout(this);
                zooAnimalLayout.showArrow(false);
                zooAnimalLayout.setHandler(handlerScenarize);
                zooAnimalLayout.setLayoutParams(layoutParamsExView);
                zooAnimalLayout.init(ZooAnimalLayout.ANIMAL_AREA.熱帶雨林動物區);
                robotHead.addView(zooAnimalLayout);
                zooAnimalLayout.startSlideShow(nTimeoutAnimal, false);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_CUT == nIndex)
            {
                ++mnZooAreaCount;
                robotHead.removeView(zooAreaLayout);
                zooAnimalLayout = new ZooAnimalLayout(this);
                zooAnimalLayout.showArrow(false);
                zooAnimalLayout.setHandler(handlerScenarize);
                zooAnimalLayout.setLayoutParams(layoutParamsExView);
                zooAnimalLayout.init(ZooAnimalLayout.ANIMAL_AREA.可愛動物區);
                robotHead.addView(zooAnimalLayout);
                zooAnimalLayout.startSlideShow(nTimeoutAnimal, false);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_AFFICA == nIndex)
            {
                ++mnZooAreaCount;
                robotHead.removeView(zooAreaLayout);
                zooAnimalLayout = new ZooAnimalLayout(this);
                zooAnimalLayout.showArrow(false);
                zooAnimalLayout.setHandler(handlerScenarize);
                zooAnimalLayout.setLayoutParams(layoutParamsExView);
                zooAnimalLayout.init(ZooAnimalLayout.ANIMAL_AREA.非洲動物區);
                robotHead.addView(zooAnimalLayout);
                zooAnimalLayout.startSlideShow(nTimeoutAnimal, false);
            }
           
            if (SCEN.SCEN_INDEX_FOOD_STORE == nIndex)
            {
                robotHead.removeView(zooAnimalLayout);
            }
            
            if (SCEN.SCEN_INDEX_FOOD_CHOICE == nIndex)
            {
                robotHead.addView(foodListLayout);
            }
            
            if (SCEN.SCEN_INDEX_FOOD_EAT == nIndex)
            {
                robotHead.removeView(foodListLayout);
                Utility.loadImage(this, (int) object, imgvFoodEat);
                robotHead.addView(imgvFoodEat);
            }
            
            if (SCEN.SCEN_INDEX_CAR_OUTSIDE == nIndex)
            {
                robotHead.addView(trafficListLayout);
                trafficListLayout.setNextScenarize(GLOBAL.scenarizeCurr.ScenarizeNext);
                trafficListLayout.startSlideShow(3, false);
            }
            
            if (SCEN.SCEN_INDEX_CAR_FIX == nIndex)
            {
                robotHead.removeView(trafficListLayout);
                robotHead.addView(carFixLayout);
            }
            
            if (SCEN.SCEN_INDEX_CAR_FIX_SUCCESS == nIndex)
            {
                robotHead.removeView(carFixLayout);
            }
            
            if (SCEN.SCEN_INDEX_ZOO_DOOR == nIndex)
            {
                robotHead.removeView(carFixLayout);
            }
            
            if (SCEN.SCEN_INDEX_MRT_MAP == nIndex)
            {
                robotHead.addView(mrtMap);
            }
            
            if (SCEN.SCEN_INDEX_GAME_OVER == nIndex)
            {
                robotHead.removeView(imgvFoodEat);
            }
          
            if (SCEN.SCEN_INDEX_BUS_EMOTION_RESP == nIndex || SCEN.SCEN_INDEX_MRT_EMOTION_RESP ==
                nIndex || SCEN.SCEN_INDEX_CAR_EMOTION_RESP == nIndex)
            {
                robotHead.removeView(mrtMap);
                robotHead.removeView(carFixLayout);
                JSONObject jsonEmotion = faceEmotionEventHandler.getEmotion();
                Logs.showTrace("[ZooActivity] emotion response: " + jsonEmotion.toString());
                if (!jsonEmotion.isNull("EMOTION_NAME"))
                {
                    try
                    {
                        strTTS = (String) jsonEmotion.get("TTS_TEXT");
                        robotHead.setFace(this, (int) jsonEmotion.get("IMG_FILE_RES_ID"),
                            ImageView.ScaleType.CENTER_CROP);
                    }
                    catch (Exception e)
                    {
                        Logs.showError("Exception: " + e.getMessage());
                    }
                }
            }
          
            robotHead.setBackgroundColor(Color.rgb(108, 147, 213));
            switch (front)
            {
                case FACE:
                    robotHead.bringFaceImgtoFront();
                    break;
                case OBJECT:
                    robotHead.bringObjImgtoFront();
                    break;
            }
          
            if (SCEN.SCEN_INDEX_BUS_INSIDE == nIndex)
            {
                robotHead.addView(ivMan);
            }
            
            application.setTTSPitch(1.0f, 1.0f);
            application.playTTS(strTTS, String.valueOf(nIndex));
          
            // 傳送Tracker Data
            trackerHandler.setRobotFace(strFaceImg).setSensor("", "").setScene(String.valueOf
                (GLOBAL.scenarizeCurr.ScenarizeIndex)).setMicrophone("").setSpeaker("tts",
                strTTS, "1", "1", "").send();
        }
        catch (Exception e)
        {
            Logs.showError("[ZooActivity] Scenarize Exception:" + e.toString());
            e.printStackTrace();
        }
    }
    
    private final SelfHandler handlerScenarize = new SelfHandler(this);
    
    private static class SelfHandler extends Handler
    {
        private final WeakReference<ZooActivity> mWeakSelf;
        
        private SelfHandler(ZooActivity act)
        {
            mWeakSelf = new WeakReference<>(act);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            ZooActivity self = mWeakSelf.get();
            if (self == null)
            {
                return;
            }
            
            self.Scenarize(msg.what, msg.obj);
        }
    }
    
    ;
    
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
            }
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
    
}
