package com.iii.more.game.parktour;

import android.app.Activity;
import android.media.MediaPlayer;
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
    private int mnScenarize = Scenarize.SCEN_START_ZOO;
    private static ParktourActivity theActivity = null;
    private MediaPlayer mp = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        theActivity = this;
        Utility.fullScreenNoBar(this);
        faceView = new FaceView(this);
        setContentView(faceView);
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
                int nIndex = Integer.valueOf(utteranceId);
                switch (nIndex)
                {
                    case Scenarize.SCEN_START_ZOO:
                        theActivity.scenarize(Scenarize.SCEN_LION_STAY, null);
                        break;
                    case Scenarize.SCEN_LION_STAY:
                        theActivity.scenarize(Scenarize.SCEN_LION_HO, null);
                        break;
                }
            }
        });
    }
    
    private void scenarize(int nIndex, Object object)
    {
        mnScenarize = nIndex;
        Logs.showTrace("[ParktourActivity] scenarize index: " + mnScenarize);
        switch (mnScenarize)
        {
            case Scenarize.SCEN_START_ZOO:
                faceView.setBackgroundResource(R.drawable.iii_zoo_101);
                application.playTTS("今天是欣欣動物園年度園遊會，沿路都可以看到大家開心的來參加，所有的動物也都來了，讓我們來看看今天會遇到誰呢，走吧",
                    String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_LION_STAY:
                faceView.setBackgroundResource(R.drawable.iii_lion_102);
                application.playTTS("哇，來到了非洲動物區，獅子在舉行吼叫大賽，看誰可以做出最生氣的表情，比獅子兇就贏了，快來試試看，做出你最生氣的表情",
                    String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_LION_HO:
                faceView.loadImage(R.drawable.iii_lion_ho_102);
                managerOfSound(R.raw.iii_lion_ho);
                break;
            case Scenarize.SCEN_LION_GO:
                faceView.loadImage(R.drawable.iii_lion_102);
                application.playTTS("太好了，獅子都下跑了，讓我們繼續看看，有什麼好玩的吧，走", String.valueOf(mnScenarize));
                break;
        }
    }
    
    protected void managerOfSound(final int nResId)
    {
        if (mp != null)
        {
            mp.reset();
            mp.release();
        }
        
        mp = MediaPlayer.create(this, nResId);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                Logs.showTrace("[ParktourActivity] managerOfSound onCompletion...");
                theActivity.scenarize(Scenarize.SCEN_LION_GO, null);
            }
        });
        mp.start();
    }
}
