package com.iii.more.oobe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.iii.more.main.MainActivity;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;
import com.iii.more.oobe.logic.OobeLogicElement;
import com.iii.more.oobe.logic.OobeLogicHandler;
import com.iii.more.oobe.logic.OobeLogicParameters;
import com.iii.more.oobe.logic.OobeTTSParameters;
import com.iii.more.oobe.view.OobeDisplayHandler;
import com.iii.more.oobe.view.OobeDisplayParameters;
import com.iii.more.screen.view.display.DisplayParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/10/27.
 */

public class OobeActivity extends AppCompatActivity
{
    private OobeLogicHandler mOobeLogicHandler = null;
    private OobeDisplayHandler mOobeDisplayHandler = null;
    private ArrayList<OobeLogicElement> mStateData = null;
    private MainApplication mMainApplication = null;
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[OobeActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    public void handleMessages(Message msg)
    {
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
                                case "RFID":
                                    // ### set a timer to get
                                    
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
                            if (mOobeLogicHandler.getState() == 0)
                            {
                                mMainApplication.setName(Parameters.ID_CHILD_NAME, message.get("message"));
                                
                                
                            }
                            else if (mOobeLogicHandler.getState() == 1)
                            {
                                mMainApplication.setName(Parameters.ID_ROBOT_NAME, message.get("message"));
                                
                            }
                            mOobeLogicHandler.setState(mOobeLogicHandler.getState() + 1);
                            doNext();
                            
                            
                        }
                        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
                        {
                            if (mStateData.get(mOobeLogicHandler.getState()).getRegret() > 0)
                            {
                                Logs.showTrace("[OobeActivity] speech regret once");
                                doNext();
                            }
                            else
                            {
                                //### jump to Normal mode
                                Logs.showTrace("[OobeActivity] speech regret NOT --> go to normal mode");
                            }
                        }
                        
                        
                        break;
                    
                    case OobeLogicParameters.METHOD_STREAM:
                        
                        break;
                    
                    
                }
                
                
                break;
            case DisplayParameters.CLASS_DISPLAY:
                
                
                break;
            
            
            default:
                
                break;
            
        }
    }
    
    @Override
    protected void onDestroy()
    {
        mOobeLogicHandler.endAll();
        mOobeLogicHandler.killAll();
        
        mOobeDisplayHandler.killAll();
        super.onDestroy();
    }
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oobe);
        
        
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        
        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            
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
        }
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
    
    public String replaceScriptText(String text)
    {
        String newText = "";
        String childName = mMainApplication.getName(Parameters.ID_CHILD_NAME);
        String robotName = mMainApplication.getName(Parameters.ID_ROBOT_NAME);
        
        if (null != childName && null != robotName)
        {
            newText = text.replace("oo", childName);
            newText = newText.replace("xx", robotName);
        }
        else
        {
            if (null != childName)
            {
                newText = text.replace("oo", childName);
            }
            
            if (null != robotName)
            {
                newText = text.replace("xx", robotName);
            }
            else
            {
                newText = text.replace("xx", Parameters.STRING_DEFAULT_ROBOT_NAME);
            }
        }
        return newText;
    }
    
    
    public void doNext()
    {
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (mOobeLogicHandler.getState() <= mStateData.size())
                {
                    String state = String.valueOf(mStateData.get(mOobeLogicHandler.getState()).state);
                    String tts = replaceScriptText(mStateData.get(mOobeLogicHandler.getState()).tts);
                    String displayJson = mStateData.get(mOobeLogicHandler.getState()).getDisplayJsonString();
                    Logs.showTrace("[OobeActivity] doNext: tts:" + tts);
                    mOobeLogicHandler.ttsService(state, tts, "zh");
                    
                    if (null != displayJson)
                    {
                        try
                        {
                            mOobeDisplayHandler.setDisplayJson(new JSONObject(displayJson));
                            mOobeDisplayHandler.startDisplay();
                        }
                        catch (JSONException e)
                        {
                            Logs.showTrace("[OobeActivity] doNext JSONException: " + e.toString());
                        }
                    }
                    else
                    {
                        mOobeDisplayHandler.setVideoViewVisibility(true);
                        mOobeDisplayHandler.setImageViewVisibility(false);
                        String movieFileName = mStateData.get(mOobeLogicHandler.getState()).movie;
                        if (!movieFileName.isEmpty())
                        {
                            mOobeLogicHandler.playStreaming(OobeParameters.TEST_VIDEO_HOST_URL, movieFileName);
                        }
                    }
                }
                
            }
        }, 2000);
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
        mOobeLogicHandler.setVideoSurfaceHolder(mOobeDisplayHandler.setMediaDisplayView(R.id.oobe_video_surface_view));
        mOobeLogicHandler.init();
        
        mStateData = getLogicData(OobeParameters.LOGIC_OOBE);
        
        mMainApplication = (MainApplication) this.getApplication();
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    
    
    private void startMainActivity()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.setClass(OobeActivity.this, MainActivity.class);
        startActivity(startMain);
        finish();
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
                    logicArrayList.add(new OobeLogicElement(state, wait, tts, png, resp, movie));
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
            Logs.showTrace("[oobeActivity] getResponseLogic mStateData.get(i).state : " + String.valueOf(mStateData.get(i).state));
            if (String.valueOf(mStateData.get(i).state).equals(textID))
            {
                Logs.showTrace("[oobeActivity] response: " + mStateData.get(i).response);
                return mStateData.get(i).response;
            }
            
        }
        return null;
    }
    
    
}
