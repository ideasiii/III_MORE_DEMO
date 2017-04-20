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
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/4/13.
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
        if (requestCode == SpotifyParameters.REQUSET_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN)
            {
                Config playerConfig = new Config(SpotifyHandler.this.mContext, response.getAccessToken(), SpotifyParameters.CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver()
                {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer)
                    {
                        mSpotifyPlayer = spotifyPlayer;
                        mSpotifyPlayer.addConnectionStateCallback(SpotifyHandler.this);
                        mSpotifyPlayer.addNotificationCallback(SpotifyHandler.this);
                    }
                    
                    @Override
                    public void onError(Throwable throwable)
                    {
                        Logs.showTrace("[SpotifyHandler] Could not initialize player: " + throwable.getMessage());
                    }
                    
                });
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
        
        AuthenticationClient.openLoginActivity((Activity) this.mContext, SpotifyParameters.REQUSET_CODE, request);
        
        
    }
    
    
    public void closeSpotify()
    {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
    }
    
    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent)
    {
        Logs.showTrace("Playback event received: " + playerEvent.name());
        switch (playerEvent)
        {
            // Handle event type as necessary
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
    
    public void playMusic(String musicName)
    {
        Logs.showTrace("[SpotifyHandler] now play music!");
        
        mSpotifyPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
        
        
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
