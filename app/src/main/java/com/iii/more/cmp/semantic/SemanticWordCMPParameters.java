package com.iii.more.cmp.semantic;

/**
 * Created by joe on 2017/4/19.
 */

public abstract class SemanticWordCMPParameters
{
    private static int wordID = 1;
    
    public static int getWordID()
    {
        return wordID++;
    }
    
    public static final int MAX_WORD_LEN = 1950;
    
    public static final int TYPE_REQUEST_UNKNOWN = 0;
    public static final int TYPE_REQUEST_CONTROL = 1;
    public static final int TYPE_REQUEST_CONVERSATION = 2;
    public static final int TYPE_REQUEST_RECORD = 3;
    
    public static final int TYPE_RESPONSE_UNKNOWN = 0;
    public static final int TYPE_RESPONSE_SPOTIFY = 1;
    public static final int TYPE_RESPONSE_STORY = 2;
    public static final int TYPE_RESPONSE_TTS = 3;
    public static final int TYPE_RESPONSE_MUSIC = 4;
    public static final int TYPE_RESPONSE_VIDEO = 5;
    
    
}
