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
    private static int INIT_SPOTIFY = 0;
    private static int INIT_TTS = 0;
    
    
    public InitCheckBoard(Context mContext)
    {
        super(mContext);
    }
    
    
    public void init()
    {
        Thread mThread = new Thread(new InitCheckRunnable());
        mThread.start();
        
    }
    private static void setInitKnown()
    {
        INIT_SPOTIFY = 0;
        INIT_TTS = 0;
    }
    
    
    public static void setSpotifyInit(boolean isOk)
    {
        if (isOk)
        {
            INIT_SPOTIFY = 1;
        }
        else
        {
            INIT_SPOTIFY = -1;
        }
        
    }
    
    public static void setTTSInit(boolean isOk)
    {
        if (isOk)
        {
            INIT_TTS = 1;
        }
        else
        {
            INIT_TTS = -1;
        }
    }
    
    
    private static boolean isAllInit()
    {
        if (INIT_SPOTIFY == 1 && INIT_TTS == 1)
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
                    HashMap<String, String> message = new HashMap<>();
                    if (InitCheckBoard.isAllInit())
                    {
                        InitCheckBoard.setInitKnown();
                        message.put("message", "success");
                       
                        callBackMessage(ResponseCode.ERR_SUCCESS, InitCheckBoardParameters.CLASS_INIT, InitCheckBoardParameters.METHOD_INIT, message);
                        break;
                    }
                    else
                    {
                        if (INIT_SPOTIFY == -1)
                        {
                            message.put("message", "Spofity not init");
                            callBackMessage(ResponseCode.ERR_NOT_INIT, InitCheckBoardParameters.CLASS_INIT, InitCheckBoardParameters.METHOD_INIT, message);
                            
                        }
                        else if (INIT_TTS == -1)
                        {
                            message.put("message", "TTS not init");
                            callBackMessage(ResponseCode.ERR_NOT_INIT, InitCheckBoardParameters.CLASS_INIT, InitCheckBoardParameters.METHOD_INIT, message);
                        }
                    }
                    
                    
                    Thread.sleep(3000);
                }
            }
            catch (Exception e)
            {
                Logs.showError(e.toString());
            }
        }
    }
    
}
