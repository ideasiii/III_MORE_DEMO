package com.iii.more.stream;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.SparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;

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
    private Thread mStoryMoodThread = null;
    private SparseArray<String> mMoodArray = null;
    private final static int RANGE = 50;
    
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
    
    public void startPlayMediaStream(JSONArray moodJsonArray)
    {
        if (null != moodJsonArray)
        {
            mMoodArray = new SparseArray<>();
            for (int i = 0; i < moodJsonArray.length(); i++)
            {
                JSONObject tmp = null;
                try
                {
                    tmp = (JSONObject) moodJsonArray.get(i);
                    if (tmp.has("time") && tmp.has("host") && tmp.has("file"))
                    {
                        int time = Integer.valueOf(tmp.getString("time"));
                        String url = tmp.getString("host") + tmp.getString("file");
                        mMoodArray.append(time, url);
                    }
                    
                }
                catch (JSONException e)
                {
                    Logs.showTrace(e.toString());
                }
                
                
            }
        }
        mStoryMoodThread = new Thread(new StoryMoodChangeRunnable());
        
        startPlayMediaStream();
        
        
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
                HashMap<String, String> message = new HashMap<>();
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
                
                HashMap<String, String> message;
                message = new HashMap<String, String>();
                message.put("message", "something ERROR while playing");
                callBackMessage(ResponseCode.ERR_UNKNOWN, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
                
                return false;
            }
        });
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
        {
            @Override
            public void onPrepared(MediaPlayer mp)
            {
                Logs.showTrace("[WebMediaPlayerHandler] now start!");
                mp.start();
                
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "success");
                callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.START_PLAY, message);
    
                if (null != mStoryMoodThread)
                {
                    mStoryMoodThread.start();
                }
            }
        });
        
        try
        {
            if (!hostPath.isEmpty() && !filePath.isEmpty())
            {
                Logs.showTrace("[WebMediaPlayerHandler] Stream URL: " + hostPath + filePath);
                mMediaPlayer.setDataSource(hostPath + filePath);
                mMediaPlayer.prepareAsync();
                
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
        if (null != mMediaPlayer)
        {
            if (mMediaPlayer.isPlaying())
            {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                
                //clear moodJsonArray Data
                if (null != mMoodArray)
                {
                    mMoodArray.clear();
                    mMoodArray = null;
                }
                //stop thread which handle moodJsonArray Data
                if (null != mStoryMoodThread)
                {
                    mStoryMoodThread.interrupt();
                    mStoryMoodThread = null;
                }
                
                HashMap<String, String> message = new HashMap<String, String>();
                message.put("message", "success");
                callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.STOP_PLAY, message);
                
            }
        }
        
    }
    
    
    private class StoryMoodChangeRunnable implements Runnable
    {
        
        @Override
        public void run()
        {
            try
            {
                
                if (null != mMoodArray)
                {
                    
                    //  Logs.showTrace("[WebMediaPlayerHandler] story length:" + String.valueOf(mMediaPlayer.getDuration()));
                    for(int i=0;i<mMoodArray.size();i++)
                    {
                        Logs.showTrace("mood time"+String.valueOf(mMoodArray.keyAt(i)));
                        if(mMoodArray.keyAt(i) == 0)
                        {
                            HashMap<String, String> message = new HashMap<String, String>();
                            message.put("message", mMoodArray.get(mMoodArray.keyAt(i)));
                            callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.MOOD_IMAGE_SHOW, message);
                        }
                        
    
                    }
                    while (true)
                    {
                        int currentTime = mMediaPlayer.getCurrentPosition();
                        //  Logs.showTrace("[WebMediaPlayerHandler] now current:" + String.valueOf(currentTime));
                        
                        for (int i = 0; i < mMoodArray.size(); i++)
                        {
                            if (mMoodArray.keyAt(i) > currentTime && mMoodArray.keyAt(i) <= currentTime + RANGE)
                            {
                                HashMap<String, String> message = new HashMap<String, String>();
                                message.put("message", mMoodArray.get(mMoodArray.keyAt(i)));
                                callBackMessage(ResponseCode.ERR_SUCCESS, WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER, WebMediaPlayerParameters.MOOD_IMAGE_SHOW, message);
                                break;
                            }
                        }
                        
                        if (mMediaPlayer.getDuration() == mMediaPlayer.getCurrentPosition())
                        {
                            break;
                        }
                        
                        
                        Thread.sleep(50);
                        
                    }
                }
            }
            catch (InterruptedException e)
            {
                Logs.showTrace("[WebMediaPlayerHandler] story track STOP");
            }
            catch (Exception e)
            {
                Logs.showTrace("[WebMediaPlayerHandler] story error" + e.toString());
            }
            
        }
    }
    
    
}
