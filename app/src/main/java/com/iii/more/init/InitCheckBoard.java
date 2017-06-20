package com.iii.more.init;

import android.content.Context;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/6/19.
 */

public class InitCheckBoard extends BaseHandler
{
    private static boolean INIT_SPOTIFY = false;
    private static boolean INIT_TTS = false;
    
    
    public InitCheckBoard(Context mContext)
    {
        super(mContext);
    }
    
    
    public void init()
    {
        Thread mThread = new Thread(new InitCheckRunnable());
        mThread.start();
        
    }
    
    
    public static void setSpotifyInit(boolean isOk)
    {
        INIT_SPOTIFY = isOk;
    }
    
    public static void setTTSInit(boolean isOk)
    {
        INIT_TTS = isOk;
    }
    
    
    private static boolean isAllInit()
    {
        if (INIT_SPOTIFY && INIT_TTS)
        {
            return true;
        }
        return false;
    }
    
    
    private class InitCheckRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    Logs.showTrace("[InitCheckBoard] check init again!");
                    if (InitCheckBoard.isAllInit())
                    {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "success");
                        callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT, InitCheckBoardParameters.METHOD_INIT, message);
                        break;
                    }
                    
                    
                    Thread.sleep(1000);
                }
            }
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
        }
    }
    
}
