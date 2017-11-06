package com.iii.more.game.module;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.SparseArray;

import java.util.Locale;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/11/2.
 */

public class TtsHandler
{
    private final boolean[] bTtsValid = {false};
    private TextToSpeech tts;
    private Context theContext = null;
    private SparseArray<OnTTSStartedListener> listOnTTSStartedListener = null;
    
    public static interface OnTTSStartedListener
    {
        public void OnStarted();
    }
    
    public void setOnTTSStartedListener(OnTTSStartedListener listener)
    {
        if (null != listener && null != listOnTTSStartedListener)
        {
            listOnTTSStartedListener.put(listOnTTSStartedListener.size(), listener);
        }
    }
    
    public TtsHandler(Context context)
    {
        theContext = context;
        listOnTTSStartedListener = new SparseArray<OnTTSStartedListener>();
    }
    
    @Override
    protected void finalize() throws Throwable
    {
        release();
        super.finalize();
    }
    
    public boolean createTTS()
    {
        if (tts == null && null != theContext)
        {
            tts = new TextToSpeech(theContext, new TextToSpeech.OnInitListener()
            {
                @Override
                public void onInit(int arg0)
                {
                    // TTS 初始化成功
                    bTtsValid[0] = false;
                    if (arg0 == TextToSpeech.SUCCESS)
                    {
                        bTtsValid[0] = true;
                        Logs.showTrace("[TtsHandler] createTTS success");
                        if (null != listOnTTSStartedListener)
                        {
                            for (int i = 0; i < listOnTTSStartedListener.size(); ++i)
                            {
                                listOnTTSStartedListener.get(i).OnStarted();
                            }
                        }
                    }
                }
            }
            );
        }
        return bTtsValid[0];
    }
    
    public void release()
    {
        bTtsValid[0] = false;
        if (null != tts)
        {
            tts.shutdown();
        }
    }
    
    public boolean isValid()
    {
        return bTtsValid[0];
    }
    
    public void SetOnUtteranceProgressListener(UtteranceProgressListener utteranceProgressListener)
    {
        if (null != tts && bTtsValid[0])
        {
            tts.setOnUtteranceProgressListener(utteranceProgressListener);
        }
    }
    
    public boolean setLanguage(Locale l)
    {
        // 目前指定的【語系+國家】TTS, 已下載離線語音檔, 可以離線發音
        int nResult;
        
        if (null != tts && (tts.isLanguageAvailable(l) == TextToSpeech.LANG_COUNTRY_AVAILABLE))
        {
            nResult = tts.setLanguage(l);
            return !(TextToSpeech.LANG_MISSING_DATA == nResult || TextToSpeech.LANG_NOT_SUPPORTED == nResult);
        }
        return false;
    }
    
    public void speack(String strWord, String strUtteranceld)
    {
        if (null == tts)
        {
            Logs.showTrace("speack fail tts invalid ");
            return;
        }
        Logs.showTrace("TTS speack: " + strWord);
        tts.speak(strWord, TextToSpeech.QUEUE_FLUSH, null, strUtteranceld);
    }
    
}
