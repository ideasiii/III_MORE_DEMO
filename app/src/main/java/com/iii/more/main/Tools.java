package com.iii.more.main;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/4/18.
 */

public class Tools
{
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
    
    
    
}
