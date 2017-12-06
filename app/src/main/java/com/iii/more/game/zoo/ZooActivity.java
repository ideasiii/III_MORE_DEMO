package com.iii.more.game.zoo;


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
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;

import android.os.Handler;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

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
    public ImageView ivHambergur = null;
    public ImageView ivIceCream = null;
    public ImageView ivDonuts = null;
    private ImageView ivMan = null;
    private LinearLayout linearFood = null;
    private int mnDroppedX = 0;
    private int mnTraffic = 0;
    private MrtMap mrtMap = null;
    private TTSEventHandler ttsEventHandler = null;
    private SensorEventHandler sensorEventHandler = null;
    private FaceEmotionEventHandler faceEmotionEventHandler = null;
    private ScenarizeHandler scenarizeHandler = null;
    
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
                mnDroppedX = nX;
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_DROP_CUSTOM);
                Logs.showTrace("onDropped view: " + view.getTag() + "x: " + String.valueOf
                    (mnDroppedX));
            }
        });
        application = (MainApplication) getApplication();
        
        ttsEventHandler = new TTSEventHandler(handlerScenarize);
        sensorEventHandler = new SensorEventHandler(handlerScenarize);
        faceEmotionEventHandler = new FaceEmotionEventHandler(handlerScenarize);
        scenarizeHandler = new ScenarizeHandler(this);
        scenarizeHandler.createScenarize(GLOBAL.scenarize);
        
        // 註冊 Sensor 感應 From Application
        application.setCockpitSensorEventListener(sensorEventHandler.getSensorEventListener());
        // 註冊TTS Listener
        application.setTTSEventListener(ttsEventHandler.getTTSEventListener());
        // 註冊FaceEmotionEventListener
        application.setFaceEmotionEventListener(faceEmotionEventHandler
            .getFaceEmotionEventListener());
        
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
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(handlerSpeech);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        GLOBAL.ChildName = application.getName(Parameters.ID_CHILD_NAME);
        Scenarize(SCEN.SCEN_INDEX_START);
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
        GLOBAL.mnScenarizeIndex = nIndex;
        String strTTS = "";
        String strFaceImg = "";
        
        Logs.showTrace("[ZooActivity] Scenarize Index:" + nIndex);
        
        if (GLOBAL.scenarize.indexOfKey(nIndex) < 0)
        {
            Logs.showError("[ZooActivity] Scenarize invalid Index:" + nIndex);
            return;
        }
        
        try
        {
            JSONObject jsonScenarize = GLOBAL.scenarize.get(nIndex);
            int nNext = jsonScenarize.getInt("next");
            robotHead.showFaceImg(jsonScenarize.getBoolean("face_show"));
            robotHead.showObjectImg(jsonScenarize.getBoolean("object_show"));
            robotHead.setFace(jsonScenarize.getInt("face_id"), (ImageView.ScaleType)
                jsonScenarize.get("face_scale_type"));
            robotHead.setObjectImg(jsonScenarize.getInt("object_id"), (ImageView.ScaleType)
                jsonScenarize.get("object_scale_type"));
            strFaceImg = jsonScenarize.getString("face_image");
            ScenarizeHandler.FRONT front = (ScenarizeHandler.FRONT) jsonScenarize.get("front");
            switch (front)
            {
                case FACE:
                    robotHead.bringFaceImgtoFront();
                    break;
                case OBJECT:
                    robotHead.bringObjImgtoFront();
                    break;
            }
            
            strTTS = jsonScenarize.getString("tts_text");
            if (SCEN.SCEN_INDEX_BUS_INSIDE == nIndex)
            {
                robotHead.addView(ivMan);
            }
            
            
            application.setTTSPitch(1.0f, 1.0f);
            application.playTTS(strTTS, String.valueOf(nIndex));
            
            // 傳送Tracker Data
            trackerHandler.setRobotFace(strFaceImg).setSensor("", "").setScene(String.valueOf
                (GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", strTTS, "1", "1",
                "").send();
            Logs.showTrace("[ZooActivity] Scenarize : " + jsonScenarize.toString());
        }
        catch (Exception e)
        {
            Logs.showError("[ZooActivity] Scenarize Exception:" + e.getMessage());
        }
        
        
    }
    
    private final Handler handlerScenarize = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Scenarize(msg.what);
        }
    };
    
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
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_GAME_OVER);
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
                handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_GAME_OVER);
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
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_EAT_HAMBERB);
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_EAT_HAMBERB);
                    return true;
                }
                if (0 == strTag.compareTo("DNUTE"))
                {
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_EAT_DNUTE);
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_EAT_DNUTE);
                    return true;
                }
                if (0 == strTag.compareTo("ICECREAM"))
                {
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_EAT_ICECREAME);
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_EAT_ICECREAME);
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
    
}
