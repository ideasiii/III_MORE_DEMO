package com.iii.more.main;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iii.more.ai.HttpAPIHandler;
import com.iii.more.ai.HttpAPIParameters;
import com.iii.more.dmp.device.DeviceDMPParameters;

import com.iii.more.bluetooth.ble.ReadPenBLEHandler;
import com.iii.more.bluetooth.ble.ReadPenBLEParameters;
import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;
import com.iii.more.game.zoo.ZooActivity;
import com.iii.more.logic.LogicHandler;
import com.iii.more.logic.LogicParameters;
import com.iii.more.screen.view.display.DisplayHandler;
import com.iii.more.screen.view.display.DisplayParameters;

import com.iii.more.screen.view.fab.FloatingActionButtonHandler;
import com.iii.more.screen.view.fab.FloatingActionButtonParameters;
import com.iii.more.screen.view.menu.MenuHandler;
import com.iii.more.screen.view.menu.MenuParameters;

import com.iii.more.screen.view.alterdialog.AlertDialogHandler;
import com.iii.more.screen.view.alterdialog.AlertDialogParameters;
import com.iii.more.screen.view.progressDialog.ProgressDialog;
import com.scalified.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


/**
 * //### is pending to write!!
 **/


public class MainActivity extends AppCompatActivity implements CockpitFilmMakingEventListener,
        FaceEmotionEventListener
{
    private static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN_ERROR =
            "c4b008ba-8b2f-404e-9e44-dd0605486446";
    private static final String ALERT_DIALOG_ENTER_BLE_READ_PEN_ID = "c4b008ba-8b2f-404e-9e44-dd0605486226";
    private static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN =
            "c4b008ca-8b2f-404e-9e44-dd0605482229";
    
    //show progress dialog
    private ProgressDialog mProgressDialog = null;
    
    //show Alert Dialog
    private AlertDialogHandler mAlertDialogHandler = null;
    
    //Jugo server connect
    private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    
    //AI Server connect
    private HttpAPIHandler mHttpAPIHandler = null;
    
    
    //Analysis Activity Json
    private LogicHandler mLogicHandler = null;
    
    //Analysis Display Json
    private DisplayHandler mDisplayHandler = null;
    
    //layout handler
    private MenuHandler mMenuHandler = null;
    private FloatingActionButtonHandler mFABHandler = null;
    
    //BLE connect read pen
    private ReadPenBLEHandler mReadPenBLEHandler = null;
    
    private volatile boolean isBlockFaceEmotionListener = false;
    
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[MainActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf
                    (msg.what) + " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Logs.showTrace("[MainActivity] onCreate");
        
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
        
        MainApplication mMainApp = (MainApplication) this.getApplication();
        mMainApp.setCockpitFilmMakingEventListener(this);
        
        
        mProgressDialog = new ProgressDialog(this);
        
        initAlertDialog();
        
        //showAlertDialogConfirmConnectBLEReadPen();
        init();
    }
    
    
    @Override
    public void onBackPressed()
    {
        finish();
    }
    
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
                .SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
                .SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
                .SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    
    public void initAlertDialog()
    {
        mAlertDialogHandler = new AlertDialogHandler(this);
        mAlertDialogHandler.setHandler(mHandler);
        mAlertDialogHandler.init();
    }
    
    public void initReadPen()
    {
        mReadPenBLEHandler = new ReadPenBLEHandler(this);
        mReadPenBLEHandler.setHandler(mHandler);
        mReadPenBLEHandler.init();
    }
    
    
    public void init()
    {
        Logs.showTrace("[MainActivity] start to init");
        setContentView(R.layout.main);
        mMenuHandler = new MenuHandler(this);
        mMenuHandler.setHandler(mHandler);
        mMenuHandler.setIDs(R.id.root_layout, R.id.menu_layout, R.id.arc_layout, R.id.play_btn);
        
        mFABHandler = new FloatingActionButtonHandler(this);
        mFABHandler.setHandler(mHandler);
        mFABHandler.setID(R.id.fab_btn);
        mFABHandler.init(R.drawable.start_image, 50.0f, ActionButton.Animations.SCALE_UP, ActionButton
                .Animations.SCALE_DOWN);
        
        
        TextView mTextView = (TextView) findViewById(R.id.textView);
        TextView mResultTextView = (TextView) findViewById(R.id.result_text);
        ImageView mImageView = (ImageView) findViewById(R.id.imageView);
        RelativeLayout mRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        
        CMPHandler.setIPAndPort(Parameters.CMP_HOST_IP, Parameters.CMP_HOST_PORT);
        mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
        mSemanticWordCMPHandler.setHandler(mHandler);
        
        HashMap<Integer, View> hashMapViews = new HashMap<>();
        hashMapViews.put(DisplayParameters.RELATIVE_LAYOUT_ID, mRelativeLayout);
        hashMapViews.put(DisplayParameters.TEXT_VIEW_ID, mTextView);
        hashMapViews.put(DisplayParameters.RESULT_TEXT_VIEW_ID, mResultTextView);
        hashMapViews.put(DisplayParameters.IMAGE_VIEW_ID, mImageView);
        
        mDisplayHandler = new DisplayHandler(this);
        mDisplayHandler.setHandler(mHandler);
        mDisplayHandler.setDisplayView(hashMapViews);
        mDisplayHandler.init();
        mDisplayHandler.resetAllDisplayViews();
        
        mLogicHandler = new LogicHandler(this);
        mLogicHandler.setHandler(mHandler);
        //mLogicHandler.init();
        
        
        mHttpAPIHandler = new HttpAPIHandler(this);
        mHttpAPIHandler.setHandler(mHandler);
        
        
    }
    
    
    @Override
    protected void onDestroy()
    {
        Logs.showTrace("[MainActivity] onDestroy");
        if (null != mDisplayHandler)
        {
            mDisplayHandler.killAll();
        }
        
        if (null != mReadPenBLEHandler)
        {
            mReadPenBLEHandler.disconnect();
        }
        
        super.onDestroy();
    }
    
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("[MainActivity] onStop");
        MainApplication mainApplication = (MainApplication) this.getApplication();
        mainApplication.stopFaceEmotion();
        if (null != mLogicHandler)
        {
            mLogicHandler.endAll();
        }
        
        super.onStop();
    }
    
    @Override
    protected void onPause()
    {
        Logs.showTrace("[MainActivity] onPause");
        super.onPause();
    }
    
    public void handleMessages(Message msg)
    {
        
        switch (msg.what)
        {
            case CMPParameters.CLASS_CMP_SEMANTIC_WORD:
                handleMessageSWCMP(msg);
                break;
            
            case AlertDialogParameters.CLASS_ALERT_DIALOG:
                handleMessageAlertDialog(msg);
                break;
            case FloatingActionButtonParameters.CLASS_FAB:
                handleMessageFAB(msg);
                break;
            case MenuParameters.CLASS_MENU:
                handleMessageMenu(msg);
                break;
            case LogicParameters.CLASS_LOGIC:
                handleMessageLogic(msg);
                break;
            
            case ReadPenBLEParameters.CLASS_ReadPenBLE:
                handleMessageReadPenBLE(msg);
                break;
            
            case DisplayParameters.CLASS_DISPLAY:
                handleMessageDisplay(msg);
                break;
            
            case HttpAPIParameters.CLASS_HTTP_API:
                handleMessageHttpAPI(msg);
                break;
            
            
            default:
                break;
        }
    }
    
    
    private boolean handMadeCheckBluetoothRFIDData(String inputData)
    {
        if (inputData.contains("RFID"))
        {
            JSONObject animate = new JSONObject();
            try
            {
                animate.put("type", 2);
                
                animate.put("duration", 3000);
                animate.put("repeat", 0);
                animate.put("interpolate", 1);
                
                //create display json
                JSONObject data = new JSONObject();
                data.put("time", 0);
                data.put("host", "https://smabuild.sytes.net/edubot/OCTOBO_Expressions/");
                data.put("color", "#FFA0C9EC");
                data.put("description", "快樂");
                data.put("animation", animate);
                data.put("text", new JSONObject());
                data.put("file", "OCTOBO_Expressions-17.png");
                JSONArray show = new JSONArray();
                show.put(data);
                
                JSONObject display = new JSONObject();
                display.put("enable", 1);
                display.put("show", show);
                if (null != mDisplayHandler)
                {
                    mDisplayHandler.setDisplayJson(display);
                    mDisplayHandler.startDisplay();
                }
                
                mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, "This  is  an  Apple !", "en");
                
            }
            catch (JSONException e)
            {
                Logs.showError("[MainActivity] handMadeCheckBluetoothRFIDData ERROR: " + e.toString());
            }
            return true;
        }
        return false;
        
    }
    
    
    private void handleMessageHttpAPI(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        mLogicHandler.ttsService(TTSParameters.ID_SERVICE_FRIEND_RESPONSE, message.get("message"), "zh");
    }
    
    
    private void handleMessageDisplay(Message msg)
    {
        switch (msg.arg2)
        {
            case DisplayParameters.METHOD_CLICK:
                Logs.showTrace("[MainActivity] Screen get On Click!");
                
                switch (mLogicHandler.getMode())
                {
                    case LogicParameters.MODE_STORY:
                        
                        //### pause story Streaming and call TTS to
                        //### ask question
                        mLogicHandler.pauseStoryStreaming();
                        mDisplayHandler.resetAllDisplayViews();
                        mLogicHandler.startUpStory(null, null, null);
                        break;
                    case LogicParameters.MODE_FRIEND:
                        
                        mLogicHandler.endAll();
                        mDisplayHandler.resetAllDisplayViews();
                        mLogicHandler.startUpFriend();
                        break;
                    
                    case LogicParameters.MODE_GAME:
                        
                        
                        break;
                    
                }
                break;
            default:
                break;
            
        }
    }
    
    
    private void handleMessageReadPenBLE(Message msg)
    {
        Logs.showTrace("[MainActivity] handleMessageReadPenBLE");
        switch (msg.arg2)
        {
            case ReadPenBLEParameters.METHOD_CONNECT:
                //### process bar cancel it
                mProgressDialog.dismiss();
                
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    //###
                    Logs.showTrace("[MainActivity] read pen connect success!!!");
                }
                else
                {
                    Logs.showTrace("[MainActivity] read pen connect fail !!!");
                    showAlertDialogConnectBLEReadPenError();
                }
                
                
                break;
            case ReadPenBLEParameters.METHOD_RECEIVE:
                HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                if (mLogicHandler.getMode() == LogicParameters.MODE_STORY)
                {
                    Logs.showTrace("[MainActivity] send ble data to server");
                    mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                            SemanticWordCMPParameters.TYPE_REQUEST_BLE, message.get("message"));
                }
                
                
                break;
            
            
        }
        
    }
    
    
    private void handleMessageLogic(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            switch (msg.arg2)
            {
                case LogicParameters.METHOD_VOICE:
                    switch (mLogicHandler.getMode())
                    {
                        case LogicParameters.MODE_STORY:
                            Logs.showTrace("[MainActivity] Send Story Message to Jugo Server");
                            
                            mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters
                                    .getWordID(), SemanticWordCMPParameters.TYPE_REQUEST_STORY, message.get
                                    ("message"));
                            break;
                        case LogicParameters.MODE_GAME:
                            
                            
                            break;
                        case LogicParameters.MODE_FRIEND:
                            Logs.showTrace("[MainActivity] Send Friend Message to AI Server");
                            mHttpAPIHandler.execute(message.get("message"));
                            break;
                        default:
                            
                            break;
                    }
                    break;
                
                case LogicParameters.METHOD_STORY_PAUSE:
                    //### let displayHandler pause stream
                    try
                    {
                        Logs.showTrace("[MainActivity] METHOD_STORY_PAUSE: " + message.get("message"));
                        int StoryPauseSecond = Integer.valueOf(message.get("message"));
                        mDisplayHandler.pauseDisplaying(StoryPauseSecond);
                    }
                    catch (Exception e)
                    {
                        Logs.showError("[MainActivity] StoryPauseSecond ERROR: " + e.toString());
                        e.printStackTrace();
                    }
                    
                    break;
                
                case LogicParameters.METHOD_STORY_RESUME:
                    //### get Story Pause Second  and let displayHandler know next second
                    mDisplayHandler.resumeDisplaying();
                    
                    
                    break;
                
                case LogicParameters.METHOD_TTS:
                    if (message.get("ttsID").equals(TTSParameters
                            .ID_SERVICE_INTERRUPT_STORY_EMOTION_RESPONSE))
                    {
                        // ### call faceEmotionInterruptHandler to set record emotion on
                        mHandler.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mLogicHandler.resumeStoryStreaming();
                                mDisplayHandler.resumeDisplaying();
                                Logs.showTrace("#################set isBlockFaceEmotionListener " +
                                        "false############");
                                isBlockFaceEmotionListener = false;
                            }
                        }, 3000);
                        
                        
                    }
                    
                    
                    break;
                
                
                default:
                    
                    
                    break;
                
            }
        }
    }
    
    @Override
    protected void onStart()
    {
        super.onStart();
        Logs.showTrace("[MainActivity] onStart");
        if (null != mLogicHandler)
        {
            mLogicHandler.init();
        }
        Logs.showTrace("");
        MainApplication mainApplication = (MainApplication) getApplication();
        mainApplication.setFaceEmotionEventListener(this);
        mainApplication.startFaceEmotion();
    }
    
    private void handleMessageMenu(Message msg)
    {
        //hide menu
        mMenuHandler.hideMenu(mFABHandler.getFabX(), mFABHandler.getFabY(), mFABHandler.getFabRadius());
        
        //show fab
        
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mFABHandler.show();
            }
        }, 1200);
        
        
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (Integer.valueOf(message.get("onClick")))
        {
            case R.id.play_btn:
                Toast.makeText(this, "點選遊戲模式", Toast.LENGTH_SHORT).show();
                mLogicHandler.startUp(TTSParameters.ID_SERVICE_START_UP_GREETINGS_GAME_MODE);
                
                mHandler.postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        mLogicHandler.endAll();
                        
                        //###
                        // new Zoo Activity Intent
                        
                        // startActivity(,new Intent());
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, ZooActivity.class);
                        startActivity(intent);
                        
                    }
                }, 2000);
                
                
                break;
            case R.id.story_btn:
                Toast.makeText(this, "這點選故事模式", Toast.LENGTH_SHORT).show();
                mLogicHandler.startUp(TTSParameters.ID_SERVICE_START_UP_GREETINGS_STORY_MODE);
                
                break;
            case R.id.friend_btn:
                Toast.makeText(this, "點選交友模式", Toast.LENGTH_SHORT).show();
                mLogicHandler.startUp(TTSParameters.ID_SERVICE_START_UP_GREETINGS_FRIEND_MODE);
                break;
            default:
                Logs.showError("unknown button !");
                break;
            
            
        }
        
        
        //launch service
        
        
    }
    
    private void handleMessageFAB(Message msg)
    {
        //hide button
        mFABHandler.hide();
        
        //show menu layout
        mMenuHandler.showMenu(mFABHandler.getFabX(), mFABHandler.getFabY(), mFABHandler.getFabRadius());
        
        //stop logic service
        mLogicHandler.setMode(LogicParameters.MODE_UNKNOWN);
        
        mLogicHandler.endAll();
        mDisplayHandler.resetAllDisplayViews();
    }
    
    
    private void showAlertDialogEnterBLEReadPenID()
    {
        mAlertDialogHandler.setText(ALERT_DIALOG_ENTER_BLE_READ_PEN_ID, "章魚點讀筆", "請輸入章魚點讀筆ID", "OK", "",
                true);
        mAlertDialogHandler.setEditText(Parameters.DEFAULT_DEVICE_ID);
        mAlertDialogHandler.show();
    }
    
    private void showAlertDialogConfirmConnectBLEReadPen()
    {
        mAlertDialogHandler.setText(ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN, "章魚點讀筆", "是否要與章魚點讀筆連線",
                "是的", "不要", false);
        mAlertDialogHandler.show();
    }
    
    private void showAlertDialogConnectBLEReadPenError()
    {
        mAlertDialogHandler.setText(ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN_ERROR, "章魚點讀筆",
                "與裝置Bluetooth點讀筆連線失敗，請先確認智慧型裝置Bluetooth是否開啟或是章魚裝置Bluetooth是否開起，重新再試一次!", "是", "", false);
        mAlertDialogHandler.show();
    }
    
    
    public void handleMessageAlertDialog(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("[MainActivity] in handleMessageAlertDialog");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            Logs.showTrace("[MainActivity] id:" + message.get("id"));
            switch (message.get("id"))
            {
                case ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN_ERROR:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        finish();
                    }
                    break;
                case ALERT_DIALOG_ENTER_BLE_READ_PEN_ID:
                    
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        Logs.showTrace("[MainActivity] getText:" + message.get("edit"));
                        DeviceDMPParameters.setDeviceID(message.get("edit"));
                        
                        //###
                        // connect ble read pen
                        initReadPen();
                        
                        //###
                        // show process bar
                        mProgressDialog.init("connecting BLE read pen...");
                        mProgressDialog.show();
                        
                        
                    }
                    break;
                case ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        showAlertDialogEnterBLEReadPenID();
                    }
                    else if (message.get("message").equals(AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON))
                    {
                        init();
                    }
                    break;
            }
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
                mLogicHandler.onError(TTSParameters.ID_SERVICE_UNKNOWN);
            }
        }
        else
        {
            //異常例外處理
            Logs.showError("[MainActivity] ERROR while sending message to CMP Controller");
            
            //call logicHandler onERROR
            mLogicHandler.onError(TTSParameters.ID_SERVICE_UNKNOWN);
        }
    }
    
    
    public void analysisSemanticWord(String data)
    {
        try
        {
            JSONObject responseData = new JSONObject(data);
            
            if (responseData.has("display"))
            {
                Logs.showTrace("[MainActivity] display Data:" + responseData.getJSONObject("display")
                        .toString());
                if (responseData.getJSONObject("display").length() != 0)
                {
                    mDisplayHandler.resetAllDisplayViews();
                    mDisplayHandler.setDisplayJson(responseData.getJSONObject("display"));
                    mDisplayHandler.startDisplay();
                }
                else
                {
                    Logs.showError("[MainActivity] No Display Data!!");
                }
            }
            
            if (responseData.has("activity"))
            {
                Logs.showTrace("[MainActivity] activity Data:" + responseData.getJSONObject("activity")
                        .toString());
                if (responseData.getJSONObject("activity").length() != 0)
                {
                    mLogicHandler.setActivityJson(responseData.getJSONObject("activity"));
                    mLogicHandler.startActivity();
                }
                else
                {
                    Logs.showError("[MainActivity] No Activity Data!!");
                }
                
            }
            else
            {
                Logs.showError("[MainActivity] No Activity Data!!");
                
            }
        }
        catch (JSONException e)
        {
            mLogicHandler.onError(TTSParameters.ID_SERVICE_IO_EXCEPTION);
            Logs.showError("[MainActivity] analysisSemanticWord Exception:" + e.toString());
        }
    }
    
    @Override
    public void onTTS(Object sender, String text, String lang)
    {
        Logs.showTrace("[MainActivity] remote mode Get TTS: " + text);
        if (null != mLogicHandler)
        {
            if (mLogicHandler.getMode() == LogicParameters.MODE_UNKNOWN)
            {
                mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, text, "zh");
            }
        }
    }
    
    @Override
    public void onEmotionImage(Object sender, String imageFilename)
    {
        if (null != mLogicHandler && mLogicHandler.getMode() == LogicParameters.MODE_UNKNOWN)
        {
            JSONObject animate = new JSONObject();
            try
            {
                animate.put("type", 5);
                
                animate.put("duration", 1000);
                animate.put("repeat", 1);
                animate.put("interpolate", 1);
                
                //create display json
                JSONObject data = new JSONObject();
                data.put("time", 0);
                data.put("host", "https://smabuild.sytes.net/edubot/OCTOBO_Expressions/");
                data.put("color", "#FFA0C9EC");
                data.put("description", "快樂");
                data.put("animation", animate);
                data.put("text", new JSONObject());
                data.put("file", imageFilename);
                JSONArray show = new JSONArray();
                show.put(data);
                
                JSONObject display = new JSONObject();
                display.put("enable", 1);
                display.put("show", show);
                if (null != mDisplayHandler)
                {
                    mDisplayHandler.setDisplayJson(display);
                    mDisplayHandler.startDisplay();
                }
            }
            catch (JSONException e)
            {
                Logs.showError(e.toString());
            }
        }
    }
    
    
    @Override
    public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String, String> ttsHashMap,
            HashMap<String, String> imageHashMap, Object extendData)
    {
        Logs.showTrace("[MainActivity] faceEmotionData: " + faceEmotionData);
        if (null != ttsHashMap)
        {
            Logs.showTrace("[MainActivity] ttsEmotionData: " + ttsHashMap);
        }
        if (!isBlockFaceEmotionListener)
        {
            
            if (mLogicHandler.getMode() == LogicParameters.MODE_STORY)
            {
                
                if (null != mLogicHandler)
                {
                    
                    if (null != ttsHashMap)
                    {
                        isBlockFaceEmotionListener = true;
                        
                        //pause story
                        mLogicHandler.pauseStoryStreaming();
                        
                        
                        Logs.showTrace("[MainActivity] tts" + ttsHashMap.get(FaceEmotionInterruptParameters
                                .STRING_TTS_TEXT));
                        mLogicHandler.ttsService(TTSParameters.ID_SERVICE_INTERRUPT_STORY_EMOTION_RESPONSE,
                                ttsHashMap.get(FaceEmotionInterruptParameters.STRING_TTS_TEXT), "zh");
                        
                        
                    }
                }
                if (null != imageHashMap)
                {
                    mDisplayHandler.setImageViewImageFromDrawable(mDisplayHandler.getDrawableFromFileName
                            (imageHashMap.get("IMG_FILE_NAME")));
                }
                
            }
        }
    }
    
    
    private void showDisplayImage(String imageFilename)
    {
        JSONObject animate = new JSONObject();
        try
        {
            animate.put("type", 5);
            
            animate.put("duration", 1000);
            animate.put("repeat", 1);
            animate.put("interpolate", 1);
            
            //create display json
            JSONObject data = new JSONObject();
            data.put("time", 0);
            data.put("host", "https://smabuild.sytes.net/edubot/OCTOBO_Expressions/");
            data.put("color", "#FFA0C9EC");
            data.put("description", "快樂");
            data.put("animation", animate);
            data.put("text", new JSONObject());
            data.put("file", imageFilename);
            JSONArray show = new JSONArray();
            show.put(data);
            
            JSONObject display = new JSONObject();
            display.put("enable", 1);
            display.put("show", show);
            if (null != mDisplayHandler)
            {
                mDisplayHandler.setDisplayJson(display);
                mDisplayHandler.startDisplay();
            }
        }
        catch (JSONException e)
        {
            Logs.showError(e.toString());
        }
        
    }
    
    @Override
    public void onFaceDetectResult(boolean isDetectFace)
    {
        Logs.showTrace("[MainActivity] Face Detect: " + String.valueOf(isDetectFace));
    }
}
