package com.iii.more.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.pocketshinx.PocketSphinxHandler;
import com.iii.more.pocketshinx.PocketSphinxParameters;
import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.spotify.SpotifyHandler;
import com.iii.more.stream.WebMediaPlayerHandler;
import com.iii.more.stream.WebMediaPlayerParameters;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.premisson.RuntimePermissionHandler;
import sdk.ideas.tool.speech.tts.TextToSpeechHandler;
import sdk.ideas.tool.speech.voice.VoiceRecognition;


public class MainActivity extends Activity
{
    private RuntimePermissionHandler mRuntimePermissionHandler = null;
    private PocketSphinxHandler mPocketSphinxHandler = null;
    private TextToSpeechHandler mTextToSpeechHandler = null;
    private SpotifyHandler mSpotifyHandler = null;
    private VoiceRecognition mVoiceRecognition = null;
    private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    private WebMediaPlayerHandler mWebMediaPlayerHandler = null;
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.RECORD_AUDIO);
        mRuntimePermissionHandler = new RuntimePermissionHandler(this, permissions);
        mRuntimePermissionHandler.setHandler(mHandler);
        mRuntimePermissionHandler.startRequestPermissions();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        mSpotifyHandler.onActivityResult(requestCode, resultCode, data);
    }
    
    public void init()
    {
        mPocketSphinxHandler = new PocketSphinxHandler(this);
        mPocketSphinxHandler.setHandler(mHandler);
        mPocketSphinxHandler.startListenAction();
        
        mTextToSpeechHandler = new TextToSpeechHandler(this);
        mTextToSpeechHandler.setHandler(mHandler);
        mTextToSpeechHandler.init();
        
        mSpotifyHandler = new SpotifyHandler(this);
        mSpotifyHandler.setHandler(mHandler);
        mSpotifyHandler.init();
        
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(mHandler);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        
        CMPHandler.setIPAndPort(Parameters.CMP_HOST_IP, Parameters.CMP_HOST_PORT);
        mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
        mSemanticWordCMPHandler.setHandler(mHandler);
        
        mWebMediaPlayerHandler = new WebMediaPlayerHandler(this);
        mWebMediaPlayerHandler.setHandler(mHandler);
        
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            String[] permissions, int[] grantResults)
    {
        mRuntimePermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onDestroy()
    {
        Logs.showTrace("onDestroy");
        
        mPocketSphinxHandler.stopListenAction();
        
        mTextToSpeechHandler.stop();
        mTextToSpeechHandler.shutdown();
        
        mSpotifyHandler.closeSpotify();
        
        super.onDestroy();
    }
    
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        super.onStop();
    }
    
    
    public void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case CMPParameters.CLASS_CMP_SEMANTIC_WORD:
                handleMessageSWCMP(msg);
                break;
            case CtrlType.MSG_RESPONSE_PERMISSION_HANDLER:
                handleMessagePermission(msg);
                break;
            case WebMediaPlayerParameters.CLASS_WEB_MEDIA_PLAYER:
                handleMessageWebMediaPlayer(msg);
                break;
            case PocketSphinxParameters.CLASS_POCKET_SPHINX:
                handleMessageSphinx(msg);
                break;
            case CtrlType.MSG_RESPONSE_TEXT_TO_SPEECH_HANDLER:
                handleMessageTTS(msg);
                break;
            case CtrlType.MSG_RESPONSE_VOICE_RECOGNITION_HANDLER:
                handleMessageVoiceRecognition(msg);
                break;
            default:
                break;
        }
    }
    
    public void handleMessagePermission(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //start to init
            Logs.showTrace("start to init!");
            init();
        }
        else
        {
            //if not permission, close app
            finish();
        }
    }
    
    public void handleMessageTTS(Message msg)
    {
        switch (msg.arg1)
        {
            case ResponseCode.ERR_SUCCESS:
                analysisTTSResponse((HashMap<String, String>) msg.obj);
                
                
                break;
            case ResponseCode.ERR_NOT_INIT:
                break;
            case ResponseCode.ERR_FILE_NOT_FOUND_EXCEPTION:
                
                //deal with not found Google TTS Exception
                mTextToSpeechHandler.downloadTTS();
                
                //deal with ACCESSIBILITY page can not open Exception
                //Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                //startActivityForResult(intent, 0);
                
                break;
            case ResponseCode.ERR_UNKNOWN:
                break;
            default:
                break;
        }
        
    }
    
    public void handleMessageSWCMP(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("Get Response from CMP_SEMANTIC_WORD");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            if (message.containsKey("message"))
            {
                analysisSemanticWord(message.get("message"));
            }
            else
            {
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
            }
        }
        else
        {
            Logs.showError("[MainActivity] ERROR while sending message to CMP Controller");
            mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
        }
        
    }
    
    public void handleMessageSphinx(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            
            //stop all service
            if (null != mSpotifyHandler)
            {
                Logs.showTrace("####mSpotifyHandler");
                mSpotifyHandler.pauseMusic();
            }
            if (null != mWebMediaPlayerHandler)
            {
                mWebMediaPlayerHandler.stopPlayMediaStream();
            }
            
            //start to TTS Service
            mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_START_UP_GREETINGS, Parameters.ID_SERVICE_START_UP_GREETINGS);
        }
        else
        {
            Logs.showError("ERROR Message:" + msg.obj);
        }
        
    }
    
    public void handleMessageWebMediaPlayer(Message msg)
    {
        
        
    }
    
    public void handleMessageVoiceRecognition(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognition.stopListen();
            
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            Logs.showTrace("get voice Text: " + message.get("message"));
            
            if (!message.get("message").isEmpty())
            {
                mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                        SemanticWordCMPParameters.TYPE_REQUEST_UNKNOWN, message.get("message"));
                
                //  mSpotifyHandler.playMusic(null);
            }
        }
        else
        {
            if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
            {
                HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                Logs.showTrace("get ERROR message: " + message.get("message"));
                mVoiceRecognition.stopListen();
                
                if (message.get("message").equals("No match") || message.get("message").equals("No speech input"))
                {
                    //TTS again and listen again
                    mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
                }
                
            }
            else
            {
                //startListen first handle
            }
        }
        
        
    }
    
    public void analysisSemanticWord(String data)
    {
        try
        {
            JSONObject tmp = new JSONObject(data);
            int type = tmp.getInt("type");
            switch (type)
            {
                case SemanticWordCMPParameters.TYPE_RESPONSE_UNKNOWN:
                    //UNKNOWN Command
                    mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_SPOTIFY:
                    JSONObject music = tmp.getJSONObject("music");
                    String songID = "";
                    String songAlbum = "";
                    String songName = "";
                    if (music.has("id"))
                    {
                        songID = music.getString("id");
                    }
                    if (music.has("album"))
                    {
                        songAlbum = music.getString("album");
                    }
                    if (music.has("song"))
                    {
                        songName = music.getString("song");
                    }
                    mSpotifyHandler.playMusic(songID);
                    
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_STORY:
                    JSONObject story = tmp.getJSONObject("story");
                    String storyTitle = story.getString("title");
                    String storyHostPath = story.getString("host");
                    String storyFilePath = story.getString("story");
                    
                    mWebMediaPlayerHandler.setHostAndFilePath(storyHostPath, storyFilePath);
                    mWebMediaPlayerHandler.startPlayMediaStream();
                    mPocketSphinxHandler.startListenAction();
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_TTS:
                    String toTTS = tmp.getString("tts");
                    mTextToSpeechHandler.textToSpeech(toTTS, Parameters.ID_SERVICE_TTS_BEGIN);
                    
                    break;
            }
            
        }
        catch (JSONException e)
        {
            Logs.showError("[MainActivity] analysisSemanticWord Exception:" + e.toString());
        }
        
        
    }
    
    @Override
    protected void onPause()
    {
        Logs.showTrace("onPause");
        super.onPause();
    }
    
    public void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            if (textStatusDone)
            {
                switch (message.get("TextID"))
                {
                    case Parameters.ID_SERVICE_START_UP_GREETINGS:
                        mVoiceRecognition.startListen();
                        
                        break;
                    case Parameters.ID_SERVICE_MUSIC_BEGIN:
                        
                        
                        break;
                    case Parameters.ID_SERVICE_STORY_BEGIN:
                        
                        
                        break;
                    case Parameters.ID_SERVICE_TTS_BEGIN:
                        mPocketSphinxHandler.startListenAction();
                        
                        break;
                    case Parameters.ID_SERVICE_UNKNOWN:
                        mPocketSphinxHandler.startListenAction();
                        break;
                    
                    default:
                        break;
                    
                    
                }
                
                
            }
        }
    }
}
