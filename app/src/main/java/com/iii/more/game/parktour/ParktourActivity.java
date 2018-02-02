package com.iii.more.game.parktour;

import android.app.Activity;
import android.os.Bundle;

import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.R;
import com.iii.more.main.listeners.TTSEventListener;

import sdk.ideas.common.Logs;

public class ParktourActivity extends Activity
{
    private MainApplication application = null;
    private RobotHead robotHead = null;
    public static TrackerHandler trackerHandler = null;
    private FaceView faceView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utility.fullScreenNoBar(this);
        faceView = new FaceView(this);
        setContentView(faceView);
        faceView.setBackgroundResource(R.drawable.iii_zoo_101);
        application = (MainApplication) getApplication();
        registerService();
        scenarize(Scenarize.SCEN_START_ZOO, null);
    }
    
    private void registerService()
    {
        application.setTTSEventListener(new TTSEventListener()
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
                Logs.showTrace("[ParktourActivity] onUtteranceDone utteranceId: " + utteranceId);
            }
        });
    }
    
    private void scenarize(int nIndex, Object object)
    {
        switch (nIndex)
        {
            case Scenarize.SCEN_START_ZOO:
                application.playTTS("今天是欣欣動物園年度園遊會，沿路都可以看到大家開心的來參加，所有的動物也都來了，讓我們來看看今天會遇到誰呢?走吧!",
                    String.valueOf(nIndex));
                break;
        }
    }
}
