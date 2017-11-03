package com.iii.more.oobe.logic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.iii.more.stream.WebMediaPlayerHandler;
import com.iii.more.stream.WebMediaPlayerParameters;
import com.iii.more.tts.TTSCache;

import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.speech.tts.TextToSpeechHandler;
import sdk.ideas.tool.speech.voice.VoiceRecognition;

/**
 * Created by joe on 2017/11/01.
 */

public class OobeLogicHandler extends BaseHandler
{
    private TextToSpeechHandler mTextToSpeechHandler = null;
    private VoiceRecognition mVoiceRecognitionHandler = null;
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    private int oobeState = 0;
    
    public void setState(int state)
    {
        oobeState = state;
    }
    
    public int getState()
    {
        return oobeState;
    }
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            handleMessages(msg);
        }
    };
    
    public OobeLogicHandler(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        mTextToSpeechHandler = new TextToSpeechHandler(mContext);
        mTextToSpeechHandler.setHandler(mHandler);
        mTextToSpeechHandler.init();
        
        
        mVoiceRecognitionHandler = new VoiceRecognition(mContext);
        mVoiceRecognitionHandler.setHandler(mHandler);
        
        mWebMediaPlayerHandler = new WebMediaPlayerHandler(mContext);
        mWebMediaPlayerHandler.setHandler(mHandler);
        
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
            
            case CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER:
                handleMessageTTS(msg);
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
    
    private void handleMessageTTS(Message msg)
    {
        switch (msg.arg1)
        {
            case ResponseCode.ERR_SUCCESS:
                analysisTTSResponse((HashMap<String, String>) msg.obj);
                
                
                break;
            case ResponseCode.ERR_NOT_INIT:
                // InitCheckBoard.setTTSInit(false);
                Logs.showError("TTS not init success");
                break;
            case ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION:
                //InitCheckBoard.setTTSInit(false);
                //deal with not found Google TTS Exception
                mTextToSpeechHandler.downloadTTS();
                
                //deal with ACCESSIBILITY page can not open Exception
                //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(intent, 0);
                
                break;
            case ResponseCode.ERR_UNKNOWN:
                //  InitCheckBoard.setTTSInit(false);
                break;
            default:
                break;
        }
        
    }
    
    private void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            boolean textStatusStart = message.get("TextStatus").equals("START");
            
            if (textStatusDone)
            {
                switch (message.get("TextID"))
                {
                    //callback to service something ERROR
                    
                    case OobeTTSParameters.ID_SERVICE_IO_EXCEPTION:
                        break;
                    
                    default:
                        HashMap<String, String> message2 = new HashMap<>();
                        message2.put("TextID", message.get("TextID"));
                        
                        callBackMessage(ResponseCode.ERR_SUCCESS, OobeLogicParameters.CLASS_OOBE_LOGIC,
                                OobeLogicParameters.METHOD_TTS, message2);
                        
                        break;
                    
                }
                
                
            }
            
        }
        else if (message.get("message").equals("init success"))
        {
            TTSCache.setTTSHandlerInit(false);
            HashMap<String, String> ttsCache = TTSCache.getTTSCache();
            if (null != ttsCache)
            {
                mTextToSpeechHandler.textToSpeech(ttsCache.get("tts"), ttsCache.get("param"));
            }
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
                returnMessage.put("message",message.get("message"));
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
    
    public void ttsService(String textID, String textString, String languageString)
    {
        Locale localeSet = null;
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
                break;
        }
        if (!mTextToSpeechHandler.getLocale().toString().equals(localeSet.toString()))
        {
            Logs.showTrace("[OobeLogicHandler] OLD getLocale():" + mTextToSpeechHandler.getLocale().toString());
            mTextToSpeechHandler.setLocale(localeSet);
            Logs.showTrace("[OobeLogicHandler] NEW getLocale():" + mTextToSpeechHandler.getLocale().toString());
            
            TTSCache.setTTSHandlerInit(true);
            mTextToSpeechHandler.init();
        }
        
        if (TTSCache.getTTSHandlerInit())
        {
            TTSCache.setTTSCache(textString, textID);
        }
        else
        {
            mTextToSpeechHandler.textToSpeech(textString, textID);
        }
        
        
    }
    
    public void killAll()
    {
        endAll();
        if (null != mTextToSpeechHandler)
        {
            Logs.showTrace("[OobeLogicHandler] mTextToSpeechHandler shutdown Start");
            mTextToSpeechHandler.shutdown();
            Logs.showTrace("[OobeLogicHandler] mTextToSpeechHandler shutdown End");
            
        }
    }
    
    public void endAll()
    {
        if (null != mTextToSpeechHandler)
        {
            mTextToSpeechHandler.stop();
        }
        
        if (null != mWebMediaPlayerHandler)
        {
            mWebMediaPlayerHandler.stopPlayMediaStream();
        }
        
        if (null != mVoiceRecognitionHandler)
        {
            mVoiceRecognitionHandler.stopListen();
        }
        
    }
}
