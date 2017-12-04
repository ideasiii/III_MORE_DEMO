package com.iii.more.logic;

import android.content.Context;
import android.os.Message;
import android.support.annotation.NonNull;

import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.listeners.TTSEventListener;
import com.iii.more.main.TTSParameters;
import com.iii.more.stream.WebMediaPlayerHandler;
import com.iii.more.stream.WebMediaPlayerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import android.os.Handler;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

import static com.iii.more.logic.LogicParameters.MODE_FRIEND;
import static com.iii.more.logic.LogicParameters.MODE_GAME;
import static com.iii.more.logic.LogicParameters.MODE_STORY;
import static com.iii.more.logic.LogicParameters.MODE_UNKNOWN;

/**
 * Created by joe on 2017/7/26
 */

public class LogicHandler extends BaseHandler
{
    private JSONObject mActivityJson = null;
    
    
    private VoiceRecognition mVoiceRecognition = null;
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    
    private int mModeNow = MODE_UNKNOWN;
    
    
    private boolean isPauseStoryMode = false;
    private CacheStory mCacheStory = null;
    
    
    public boolean getPauseStoryMode()
    {
        return isPauseStoryMode;
    }
    
    public void setPauseStoryMode(boolean storyMode)
    {
        isPauseStoryMode = storyMode;
    }
    
    
    public int getMode()
    {
        return mModeNow;
        
    }
    
    public void setMode(int mode)
    {
        mModeNow = mode;
    }

    private Handler selfHandler = new SelfHandler(this);

    private void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            
            case WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER:
                handleMessageWebMediaPlayer(msg);
                break;
            case CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER:
                handleMessageVoiceRecognition(msg);
                break;
            
            default:
                break;
        }
    }
    
    private void handleMessageWebMediaPlayer(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case WebMediaPlayerParameters.COMPLETE_PLAY:
                    mWebMediaPlayerHandler.stopPlayMediaStream();
                    // mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    break;
                case WebMediaPlayerParameters.START_PLAY:
                    
                    //callback to MainActivity to start display
                    // mDisplayHandler.startDisplay();
                    
                    break;
                case WebMediaPlayerParameters.STOP_PLAY:
                    break;
                
                case WebMediaPlayerParameters.PAUSE_PLAY:
                    HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                    String strStoryPauseSecond = message.get("message");
                    
                    int storyPauseSecond = 0;
                    try
                    {
                        storyPauseSecond = Integer.valueOf(strStoryPauseSecond);
                        Logs.showTrace("[LogicHandler] story pause Second: " + strStoryPauseSecond);
                    }
                    catch (Exception e)
                    {
                        Logs.showError("[LogicHandler]ERROR storyPauseSecond :" + e.toString());
                    }
                    
                    
                    //###save storyPauseSecond into logicHandler cache
                    mCacheStory = new CacheStory(storyPauseSecond);
                    //###set logicHandler cache is true
                    setPauseStoryMode(true);
                    //###callback to mainActivity to let display know what happened
                    HashMap<String, String> message3 = new HashMap<>();
                    message3.put("message", strStoryPauseSecond);
                    callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC, LogicParameters
                            .METHOD_STORY_PAUSE, message3);
                    break;
                default:
                    break;
            }
        }
        else
        {
            //異常例外處理
            onError(TTSParameters.ID_SERVICE_IO_EXCEPTION);
        }
    }
    
    private void handleMessageVoiceRecognition(Message msg)
    {
        final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode
                .METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognition.stopListen();
            
            Logs.showTrace("[LogicHandler] Get voice Text: " + message.get("message"));
            
            if (!message.get("message").isEmpty())
            {
                //callback mainActivity
                HashMap<String, String> returnMessage = new HashMap<>();
                returnMessage.put("message", message.get("message"));
                callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC, LogicParameters
                        .METHOD_VOICE, returnMessage);
            }
        }
        
        else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //startListen first handle
        }
        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
        {
            Logs.showTrace("get ERROR message: " + message.get("message"));
            mVoiceRecognition.stopListen();
            
            if (message.get("message").equals("No match") || message.get("message").equals("No speech input"))
            {
                //TTS again and listen again
                if (getMode() != LogicParameters.MODE_STORY)
                {
                    onError(TTSParameters.ID_SERVICE_UNKNOWN);
                }
                else
                {
                    // ### wait 3 second to continue
                    selfHandler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            // ### resume story stream
                            setPauseStoryMode(false);
                            
                            // ###callback MainActivity display streaming
                            if (null != mCacheStory)
                            {
                                HashMap<String, String> message4 = new HashMap<String, String>();
                                message4.put("message", String.valueOf(mCacheStory.pauseStorySecond));
                                callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC,
                                        LogicParameters.METHOD_STORY_RESUME, message4);
                                
                                resumeStoryStreaming();
                            }
                        }
                    }, 3000);
                }
            }
        }
        else if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
        {
            onError(TTSParameters.ID_SERVICE_IO_EXCEPTION);
        }
        else
        {
        
        }
    }
    
    public void onError(String index)
    {
        endAll();
        switch (index)
        {
            case TTSParameters.ID_SERVICE_IO_EXCEPTION:
                ttsService(TTSParameters.ID_SERVICE_IO_EXCEPTION, TTSParameters
                        .STRING_SERVICE_IO_EXCEPTION, "zh");
                break;
            default:
                ttsService(TTSParameters.ID_SERVICE_UNKNOWN, TTSParameters.STRING_SERVICE_UNKNOWN, "zh");
                break;
        }
    }
    
    
    public LogicHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        if (null == mVoiceRecognition)
        {
            mVoiceRecognition = new VoiceRecognition(mContext);
            mVoiceRecognition.setHandler(selfHandler);
            mVoiceRecognition.setLocale(Locale.TAIWAN);
        }
        if (null == mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler = new WebMediaPlayerHandler(mContext);
            mWebMediaPlayerHandler.setHandler(selfHandler);
        }

        bindTTSListenersToMainApplication();
    }
    
    
    public void startUpStory(String strTTS, String strID, String lang)
    {
        if (null != strTTS && null != strID && null != lang)
        {
            ttsService(strID, strTTS, lang);
        }
        else
        {
            MainApplication app = (MainApplication) mContext.getApplicationContext();
            ttsService(TTSParameters.ID_SERVICE_STORY_BEGIN, app.getName(Parameters.ID_CHILD_NAME) +
                    TTSParameters.STRING_SERVICE_STORY_BEGIN, "zh");
        }
        
    }
    
    public void startUpFriend()
    {
        mVoiceRecognition.startListen();
    }
    
    
    public void startUp(String ttsID)
    {
        switch (ttsID)
        {
            case TTSParameters.ID_SERVICE_START_UP_GREETINGS_STORY_MODE:
                mModeNow = MODE_STORY;
                ttsService(ttsID, TTSParameters.STRING_SERVICE_START_UP_GREETINGS_STORY_MODE, "zh");
                break;
            case TTSParameters.ID_SERVICE_START_UP_GREETINGS_GAME_MODE:
                mModeNow = MODE_GAME;
                ttsService(ttsID, TTSParameters.STRING_SERVICE_START_UP_GREETINGS_GAME_MODE, "zh");
                break;
            case TTSParameters.ID_SERVICE_START_UP_GREETINGS_FRIEND_MODE:
                mModeNow = MODE_FRIEND;
                ttsService(ttsID, TTSParameters.STRING_SERVICE_START_UP_GREETINGS_FRIEND_MODE, "zh");
                break;
        }
    }
    
    public void setActivityJson(@NonNull JSONObject activityJson)
    {
        mActivityJson = activityJson;
    }
    
    public void startActivity()
    {
        if (null != mActivityJson)
        {
            try
            {
                switch (mActivityJson.getInt(SemanticWordCMPParameters.STRING_JSON_KEY_TYPE))
                {
                    case SemanticWordCMPParameters.TYPE_RESPONSE_UNKNOWN:
                        //callback to mainActivity onERROR
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_LOCAL:
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_HOST) &&
                                mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_FILE))
                        {
                            mWebMediaPlayerHandler.setHostAndFilePath(mActivityJson.getString
                                    (SemanticWordCMPParameters.STRING_JSON_KEY_HOST), mActivityJson
                                    .getString(SemanticWordCMPParameters.STRING_JSON_KEY_FILE));
                            mWebMediaPlayerHandler.startPlayMediaStream();
                            
                        }
                        else
                        {
                            Logs.showError("[LogicHandler] ERROR while read Local Host OR File");
                        }
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_SPOTIFY:
                        break;
                    case SemanticWordCMPParameters.TYPE_RESPONSE_TTS:
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_LANG) &&
                                mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_TTS))
                        {
                            ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, mActivityJson.getString
                                    (SemanticWordCMPParameters.STRING_JSON_KEY_TTS), mActivityJson
                                    .getString(SemanticWordCMPParameters.STRING_JSON_KEY_LANG));
                        }
                        else
                        {
                            Logs.showError("[LogicHandler] ERROR while read TTS lang OR tts");
                        }
                        break;
                }
            }
            catch (JSONException e)
            {
                Logs.showError("[LogicHandler] ERROR:" + e.toString());
            }
        }
    }
    
    public void resumeStoryStreaming()
    {
        Logs.showTrace("[LogicHandler] pauseStoryStreaming");
        if (getMode() == LogicParameters.MODE_STORY && null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.resumePlayMediaStream();
        }
    }
    
    public void pauseStoryStreaming()
    {
        Logs.showTrace("[LogicHandler] pauseStoryStreaming");
        if (getMode() == LogicParameters.MODE_STORY && null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.pausePlayMediaStream();
        }
    }
    
    public void ttsService(String textID, String textString, String languageString)
    {
        Locale localeSet;
        switch (languageString)
        {
            case "zh":
                localeSet = Locale.TAIWAN;
                break;
            case "en":
                localeSet = Locale.US;
                break;
            default:
                localeSet = Locale.TAIWAN;
        }
        
        MainApplication app = (MainApplication) mContext.getApplicationContext();
        app.setTTSLanguage(localeSet);
        app.playTTS(textString, textID);
    }
    
    
    public void endAll()
    {
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.stopPlayMediaStream();
        }
        
        if (null != mVoiceRecognition)
        {
            mVoiceRecognition.stopListen();
        }
    }
    
    // this should be call in onResume() to override existing listeners in MainApplication
    private void bindTTSListenersToMainApplication()
    {
        
        MainApplication mainApp = (MainApplication) mContext.getApplicationContext();
        mainApp.setTTSEventListener(mTTSEventListener);
    }
    
    public void stopApplicationTTS()
    {
        MainApplication app = (MainApplication) mContext.getApplicationContext();
        app.stopTTS();
    }
    
    public void unBindTTSListenersToMainApplication()
    {
        MainApplication app = (MainApplication) mContext.getApplicationContext();
        app.stopTTS();
        app.setTTSEventListener(null);
    }
    
    private class CacheStory
    {
        int pauseStorySecond;
        
        CacheStory(int pauseStorySecond)
        {
            this.pauseStorySecond = pauseStorySecond;
        }
    }

    private static class SelfHandler extends Handler
    {
        private final WeakReference<LogicHandler> mWeakSelf;

        SelfHandler(LogicHandler lh)
        {
            mWeakSelf = new WeakReference<>(lh);
        }

        @Override
        public void handleMessage(Message msg)
        {
            LogicHandler self = mWeakSelf.get();
            if (null == self)
            {
                return;
            }

            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) + " " +
                    "" + "" + "" + "" + "" + "" + "" + "" + "" + "" + "From: " + String.valueOf(msg.arg2) +
                    " " + "Message: " + msg.obj);
            self.handleMessages(msg);
        }
    }

    private TTSEventListener mTTSEventListener = new TTSEventListener()
    {
        @Override
        public void onInitSuccess()
        {
            Logs.showTrace("[OobeLogicHandler] TTS onInitSuccess() is not handled");
        }
        
        @Override
        public void onInitFailed(int status, String message)
        {
            Logs.showError("TTS not init success");
        }
        
        @Override
        public void onUtteranceStart(String utteranceId)
        {
            Logs.showTrace("[LogicHandler] TTS onUtteranceStart()");
        }
        
        @Override
        public void onUtteranceDone(String utteranceId)
        {
            Logs.showTrace("[LogicHandler] TTS onUtteranceDone()  ");
            
            switch (utteranceId)
            {
                case TTSParameters.ID_SERVICE_START_UP_GREETINGS:
                    mVoiceRecognition.startListen();
                    
                    break;
                
                case TTSParameters.ID_SERVICE_START_UP_GREETINGS_STORY_MODE:
                    
                    break;
                case TTSParameters.ID_SERVICE_START_UP_GREETINGS_FRIEND_MODE:
                    // XXXXX do friend mode
                    // link API
                    
                    break;
                
                case TTSParameters.ID_SERVICE_START_UP_GREETINGS_GAME_MODE:
                    // XXXXX do game mode
                    //
                    
                    break;
                
                case TTSParameters.ID_SERVICE_FRIEND_RESPONSE:
                    
                    break;
                
                
                case TTSParameters.ID_SERVICE_MUSIC_BEGIN:
                    
                    
                    break;
                case TTSParameters.ID_SERVICE_STORY_BEGIN:
                    
                    mVoiceRecognition.startListen();
                    break;
                case TTSParameters.ID_SERVICE_TTS_BEGIN:
                       /* HashMap<String, String> data2 = new HashMap<>();
    
                        data2.put("ttsID", TTSParameters.ID_SERVICE_TTS_BEGIN);
                        callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC,
                        LogicParameters.METHOD_TTS, data2);
                     */
                    break;
                case TTSParameters.ID_SERVICE_UNKNOWN:
                    
                    //callback to service something ERROR
                    
                    break;
                case TTSParameters.ID_SERVICE_IO_EXCEPTION:
                    break;
                case TTSParameters.ID_SERVICE_INIT_SUCCESS:
                    Logs.showTrace("ID_SERVICE_INIT_SUCCESS");
                    break;
                case TTSParameters.ID_SERVICE_INTERRUPT_STORY_EMOTION_RESPONSE:
                    HashMap<String, String> data = new HashMap<>();
                    
                    data.put("ttsID", TTSParameters.ID_SERVICE_INTERRUPT_STORY_EMOTION_RESPONSE);
                    callBackMessage(ResponseCode.ERR_SUCCESS, LogicParameters.CLASS_LOGIC, LogicParameters
                            .METHOD_TTS, data);
                    break;
                
                default:
                    break;
            }
        }
    };
    
}
