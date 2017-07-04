package com.iii.more.tts;

import java.util.HashMap;

/**
 * Created by joe on 2017/6/26.
 */

public class TTSCache
{
    private static HashMap<String, String> ttsCache = new HashMap<>();
    private static boolean ttsHandlerInit = false;
    
    public static synchronized boolean getTTSHandlerInit()
    {
        return ttsHandlerInit;
    }
    
    public static synchronized void setTTSHandlerInit(boolean isInit)
    {
        ttsHandlerInit = isInit;
    }
    
    
    public static synchronized boolean setTTSCache(String setTTSString, String setTTSParameter)
    {
        
        if (!ttsCache.isEmpty())
        {
            return false;
        }
        else
        {
            ttsCache.put("tts", setTTSString);
            ttsCache.put("param", setTTSParameter);
            return true;
        }
    }
    
    public static synchronized HashMap<String, String> getTTSCache()
    {
        if (!ttsCache.isEmpty())
        {
            HashMap<String, String> returnTTSCache = new HashMap<>();
            returnTTSCache.put("tts", ttsCache.get("tts"));
            returnTTSCache.put("param", ttsCache.get("param"));
            ttsCache.clear();
            
            return returnTTSCache;
        }
        else
        {
            return null;
        }
    }
    
    
}
