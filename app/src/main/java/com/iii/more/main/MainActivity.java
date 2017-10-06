package com.iii.more.main;

import android.Manifest;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.iii.more.ai.HttpAPIHandler;
import com.iii.more.ai.HttpAPIParameters;
import com.iii.more.dmp.device.DeviceDMPHandler;
import com.iii.more.dmp.device.DeviceDMPParameters;
import com.iii.more.animate.AnimationHandler;


import com.iii.more.bluetooth.ble.ReadPenBLEHandler;
import com.iii.more.bluetooth.ble.ReadPenBLEParameters;
import com.iii.more.cmp.CMPHandler;
import com.iii.more.cmp.CMPParameters;
import com.iii.more.cmp.semantic.SemanticDeviceID;
import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
import com.iii.more.cmp.semantic.SemanticWordCMPParameters;
import com.iii.more.http.server.DeviceHttpServerHandler;
import com.iii.more.http.server.DeviceHttpServerParameters;
import com.iii.more.init.InitCheckBoardHandler;
import com.iii.more.init.InitCheckBoardParameters;
import com.iii.more.interrupt.logic.InterruptLogicHandler;
import com.iii.more.interrupt.logic.InterruptLogicParameters;
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
import com.scalified.fab.ActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import premission.settings.WriteSettingPermissionHandler;
import premission.settings.WriteSettingPermissionParameters;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.ctrl.bluetooth.BluetoothHandler;
import sdk.ideas.tool.premisson.RuntimePermissionHandler;


public class MainActivity extends AppCompatActivity

{
    //permission check board
    private WriteSettingPermissionHandler mWriteSettingPermissionHandler = null;
    private RuntimePermissionHandler mRuntimePermissionHandler = null;
    private AlertDialogHandler mAlertDialogHandler = null;
    
    //Jugo server connect
    private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    
    //AI Server connect
    private HttpAPIHandler mHttpAPIHandler = null;
    
    //Analysis Activity Json
    private LogicHandler mLogicHandler = null;
    //Analysis Display Json
    private DisplayHandler mDisplayHandler = null;
    
    //private SensorHandler mSensorHandler = null;
    
    private RelativeLayout mRelativeLayout = null;
    private TextView mTextView = null;
    private TextView mResultTextView = null;
    private ImageView mImageView = null;
    
    //layout handler
    private MenuHandler mMenuHandler = null;
    private FloatingActionButtonHandler mFABHandler = null;
    
    //init handler
    private InitCheckBoardHandler mInitCheckBoardHandler = null;
    
    //BLE connect read pen
    private ReadPenBLEHandler mReadPenBLEHandler = null;
    
    //2 floor device server connect
    private DeviceDMPHandler mDeviceDMPHandler = null;
    
    //2 floor deivce http server connect
    private DeviceHttpServerHandler mDeviceHttpServerHandler = null;
    
    private InterruptLogicHandler mInterruptLogicHandler = null;
    
    //connect to bluetooth device
    private BluetoothHandler mBluetoothHandler = null;
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[MainActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Logs.showTrace("[MainActivity] onCreate");
        
        initInterruptLogic();
        
        mAlertDialogHandler = new AlertDialogHandler(this);
        mAlertDialogHandler.setHandler(mHandler);
        mAlertDialogHandler.init();
        showMoreWelcomeLogo();
        
    }
    
    private void showMoreWelcomeLogo()
    {
        setContentView(R.layout.welcome_layout);
        AnimationHandler animationHandler = new AnimationHandler(this);
        animationHandler.setView(findViewById(R.id.logo_image_view));
        try
        {
            animationHandler.setAnimateJsonBehavior(new JSONObject("{\"type\":1,\"duration\":3000,\"repeat\":0, \"interpolate\":1}"));
            animationHandler.startAnimate();
            mHandler.sendEmptyMessageDelayed(Parameters.MESSAGE_END_WELCOME_LAYOUT, 3100);
        }
        catch (JSONException e)
        {
            Logs.showError("[MainActivity] " + e.toString());
        }
        
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (null != mWriteSettingPermissionHandler)
        {
            mWriteSettingPermissionHandler.onActivityResult(requestCode, resultCode, data);
        }
        if (null != mBluetoothHandler)
        {
            mBluetoothHandler.onActivityResult(requestCode, resultCode, data);
        }
        
    }
    
    public void initLoadingData(int flag)
    {
        mInitCheckBoardHandler = new InitCheckBoardHandler(this);
        mInitCheckBoardHandler.setHandler(mHandler);
        mInitCheckBoardHandler.init(flag);
        mInitCheckBoardHandler.startCheckInit();
    }
    
    public void initDeviceSocketServer()
    {
        mDeviceDMPHandler = new DeviceDMPHandler(this);
        mDeviceDMPHandler.setHandler(mHandler);
        mDeviceDMPHandler.init(Parameters.DMP_HOST_IP, Parameters.DMP_HOST_PORT,
                SemanticDeviceID.getDeiceID(this));
    }
    
    public void initReadPen()
    {
        mReadPenBLEHandler = new ReadPenBLEHandler(this);
        mReadPenBLEHandler.setHandler(mHandler);
        mReadPenBLEHandler.init();
    }
    
    public void initDeviceHttpServer()
    {
        mDeviceHttpServerHandler = new DeviceHttpServerHandler(this);
        mDeviceHttpServerHandler.setHandler(mHandler);
        mDeviceHttpServerHandler.connectToServerByGet(DeviceHttpServerParameters.URL_DEFAULT_PARAM);
    }
    
    public void initInterruptLogic()
    {
        mInterruptLogicHandler = new InterruptLogicHandler(this);
        mInterruptLogicHandler.setHandler(mHandler);
        Logs.showTrace("[MainActivity] initInterruptLogic success!");
    }
    
    public void initBluetoothDevice()
    {
        mBluetoothHandler = new BluetoothHandler(this);
        mBluetoothHandler.setHandler(mHandler);
        mBluetoothHandler.startListenAction();
        
        mBluetoothHandler.setBluetooth(true);
        
    }
    
    public void init()
    {
        setContentView(R.layout.main);
        mMenuHandler = new MenuHandler(this);
        mMenuHandler.setHandler(mHandler);
        mMenuHandler.setIDs(R.id.root_layout, R.id.menu_layout, R.id.arc_layout, R.id.play_btn);
        
        
        mFABHandler = new FloatingActionButtonHandler(this);
        mFABHandler.setHandler(mHandler);
        mFABHandler.setID(R.id.fab_btn);
        mFABHandler.init(R.drawable.start_image, 50.0f, ActionButton.Animations.SCALE_UP, ActionButton.Animations.SCALE_DOWN);
        
        
        mTextView = (TextView) findViewById(R.id.textView);
        
        mResultTextView = (TextView) findViewById(R.id.result_text);
        
        mImageView = (ImageView) findViewById(R.id.imageView);
        
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativelayout);
        
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
        mLogicHandler.init();
        
        mHttpAPIHandler = new HttpAPIHandler(this);
        mHttpAPIHandler.setHandler(mHandler);
        
      
        
       
        
        /*
        mSensorHandler = new SensorHandler(this);
        ArrayList<Integer> m = new ArrayList<>();
        m.add(SensorParameters.TYPE_LIGHT);
        m.add(SensorParameters.TYPE_PROXIMITY);
        mSensorHandler.init(m);
        mSensorHandler.startListenAction();
        */
    }
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mRuntimePermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onDestroy()
    {
        Logs.showTrace("onDestroy");
        if (null != mBluetoothHandler)
        {
            mBluetoothHandler.stopListenAction();
            mBluetoothHandler.closeBluetoothLink();
        }
        
        if (null != mDisplayHandler)
        {
            mDisplayHandler.killAll();
        }
        if (null != mLogicHandler)
        {
            mLogicHandler.killAll();
        }
        
        //close 2 floor server socket connect
        if (null != mDeviceDMPHandler)
        {
            mDeviceDMPHandler.stopConnectedThread();
        }
        
        
        super.onDestroy();
    }
    
    
    @Override
    protected void onStop()
    {
        Logs.showTrace("onStop");
        endAll();
        
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
        if (null != mLogicHandler)
        {
            mLogicHandler.endAll();
        }
    }
    
    public void handleMessages(Message msg)
    {
        
        switch (msg.what)
        {
            case Parameters.MESSAGE_END_WELCOME_LAYOUT:
                //start to check permission
                writeSettingPermissionCheck();
                break;
            
            case CMPParameters.CLASS_CMP_SEMANTIC_WORD:
                handleMessageSWCMP(msg);
                break;
            
            case CtrlType.MSG_RESPONSE_PERMISSION_HANDLER:
                handleMessagePermission(msg);
                break;
            
            case WriteSettingPermissionParameters.CLASS_WRITE_SETTING:
                handleMessageWriteSettingPermission(msg);
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
            case InitCheckBoardParameters.CLASS_INIT:
                handleMessageInitCheckBoard(msg);
                break;
            
            case ReadPenBLEParameters.CLASS_ReadPenBLE:
                handleMessageReadPenBLE(msg);
                break;
            
            case DisplayParameters.CLASS_DISPLAY:
                handleMessageDisplay(msg);
                break;
            
            case DeviceDMPParameters.CLASS_DMP_DEVICE:
                handleMessageDeviceDMP(msg);
                break;
            case HttpAPIParameters.CLASS_HTTP_API:
                handleMessageHttpAPI(msg);
                break;
            case DeviceHttpServerParameters.CLASS_DEVICE_HTTP_SERVER:
                handleMessageDeviceHttpServer(msg);
                break;
            
            case CtrlType.MSG_RESPONSE_BLUETOOTH_HANDLER:
                handleMessageBluetoothDevice(msg);
                break;
            
            case InterruptLogicParameters.CLASS_INTERRUPT_LOGIC:
                handleMessageInterruptLogic(msg);
                break;
            
            default:
                break;
        }
    }
    
    private void handleMessageInterruptLogic(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        
        switch (msg.arg2)
        {
            case InterruptLogicParameters.METHOD_LOGIC_RESPONSE:
                String trigger_result = message.get(InterruptLogicParameters.JSON_STRING_DESCRIPTION);
                if (null != mLogicHandler)
                {
                    switch (trigger_result)
                    {
                        case "握手":
                            mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, "你好呀!今天有沒有乖乖啊!", "zh");
                            break;
                        case "拍手":
                            mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, "你好棒棒!。我們一起拍手吧!", "zh");
                            break;
                        case "擠壓":
                            mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, "我的臉好痛喔，不可以壓我的臉喔。", "zh");
                            break;
                        case "拍頭":
                            mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN, "你 幹 嘛打我，打人是不對的喲!", "zh");
                            break;
                    }
                }
                
                if (null != mDisplayHandler)
                {
                    try
                    {
                        mDisplayHandler.setDisplayJson(new JSONObject(message.get("display")));
                        mDisplayHandler.startDisplay();
                    }
                    catch (JSONException e)
                    {
                        Logs.showError("[MainActivity] handleMessageInterruptLogic ERROR: " + e.toString());
                    }
                    
                }
                break;
            
        }
        
        
    }
    
    private void handleMessageBluetoothDevice(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.arg2)
        {
            case ResponseCode.METHOD_SETUP_BLUETOOTH:
                if (msg.arg1 == ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER)
                {
                    mInitCheckBoardHandler.setBluetoothDeviceState(InitCheckBoardParameters.STATE_DEVICE_DISCONNECT);
                }
                break;
            case ResponseCode.BLUETOOTH_IS_ON:
                mBluetoothHandler.connectDeviceByName(Parameters.DEFAULT_DEVICE_ID);
                break;
            case ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND:
                
                break;
            
            case ResponseCode.METHOD_OPEN_BLUETOOTH_CONNECTED_LINK:
                
                if (msg.arg1 == ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND)
                {
                    //show ERROR Bluetooth device not found
                    mInitCheckBoardHandler.setBluetoothDeviceState(InitCheckBoardParameters.STATE_DEVICE_DISCONNECT);
                    
                }
                else if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
                {
                    //show ERROR Bluetooth found and paired not io Exception
                    mInitCheckBoardHandler.setBluetoothDeviceState(InitCheckBoardParameters.STATE_DEVICE_DISCONNECT);
                }
                else if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    
                    Logs.showTrace("[MainActivity] setBluetoothDeviceState :true ");
                    mInitCheckBoardHandler.setBluetoothDeviceState(InitCheckBoardParameters.STATE_DEVICE_CONNECT);
                }
                break;
            case ResponseCode.METHOD_GET_MESSAGE_BLUETOOTH:
                
                if (null != mLogicHandler)
                {
                    if (mLogicHandler.getMode() == LogicParameters.MODE_GAME ||
                            mLogicHandler.getMode() == LogicParameters.MODE_UNKNOWN)
                    {
                        
                        if (handMadeCheckBluetoothRFIDData(message.get("message")) == false)
                        {
                            if (null != mInterruptLogicHandler)
                            {
                                mInterruptLogicHandler.setDeviceEventData(message.get("message"));
                                mInterruptLogicHandler.startEventDataAnalysis();
                            }
                        }
                        
                    }
                    else if(mLogicHandler.getMode() == LogicParameters.MODE_STORY)
                    {
                       // mLogicHandler
                        
                        
                    }
                }
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
                
                mLogicHandler.ttsService(TTSParameters.ID_SERVICE_TTS_BEGIN,
                        "This  is  an  Apple !", "en");
                
            }
            catch (JSONException e)
            {
                Logs.showError("[MainActivity] handMadeCheckBluetoothRFIDData ERROR: " + e.toString());
            }
            return true;
        }
        return false;
        
        
    }
    
    private void handleMessageDeviceHttpServer(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.arg2)
        {
            case DeviceHttpServerParameters.METHOD_HTTP_GET_RESPONSE:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    try
                    {
                        JSONObject tmp = new JSONObject(message.get("message"));
                        if (tmp.has("rules"))
                        {
                            JSONArray rules = tmp.getJSONArray("rules");
                            mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(rules.toString());
                        }
                    }
                    catch (JSONException e)
                    {
                        Logs.showError("[MainActivity] handleMessageDeviceHttpServer: " + e.toString());
                        Logs.showError("[MainActivity] use DEFAULT_LOGIC_BEHAVIOR_DATA ");
                        mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(
                                InterruptLogicParameters.DEFAULT_LOGIC_BEHAVIOR_DATA);
                    }
                    
                    Logs.showTrace("[MainActivity] connect Server Success, use server logic behavior!");
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_SUCCESS);
                }
                else
                {
                    
                    mInterruptLogicHandler.setInterruptLogicBehaviorDataArray(
                            InterruptLogicParameters.DEFAULT_LOGIC_BEHAVIOR_DATA);
                    
                    Logs.showTrace("[MainActivity] connect Server Error, use default logic behavior!");
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_SUCCESS);
                    
                }
                break;
        }
    }
    
    private void handleMessageHttpAPI(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        mLogicHandler.ttsService(TTSParameters.ID_SERVICE_FRIEND_RESPONSE, message.get("message"));
    }
    
    private void handleMessageDeviceDMP(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.arg2)
        {
            case DeviceDMPParameters.METHOD_INIT:
                if (msg.arg1 == ResponseCode.ERR_IO_EXCEPTION)
                {
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_FAIL);
                }
                else
                {
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters.STATE_DEVICE_SERVER_INIT_SUCCESS);
                }
                break;
            case DeviceDMPParameters.METHOD_DISPLAY:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    //Logs.showTrace("[MainActivity]" +);
                    try
                    {
                        if (null != mDisplayHandler)
                        {
                            if (mLogicHandler.getMode() == LogicParameters.MODE_STORY
                                    || mLogicHandler.getMode() == LogicParameters.MODE_GAME)
                            {
                                mDisplayHandler.setDisplayJson(new JSONObject(message.get("display")));
                                mDisplayHandler.startDisplay();
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    if (null != mSemanticWordCMPHandler)
                    {
                        if (mLogicHandler.getMode() == LogicParameters.MODE_STORY)
                        {
                            mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                                    SemanticWordCMPParameters.TYPE_REQUEST_STORY, message.get("rfid_card"));
                        }
                    }
                }
                break;
            
            default:
                
                
                break;
        }
        
    }
    
    private void handleMessageDisplay(Message msg)
    {
        switch (msg.arg2)
        {
            case DisplayParameters.METHOD_CLICK:
                Logs.showTrace("[MainActivity] get OnClick!");
                
                switch (mLogicHandler.getMode())
                {
                    case LogicParameters.MODE_STORY:
                        mLogicHandler.endAll();
                        mDisplayHandler.resetAllDisplayViews();
                        mLogicHandler.startUp();
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
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    mInitCheckBoardHandler.setBLEState(InitCheckBoardParameters.STATE_READ_PEN_CONNECT);
                }
                else
                {
                    mInitCheckBoardHandler.setBLEState(InitCheckBoardParameters.STATE_READ_PEN_DISCONNECT);
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
    
    private void handleMessageInitCheckBoard(Message msg)
    {
        Logs.showTrace("[MainActivity] handleMessageInitCheckBoard");
        
        
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            switch (msg.arg2)
            {
                case InitCheckBoardParameters.METHOD_DEVICE_SOCKET_SERVER:
                    initDeviceSocketServer();
                    break;
                case InitCheckBoardParameters.METHOD_READ_PEN:
                    initReadPen();
                    break;
                
                case InitCheckBoardParameters.METHOD_DEVICE_HTTP_SERVER:
                    initDeviceHttpServer();
                    break;
                
                case InitCheckBoardParameters.METHOD_BLUETOOTH_DEVICE:
                    initBluetoothDevice();
                    break;
                
                case InitCheckBoardParameters.METHOD_INIT:
                    init();
                    break;
                default:
                    break;
                
            }
            
        }
        else if (ResponseCode.ERR_BLUETOOTH_CANCELLED_BY_USER == msg.arg1)
        {
            showAlertDialogConnectDeviceServerERROR(3);
        }
        else if (ResponseCode.ERR_BLUETOOTH_DEVICE_NOT_FOUND == msg.arg1)
        {
            showAlertDialogConnectDeviceServerERROR(0);
            
        }
        else if (ResponseCode.ERR_IO_EXCEPTION == msg.arg1)
        {
            showAlertDialogConnectDeviceServerERROR(1);
            
        }
        else if (ResponseCode.ERR_UNKNOWN == msg.arg1)
        {
            showAlertDialogConnectDeviceServerERROR(2);
            
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
                            
                            mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
                                    SemanticWordCMPParameters.TYPE_REQUEST_STORY, message.get("message"));
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
                case LogicParameters.METHOD_SPHINX:
                    mDisplayHandler.resetAllDisplayViews();
                
                
                default:
                    
                    
                    break;
                
            }
        }
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
        //XXXXX
        
        
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
    
    
    private void writeSettingPermissionCheck()
    {
        mWriteSettingPermissionHandler = new WriteSettingPermissionHandler(this);
        mWriteSettingPermissionHandler.setHandler(mHandler);
        if (!mWriteSettingPermissionHandler.check())
        {
            showAlertDialogWritingPermission();
        }
        else
        {
            runtimePermissionCheck();
        }
    }
    
    private void showAlertDialogWritingPermission()
    {
        
        mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_WRITE_PERMISSION, getResources().getString(R.string.writesettingtitle),
                getResources().getString(R.string.writesettingcontent), getResources().getString(R.string.positivebutton),
                getResources().getString(R.string.negativebutton), false);
        
        mAlertDialogHandler.show();
    }
    
    private void showAlertDialogDeviceID()
    {
        mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_ENTER_DEVICE_ID, "章魚Device ID",
                "請輸入章魚Device ID", "OK", "", true);
        mAlertDialogHandler.setEditText(Parameters.DEFAULT_DEVICE_ID);
        mAlertDialogHandler.show();
        
    }
    
    private void showAlertDialogConfirmConnectDeviceServer()
    {
        mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_CONFIRM_CONNECT_DEVICE, "章魚裝置連結",
                "是否要與章魚裝置連線", "是的", "不要", false);
        mAlertDialogHandler.show();
        
    }
    
    private void showAlertDialogConnectDeviceServerERROR(int flag)
    {
        if (flag == 0)
        {
            mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_CONNECTING_DEVICE_BLUETOOTH, "章魚裝置連結",
                    "與章魚裝置Bluetooth連線失敗，按是不進行連線，按否則結束程式，重新再試一次!", "是", "否", false);
            mAlertDialogHandler.show();
        }
        else if (flag == 1)
        {
            mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_CONNECTING_DEVICE, "章魚裝置連結",
                    "與章魚裝置連線失敗，請確認章魚裝置是否開啟或網路是否開啟，重開APP再試一次!", "是的", "", false);
            mAlertDialogHandler.show();
        }
        else if (flag == 2)
        {
            mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_CONNECTING_DEVICE, "章魚裝置連結",
                    "與章魚裝置連線不明失敗，請確認章魚裝置是否開啟或網路是否開啟，重開APP再試一次!", "是的", "", false);
            mAlertDialogHandler.show();
        }
        else if (flag == 3)
        {
            mAlertDialogHandler.setText(Parameters.ALERT_DIALOG_CONNECTING_DEVICE, "章魚裝置連結",
                    "與裝置Bluetooth點讀筆連線失敗，請先確認智慧型裝置Bluetooth是否開啟或是章魚裝置Bluetooth是否開起，重新再試一次!"
                    , "是", "", false);
            mAlertDialogHandler.show();
        }
        
        
    }
    
    public void handleMessageWriteSettingPermission(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            runtimePermissionCheck();
        }
        else
        {
            //close app
            finish();
        }
        
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
                
                case Parameters.ALERT_DIALOG_WRITE_PERMISSION:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON))
                    {
                        finish();
                    }
                    else if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        mWriteSettingPermissionHandler.getPermission();
                    }
                    break;
                case Parameters.ALERT_DIALOG_CONNECTING_DEVICE:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        finish();
                        //init();
                        //can not connect to 2 floor server set flag or something handle
                    }
                    
                    break;
                case Parameters.ALERT_DIALOG_CONNECTING_DEVICE_BLUETOOTH:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        
                        init();
                        //can not connect to 2 floor server set flag or something handle
                    }
                    else if (message.get("message").equals(AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON))
                    {
                        finish();
                    }
                    break;
                case Parameters.ALERT_DIALOG_ENTER_DEVICE_ID:
                    
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        Logs.showTrace("[MainActivity] getText:" + message.get("edit"));
                        DeviceDMPParameters.setDeviceID(message.get("edit"));
                        initLoadingData(Parameters.MODE_CONNECT_DEVICE);
                    }
                    break;
                case Parameters.ALERT_DIALOG_CONFIRM_CONNECT_DEVICE:
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        //set flag false
                        Parameters.setModeFlag(Parameters.MODE_CONNECT_DEVICE);
                        //start to set device id
                        showAlertDialogDeviceID();
                        
                        
                    }
                    else if (message.get("message").equals(AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON))
                    {
                        //set flag true
                        Parameters.setModeFlag(Parameters.MODE_NOT_CONNECT_DEVICE);
                        
                        //not to connect 2 floor server and readPen
                        initLoadingData(Parameters.MODE_NOT_CONNECT_DEVICE);
                        
                        
                    }
                    break;
                
                
            }
        }
    }
    
    public void handleMessagePermission(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            //start to init
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            for (String key : message.keySet())
            {
                if (!message.get(key).equals("1"))
                {
                    finish();
                }
            }
            Logs.showTrace("[MainActivity] END Permission Check");
            Logs.showTrace("[MainActivity] start to confirm Connect Device Server!");
            //XXXXXX
            
            showAlertDialogConfirmConnectDeviceServer();
            //init();
        }
        else
        {
            //if not permission, close app
            finish();
        }
    }
    
    public void runtimePermissionCheck()
    {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        mRuntimePermissionHandler = new RuntimePermissionHandler(this, permissions);
        mRuntimePermissionHandler.setHandler(mHandler);
        mRuntimePermissionHandler.startRequestPermissions();
    }
    
    
    //from jugo server
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
                Logs.showTrace("[MainActivity] display Data:" + responseData.getJSONObject("display").toString());
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
                Logs.showTrace("[MainActivity] activity Data:" + responseData.getJSONObject("activity").toString());
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
    
    
}
