package com.iii.more.oobe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;


import com.iii.more.main.listeners.CockpitSensorEventListener;
import com.iii.more.main.MainActivity;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;
import com.iii.more.oobe.logic.OobeLogicElement;
import com.iii.more.oobe.logic.OobeLogicHandler;
import com.iii.more.oobe.logic.OobeLogicParameters;
import com.iii.more.oobe.track.OobeTracker;
import com.iii.more.oobe.view.OobeDisplayHandler;
import com.iii.more.screen.view.display.DisplayParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/10/27
 */

public class OobeActivity extends AppCompatActivity implements CockpitSensorEventListener
{
    private OobeLogicHandler mOobeLogicHandler = null;
    private OobeDisplayHandler mOobeDisplayHandler = null;
    private ArrayList<OobeLogicElement> mStateData = null;
    private MainApplication mMainApplication = null;
    private InClassHandler mHandler = new InClassHandler(this);
    private OobeTracker mOobeTracker = null;
    
    private volatile boolean sensorGet = false;
    private volatile String sensorGetEvent = "";
    
    private volatile String rfidString = "";
    
    private VideoView mVideoView = null;
    
    private MediaPlayer mediaPlayer = null;
    
    public void handleMessages(Message msg)
    {
        if (null == mOobeLogicHandler)
        {
            return;
        }
        
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.what)
        {
            case OobeLogicParameters.CLASS_OOBE_LOGIC:
                switch (msg.arg2)
                {
                    case OobeLogicParameters.METHOD_TTS:
                        
                        Logs.showTrace("[OobeActivity] METHOD_TTS message" + msg.obj);
                        String nextToDo = getResponseLogic(message.get("TextID"));
                        Logs.showTrace("[OobeActivity] METHOD_TTS nextToDo " + nextToDo);
                        
                        if (null != nextToDo)
                        {
                            switch (nextToDo)
                            {
                                case "STT":
                                    mOobeLogicHandler.sttLaunch();
                                    
                                    break;
                                case "SensorTag":
                                    // ### set a timer to get
                                    
                                    Thread t = new Thread(new HardwareCheckRunnable(1, mStateData.get
                                        (mOobeLogicHandler.getState()).wait));
                                    t.start();
                                    
                                    
                                    break;
                                case "RFID":
                                    Thread t2 = new Thread(new HardwareCheckRunnable(2, mStateData.get
                                        (mOobeLogicHandler.getState()).wait));
                                    t2.start();
                                    
                                    
                                    break;
                                case "no":
                                    
                                    //forced to do next step
                                    mHandler.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                                            doNext();
                                        }
                                    }, 1000);
                                    
                                    break;
                            }
                            
                        }
                        break;
                    case OobeLogicParameters.METHOD_VOICE:
                        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                        {
                            
                            //###
                            //add tracker microphone
                            try
                            {
                                HashMap<String, Object> trackHashMap = new HashMap<>();
                                
                                HashMap<String, Object> microphone = new HashMap<>();
                                microphone.put("Text", message.get("message"));
                                
                                trackHashMap.put("Scene", String.valueOf(mOobeLogicHandler.getState()));
                                trackHashMap.put("Microphone", microphone);
                                
                                mOobeTracker.tracker(trackHashMap);
                            }
                            catch (Exception e)
                            {
                                Logs.showError("[OobeActivity] log tracker data ERROR: " + e.toString());
                            }
                            
                            if (mOobeLogicHandler.getState() == 1 || mOobeLogicHandler.getState() == 3)
                            {
                                if (!isNoAIJudge(message.get("message")))
                                {
                                    Logs.showTrace("[OobeActivity] answer YES in state: " +
                                        mOobeLogicHandler.getState());
                                    
                                    mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                                }
                                else
                                {
                                    Logs.showTrace("[OobeActivity] answer NO in state: " +
                                        mOobeLogicHandler.getState());
                                    
                                    if (mStateData.get(mOobeLogicHandler.getState()).getRegret() > 0)
                                    {
                                        //返回上一次步驟
                                        mOobeLogicHandler.setState(mOobeLogicHandler.getState() - 1);
                                    }
                                    else
                                    {
                                        //以三次命名錯誤，設其值為空
                                        if (mOobeLogicHandler.getState() == 1)
                                        {
                                            mMainApplication.setName(Parameters.ID_CHILD_NAME, "");
                                        }
                                        else if (mOobeLogicHandler.getState() == 3)
                                        {
                                            mMainApplication.setName(Parameters.ID_ROBOT_NAME, Parameters
                                                .STRING_DEFAULT_ROBOT_NAME);
                                        }
                                        
                                        //jump to next step
                                        mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                                    }
                                    
                                }
                                doNext();
                            }
                            else
                            {
                                if (mOobeLogicHandler.getState() == 0)
                                {
                                    mMainApplication.setName(Parameters.ID_CHILD_NAME,
                                        replaceScriptNameText(message.get("message")));
                                }
                                else if (mOobeLogicHandler.getState() == 2)
                                {
                                    mMainApplication.setName(Parameters.ID_ROBOT_NAME, message.get
                                        ("message"));
                                }
                                mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                                doNext();
                            }
                        }
                        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
                        {
                            //if (mStateData.get(mOobeLogicHandler.getState()).getRegret() > 0)
                            {
                                Logs.showTrace("[OobeActivity] speech ERR_SPEECH_ERROR_MESSAGE");
                                doNext();
                            }/*
                            else
                            {
                                //### jump to Normal mode
                                Logs.showTrace("[OobeActivity] Speech regret " + "NOT --> Jump to normal "
                                        + "mode");
                                
                                startMainActivity();
                            }*/
                        }
                        
                        break;
                    
                    case OobeLogicParameters.METHOD_STREAM:
                        
                        break;
                    
                    
                }
                
                
                break;
            case DisplayParameters.CLASS_DISPLAY:
                
                
                break;
            
            case OobeParameters.RUNNABLE_HARDWARE_CHECK:
                switch (msg.arg1)
                {
                    case OobeParameters.METHOD_TIME_OUT:
                        
                        if (mStateData.get(mOobeLogicHandler.getState()).getRegret() > 0)
                        {
                            Logs.showTrace("[OobeActivity] hardware check " + "time out and regard once");
                            doNext();
                        }
                        else
                        {
                            Logs.showTrace("[OobeActivity] hardware check " + "time out!! Jump to Normal "
                                + "Mode");
                            startMainActivity();
                        }
                        
                        break;
                    case OobeParameters.METHOD_SENSOR_DETECT:
                        
                        //###
                        //add Tracker sensor detect
                        try
                        {
                            HashMap<String, Object> trackHashMap = new HashMap<>();
                            
                            HashMap<String, Object> sensorHashMap = new HashMap<>();
                            sensorHashMap.put("TYPE", sensorGetEvent);
                            sensorHashMap.put("Value", "");
                            
                            trackHashMap.put("Sensor", sensorHashMap);
                            trackHashMap.put("Scene", String.valueOf(mOobeLogicHandler.getState()));
                            
                            mOobeTracker.tracker(trackHashMap);
                        }
                        catch (Exception e)
                        {
                            Logs.showError("[OobeActivity] log tracker data ERROR: " + e.toString());
                        }
                        
                        
                        mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                        doNext();
                        
                        break;
                    case OobeParameters.METHOD_RFID_DETECT:
                        
                        //###
                        //add Tracker RFID detect
                        
                        try
                        {
                            HashMap<String, Object> trackHashMap = new HashMap<>();
                            HashMap<String, Object> sensorHashMap = new HashMap<>();
                            sensorHashMap.put("TYPE", "rfid");
                            sensorHashMap.put("Value", message.get("rfidCode"));
                            
                            trackHashMap.put("Sensor", sensorHashMap);
                            trackHashMap.put("Scene", String.valueOf(mOobeLogicHandler.getState()));
                            
                            mOobeTracker.tracker(trackHashMap);
                        }
                        catch (Exception e)
                        {
                            Logs.showError("[OobeActivity] log tracker data ERROR: " + e.toString());
                        }
                        
                        
                        // ### maybe happened error
                        //
                        
                        mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                        doNext();
                        break;
                    
                    
                }
                
                break;
            
            default:
                
                break;
            
        }
    }
    
    @Override
    protected void onStop()
    {
        super.onStop();
        
        MainApplication mainApp = (MainApplication) getApplication();
        mainApp.stopTTS();
    }
    
    @Override
    protected void onDestroy()
    {
        Logs.showTrace("[OobeActivity] onDestroy");
        
        if (null != mOobeLogicHandler)
        {
            mOobeLogicHandler.endAll();
            mOobeLogicHandler.killAll();
            mOobeLogicHandler = null;
        }
        
        if (null != mOobeDisplayHandler)
        {
            mOobeDisplayHandler.killAll();
            mOobeDisplayHandler = null;
        }
        if (null != mVideoView)
        {
            mVideoView.stopPlayback();
            mVideoView = null;
        }
        
        super.onDestroy();
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oobe);
        
        // 註冊 Sensor 感應 From Application
        MainApplication mAPP = (MainApplication) getApplication();
        mAPP.setCockpitSensorEventListener(this);
        
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View
            .SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        
        // This work only for android 4.4+
        
        getWindow().getDecorView().setSystemUiVisibility(flags);
        
        // Code below is to handle presses of Volume up or Volume down.
        // Without this, after pressing volume buttons, the navigation bar will
        // show up and won't hide
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
        init();
        
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        //start 腳本
        if (mStateData.size() > 0)
        {
            mOobeLogicHandler.setState(OobeLogicParameters.DEFAULT_STATE);
            doNext();
        }
    }
    
    public String replaceScriptNameText(String nameText)
    {
        String newText = "";
        if (!nameText.isEmpty())
        {
            newText = nameText.replace("我叫做", "");
            newText = newText.replace("我是", "");
            newText = newText.replace("我的名字是", "");
        }
        return newText;
    }
    
    public String replaceScriptText(String text)
    {
        String newText = "";
        String childName = mMainApplication.getName(Parameters.ID_CHILD_NAME);
        String robotName = mMainApplication.getName(Parameters.ID_ROBOT_NAME);
        
        if (robotName.length() == 0)
        {
            mMainApplication.setName(Parameters.ID_ROBOT_NAME, Parameters.STRING_DEFAULT_ROBOT_NAME);
            robotName = Parameters.STRING_DEFAULT_ROBOT_NAME;
        }
        
        newText = text.replace("oo", childName);
        newText = newText.replace("xx", robotName);
        
        return newText;
    }
    
    
    public void doNext()
    {
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (null != mOobeLogicHandler)
                {
                    if (mOobeLogicHandler.getState() < mStateData.size())
                    {
                        String state = String.valueOf(mStateData.get(mOobeLogicHandler.getState()).state);
                        
                        String tts = "";
                        
                        if (mStateData.get(mOobeLogicHandler.getState()).isRegretOnce())
                        {
                            tts = mStateData.get(mOobeLogicHandler.getState()).getRegretTTS();
                            
                            if (tts.isEmpty())
                            {
                                Logs.showTrace("[OobeActivity] state TTS: " + mStateData.get
                                    (mOobeLogicHandler.getState()).getStateTTS());
                                tts = replaceScriptText(mStateData.get(mOobeLogicHandler.getState())
                                    .getStateTTS());
                            }
                            else
                            {
                                Logs.showTrace("[OobeActivity] regret TTS: " + tts);
                            }
                        }
                        else
                        {
                            tts = replaceScriptText(mStateData.get(mOobeLogicHandler.getState())
                                .getStateTTS());
                        }
                        
                        String displayJson = mStateData.get(mOobeLogicHandler.getState())
                            .getDisplayJsonString();
                        Logs.showTrace("[OobeActivity] doNext: tts:" + tts);
                        if (!tts.isEmpty())
                        {
                            mOobeLogicHandler.ttsService(state, tts, "zh");
                        }
                        
                        //###
                        // add robot face and speaker output
                        HashMap<String, Object> trackHashMap = new HashMap<>();
                        
                        HashMap<String, Object> faceTrackHashMap = new HashMap<>();
                        HashMap<String, Object> ttsTrackHashMap = new HashMap<>();
                        HashMap<String, Object> mediaTrackHashMap = new HashMap<>();
                        
                        trackHashMap.put("Scene", String.valueOf(mOobeLogicHandler.getState()));
                        
                        //robot face
                        
                        faceTrackHashMap.put("File", mStateData.get(mOobeLogicHandler.getState()).imageFile);
                        trackHashMap.put("RobotFace", faceTrackHashMap);
                        
                        
                        if (!tts.isEmpty())
                        {
                            ttsTrackHashMap.put("Text", tts);
                            ttsTrackHashMap.put("Pitch", "1");
                            ttsTrackHashMap.put("Speed", "1");
                        }
                        
                        
                        if (null != displayJson)
                        {
                            try
                            {
                                mOobeDisplayHandler.setDisplayJson(new JSONObject(displayJson));
                                mOobeDisplayHandler.startDisplay();
                            }
                            catch (JSONException e)
                            {
                                Logs.showTrace("[OobeActivity] doNext " + "JSONException: " + e.toString());
                            }
                        }
                        else
                        {
                            
                            String movieFileName = mStateData.get(mOobeLogicHandler.getState()).movie;
                            if (!movieFileName.isEmpty())
                            {
                                if (null != mVideoView)
                                {
                                    try
                                    {
                                        mediaTrackHashMap.put("Type", "local");
                                        mediaTrackHashMap.put("URL", "");
                                        mediaTrackHashMap.put("Local", "oobe_movie");
                                        
                                        
                                        mOobeDisplayHandler.setImageViewImageFromDrawable(R.drawable.noeye);
                                        mVideoView.setVisibility(View.VISIBLE);
                                        
                                        Uri video = Uri.parse("android" + ".resource://" + getPackageName()
                                            + "/" + R.raw.oobe_movie);
                                        
                                        mVideoView.setVideoURI(video);
                                        
                                        mVideoView.start();
                                        
                                        mVideoView.setOnCompletionListener(new MediaPlayer
                                            .OnCompletionListener()
                                        {
                                            @Override
                                            public void onCompletion(MediaPlayer mediaPlayer)
                                            {
                                                Logs.showTrace("[OobeActivity]### video is complete!!");
                                                
                                                // run next step
                                                mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                                                doNext();
                                            }
                                        });
                                    }
                                    catch (Exception e)
                                    {
                                        Logs.showError("[OobeActivity] " + "playing video ERROR" + e
                                            .toString());
                                    }
                                }
                            }
                        }
                        
                        
                        //###
                        // add tracker speaker
                        HashMap<String, Object> speakerTrackHashMap = new HashMap<>();
                        
                        if (!ttsTrackHashMap.isEmpty() && !mediaTrackHashMap.isEmpty())
                        {
                            speakerTrackHashMap.put("Type", "both");
                            speakerTrackHashMap.put("TTS", ttsTrackHashMap);
                            speakerTrackHashMap.put("Media", mediaTrackHashMap);
                        }
                        else if (!ttsTrackHashMap.isEmpty())
                        {
                            speakerTrackHashMap.put("Type", "tts");
                            speakerTrackHashMap.put("TTS", ttsTrackHashMap);
                        }
                        else if (!mediaTrackHashMap.isEmpty())
                        {
                            speakerTrackHashMap.put("Type", "media");
                            speakerTrackHashMap.put("Media", mediaTrackHashMap);
                        }
                        
                        trackHashMap.put("Speaker", speakerTrackHashMap);
                        
                        
                        mOobeTracker.tracker(trackHashMap);
                        
                        
                    }
                    
                    else
                    {
                        Logs.showTrace("[OobeActivity] end OobeActivity jump " + "to mainActivity");
                        startMainActivity();
                    }
                }
                
            }
        }, 1000);
    }
    
    public void init()
    {
        mOobeDisplayHandler = new OobeDisplayHandler(this);
        mOobeDisplayHandler.setHandler(mHandler);
        
        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.oobe_relative_layout);
        ImageView mImageView = (ImageView) findViewById(R.id.oobe_image_view);
        
        HashMap<Integer, View> hashMapViews = new HashMap<>();
        
        hashMapViews.put(DisplayParameters.RELATIVE_LAYOUT_ID, mRelativeLayout);
        hashMapViews.put(DisplayParameters.IMAGE_VIEW_ID, mImageView);
        
        mOobeDisplayHandler.setDisplayView(hashMapViews);
        mOobeDisplayHandler.init();
        
        mOobeDisplayHandler.resetAllDisplayViews();
        
        mOobeLogicHandler = new OobeLogicHandler(this);
        mOobeLogicHandler.setHandler(mHandler);
        mOobeLogicHandler.init();
        mOobeLogicHandler.bindListenersToMainApplication();
        
        mStateData = getLogicData(OobeParameters.LOGIC_OOBE);
        
        mMainApplication = (MainApplication) this.getApplication();
        
        mVideoView = (VideoView) findViewById(R.id.oobe_video_view);
        
        mOobeTracker = new OobeTracker(this);
    }
    
    
    @Override
    public void onBackPressed()
    {
        finish();
    }
    
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
            .SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
            .SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
            .SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    
    
    private void startMainActivity()
    {
        Toast.makeText(this.getApplicationContext(), "即將結束OOBE模式，跳轉至一般模式", Toast.LENGTH_LONG).show();
        
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.setClass(OobeActivity.this, MainActivity.class);
                startActivity(startMain);
                finish();
            }
        }, 3000);
        
    }
    
    private ArrayList<OobeLogicElement> getLogicData(String data)
    {
        ArrayList<OobeLogicElement> logicArrayList = new ArrayList<>();
        try
        {
            JSONObject tmp = new JSONObject(data);
            if (tmp.has("stateArray"))
            {
                JSONArray jsonArray = tmp.getJSONArray("stateArray");
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    
                    int state = jsonData.getInt("state");
                    String tts = jsonData.getString("tts");
                    String png = "";
                    if (jsonData.has("png"))
                    {
                        png = jsonData.getString("png");
                    }
                    String resp = "";
                    if (jsonData.has("response"))
                    {
                        resp = jsonData.getString("response");
                    }
                    int wait = 0;
                    if (jsonData.has("wait"))
                    {
                        wait = jsonData.getInt("wait");
                    }
                    String movie = "";
                    if (jsonData.has("movie"))
                    {
                        movie = jsonData.getString("movie");
                    }
                    int regretTime = 3;
                    if (jsonData.has("regretTime"))
                    {
                        regretTime = jsonData.getInt("regretTime");
                    }
                    OobeLogicElement element = new OobeLogicElement(state, wait, regretTime, tts, png,
                        resp, movie);
                    if (jsonData.has("regretTTS"))
                    {
                        element.setRegretTTS(jsonData.getJSONArray("regretTTS"));
                    }
                    
                    logicArrayList.add(element);
                }
            }
            
            
        }
        catch (JSONException e)
        {
            Logs.showError("[OobeActivity] ERROR in getLogicData " + e.toString());
        }
        //debugging using
        for (int i = 0; i < logicArrayList.size(); i++)
        {
            logicArrayList.get(i).print();
        }
        
        return logicArrayList;
    }
    
    
    private String getResponseLogic(String textID)
    {
        Logs.showTrace("[oobeActivity] getResponseLogic textID: " + textID);
        for (int i = 0; i < mStateData.size(); i++)
        {
            Logs.showTrace("[oobeActivity] getResponseLogic mStateData.get(i)" + ".state : " + String
                .valueOf(mStateData.get(i).state));
            if (String.valueOf(mStateData.get(i).state).equals(textID))
            {
                Logs.showTrace("[oobeActivity] response: " + mStateData.get(i).response);
                return mStateData.get(i).response;
            }
            
        }
        return null;
    }
    
    private boolean isNoAIJudge(String text)
    {
        for (int i = 0; i < OobeParameters.NO_THESAURUS.length; i++)
        {
            if (text.contains(OobeParameters.NO_THESAURUS[i]))
            {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void onShakeHands(Object sender)
    {
        Logs.showTrace("[OobeActivity] onShakeHands 握手");
        
        sensorGet = true;
        sensorGetEvent = "shake_hand";
    }
    
    @Override
    public void onClapHands(Object sender)
    {
        Logs.showTrace("[OobeActivity] onClapHands 拍手");
        sensorGetEvent = "clap_hand";
        sensorGet = true;
        
    }
    
    @Override
    public void onPinchCheeks(Object sender)
    {
        Logs.showTrace("[OobeActivity] onPinchCheeks 擠壓");
        sensorGetEvent = "squeeze";
        sensorGet = true;
        
    }
    
    @Override
    public void onPatHead(Object sender)
    {
        Logs.showTrace("[OobeActivity] onPatHead 拍頭");
        sensorGetEvent = "pat_head";
        sensorGet = true;
        
    }
    
    @Override
    public void onScannedRfid(Object sensor, String scannedResult)
    {
        Logs.showTrace("[OobeActivity] onScanned RFID : scannedResult: " + String.valueOf(scannedResult));
        
        rfidString = scannedResult;
    }
    
    
    private class HardwareCheckRunnable implements Runnable
    {
        private int TAG_SENSOR = 1;
        private int TAG_RFID = 2;
        private int sensorDetect = 0;
        private int rfidDetect = 0;
        private int countMax = 0;
        private int i = 0;
        
        public HardwareCheckRunnable(int tag, int time)
        {
            if (TAG_RFID == tag)
            {
                rfidDetect = 1;
            }
            else if (TAG_SENSOR == tag)
            {
                sensorDetect = 1;
            }
            countMax = time / OobeParameters.CHECK_TIME;
        }
        
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    i++;
                    if (i >= countMax)
                    {
                        //###
                        // can send message to let know no response
                        Message msg = new Message();
                        msg.what = OobeParameters.RUNNABLE_HARDWARE_CHECK;
                        msg.arg1 = OobeParameters.METHOD_TIME_OUT;
                        
                        mHandler.sendMessage(msg);
                        
                        
                        break;
                    }
                    else
                    {
                        Logs.showTrace("[HardwareCheckRunnable] check " + "hardware again!");
                        if (rfidDetect == 1)
                        {
                            if (null != rfidString)
                            {
                                if (rfidString.length() == 0)
                                {
                                    Logs.showTrace("[HardwareCheckRunnable] " + "no RFID data");
                                }
                                else
                                {
                                    Logs.showTrace("[HardwareCheckRunnable] " + "Get RFID String:" +
                                        rfidString);
                                    //###
                                    // can send message to let know have
                                    // response
                                    
                                    if (rfidString.equals(OobeParameters.ID_RFID_STRIPED_FISH))
                                    {
                                        Message msg = new Message();
                                        msg.what = OobeParameters.RUNNABLE_HARDWARE_CHECK;
                                        msg.arg1 = OobeParameters.METHOD_RFID_DETECT;
                                        
                                        HashMap<String, String> message = new HashMap<>();
                                        message.put("rfidCode", rfidString);
                                        msg.obj = message;
                                        
                                        mHandler.sendMessage(msg);
                                        break;
                                    }
                                    
                                }
                            }
                        }
                        else if (sensorDetect == 1)
                        {
                            if (sensorGet)
                            {
                                //###
                                // can send message to let know have response
                                Logs.showTrace("[HardwareCheckRunnable] " + "sensorDetect: true");
                                
                                Message msg = new Message();
                                msg.what = OobeParameters.RUNNABLE_HARDWARE_CHECK;
                                msg.arg1 = OobeParameters.METHOD_SENSOR_DETECT;
                                
                                mHandler.sendMessage(msg);
                                break;
                                
                            }
                            else
                            {
                                Logs.showTrace("[HardwareCheckRunnable] " + "sensorDetect: false");
                                
                            }
                            
                            
                        }
                    }
                    
                    Thread.sleep(OobeParameters.CHECK_TIME);
                }
            }
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
        }
    }
    
    private static class InClassHandler extends Handler
    {
        private final WeakReference<OobeActivity> mWeakSelf;
        
        public InClassHandler(OobeActivity a)
        {
            mWeakSelf = new WeakReference<OobeActivity>(a);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            OobeActivity activity = mWeakSelf.get();
            
            if (activity != null)
            {
                Logs.showTrace("[OobeActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String
                    .valueOf(msg.what) + " " + "From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
                activity.handleMessages(msg);
            }
            
        }
    }
    
}
