package com.iii.more.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii.more.cmp.semantic.SemanticWordCMPParameters;

import iii.ideas.ideassphinx.pocketshinx.PocketSphinxHandler;
import iii.ideas.ideassphinx.pocketshinx.PocketSphinxParameters;

import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.download.image.ImageDownloadHandler;
import com.iii.more.init.InitCheckBoard;
import com.iii.more.init.InitCheckBoardParameters;
import com.iii.more.spotify.SpotifyHandler;
import com.iii.more.spotify.SpotifyParameters;
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
    
    private InitCheckBoard mInitCheckBoard = null;
    
    private TextView mTextView = null;
    private TextView mResultTextView = null;
    private ImageView mImageView = null;
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
        mTextView = (TextView) findViewById(R.id.textView);
        mResultTextView = (TextView) findViewById(R.id.result_text);
        mImageView = (ImageView) findViewById(R.id.imageView);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.RECORD_AUDIO);
        mRuntimePermissionHandler = new RuntimePermissionHandler(this, permissions);
        mRuntimePermissionHandler.setHandler(mHandler);
        mRuntimePermissionHandler.startRequestPermissions();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Logs.showTrace("mSpotifyHandler onActivityResult!!");
        mSpotifyHandler.onActivityResult(requestCode, resultCode, data);
    }
    
    public void init()
    {
        mInitCheckBoard = new InitCheckBoard(this);
        mInitCheckBoard.setHandler(mHandler);
        mInitCheckBoard.init();
        
        mPocketSphinxHandler = new PocketSphinxHandler(this);
        mPocketSphinxHandler.setHandler(mHandler);
        // mPocketSphinxHandler.setKeyWord("資策會");
        // mPocketSphinxHandler.setLanguageLocation("zh-tw");
        
        mTextToSpeechHandler = new TextToSpeechHandler(this);
        mTextToSpeechHandler.setHandler(mHandler);
        mTextToSpeechHandler.init();
        
        mVoiceRecognition = new VoiceRecognition(this);
        mVoiceRecognition.setHandler(mHandler);
        mVoiceRecognition.setLocale(Locale.TAIWAN);
        
        CMPHandler.setIPAndPort(Parameters.CMP_HOST_IP, Parameters.CMP_HOST_PORT);
        mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
        mSemanticWordCMPHandler.setHandler(mHandler);
        
        mWebMediaPlayerHandler = new WebMediaPlayerHandler(this);
        mWebMediaPlayerHandler.setHandler(mHandler);
        
        mSpotifyHandler = new SpotifyHandler(this);
        mSpotifyHandler.setHandler(mHandler);
        mSpotifyHandler.init();
        
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
        super.onDestroy();
    }
    
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        endAll();
        finish();
        super.onStop();
    }
    
    @Override
    protected void onPause()
    {
        Logs.showTrace("onPause");
        super.onPause();
    }
    
    public void endAll()
    {
        mPocketSphinxHandler.stopListenAction();
        
        mTextToSpeechHandler.stop();
        mTextToSpeechHandler.shutdown();
        
        mSpotifyHandler.closeSpotify();
        
        mWebMediaPlayerHandler.stopPlayMediaStream();
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
            case SpotifyParameters.CLASS_SPOTIFY:
                handleMessageSpotify(msg);
                break;
            case InitCheckBoardParameters.CLASS_INIT:
                handleMessageInitCheckBoard(msg);
                break;
            default:
                break;
        }
    }
    
    public void handleMessageInitCheckBoard(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_INIT_SUCCESS, Parameters.ID_SERVICE_INIT_SUCCESS);
        }
        
    }
    
    public void handleMessageSpotify(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        Logs.showTrace("msg.arg2: " + String.valueOf(msg.arg2) + " message:" + message);
        if (msg.arg2 == SpotifyParameters.METHOD_INIT)
        {
            if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                InitCheckBoard.setSpotifyInit(true);
            }
            else
            {
                Logs.showError("ERROR message" + message.get("message"));
            }
            
        }
        else
        {
            if (msg.arg1 == ResponseCode.ERR_SUCCESS)
            {
                
                
                if (message.get("message").equals("DONE"))
                {
                    //歌曲結束後
                    
                    
                }
            }
            else
            {
                //異常例外處理
                mSpotifyHandler.pauseMusic();
                mPocketSphinxHandler.stopListenAction();
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_SPOTIFY_UNAUTHORIZED, Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED);
                
                
            }
        }
        
    }
    
    public void handleMessagePermission(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //start to init
            Logs.showTrace("start to init!");
            init();
            //Logs.showTrace("start to say init success");
            //mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_INIT_SUCCESS, Parameters.ID_SERVICE_INIT_SUCCESS);
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
                Logs.showError("TTS not init success");
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
                onError(Parameters.ID_SERVICE_UNKNOWN);
            }
        }
        else
        {
            //異常例外處理
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
            //異常例外處理
            Logs.showError("ERROR Message:" + msg.obj);
        }
        
    }
    
    public void handleMessageWebMediaPlayer(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case WebMediaPlayerParameters.COMPLETE_PLAY:
                    mWebMediaPlayerHandler.stopPlayMediaStream();
                    // mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    break;
                case WebMediaPlayerParameters.START_PLAY:
                    break;
                case WebMediaPlayerParameters.STOP_PLAY:
                    break;
                default:
                    break;
            }
        }
        else
        {
            //異常例外處理
            onError(Parameters.ID_SERVICE_IO_EXCEPTION);
        }
        
        
    }
    
    public void handleMessageVoiceRecognition(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS && msg.arg2 == ResponseCode.METHOD_RETURN_TEXT_VOICE_RECOGNIZER)
        {
            mVoiceRecognition.stopListen();
            
            final HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            Logs.showTrace("get voice Text: " + message.get("message"));
            
            if (!message.get("message").isEmpty())
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mTextView.setText("您說的是: \"" + message.get("message") + " \"");
                    }
                });
                mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                        SemanticWordCMPParameters.TYPE_REQUEST_UNKNOWN, message.get("message"));
                
                //mSpotifyHandler.playMusic(null);
            }
        }
        else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //startListen first handle
        }
        else if (msg.arg1 == ResponseCode.ERR_SPEECH_ERRORMESSAGE)
        {
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            Logs.showTrace("get ERROR message: " + message.get("message"));
            mVoiceRecognition.stopListen();
            
            if (message.get("message").equals("No match") || message.get("message").equals("No speech input"))
            {
                //TTS again and listen again
                onError(Parameters.ID_SERVICE_UNKNOWN);
            }
            
        }
        else
        {
            
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
                    onError(Parameters.ID_SERVICE_UNKNOWN);
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_SPOTIFY:
                    JSONObject music = tmp.getJSONObject("music");
                    
                    final String songID;
                    final String songAlbum;
                    final String songName;
                    final String songArtist;
                    final String songImageURL;
                    int source_from = -1;
                    if (music.has("source"))
                    {
                        source_from = music.getInt("source");
                    }
                    
                    if (music.has("id"))
                    {
                        songID = music.getString("id");
                    }
                    
                    else
                    {
                        songID = "";
                    }
                    if (music.has("album"))
                    {
                        songAlbum = music.getString("album");
                    }
                    else
                    {
                        songAlbum = "";
                    }
                    if (music.has("song"))
                    {
                        songName = music.getString("song");
                    }
                    else
                    {
                        songName = "";
                    }
                    if (music.has("artist"))
                    {
                        songArtist = music.getString("artist");
                    }
                    else
                    {
                        songArtist = "";
                    }
                    if (music.has("cover"))
                    {
                        songImageURL = music.getString("cover");
                        
                    }
                    else
                    {
                        songImageURL = "https://i.scdn.co/image/be3210e0838ec7c2f40155d47ca80420e8b080a0";
                    }
                    switch (source_from)
                    {
                        case 1:
                            if (music.has("host") && music.has("file"))
                            {
                                mWebMediaPlayerHandler.setHostAndFilePath(music.getString("host"), music.getString("file"));
                                mWebMediaPlayerHandler.startPlayMediaStream();
                                mPocketSphinxHandler.startListenAction(Parameters.MEDIA_PLAYED_SPHINX_THRESHOLD);
                            }
                            else
                            {
                                onError(Parameters.ID_SERVICE_IO_EXCEPTION);
                            }
                            break;
                        case 2:
                            if (songID.isEmpty())
                            {
                                //mPocketSphinxHandler.stopListenAction();
                                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_SPOTIFY_UNAUTHORIZED, Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED);
                                return;
                            }
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    ImageDownloadHandler mImageDownloadHandler = new ImageDownloadHandler(mImageView);
                                    mImageDownloadHandler.execute(songImageURL);
                                    
                                    mResultTextView.setText("目前Spotify播放: 歌手:" + songArtist + " 專輯:" + songAlbum + " 歌曲:" + songName);
                                }
                            });
                            
                            
                            mSpotifyHandler.playMusic(songID);
                            mPocketSphinxHandler.startListenAction(Parameters.MEDIA_PLAYED_SPHINX_THRESHOLD);
                            break;
                        default:
                            onError(Parameters.ID_SERVICE_UNKNOWN);
                            break;
                    }
                    
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_STORY:
                    JSONObject story = tmp.getJSONObject("story");
                    Logs.showTrace("Story: Json :" + story.toString());
                    final String storyTitle = story.getString("story");
                    
                    
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mResultTextView.setText("目前故事播放名稱: " + storyTitle);
                        }
                    });
                    if (story.has("host") && story.has("file"))
                    {
                        mWebMediaPlayerHandler.setHostAndFilePath(story.getString("host"), story.getString("file"));
                        mWebMediaPlayerHandler.startPlayMediaStream();
                        mPocketSphinxHandler.startListenAction(Parameters.MEDIA_PLAYED_SPHINX_THRESHOLD);
                    }
                    else
                    {
                        onError(Parameters.ID_SERVICE_IO_EXCEPTION);
                    }
                    
                    
                    break;
                case SemanticWordCMPParameters.TYPE_RESPONSE_TTS:
                    
                    mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    
                    final String toTTS = tmp.getString("tts");
                    mTextToSpeechHandler.textToSpeech(toTTS, Parameters.ID_SERVICE_TTS_BEGIN);
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            mResultTextView.setText("目前TTS輸出: " + toTTS);
                        }
                    });
                    break;
            }
            
        }
        catch (JSONException e)
        {
            onError(Parameters.ID_SERVICE_IO_EXCEPTION);
            Logs.showError("[MainActivity] analysisSemanticWord Exception:" + e.toString());
        }
        
        
    }
    
    public void onError(String index)
    {
        switch (index)
        {
            case Parameters.ID_SERVICE_IO_EXCEPTION:
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_IO_EXCEPTION, Parameters.ID_SERVICE_IO_EXCEPTION);
                break;
            default:
                mTextToSpeechHandler.textToSpeech(Parameters.STRING_SERVICE_UNKNOWN, Parameters.ID_SERVICE_UNKNOWN);
                break;
        }
    }
    
    
    public void analysisTTSResponse(HashMap<String, String> message)
    {
        if (message.containsKey("TextID") && message.containsKey("TextStatus"))
        {
            boolean textStatusDone = message.get("TextStatus").equals("DONE");
            boolean textStatusStart = message.get("TextStatus").equals("START");
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
                        
                        
                        break;
                    case Parameters.ID_SERVICE_UNKNOWN:
                        mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        break;
                    case Parameters.ID_SERVICE_IO_EXCEPTION:
                        mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        break;
                    case Parameters.ID_SERVICE_INIT_SUCCESS:
                        Logs.showTrace("ID_SERVICE_INIT_SUCCESS");
                        mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                        break;
                    case Parameters.ID_SERVICE_SPOTIFY_UNAUTHORIZED:
                        mPocketSphinxHandler.startListenAction(Parameters.DEFAULT_SPHINX_THRESHOLD);
                    default:
                        break;
                    
                }
                
                
            }
            if (textStatusStart)
            {
                switch (message.get("TextID"))
                {
                    case Parameters.ID_SERVICE_START_UP_GREETINGS:
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mTextView.setText("");
                                mResultTextView.setText("");
                                mImageView.setImageResource(android.R.color.transparent);
                            }
                        });
                        
                        break;
                    default:
                        break;
                }
            }
        }
        else if (message.get("message").equals("init success"))
        {
            InitCheckBoard.setTTSInit(true);
            
        }
    }
    
    
}
