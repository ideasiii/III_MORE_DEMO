package com.iii.more.main;

import android.content.Context;
import android.os.Message;

import com.iii.more.main.listeners.TTSEventListener;

import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * 將 TextToSpeechHandler、CReaderAdapter 丟來的訊息盡量委託給內部的 listener 處理
 */
class TTSEventListenerBridge
{
    private TTSEventListener mTtsEventListener;
    //private Context mContext;

    TTSEventListenerBridge(Context context)
    {
        //mContext = context;
    }

    void setEventListener(TTSEventListener l)
    {
        mTtsEventListener = l;
    }

    /**
     * 處理來自 TextToSpeechHandler 的事件
     */
    void handleTTSMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case ResponseCode.ERR_SUCCESS:
                analysisTTSResponse((HashMap<String, String>) msg.obj);
                break;
            case ResponseCode.ERR_NOT_INIT:
                Logs.showError("TTS service method called before initialization");
                break;
            case ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION:
                //deal with not found Google TTS Exception
                //mGoogleTtsHandler.downloadTTS();

                //deal with ACCESSIBILITY page can not open Exception
                //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(intent, 0);
                if (null != mTtsEventListener)
                {
                    String message = ((HashMap<String, String>) msg.obj).get("message");
                    mTtsEventListener.onInitFailed(msg.arg1, message);
                }
                break;
            case ResponseCode.ERR_UNKNOWN:
                Logs.showError("[MainApplication] handleTTSMessage() unknown error occurred");
                if (null != mTtsEventListener)
                {
                    HashMap<String, String> data = (HashMap<String, String>) msg.obj;
                    Logs.showTrace("################# TTS ERROR :" + data);
                    String message = ((HashMap<String, String>) msg.obj).get("message");
                    mTtsEventListener.onInitFailed(msg.arg1, message);
                }
                break;
            default:
                Logs.showError("[MainApplication] handleTTSMessage() unknown msg.arg2: " + msg.arg2);
        }
    }

    /**
     * 進一步處理來自 TextToSpeechHandler 的事件
     */
    private void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            String utteranceId = message.get("TextID");
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            boolean textStatusStart = message.get("TextStatus").equals("START");

            if (textStatusDone)
            {
                if (null != mTtsEventListener)
                {
                    mTtsEventListener.onUtteranceDone(utteranceId);
                }
            }
            else if (textStatusStart)
            {
                if (null != mTtsEventListener)
                {
                    mTtsEventListener.onUtteranceStart(utteranceId);
                }
            }
        }
        else if (message.get("message").equals("init success"))
        {
            if (null != mTtsEventListener)
            {
                mTtsEventListener.onInitSuccess();
            }
        }
    }

    /**
     * 處理來自 CReaderAdapter 的事件
     */
    void handleCReaderMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case CReaderAdapter.Event.INIT_OK:
                Logs.showTrace("handleCReaderMessage() INIT_OK");
                if (null != mTtsEventListener)
                {
                    mTtsEventListener.onInitSuccess();
                }
                break;
            case CReaderAdapter.Event.INIT_FAILED:
                Logs.showTrace("handleCReaderMessage() INIT_FAILED");
                if (null != mTtsEventListener)
                {
                    HashMap<String, String> m = (HashMap<String, String>) msg.obj;
                    int status = Integer.valueOf(m.get("nRes"));
                    String msgText = m.get("message");
                    mTtsEventListener.onInitFailed(status, msgText);
                }
                break;
            case CReaderAdapter.Event.UTTERANCE_START:
                Logs.showTrace("handleCReaderMessage() UTTERANCE_START");
                if (null != mTtsEventListener)
                {
                    HashMap<String, String> m = (HashMap<String, String>) msg.obj;
                    mTtsEventListener.onUtteranceStart(m.get("utteranceId"));
                }
                break;
            case CReaderAdapter.Event.UTTERANCE_STOP:
                Logs.showTrace("handleCReaderMessage() UTTERANCE_STOP");
                /*if (null != mTtsEventListener)
                {
                }*/
                break;
            case CReaderAdapter.Event.UTTERANCE_DONE:
                Logs.showTrace("handleCReaderMessage() UTTERANCE_DONE");
                if (null != mTtsEventListener)
                {
                    HashMap<String, String> m = (HashMap<String, String>) msg.obj;
                    mTtsEventListener.onUtteranceDone(m.get("utteranceId"));
                }
                break;
            default:
                Logs.showError("handleCReaderMessage() unknown msg.arg2: " + msg.arg2);
        }
    }
}
