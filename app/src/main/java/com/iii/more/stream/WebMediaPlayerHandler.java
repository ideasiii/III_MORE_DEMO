package com.iii.more.stream;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/4/17.
 */

public class WebMediaPlayerHandler extends BaseHandler
{
    private MediaPlayer mMediaPlayer = null;
    private String hostPath = "";
    private String filePath = "";
    
    
    public WebMediaPlayerHandler(Context context)
    {
        super(context);
    }
    
    public boolean setHostAndFilePath(String hostPath, String filePath)
    {
        boolean anyError = false;
        this.hostPath = hostPath;
        try
        {
            this.filePath = URLEncoder.encode(filePath, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            anyError = true;
            Logs.showError("[WebMediaPlayerHandler] " + e.toString());
        }
        return anyError;
    }
    
    public void startPlayMediaStream()
    {
        stopPlayMediaStream();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
        {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                mp.release();
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("message", "success");
                callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.COMPLETE_PLAY, message);
            }
        });
        mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener()
        {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra)
            {
                Logs.showTrace("[WebMediaPlayerHandler] something ERROR");
                
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("message", "something ERROR while playing");
                callBackMessage(ResponseCode.ERR_UNKNOWN, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
    
                return false;
            }
        });
        
        try
        {
            if (!hostPath.isEmpty() && !filePath.isEmpty())
            {
                Logs.showTrace("[WebMediaPlayerHandler] Stream URL: " + hostPath + filePath);
                mMediaPlayer.setDataSource(hostPath + filePath);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
            else
            {
                Logs.showTrace("[WebMediaPlayerHandler] hostPath OR filePath is null!");
                
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("message", "hostPath OR filePath is null");
                callBackMessage(ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
            }
        }
        catch (IOException e)
        {
            Logs.showError("[WebMediaPlayerHandler] " + e.toString());
            HashMap<String, String> message = new HashMap<String, String>();
            message.put("message", e.toString());
            callBackMessage(ResponseCode.ERR_IO_EXCEPTION, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
    
        }
        catch (Exception e)
        {
            Logs.showError("[WebMediaPlayerHandler] " + e.toString());
            HashMap<String, String> message = new HashMap<String, String>();
            message.put("message", e.toString());
            callBackMessage(ResponseCode.ERR_UNKNOWN, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
    
        }
        
        
    }
    
    public void pausePlayMediaStream()
    {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.pause();
        }
        
    }
    
    public void stopPlayMediaStream()
    {
        if (null != mMediaPlayer && mMediaPlayer.isPlaying())
        {
            mMediaPlayer.stop();
            mMediaPlayer = null;
            HashMap<String, String> message = new HashMap<String, String>();
            message.put("message", "success");
            callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.STOP_PLAY, message);
    
        }
        
    }
    
    
}
