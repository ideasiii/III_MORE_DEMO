package com.iii.more.main;

public abstract class TTSEventListener
{
    public abstract void onInitSuccess();
    public abstract void onInitFailed(int status, String message);

    public abstract void onUtteranceStart(String utteranceId);

    public abstract void onUtteranceDone(String utteranceId);

    /*public void onUtteranceError(String utteranceId, int errorCode)
    {
    }

    public void onUtteranceStop(String utteranceId, boolean interrupted)
    {
    }*/
}
