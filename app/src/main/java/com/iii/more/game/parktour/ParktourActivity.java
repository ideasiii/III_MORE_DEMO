package com.iii.more.game.parktour;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

import com.iii.more.emotion.EmotionParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;
import com.iii.more.game.module.EmotionBar;
import com.iii.more.game.module.RobotHead;
import com.iii.more.game.module.TrackerHandler;
import com.iii.more.game.module.Utility;
import com.iii.more.main.MainApplication;
import com.iii.more.main.R;
import com.iii.more.main.listeners.FaceEmotionEventListener;
import com.iii.more.main.listeners.TTSEventListener;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

public class ParktourActivity extends Activity
{
    private MainApplication application = null;
    public static TrackerHandler trackerHandler = null;
    private FaceView faceView = null;
    private int mnScenarize = Scenarize.SCEN_START_ZOO;
    private static ParktourActivity theActivity = null;
    private MediaPlayer mp = null;
    private EmotionBar emotionBar = null;
    private VoiceRecognition mVoiceRecognition = null;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        theActivity = this;
        Utility.fullScreenNoBar(this);
        faceView = new FaceView(this);
        setContentView(faceView);
        application = (MainApplication) getApplication();
        application.stopFaceEmotion();
        application.startFaceEmotion();
        scenarize(Scenarize.SCEN_START_ZOO, null);
        
        //========= Emotion Bar ============//
        emotionBar = new EmotionBar(this);
        RelativeLayout.LayoutParams lpEmotionBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpEmotionBar.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lpEmotionBar.setMargins(0, 0, 0, 150);
        emotionBar.setLayoutParams(lpEmotionBar);
        faceView.addView(emotionBar);
        emotionBar.setVisibility(View.INVISIBLE);
        
        //========= TTS Pitch ===========//
        application.setTTSPitch(1.0f, 1.0f);
        
        //========= Google Speech =========//
        mVoiceRecognition = new VoiceRecognition(this);
        
        registerService();
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
            
            //=========== TTS 講完幹話後 =============//
            @Override
            public void onUtteranceDone(String utteranceId)
            {
                Logs.showTrace("[ParktourActivity] onUtteranceDone utteranceId: " + utteranceId);
                int nIndex = Integer.valueOf(utteranceId);
                switch (nIndex)
                {
                    case Scenarize.SCEN_START_ZOO:
                        mVoiceRecognition.startListen(String.valueOf(nIndex));
                        break;
                    case Scenarize.SCEN_PARKTOUR_GO:
                        theActivity.scenarize(Scenarize.SCEN_LION_STAY, null);
                        break;
                    case Scenarize.SCEN_LION_STAY:
                        theActivity.scenarize(Scenarize.SCEN_LION_HO, null);
                        break;
                    case Scenarize.SCEN_LION_ANGRY_AGAIN_1:
                        theActivity.scenarize(Scenarize.SCEN_LION_ANGRY_AGAIN_2, null);
                        break;
                    case Scenarize.SCEN_LION_ANGRY_AGAIN_2:
                        theActivity.scenarize(Scenarize.SCEN_LION_ANGRY_AGAIN_3, null);
                        break;
                    case Scenarize.SCEN_LION_GO:
                        emotionBar.setVisibility(View.INVISIBLE);
                        theActivity.scenarize(Scenarize.SCEN_MONKEY_SEE, null);
                        break;
                    case Scenarize.SCEN_MONKEY_SEE:
                        theActivity.scenarize(Scenarize.SCEN_MONKEY_FUNNY, null);
                        break;
                    case Scenarize.SCEN_MONKEY_GO:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_1, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_1:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_2, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_3:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_4, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_4:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_5, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_5:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_6, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_6:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_7, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_7:
                        mVoiceRecognition.startListen(String.valueOf(nIndex));
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_8:
                    case Scenarize.SCEN_ANIMAL_RACE_9:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_7, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_10:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_11, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_11:
                        managerOfSound(R.raw.iii_animal_race_gun);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_12:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_13, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_13:
                        theActivity.scenarize(Scenarize.SCEN_END_PHOTO_1, null);
                        break;
                }
            }
        });
        
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        mVoiceRecognition.setHandler(selfHandler);
    }
    
    private void scenarize(int nIndex, Object object)
    {
        mnScenarize = nIndex;
        Logs.showTrace("[ParktourActivity] scenarize index: " + mnScenarize);
        switch (mnScenarize)
        {
            case Scenarize.SCEN_START_ZOO:
                faceView.loadImage(R.drawable.iii_zoo_101);
                application.playTTS("今天是動物園一年一度的園遊會，沿路都可以看到大家開心的來參加，所有的動物也都來了，要不要一起去動物園看看呀", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_PARKTOUR_GO:
                application.playTTS("那我們出發吧", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_LION_STAY:
                faceView.loadImage(R.drawable.iii_lion_ani);
                application.playTTS("哇！來到了非洲動物區！獅子在舉行吼叫大賽，看誰可以做出最生氣的表情，比獅子兇就贏了,準備開始囉,3,2,1,開始", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_LION_HO:
                application.setFaceEmotionEventListener(faceEmotionEventListener);
                emotionBar.setColor(252, 42, 29, 253, 183, 175);
                emotionBar.setIcon(R.drawable.iii_face_joy, R.drawable.iii_face_angry);
                emotionBar.setVisibility(View.VISIBLE);
                emotionBar.setPosition(0);
                managerOfSound(R.raw.lion_sound_effect);
                break;
            case Scenarize.SCEN_LION_ANGRY_AGAIN_1:
                application.playTTS("不夠兇餒,快再試試看做出你最生氣的表情", String.valueOf(mnScenarize));
                emotionBar.setPosition(10);
                break;
            case Scenarize.SCEN_LION_ANGRY_AGAIN_2:
                application.playTTS("試試看，做出你最生氣的表情", String.valueOf(mnScenarize));
                emotionBar.setPosition(20);
                break;
            case Scenarize.SCEN_LION_ANGRY_AGAIN_3:
                application.playTTS("還不夠,還是沒有比獅子兇耶,再試著生氣一點", String.valueOf(mnScenarize));
                emotionBar.setPosition(30);
                break;
            case Scenarize.SCEN_LION_GO:
                application.setFaceEmotionEventListener(null);
                faceView.loadImage(R.drawable.iii_lion_102);
                application.playTTS("太好了，獅子都下跑了，讓我們繼續看看，有什麼好玩的吧，走", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_MONKEY_SEE:
                faceView.loadImage(R.drawable.iii_monkey_103);
                application.playTTS("耶~~那裏有一隻猴子，原來我們來到了台灣動物區，我們去看看他在做什麼吧,原來猴子們正在舉辦不能笑比賽，被牠逗笑就輸囉," + "那我們要小心,忍住不要笑喔,要忍住喔", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_MONKEY_FUNNY:
                application.setFaceEmotionEventListener(faceEmotionEventListener);
                emotionBar.setColor(27, 80, 132, 165, 222, 249);
                emotionBar.setIcon(R.drawable.iii_face_sad, R.drawable.iii_face_cry);
                emotionBar.setVisibility(View.VISIBLE);
                emotionBar.setPosition(0);
                faceView.loadImage(R.drawable.iii_monkey_ani_1);
                managerOfSound(R.raw.monkey_sound_effect);
                break;
            case Scenarize.SCEN_MONKEY_GO:
                application.setFaceEmotionEventListener(null);
                emotionBar.setPosition(10);
                faceView.loadImage(R.drawable.iii_monkey_103);
                application.playTTS("你好厲害喔，都沒有笑出來耶，那我們再繼續去探險吧", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_1:
                emotionBar.setVisibility(View.INVISIBLE);
                emotionBar.setPosition(0);
                faceView.loadImage(R.drawable.iii_animal_race_1);
                application.playTTS("前面圍了好多人，我們去看看怎麼回事", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_2:
                managerOfSound(R.raw.iii_animal_race_oil_add);
                break;
            case Scenarize.SCEN_ANIMAL_RACE_3:
                faceView.loadImage(R.drawable.iii_animal_race_2);
                application.playTTS("聽到了好大聲的加油聲，原來是花豹、黑熊、獅子三種動物在賽跑，最快的人可以獲得食物當獎勵，那我們也一起去幫忙加油吧", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_4:
                faceView.loadImage(R.drawable.iii_animal_race_3_1);
                application.playTTS("我是黑熊，強壯又會爬樹！拿到食物不是問題吧", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_5:
                faceView.loadImage(R.drawable.iii_animal_race_3_2);
                application.playTTS("我是花豹，我的腿很有力，食物一定是我的啦", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_6:
                faceView.loadImage(R.drawable.iii_animal_race_3_3);
                application.playTTS("我是獅子，萬獸之王啊，挖哈哈，食物絕對是我的", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_7:
                faceView.loadImage(R.drawable.iii_animal_race_4);
                application.playTTS("小朋友，那你來猜猜看最快到達的動物是誰", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_8:
                faceView.loadImage(R.drawable.iii_wrong_107);
                application.playTTS("你再想想看,黑熊是一種很會爬樹的動物", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_9:
                faceView.loadImage(R.drawable.iii_wrong_107);
                application.playTTS("成年獅子的體重大概兩百公斤,你覺得他會跑得很快嗎", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_10:
                faceView.loadImage(R.drawable.iii_correct_106);
                application.playTTS("花豹的奔跑時速可以達到每小時80公里，是跑得最快的動物", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_11:
                faceView.loadImage(R.drawable.iii_animal_race_5);
                application.playTTS("那就讓我們看看比賽結果", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_12:
                application.playTTS("槍聲響起，比賽開始了，果然花豹一路領先，得到了第一名呢", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_ANIMAL_RACE_13:
                faceView.loadImage(R.drawable.iii_animal_race_6);
                managerOfSound(R.raw.iii_animal_race_yaya);
                application.playTTS("花豹最先抵達終點囉,也獲得了好多好吃的食物，你看!他吃的多開心", String.valueOf(mnScenarize));
                break;
            case Scenarize.SCEN_END_PHOTO_1:
                break;
        }
    }
    
    //========== 幹 聲音播完了 =============//
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
                switch (mnScenarize)
                {
                    case Scenarize.SCEN_LION_HO:
                        theActivity.scenarize(Scenarize.SCEN_LION_ANGRY_AGAIN_1, null);
                        break;
                    case Scenarize.SCEN_MONKEY_FUNNY:
                        theActivity.scenarize(Scenarize.SCEN_MONKEY_GO, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_2:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_3, null);
                        break;
                    case Scenarize.SCEN_ANIMAL_RACE_11:
                        theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_12, null);
                        break;
                }
            }
        });
        mp.start();
    }
    
    //========= 臉部表情 ===========//
    private FaceEmotionEventListener faceEmotionEventListener = new FaceEmotionEventListener()
    {
        @Override
        public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String, String> ttsEmotionData, HashMap<String, String> imageEmotionData, Object
            extendData)
        {
            Logs.showTrace("[ParktourActivity] onFaceEmotionResult Trig.");
            try
            {
                if (null != faceEmotionData)
                {
                    int nValue = 50;
                    String strEmotionName = faceEmotionData.get(FaceEmotionInterruptParameters.STRING_EMOTION_NAME);
                    String strEmotionValue = faceEmotionData.get(FaceEmotionInterruptParameters.STRING_EMOTION_VALUE);
                    if (null != strEmotionValue)
                    {
                        double d = Double.parseDouble(strEmotionValue);
                        nValue = (int) d;
                    }
                    Logs.showTrace("[ParktourActivity] onFaceEmotionResult EMOTION_NAME:" + strEmotionName + " EMOTION_VALUE: " + strEmotionValue);
                    
                    switch (mnScenarize)
                    {
                        case Scenarize.SCEN_LION_HO:
                        case Scenarize.SCEN_LION_ANGRY_AGAIN_1:
                        case Scenarize.SCEN_LION_ANGRY_AGAIN_2:
                        case Scenarize.SCEN_LION_ANGRY_AGAIN_3:
                            if (null != strEmotionName && 0 == strEmotionName.compareTo(EmotionParameters.STRING_EMOTION_ANGER))
                            {
                                emotionBar.setPosition(75);
                                theActivity.scenarize(Scenarize.SCEN_LION_GO, null);
                            }
                            break;
                        case Scenarize.SCEN_MONKEY_FUNNY:
                            if (null != strEmotionName && 0 == strEmotionName.compareTo(EmotionParameters.STRING_EMOTION_JOY))
                            {
                                emotionBar.setPosition(nValue / 2);
                            }
                            break;
                    }
                }
            }
            catch (Exception e)
            {
                Logs.showError("[FaceEmotionEventHandler] onFaceEmotionResult Exception:" + e.getMessage());
            }
        }
        
        @Override
        public void onFaceDetectResult(boolean isDetectFace)
        {
        
        }
    };
    
    private Handler selfHandler = new ParktourActivity.SelfHandler(this);
    
    private static class SelfHandler extends Handler
    {
        private final WeakReference<ParktourActivity> mWeakSelf;
        
        SelfHandler(ParktourActivity lh)
        {
            mWeakSelf = new WeakReference<>(lh);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            ParktourActivity self = mWeakSelf.get();
            if (null == self)
            {
                return;
            }
            self.handleMessages(msg);
        }
    }
    
    private void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER:
                handleMessageVoiceRecognition(msg);
                break;
            default:
                break;
        }
    }
    
    private void handleMessageVoiceRecognition(Message msg)
    {
        if (null == msg)
        {
            return;
        }
        
        final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (null != message && msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognition.stopListen();
            String strWord = message.get("message");
            Logs.showTrace("[LogicHandler] Get voice Text: " + strWord);
            
            if (null != strWord && !strWord.isEmpty())
            {
                if (null != message.get("sttID"))
                {
                    int nId = Integer.valueOf(message.get("sttID"));
                    switch (nId)
                    {
                        case Scenarize.SCEN_START_ZOO:
                            for (String aNo : Dictionary.no)
                            {
                                if (strWord.contains(aNo))
                                {
                                    Logs.showTrace("[ParktourActivity] handleMessageVoiceRecognition Word: " + strWord + " Contain: " + aNo);
                                    theActivity.scenarize(Scenarize.SCEN_START_ZOO, null);
                                    return;
                                }
                            }
                            
                            for (String aYes : Dictionary.yes)
                            {
                                if (strWord.contains(aYes))
                                {
                                    Logs.showTrace("[ParktourActivity] handleMessageVoiceRecognition Word: " + strWord + " Contain: " + aYes);
                                    theActivity.scenarize(Scenarize.SCEN_PARKTOUR_GO, null);
                                    return;
                                }
                            }
                            theActivity.scenarize(Scenarize.SCEN_START_ZOO, null);
                            break;
                        case Scenarize.SCEN_ANIMAL_RACE_7:
                            for (String aBear : Dictionary.bear)
                            {
                                if (strWord.contains(aBear))
                                {
                                    Logs.showTrace("[ParktourActivity] handleMessageVoiceRecognition Word: " + strWord + " Contain: " + aBear);
                                    theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_8, null);
                                    return;
                                }
                            }
                            
                            for (String aLion : Dictionary.lion)
                            {
                                if (strWord.contains(aLion))
                                {
                                    Logs.showTrace("[ParktourActivity] handleMessageVoiceRecognition Word: " + strWord + " Contain: " + aLion);
                                    theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_9, null);
                                    return;
                                }
                            }
                            
                            for (String aLeopard : Dictionary.leopard)
                            {
                                if (strWord.contains(aLeopard))
                                {
                                    Logs.showTrace("[ParktourActivity] handleMessageVoiceRecognition Word: " + strWord + " Contain: " + aLeopard);
                                    theActivity.scenarize(Scenarize.SCEN_ANIMAL_RACE_10, null);
                                    return;
                                }
                            }
                            break;
                    }
                }
            }
        }
        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
        {
            Logs.showTrace("get ERROR message: " + message.get("message"));
            mVoiceRecognition.stopListen();
            switch (mnScenarize)
            {
                case Scenarize.SCEN_START_ZOO:
                    theActivity.scenarize(Scenarize.SCEN_START_ZOO, null);
                    break;
            }
            
        }
    }
}
