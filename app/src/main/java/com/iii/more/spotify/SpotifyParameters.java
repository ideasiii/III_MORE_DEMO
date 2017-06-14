package com.iii.more.spotify;

/**
 * Created by joe on 2017/4/13.
 */

public abstract class SpotifyParameters
{
    //Replace with your client ID
    public static final String CLIENT_ID = "38295c2f5d3c4371b811ddfb0075de81";
    //Replace with your redirect URI
    public static final String REDIRECT_URI = "yourcustomprotocol://callback";
    
    public static final int REQUEST_CODE = 9486;
    
    public static final int CLASS_SPOTIFY = 9548;
    public static final int METHOD_PLAY_MUSIC = 1;
    public static final int METHOD_STOP_MUSIC = 2;
    public static final int METHOD_PAUSE_MUSIC = 3;
    
}
