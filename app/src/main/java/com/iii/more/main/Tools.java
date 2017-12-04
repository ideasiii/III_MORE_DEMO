package com.iii.more.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.iii.more.main.secret.ClassConstantValueLookup;
import com.iii.more.main.secret.ClassConstantValueReverseLookup;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/4/18
 */

public abstract class Tools
{
    private static final ClassConstantValueReverseLookup botAppDrawableReverseLookup = new ClassConstantValueReverseLookup(com.iii.more.main.R.drawable.class);
    private static final ClassConstantValueLookup botAppDrawableIdLookup = new ClassConstantValueLookup(com.iii.more.main.R.drawable.class);

    public static boolean validateMicAvailability()
    {
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try
        {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED)
            {
                available = false;
                
            }
            
            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING)
            {
                recorder.stop();
                available = false;
                
            }
            recorder.stop();
        }
        catch (Exception e)
        {
            Logs.showError(e.toString());
        }
        finally
        {
            recorder.release();
            recorder = null;
        }
        
        return available;
    }

    public static String getDrawableName(int drawableId)
    {
        return botAppDrawableReverseLookup.getName(drawableId);
    }

    /**
     * 從 url 中取出檔案名稱 (不含附檔名)
     * e.g., url 為 https://abc.com/def/starfish09.png，返回結果為 starfish09
     * 若 url 為 https://abc.com/def/ (斜線後沒有檔名)，返回結果為空字串
     */
    public static String stripFilenameFromUrl(String url)
    {
        if (url.endsWith("/"))
        {
            return "";
        }

        return url.substring(url.lastIndexOf('/')+1, url.length());
    }

    /**
     * strip filename (without extension) from url
     */
    public static String removeFileExtension(String file)
    {
        return file.substring(0, file.lastIndexOf('.'));
    }

    public static int getDrawableId(String name)
    {
        return botAppDrawableIdLookup.getValue(name);
    }
}
