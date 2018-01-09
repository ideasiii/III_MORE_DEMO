package com.iii.more.clock.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.main.MainApplication;
import com.iii.more.main.TTSParameters;
import com.iii.more.main.listeners.TTSEventListener;
import com.iii.more.stream.WebMediaPlayerHandler;
import com.iii.more.stream.WebMediaPlayerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/12/26
 */

public class ClockLogicHandler extends BaseHandler
{
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    private JSONObject mActivityJson = null;
    
    private Handler mSelfHandler = new ClockLogicHandler.SelfHandler(this);
    
    
    private void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER:
                handleMessageWebMediaPlayer(msg);
                break;
            
            default:
                break;
        }
    }
    
    public void init()
    {
        if (null == mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler = new WebMediaPlayerHandler(mContext);
            mWebMediaPlayerHandler.setHandler(mSelfHandler);
        }
        
    }
    
    
    public ClockLogicHandler(Context context)
    {
        super(context);
    }
    
    
    private void handleMessageWebMediaPlayer(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case WebMediaPlayerParameters.COMPLETE_PLAY:
                    mWebMediaPlayerHandler.stopPlayMediaStream();
                    //### finish and callback Activity
                    
                    HashMap<String, String> message = new HashMap<>();
                    message.put("message", "success");
                    message.put("state", "complete");
                    callBackMessage(ResponseCode.ERR_SUCCESS, ClockLogicParameters.CLASS_CLOCK_LOGIC,
                        ClockLogicParameters.METHOD_MEDIA_STREAM, message);
                    
                    
                    break;
                
                case WebMediaPlayerParameters.START_PLAY:
                case WebMediaPlayerParameters.RESUME_PLAY:
                    
                    
                    break;
                case WebMediaPlayerParameters.STOP_PLAY:
                    break;
                
                case WebMediaPlayerParameters.PAUSE_PLAY:
                    
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
    
    public void setActivityJson(@NonNull JSONObject activityJson)
    {
        mActivityJson = activityJson;
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
                                (SemanticWordCMPParameters.STRING_JSON_KEY_HOST), mActivityJson.getString
                                (SemanticWordCMPParameters.STRING_JSON_KEY_FILE));
                            mWebMediaPlayerHandler.startPlayMediaStream();
                            
                        }
                        else
                        {
                            Logs.showError("[LogicHandler] ERROR while read Local Host OR File");
                        }
                        break;
                    
                    case SemanticWordCMPParameters.TYPE_RESPONSE_TTS:
                        if (mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_LANG) &&
                            mActivityJson.has(SemanticWordCMPParameters.STRING_JSON_KEY_TTS))
                        {
                            ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, mActivityJson.getString
                                (SemanticWordCMPParameters.STRING_JSON_KEY_TTS), mActivityJson.getString
                                (SemanticWordCMPParameters.STRING_JSON_KEY_LANG));
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
    
    public void bindTTSListenersToMainApplication()
    {
        
        MainApplication mainApp = (MainApplication) mContext.getApplicationContext();
        mainApp.setTTSEventListener(mTTSEventListener);
    }
    
    
    public void unBindTTSListenersToMainApplication()
    {
        MainApplication app = (MainApplication) mContext.getApplicationContext();
        app.stopTTS();
        app.setTTSEventListener(null);
    }
    
    private TTSEventListener mTTSEventListener = new TTSEventListener()
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
            Logs.showTrace("[ClockLogicHandler] onUtteranceDone");
            if (TTSParameters.ID_SERVICE_IO_EXCEPTION.equals(utteranceId) || TTSParameters
                .ID_SERVICE_UNKNOWN.equals(utteranceId))
            {
                Logs.showError("[ClockLogicHandler] get TTS ERROR,call back to clock activity!");
                // ### call back to activity
                HashMap<String, String> message = new HashMap<>();
                message.put("message", utteranceId);
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, ClockLogicParameters.CLASS_CLOCK_LOGIC,
                    ClockLogicParameters.METHOD_TTS, message);
                
            }
            else if (utteranceId.equals(TTSParameters.ID_SERVICE_TTS_BEGIN))
            {
                Logs.showTrace("[ClockLogicHandler] onUtteranceDone: ID_SERVICE_TTS_BEGIN");
                // ### call back to activity
                HashMap<String, String> message = new HashMap<>();
                message.put("message", utteranceId);
                callBackMessage(ResponseCode.ERR_SUCCESS, ClockLogicParameters.CLASS_CLOCK_LOGIC,
                    ClockLogicParameters.METHOD_TTS, message);
                
                
            }
        }
    };
    
    public void endAll()
    {
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.stopPlayMediaStream();
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
    
    private static class SelfHandler extends Handler
    {
        private final WeakReference<ClockLogicHandler> mWeakSelf;
        
        SelfHandler(ClockLogicHandler lh)
        {
            mWeakSelf = new WeakReference<>(lh);
        }
        
        @Override
        public void handleMessage(Message msg)
        {
            ClockLogicHandler self = mWeakSelf.get();
            if (null == self)
            {
                return;
            }
            self.handleMessages(msg);
        }
    }
}
