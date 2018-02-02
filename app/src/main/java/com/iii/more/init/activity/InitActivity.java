package com.iii.more.init.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.amazonaws.util.Classes;
import com.iii.more.animate.AnimationHandler;
import com.iii.more.game.parktour.ParktourActivity;
import com.iii.more.http.server.DeviceHttpServerHandler;
import com.iii.more.http.server.DeviceHttpServerParameters;
import com.iii.more.init.InitCheckBoardHandler;
import com.iii.more.init.InitCheckBoardParameters;
import com.iii.more.main.BuildConfig;
import com.iii.more.main.MainActivity;
import com.iii.more.main.MainApplication;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;
import com.iii.more.oobe.OobeActivity;
import com.iii.more.screen.view.alterdialog.AlertDialogHandler;
import com.iii.more.screen.view.alterdialog.AlertDialogParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import dalvik.system.DexFile;
import permission.settings.WriteSettingPermissionHandler;
import permission.settings.WriteSettingPermissionParameters;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.premisson.RuntimePermissionHandler;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;


/**
 * Created by joe on 2017/10/27.
 */

/*###
this class handle welcome page,permissions , licenses.txt
http task composer API get and save in share preference
*/
public class InitActivity extends AppCompatActivity
{
    //permission check board
    private WriteSettingPermissionHandler mWriteSettingPermissionHandler = null;
    private RuntimePermissionHandler mRuntimePermissionHandler = null;
    private AlertDialogHandler mAlertDialogHandler = null;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    //2 floor device http server connect
    private DeviceHttpServerHandler mDeviceHttpServerHandler = null;
    
    //init handler
    private InitCheckBoardHandler mInitCheckBoardHandler = null;
    
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[InitActivity] Result: " + String.valueOf(msg.arg1) + " What:" +
                String.valueOf(msg.what) + " From: " + String.valueOf(msg.arg2) + " Message: " +
                msg.obj);
            handleMessages(msg);
        }
    };
    
    public void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case InitActivityParameters.MESSAGE_END_WELCOME_LAYOUT:
                //start to check permission
                writeSettingPermissionCheck();
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
            case DeviceHttpServerParameters.CLASS_DEVICE_HTTP_SERVER:
                handleMessageDeviceHttpServer(msg);
                break;
            case InitCheckBoardParameters.CLASS_INIT:
                handleMessageInitCheckBoard(msg);
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
                case InitCheckBoardParameters.METHOD_DEVICE_HTTP_SERVER:
                    initDeviceHttpServer();
                    break;
                
                case InitCheckBoardParameters.METHOD_INIT:
                    //###
                    // init finish
                    // can call other activity to start
                    //### no com.iii.more.oobe -> go to com.iii.more.oobe class
                    if (checkOobe())
                    {
                        startOobeActivity();
                    }
                    else
                    {
                        if (0 == Parameters.DEMO_REVIEW_GAME.compareTo("review"))
                        {
                            startParktourActivity();
                        }
                        else
                        {
                            startMainActivity();
                        }
                    }
                    break;
                default:
                    break;
            }
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
    
    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus)
        {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View
                .SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    
    private void handleMessageDeviceHttpServer(Message msg)
    {
        HashMap<String, String> message = (HashMap<String, String>) msg.obj;
        switch (msg.arg2)
        {
            case DeviceHttpServerParameters.METHOD_HTTP_GET_RESPONSE:
                if (msg.arg1 == ResponseCode.ERR_SUCCESS)
                {
                    //### save in share preference
                    SharedPreferences prefs = getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Parameters.TASK_COMPOSER_DATA, message.get("message"));
                    editor.apply();
                    
                    
                    //call function
                    
                    
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters
                        .STATE_DEVICE_SERVER_INIT_SUCCESS);
                }
                else
                {
                    
                    Logs.showTrace("[MainActivity] connect Server Error, use default logic " +
                        "behavior!");
                    mInitCheckBoardHandler.setDeviceServerState(InitCheckBoardParameters
                        .STATE_DEVICE_SERVER_INIT_SUCCESS);
                    
                }
                
                //call function MainApplication to initFaceEmotionInterrupt and initInterruptLogic
                MainApplication mApp = (MainApplication) getApplication();
                
                mApp.initInterruptLogic();
                mApp.initFaceEmotionInterrupt();
                
                break;
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
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        createShortCut();
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View
            .SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View
            .SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
            .SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        
        
        // This work only for android 4.4+
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            setTaskDescriptionLabelToBuildDate();
            getWindow().getDecorView().setSystemUiVisibility(flags);
            
            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View
                .OnSystemUiVisibilityChangeListener()
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
        }
        mAlertDialogHandler = new AlertDialogHandler(this);
        mAlertDialogHandler.setHandler(mHandler);
        mAlertDialogHandler.init();
        
        showMoreWelcomeLogo();
    }
    
    private void createShortCut()
    {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcutIntent.putExtra("duplicate", false);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "More APP");
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R
            .mipmap.ic_launcher);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(),
            InitActivity.class));
        sendBroadcast(shortcutIntent);
    }
    
    private void showMoreWelcomeLogo()
    {
        setContentView(R.layout.welcome_layout);
        
        /* Build App time Start*/
        TextView apkBuildDateView = (TextView) findViewById(R.id.apk_build_date_text_view);
        String formattedBuildDateText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format
            (BuildConfig.BUILD_TIME);
        apkBuildDateView.setText("Built on " + formattedBuildDateText);
        /* Build App time end*/
        
        //old More Logo
        AnimationHandler animationHandler = new AnimationHandler(this);
        animationHandler.setView(findViewById(R.id.logo_image_view));
        try
        {
            animationHandler.setAnimateJsonBehavior(new JSONObject("{\"type\":1," +
                "\"duration\":3000," + "\"repeat\":0, \"interpolate\":1}"));
            animationHandler.startAnimate();
            mHandler.sendEmptyMessageDelayed(InitActivityParameters.MESSAGE_END_WELCOME_LAYOUT,
                3100);
        }
        catch (JSONException e)
        {
            Logs.showError("[InitActivity] " + e.toString());
        }
        
        ArrayList<?> tmp = new ArrayList<>();
        
    }
    
    private void showAlertDialogConnectDeviceServerERROR(int flag)
    {
        if (flag == 1)
        {
            mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_CONNECTING_DEVICE,
                "章魚裝置連結", "與章魚裝置連線失敗，請確認章魚裝置是否開啟或網路是否開啟，重開APP再試一次!", "是的", "", false);
            mAlertDialogHandler.show();
        }
        else if (flag == 2)
        {
            mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_CONNECTING_DEVICE,
                "章魚裝置連結", "與章魚裝置連線不明失敗，請確認章魚裝置是否開啟或網路是否開啟，重開APP再試一次!", "是的", "", false);
            mAlertDialogHandler.show();
        }
    }
    
    // 將 app 切換頁內，本 app 的顯示名稱換成 build date
    private void setTaskDescriptionLabelToBuildDate()
    {
        String formattedBuildDateText = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format
            (BuildConfig.BUILD_TIME);
        
        ActivityManager.TaskDescription taskDescription = new ActivityManager.TaskDescription
            (formattedBuildDateText);
        setTaskDescription(taskDescription);
    }
    
    public void initDeviceHttpServer()
    {
        mDeviceHttpServerHandler = new DeviceHttpServerHandler(this);
        mDeviceHttpServerHandler.setHandler(mHandler);
        mDeviceHttpServerHandler.connectToServerByGet(DeviceHttpServerParameters.URL_DEFAULT_PARAM);
    }
    
    public void initLoadingData()
    {
        mInitCheckBoardHandler = new InitCheckBoardHandler(this);
        mInitCheckBoardHandler.setHandler(mHandler);
        mInitCheckBoardHandler.init();
        mInitCheckBoardHandler.startCheckInit();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (null != mWriteSettingPermissionHandler)
        {
            mWriteSettingPermissionHandler.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults)
    {
        mRuntimePermissionHandler.onRequestPermissionsResult(requestCode, permissions,
            grantResults);
    }
    
    private void showAlertDialogWritingPermission()
    {
        mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_WRITE_PERMISSION,
            getResources().getString(R.string.writesettingtitle), getResources().getString(R
                .string.writesettingcontent), getResources().getString(R.string
                .writesettingpositivebutton), getResources().getString(R.string
                .writesettingnegativebutton), false);
        
        mAlertDialogHandler.show();
    }
    
    private void showAlertDialogLicencesPermission()
    {
        mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_LICENCES_PERMISSION,
            getResources().getString(R.string.licencestitle), getContent(this, R.raw.licenses),
            getResources().getString(R.string.licencespositivebutton), null, false);
        
        mAlertDialogHandler.show();
    }
    
    protected String getContent(final Context context, final int contentResourceId)
    {
        BufferedReader reader = null;
        try
        {
            final InputStream inputStream = context.getResources().openRawResource
                (contentResourceId);
            if (inputStream != null)
            {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                return toString(reader);
            }
            else
            {
                throw new IOException("Error opening license file.");
            }
        }
        catch (final IOException e)
        {
            throw new IllegalStateException(e);
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException e)
                {
                    // Don't care.
                }
            }
        }
    }
    
    private String toString(final BufferedReader reader) throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            builder.append(line).append(LINE_SEPARATOR);
        }
        return builder.toString();
    }
    
    
    public void handleMessageAlertDialog(Message msg)
    {
        if (msg.arg1 == ResponseCode.ERR_SUCCESS)
        {
            Logs.showTrace("[InitActivity] in handleMessageAlertDialog");
            HashMap<String, String> message = (HashMap<String, String>) msg.obj;
            Logs.showTrace("[InitActivity] id:" + message.get("id"));
            switch (message.get("id"))
            {
                case InitActivityParameters.ALERT_DIALOG_WRITE_PERMISSION:
                    if (message.get("message").equals(AlertDialogParameters
                        .ONCLICK_NEGATIVE_BUTTON))
                    {
                        finish();
                    }
                    else if (message.get("message").equals(AlertDialogParameters
                        .ONCLICK_POSITIVE_BUTTON))
                    {
                        mWriteSettingPermissionHandler.getPermission();
                    }
                    break;
                case InitActivityParameters.ALERT_DIALOG_CONNECTING_DEVICE:
                    if (message.get("message").equals(AlertDialogParameters
                        .ONCLICK_POSITIVE_BUTTON))
                    {
                        finish();
                    }
                    break;
                case InitActivityParameters.ALERT_DIALOG_LICENCES_PERMISSION:
                    
                    if (message.get("message").equals(AlertDialogParameters
                        .ONCLICK_POSITIVE_BUTTON))
                    {
                        // ### connect to task composer API get data
                        initLoadingData();
                        ((MainApplication) getApplication()).startTracker();
                    }
                    break;
            }
        }
    }
    
    private boolean checkOobe()
    {
        if (Parameters.OOBE_DEBUG_ENABLE)
        {
            return true;
        }
        else
        {
            //###
            //check share preference is exist child face or name
            MainApplication app = (MainApplication) this.getApplicationContext();
            if (null == app.getName(Parameters.ID_CHILD_NAME) || app.getName(Parameters
                .ID_CHILD_NAME).equals(""))
            {
                return true;
            }
            return false;
            
            
        }
        
    }
    
    private void startOobeActivity()
    {
        Intent startOobe = new Intent(Intent.ACTION_MAIN);
        startOobe.addCategory(Intent.CATEGORY_HOME);
        startOobe.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startOobe.setClass(InitActivity.this, OobeActivity.class);
        startActivity(startOobe);
        finish();
        
    }
    
    private void startMainActivity()
    {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.setClass(InitActivity.this, MainActivity.class);
        startActivity(startMain);
        finish();
    }
    
    /**
     * 啟動 ParktourActivity
     * 主要是應付2018-03-20 審查用的垃圾程式碼
     */
    private void startParktourActivity()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClass(InitActivity.this, ParktourActivity.class);
        startActivity(intent);
        finish();
    }
    
    
    public void runtimePermissionCheck()
    {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.GET_ACCOUNTS);
        mRuntimePermissionHandler = new RuntimePermissionHandler(this, permissions);
        mRuntimePermissionHandler.setHandler(mHandler);
        mRuntimePermissionHandler.startRequestPermissions();
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
            Logs.showTrace("[InitActivity] END Permission Check");
            Logs.showTrace("[InitActivity] start to show Licences Permission!");
            
            //### show licenses.txt dialog to let user know this
            showAlertDialogLicencesPermission();
        }
        else
        {
            //if not permission, close app
            finish();
        }
    }
    
}
