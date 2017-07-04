package com.iii.more.spotify;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/6/13.
 */

public class SpotifyHandler extends BaseHandler implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback
{
    
    private SpotifyPlayer mSpotifyPlayer = null;
    
    public SpotifyHandler(Context mContext)
    {
        super(mContext);
        
    }
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        // Check if result comes from the correct activity
        if (requestCode == SpotifyParameters.REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                Logs.showTrace("[SpotifyHandler] response.getAccessToken()" + response.getAccessToken().toString());
                Config playerConfig = new Config(SpotifyHandler.this.mContext, response.getAccessToken(), SpotifyParameters.CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
                {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer)
                    {
                        mSpotifyPlayer = spotifyPlayer;
                        mSpotifyPlayer.addConnectionStateCallback(SpotifyHandler.this);
                        mSpotifyPlayer.addNotificationCallback(SpotifyHandler.this);
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "success");
                        callBackMessage(ResponseCode.ERR_SUCCESS, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_INIT, message);
    
                    }
                    
                    @Override
                    public void onError(Throwable throwable)
                    {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", throwable.getMessage());
                        callBackMessage(ResponseCode.ERR_NOT_INIT, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_INIT, message);
                        Logs.showError("[SpotifyHandler] Could not initialize player: " + throwable.getMessage());
                    }
                    
                });
            }
            else
            {
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "some error about activity not get value");
                callBackMessage(ResponseCode.ERR_NOT_INIT, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_INIT, message);
                Logs.showError("[SpotifyHandler] something ERROR");
            }
        }
        
    }
    
    public void init()
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(SpotifyParameters.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                SpotifyParameters.REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        
        AuthenticationClient.openLoginActivity((Activity) this.mContext, SpotifyParameters.REQUEST_CODE, request);
        
        
    }
    
    
    public void closeSpotify()
    {
        Spotify.destroyPlayer(this);
    }
    
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent)
    {
        
        Logs.showTrace("[SpotifyHandler] Playback event received: " + playerEvent.name());
        
        switch (playerEvent)
        {
            case kSpPlaybackNotifyAudioDeliveryDone:
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "DONE");
                callBackMessage(ResponseCode.ERR_SUCCESS, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_PLAY_MUSIC, message);
                break;
            
            default:
                break;
        }
    }
    
    @Override
    public void onPlaybackError(Error error)
    {
        Logs.showTrace("[SpotifyHandler] Playback error received: " + error.name());
        switch (error)
        {
            case kSpErrorFailed:
                //   HashMap<String, String> message = new HashMap<>();
                //   message.put("message", "ERROR");
                //   callBackMessage(ResponseCode.ERR_UNKNOWN, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_PLAY_MUSIC, message);
                
                break;
            // Handle error type as necessary
            default:
                break;
        }
    }
    
    @Override
    public void onLoggedIn()
    {
        Logs.showTrace("[SpotifyHandler] User logged in");
        
    }
    
    public void playMusic(String musicTrackID)
    {
        if (null != musicTrackID && !musicTrackID.isEmpty())
        {
            Logs.showTrace("[SpotifyHandler] now play music!");
            if (null != mSpotifyPlayer)
            {
                mSpotifyPlayer.playUri(new Player.OperationCallback()
                {
                    @Override
                    public void onSuccess()
                    {
                        Logs.showTrace("[Spotify] play Success!");
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", "START");
                        callBackMessage(ResponseCode.ERR_SUCCESS, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_PLAY_MUSIC, message);
                    }
                    
                    @Override
                    public void onError(Error error)
                    {
                        Logs.showTrace("[Spotify] play ERROR! " + error.toString());
                        HashMap<String, String> message = new HashMap<>();
                        message.put("message", error.toString());
                        callBackMessage(ResponseCode.ERR_UNKNOWN, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_PLAY_MUSIC, message);
                        
                        
                    }
                }, musicTrackID, 0, 0);
            }
            else
            {
                Logs.showTrace("[Spotify] play ERROR! IO Exception");
                HashMap<String, String> message = new HashMap<>();
                message.put("message", "IO Exception");
                callBackMessage(ResponseCode.ERR_IO_EXCEPTION, SpotifyParameters.CLASS_SPOTIFY, SpotifyParameters.METHOD_PLAY_MUSIC, message);
                
            }
        }
    }
    
    public void pauseMusic()
    {
        if (null != mSpotifyPlayer && mSpotifyPlayer.isShutdown() == false)
        {
            mSpotifyPlayer.pause(null);
        }
    }
    
    
    @Override
    public void onLoggedOut()
    {
        Logs.showTrace("[SpotifyHandler] User logged out");
    }
    
    @Override
    public void onLoginFailed(Error error)
    {
        Logs.showTrace("[SpotifyHandler] onLoginFailed:" + error.toString());
    }
    
    
    @Override
    public void onTemporaryError()
    {
        Logs.showTrace("[SpotifyHandler] Temporary error occurred");
    }
    
    @Override
    public void onConnectionMessage(String message)
    {
        Logs.showTrace("[SpotifyHandler] Received connection message: " + message);
    }
}
