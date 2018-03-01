package com.iii.more.main;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
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
import com.iii.more.clock.setting.AlarmHandler;
import com.iii.more.dmp.device.DeviceDMPParameters;

import com.iii.more.bluetooth.ble.ReadPenBLEHandler;
import com.iii.more.bluetooth.ble.ReadPenBLEParameters;
import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.emotion.EmotionParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;
import com.iii.more.game.zoo.ZooActivity;
import com.iii.more.logic.LogicHandler;
import com.iii.more.logic.LogicParameters;
import com.iii.more.main.listeners.CockpitFilmMakingEventListener;
import com.iii.more.main.listeners.CockpitSensorEventListener;
import com.iii.more.main.listeners.FaceEmotionEventListener;
import com.iii.more.main.track.MainTracker;
import com.iii.more.screen.view.display.DisplayHandler;
import com.iii.more.screen.view.display.DisplayParameters;

import com.iii.more.screen.view.fab.FloatingActionButtonHandler;
import com.iii.more.screen.view.fab.FloatingActionButtonParameters;
import com.iii.more.screen.view.menu.MenuHandler;
import com.iii.more.screen.view.menu.MenuParameters;

import com.iii.more.screen.view.alterdialog.AlertDialogHandler;
import com.iii.more.screen.view.alterdialog.AlertDialogParameters;
import com.iii.more.screen.view.progressDialog.ProgressDialog;
import com.iii.more.setting.Pref;
import com.iii.more.setting.SettingLv1Activity;
import com.iii.more.setting.struct.Sleep;
import com.scalified.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;


/**
 * //### is pending to write!!
 **/


public class MainActivity extends AppCompatActivity implements CockpitFilmMakingEventListener,
    FaceEmotionEventListener, CockpitSensorEventListener
{
    private static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN_ERROR =
        "c4b008ba-8b2f-404e-9e44-dd0605486446";
    private static final String ALERT_DIALOG_ENTER_BLE_READ_PEN_ID = "c4b008ba-8b2f-404e-9e44-dd0605486226";
    private static final String ALERT_DIALOG_CONFIRM_CONNECT_BLE_READ_PEN =
        "c4b008ca-8b2f-404e-9e44-dd0605482229";

    private static final String ID_FAB_SETTING_BUTTON = "7cdfc215-d856-4aed-a7b3-15d82c7b1678";
    private static final String ID_FAB_MENU_BUTTON = "1d1bd5f6-5bec-4c13-86a3-0d02a1421ad2";


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
    private FloatingActionButtonHandler mFABMenuHandler = null;
    private FloatingActionButtonHandler mFABSettingHandler = null;
    //BLE connect read pen
    private ReadPenBLEHandler mReadPenBLEHandler = null;

    //it can block face listen let it don't get message
    private volatile boolean isBlockFaceEmotionListener = false;

    //track user behavior
    private MainTracker mMainTracker = null;

    private AlarmHandler mAlarmHandler = null;


    private boolean isStoryAskAIFirst = false;

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

        mFABSettingHandler = new FloatingActionButtonHandler(this);
        mFABSettingHandler.setHandler(mHandler);
        mFABSettingHandler.setViewID(R.id.fab_setting_btn);
        mFABSettingHandler.setFabID(ID_FAB_SETTING_BUTTON);
        mFABSettingHandler.init(R.mipmap.fab_setting, 60.0f, 65.0f, ActionButton.Animations
            .ROLL_FROM_RIGHT, ActionButton.Animations.ROLL_TO_RIGHT, false);

        mFABMenuHandler = new FloatingActionButtonHandler(this);
        mFABMenuHandler.setHandler(mHandler);
        mFABMenuHandler.setViewID(R.id.fab_btn);
        mFABMenuHandler.setFabID(ID_FAB_MENU_BUTTON);
        mFABMenuHandler.init(R.drawable.start_image, 70.0f, 75.0f, ActionButton.Animations.SCALE_UP,
            ActionButton.Animations.SCALE_DOWN, true);


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

        mHttpAPIHandler = new HttpAPIHandler(this);
        mHttpAPIHandler.setHandler(mHandler);

        //set on device sensor
        ((MainApplication) getApplication()).setCockpitSensorEventListener(this);

        mMainTracker = new MainTracker(this);

        mAlarmHandler = new AlarmHandler(this);
        mAlarmHandler.setHandler(mHandler);
        mAlarmHandler.init();
    }


    @Override
    protected void onDestroy()
    {
        Logs.showTrace("[MainActivity] onDestroy");

        MainApplication mainApplication = (MainApplication) this.getApplication();
        mainApplication.stopFaceEmotion();

        if (null != mDisplayHandler)
        {
            mDisplayHandler.killAll();
        }

        if (null != mReadPenBLEHandler)
        {
            mReadPenBLEHandler.disconnect();
        }

        mLogicHandler.unBindTTSListenersToMainApplication();

        super.onDestroy();
    }


    @Override
    protected void onStop()
    {
        Logs.showTrace("[MainActivity] onStop");

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


    private void handleMessageHttpAPI(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.arg2)
        {
            case HttpAPIParameters.METHOD_HTTP_GET_RESPONSE:

                mLogicHandler.ttsService(TTSParameters.ID_SERVICE_FRIEND_RESPONSE, message.get("message"),
                    "zh");
                break;
            case HttpAPIParameters.METHOD_HTTP_POST_RESPONSE:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    //####
                    // success get http post data and judge what
                    JSONObject data = null;
                    try
                    {
                        data = new JSONObject(message.get("message"));
                        //data.put("TTS", new String(data.getString("TTS").getBytes("UTF-8"), "UTF-8"));
                        Logs.showTrace("[MainActivity] http post get Data: " + data.toString() /*data
                        .toString()*/);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();

                    }

                    //  if (null != data)
                    {
                        mLogicHandler.storyModeAPIAnalysis(message.get("message"));
                    }
                    //else
                    {
                        //  Logs.showError("[MainActivity] METHOD_HTTP_POST_RESPONSE ERROR");
                    }

                }
                else
                {
                    //####
                    //io exception handle
                    mLogicHandler.storyModeAPIAnalysis(HttpAPIParameters.ERROR_POST_DEFAULT_RETURN);

                }


                break;
        }

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
                        startUpStoryMode();


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
                            String sttID = message.get("sttID");
                            if (null != sttID)
                            {
                                if (sttID.equals(STTParameters.ID_FOR_SEMITIC_WORD_USED))
                                {
                                    Logs.showTrace("[MainActivity] Send Story Message to Jugo Server");

                                    mSemanticWordCMPHandler.sendSemanticWordCommand
                                        (SemanticWordCMPParameters.getWordID(), SemanticWordCMPParameters
                                            .TYPE_REQUEST_STORY, message.get("message"));
                                }
                                else if (sttID.equals(STTParameters.ID_FOR_AI_CHART_BOT_USED))
                                {
                                    // ####
                                    // write to send STT to Http Ai API
                                    HashMap<String, String> postBackData = new HashMap<>();

                                    if (isStoryAskAIFirst)
                                    {
                                        postBackData.put("STT", Parameters.CHEAT_STT);
                                        isStoryAskAIFirst = false;
                                    }
                                    else
                                    {
                                        postBackData.put("STT", message.get("message"));
                                    }

                                    if (null != mHttpAPIHandler)
                                    {
                                        mHttpAPIHandler.executeByPost("https://chatbot.srm.pw/edubot/",
                                            postBackData, true);
                                    }
                                }
                            }
                            break;
                        case LogicParameters.MODE_GAME:


                            break;
                        case LogicParameters.MODE_FRIEND:
                            Logs.showTrace("[MainActivity] Send Friend Message to AI Server");
                            mHttpAPIHandler.executeByGet(message.get("message"));
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
                    isBlockFaceEmotionListener = false;

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

        mainApplication.stopFaceEmotion();

        mainApplication.startFaceEmotion();

        mainApplication.setTTSPitch(1.0f, 1.0f);

        Pref mPref = new Pref(this);

        // ### pending to write
        mAlarmHandler.clearAlarmData();
        mAlarmHandler.addAlarmData(mPref.getSleep());
        mAlarmHandler.addAlarmData(mPref.getBrush());
        mAlarmHandler.startSetAlarms();



    }

    private void handleMessageMenu(Message msg)
    {
        //hide menu
        mMenuHandler.hideMenu(mFABMenuHandler.getFabX(), mFABMenuHandler.getFabY(), mFABMenuHandler
            .getFabRadius());

        //hide setting fab
        mFABSettingHandler.hide();


        //show menu fab
        mHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                mFABMenuHandler.show();
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
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        if (message.get("fabID").equals(ID_FAB_MENU_BUTTON))
        {

            //hide button
            mFABMenuHandler.hide();

            //show menu layout
            mMenuHandler.showMenu(mFABMenuHandler.getFabX(), mFABMenuHandler.getFabY(), mFABMenuHandler
                .getFabRadius());

            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    mFABSettingHandler.show();
                }
            }, 1200);


            //stop logic service
            mLogicHandler.setMode(LogicParameters.MODE_UNKNOWN);

            mLogicHandler.endAll();
            mDisplayHandler.resetAllDisplayViews();
        }
        else if (message.get("fabID").equals(ID_FAB_SETTING_BUTTON))
        {

            //end service
            mLogicHandler.setMode(LogicParameters.MODE_UNKNOWN);
            mLogicHandler.endAll();

            Logs.showTrace("[MainActivity] %% Hide FAB Setting %%");
            mFABSettingHandler.hide();

            mMenuHandler.hideMenu(mFABMenuHandler.getFabX(), mFABMenuHandler.getFabY(), mFABMenuHandler
                .getFabRadius());

            Toast.makeText(this, "進入家長模式", Toast.LENGTH_SHORT).show();

            // ### pending Ready write
            Logs.showTrace("[MainActivity] %% Enter Ready Setting Page %%");
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {

                    mFABMenuHandler.show();
                }
            }, 1000);


            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SettingLv1Activity.class);
                    startActivity(intent);
                }
            }, 1500);

        }
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
                /*
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
                }*/

                JSONObject defaultDisplay = new JSONObject(DisplayParameters.JSON_DISPLAY_STORY_DEFAULT);

                Logs.showTrace("[MainActivity] display Data:" + defaultDisplay.toString());

                mDisplayHandler.resetAllDisplayViews();
                mDisplayHandler.setDisplayJson(defaultDisplay);
                mDisplayHandler.startDisplay();
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
                data.put("host", "https://ryejuice.sytes.net/edubot/OCTOBO_Expressions/");
                data.put("color", "#6d94d5");
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
    public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String, String>
        ttsHashMap, HashMap<String, String> imageHashMap, Object extendData)
    {
        HashMap<String, String> emotionDetailHashMap = null;
        emotionDetailHashMap = (HashMap<String, String>) extendData;
        //debug using start
        if (null != faceEmotionData)
        {
            Logs.showTrace("[MainActivity]onFaceEmotionResult faceEmotionData: " + faceEmotionData);
        }
        /*if (null != ttsHashMap)
        {
            Logs.showTrace("[MainActivity]onFaceEmotionResult ttsHashMap: " + ttsHashMap);
        }
        if (null != imageHashMap)
        {
            Logs.showTrace("[MainActivity]onFaceEmotionResult imageHashMap: " + imageHashMap);
        }
        if (null != extendData)
        {
            emotionDetailHashMap = (HashMap<String, String>) extendData;
            Logs.showTrace("[MainActivity]onFaceEmotionResult extendData: " + emotionDetailHashMap);

        }*/
        //debug using end

        Logs.showTrace("[MainActivity]%%%%isBlockFaceEmotionListener:%%%%" + String.valueOf
            (isBlockFaceEmotionListener));

        if (!isBlockFaceEmotionListener)
        {
            //add Tracker data
            HashMap<String, Object> trackerData = new HashMap<>();

            trackerData.put("Source", "2");
            trackerData.put("Description", "多型態智能 face affectiva present");
            trackerData.put("FaceEmotionName", faceEmotionData.get(FaceEmotionInterruptParameters
                .STRING_EMOTION_NAME));

            //image file data
            HashMap<String, String> imageHashMapObj = new HashMap<>();
            if (null != imageHashMap.get(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME))
            {
                imageHashMapObj.put("File", imageHashMap.get(FaceEmotionInterruptParameters
                    .STRING_IMG_FILE_NAME));

                trackerData.put("RobotFace", imageHashMapObj);
            }

            //add tts data
            if (null != ttsHashMap)
            {
                HashMap<String, Object> ttsHashMapObj = new HashMap<>();

                ttsHashMapObj.put("Text", ttsHashMap.get(FaceEmotionInterruptParameters.STRING_TTS_TEXT));
                ttsHashMapObj.put("Pitch", ttsHashMap.get(FaceEmotionInterruptParameters.STRING_TTS_PITCH));
                ttsHashMapObj.put("Speed", ttsHashMap.get(FaceEmotionInterruptParameters.STRING_TTS_SPEED));

                trackerData.put("TTS", ttsHashMapObj);
            }
            ((MainApplication) getApplication()).sendToTrackerWithObjectMap(trackerData);

            //debug using
            //Logs.showTrace("[MainActivity]onFaceEmotionResult Tracker Data: " + trackerData);

            if (mLogicHandler.getMode() == LogicParameters.MODE_STORY)
            {
                if (null != mLogicHandler)
                {
                    //濾除 不專心 表情
                    if (!faceEmotionData.get(FaceEmotionInterruptParameters.STRING_EMOTION_NAME).equals
                        (EmotionParameters.STRING_EXPRESSION_ATTENTION))
                    {
                        if (Parameters.IS_STORY_MODE_USE_TASK_COMPOSER_EMOTION_TTS)
                        {
                            if (null != ttsHashMap)
                            {
                                isBlockFaceEmotionListener = true;

                                //pause story
                                mLogicHandler.pauseStoryStreaming();

                                //Logs.showTrace("[MainActivity] tts" + ttsHashMap.get
                                //     (FaceEmotionInterruptParameters.STRING_TTS_TEXT));
                                mLogicHandler.ttsService(TTSParameters
                                    .ID_SERVICE_INTERRUPT_STORY_EMOTION_RESPONSE, ttsHashMap.get
                                    (FaceEmotionInterruptParameters.STRING_TTS_TEXT), "zh");

                            }
                            else
                            {
                                if (mLogicHandler.getIsPlayingStory())
                                {
                                    isBlockFaceEmotionListener = true;
                                    //pause story
                                    mLogicHandler.pauseStoryStreaming();
                                    Logs.showTrace("[MainActivity]*** now No TTS HashMap and pause Story "
                                        + "Streaming!");
                                    mHandler.postDelayed(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            Logs.showTrace("[MainActivity]*** now No TTS HashMap and " +
                                                "resume" + " " + "Story " + "Streaming!");
                                            mLogicHandler.resumeStoryStreaming();
                                            mDisplayHandler.resumeDisplaying();
                                            isBlockFaceEmotionListener = false;
                                        }
                                    }, 5000);
                                }
                            }
                        }
                        else
                        {

                            if (mLogicHandler.getIsPlayingStory())
                            {
                                isBlockFaceEmotionListener = true;
                                mLogicHandler.pauseStoryStreaming();

                                //####
                                // call to http AI API


                                HashMap<String, String> postData = new HashMap<>(emotionDetailHashMap);
                                postData.put("STORYNAME", mLogicHandler.getIsPlayingStoryName());
                                postData.put("EMOTIONSTATE", faceEmotionData.get
                                    (FaceEmotionInterruptParameters.STRING_EMOTION_NAME));

                                mHttpAPIHandler.executeByPost("https://chatbot.srm.pw/edubot/", postData,
                                    true);
                                isStoryAskAIFirst = true;

                            }
                        }
                        if (null != imageHashMap)
                        {
                            mDisplayHandler.setImageViewImageFromDrawable(R.drawable.g_o_question);
                        }
                    }
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
            data.put("host", "https://ryejuice.sytes.net/edubot/OCTOBO_Expressions/");
            data.put("color", "#6d94d5");
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

        MainApplication app = Tools.getApp(this);
        if (isDetectFace)
        {
            app.replaySoundEffect(R.raw.dong);
        }
        else
        {
            app.replaySoundEffect(R.raw.dongdong);
        }
    }

    @Override
    public void onShakeHands(Object arg)
    {
        startUpStoryMode();
    }

    @Override
    public void onClapHands(Object arg)
    {
        startUpStoryMode();
    }

    @Override
    public void onPinchCheeks(Object arg)
    {
        startUpStoryMode();
    }

    @Override
    public void onPatHead(Object arg)
    {
        startUpStoryMode();
    }

    @Override
    public void onScannedRfid(Object arg, String scannedResult)
    {

    }

    private void startUpStoryMode()
    {
        if (null != mDisplayHandler && null != mLogicHandler)
        {
            if (mLogicHandler.getMode() == LogicParameters.MODE_STORY)
            {
                if (mLogicHandler.getIsPlayingStory())
                {
                    mLogicHandler.pauseStoryStreaming();
                }
                else
                {
                    mLogicHandler.endAll();
                }

                mDisplayHandler.resetAllDisplayViews();

                isBlockFaceEmotionListener = false;

                mLogicHandler.startUpStory(null, null, null);
            }
        }
    }
}
