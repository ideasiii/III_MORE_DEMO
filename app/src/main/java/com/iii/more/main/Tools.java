package com.iii.more.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.iii.more.main.secret.ClassConstantValueLookup;
import com.iii.more.main.secret.ClassConstantValueReverseLookup;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/4/18.
 */

public abstract class Tools
{
    private static final ClassConstantValueReverseLookup botAppDrawableReverseLookup = new ClassConstantValueReverseLookup(R.drawable.class);
    private static final ClassConstantValueLookup botAppDrawableIdLookup = new ClassConstantValueLookup(R.drawable.class);

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

    public static int getDrawableId(String name)
    {
        return botAppDrawableIdLookup.getValue(name);
    }
}
