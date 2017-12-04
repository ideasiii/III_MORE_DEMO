package com.iii.more.oobe.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.iii.more.main.MainApplication;
import com.iii.more.main.listeners.TTSEventListener;
import com.iii.more.stream.WebMediaPlayerHandler;
import com.iii.more.stream.WebMediaPlayerParameters;
import com.iii.more.tts.TTSCache;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

/**
 * Created by joe on 2017/11/01.
 */

public class OobeLogicHandler extends BaseHandler
{
    private VoiceRecognition mVoiceRecognitionHandler = null;
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    private final InClassHandler mHandler = new InClassHandler(this);
    private int oobeState = 0;

    public void setState(int state)
    {
        oobeState = state;
    }
    
    public int getState()
    {
        return oobeState;
    }

    public OobeLogicHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mVoiceRecognitionHandler = new VoiceRecognition(mContext);
        mVoiceRecognitionHandler.setHandler(mHandler);
        
        mWebMediaPlayerHandler = new WebMediaPlayerHandler(mContext);
        mWebMediaPlayerHandler.setHandler(mHandler);
    }

    // this should be call in onResume() to override existing listeners in MainApplication
    public void bindListenersToMainApplication()
    {
        MainApplication mainApp = (MainApplication) mContext.getApplicationContext();
        mainApp.setTTSEventListener(mTTSEventListener);
    }
    
    public void setVideoSurfaceHolder(SurfaceHolder surfaceHolder)
    {
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.setDisplay(surfaceHolder);
        }
    }
    
    public void sttLaunch()
    {
        mVoiceRecognitionHandler.startListen();
    }
    
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
                Logs.showError("[OobeLogicHandler] handleMessages() unknown msg.what: " + msg.what);
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
                    
                    
                    break;
                
                default:
                    break;
            }
        }
        else
        {
            //異常例外處理
            onError(OobeTTSParameters.ID_SERVICE_IO_EXCEPTION);
        }
    }

    private void handleMessageVoiceRecognition(Message msg)
    {
        final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognitionHandler.stopListen();
            
            Logs.showTrace("[OobeLogicHandler] Get voice Text: " + message.get("message"));
            
            if (!message.get("message").isEmpty())
            {
                //callback activity
                HashMap<String, String> returnMessage = new HashMap<>();
                returnMessage.put("message", message.get("message"));
                callBackMessage(ResponseCode.ERR_SUCCESS, OobeLogicParameters.CLASS_OOBE_LOGIC, OobeLogicParameters.METHOD_VOICE, returnMessage);
            }
        }
        
        else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //startListen first handle
        }
        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
        {
            Logs.showTrace("get ERROR message: " + message.get("message"));
            mVoiceRecognitionHandler.stopListen();
            
            if (message.get("message").equals("No match") || message.get("message").equals("No speech input"))
            {
                //TTS again and listen again
                
                // onError(OobeTTSParameters.ID_SERVICE_UNKNOWN);
                HashMap<String, String> returnMessage = new HashMap<>();
                returnMessage.put("message", message.get("message"));
                callBackMessage(ResponseCode.ERR_SPEECH_ERRORMESSAGE, OobeLogicParameters.CLASS_OOBE_LOGIC, OobeLogicParameters.METHOD_VOICE, returnMessage);
            }
        }
        else if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
        {
            onError(OobeTTSParameters.ID_SERVICE_IO_EXCEPTION);
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
            case OobeTTSParameters.ID_SERVICE_IO_EXCEPTION:
                ttsService(OobeTTSParameters.ID_SERVICE_IO_EXCEPTION, OobeTTSParameters.STRING_SERVICE_IO_EXCEPTION, "zh");
                break;
            default:
                ttsService(OobeTTSParameters.ID_SERVICE_UNKNOWN, OobeTTSParameters.STRING_SERVICE_UNKNOWN, "zh");
                break;
        }
    }
    
    public void playStreaming(String serverURL, String streamFileName)
    {
        mWebMediaPlayerHandler.setHostAndFilePathNotEncode(serverURL,streamFileName);
        mWebMediaPlayerHandler.startPlayMediaStream();
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

        MainApplication mainApp = (MainApplication) mContext.getApplicationContext();
        Locale currentTtsLocale = mainApp.getTTSLanguage();

        if (!currentTtsLocale.toString().equals(localeSet.toString()))
        {
            Logs.showTrace("[OobeLogicHandler] OLD getLocale():" + currentTtsLocale.toString());
            mainApp.setTTSLanguage(localeSet);
            Logs.showTrace("[OobeLogicHandler] NEW getLocale():" + currentTtsLocale.toString());
            
            TTSCache.setTTSHandlerInit(true);
            mainApp.initTTS();
        }
        
        if (TTSCache.getTTSHandlerInit())
        {
            TTSCache.setTTSCache(textString, textID);
        }
        else
        {
            mainApp.playTTS(textString, textID,1.0f,0.9f);
        }
    }
    
    public void killAll()
    {
        endAll();
    }
    
    public void endAll()
    {
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.stopPlayMediaStream();
        }
        
        if (null != mVoiceRecognitionHandler)
        {
            mVoiceRecognitionHandler.stopListen();
        }
    }

    private static class InClassHandler extends Handler
    {
        private final WeakReference<OobeLogicHandler> mWeakSelf;

        public InClassHandler(OobeLogicHandler h)
        {
            mWeakSelf = new WeakReference<>(h);
        }

        @Override
        public void handleMessage(Message msg)
        {
            OobeLogicHandler h = mWeakSelf.get();
            if (h != null)
            {
                h.handleMessages(msg);
            }
        }
    }

    private TTSEventListener mTTSEventListener = new TTSEventListener()
    {
        @Override
        public void onInitSuccess()
        {
            Logs.showTrace("[OobeLogicHandler] TTS onInitSuccess() is not handled");

            TTSCache.setTTSHandlerInit(false);
            HashMap<String, String> ttsCache = TTSCache.getTTSCache();
            if (null != ttsCache)
            {
                MainApplication mainApp = (MainApplication) mContext.getApplicationContext();
                mainApp.playTTS(ttsCache.get("tts"), ttsCache.get("param"));
            }
        }

        @Override
        public void onInitFailed(int status, String message)
        {
            Logs.showError("TTS not init success");
        }

        @Override
        public void onUtteranceStart(String utteranceId)
        {
            Logs.showTrace("[OobeLogicHandler] TTS onUtteranceStart()");
        }

        @Override
        public void onUtteranceDone(String utteranceId)
        {
            Logs.showTrace("[OobeLogicHandler] TTS onUtteranceDone()");

            switch (utteranceId)
            {
                //callback to service something ERROR
                case OobeTTSParameters.ID_SERVICE_IO_EXCEPTION:
                    break;
                default:
                    HashMap<String, String> message2 = new HashMap<>();
                    message2.put("TextID", utteranceId);

                    callBackMessage(ResponseCode.ERR_SUCCESS, OobeLogicParameters.CLASS_OOBE_LOGIC,
                            OobeLogicParameters.METHOD_TTS, message2);
            }
        }
    };
}
