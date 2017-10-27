package com.iii.more.init.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;


import com.iii.more.animate.AnimationHandler;
import com.iii.more.main.MainActivity;
import com.iii.more.main.Parameters;
import com.iii.more.main.R;
import com.iii.more.screen.view.alterdialog.AlertDialogHandler;
import com.iii.more.screen.view.alterdialog.AlertDialogParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import premission.settings.WriteSettingPermissionHandler;
import premission.settings.WriteSettingPermissionParameters;
import sdk.ideas.common.CtrlType;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;
import sdk.ideas.tool.premisson.RuntimePermissionHandler;


/**
 * Created by joe on 2017/10/27.
 */

//this class handle welcome page,permissions and licenses.txt

public class InitActivity extends AppCompatActivity
{
    //permission check board
    private WriteSettingPermissionHandler mWriteSettingPermissionHandler = null;
    private RuntimePermissionHandler mRuntimePermissionHandler = null;
    private AlertDialogHandler mAlertDialogHandler = null;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Logs.showTrace("[InitActivity] Result: " + String.valueOf(msg.arg1) + " What:" + String.valueOf(msg.what) +
                    " From: " + String.valueOf(msg.arg2) + " Message: " + msg.obj);
            handleMessages(msg);
        }
    };
    
    public void handleMessages(Message msg)
    {
        switch (msg.what)
        {
            case Parameters.MESSAGE_END_WELCOME_LAYOUT:
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
            Logs.showError("[InitActivity] " + e.toString());
        }
        
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
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        mRuntimePermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    private void showAlertDialogWritingPermission()
    {
        
        mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_WRITE_PERMISSION, getResources().getString(R.string.writesettingtitle),
                getResources().getString(R.string.writesettingcontent), getResources().getString(R.string.writesettingpositivebutton),
                getResources().getString(R.string.writesettingnegativebutton), false);
        
        mAlertDialogHandler.show();
    }
    
    private void showAlertDialogLicencesPermission()
    {
        mAlertDialogHandler.setText(InitActivityParameters.ALERT_DIALOG_LICENCES_PERMISSION, getResources().getString(R.string.licencestitle),
                getContent(this, R.raw.licenses), getResources().getString(R.string.licencespositivebutton),
                null, false);
        
        mAlertDialogHandler.show(8);
    }
    
    protected String getContent(final Context context, final int contentResourceId)
    {
        BufferedReader reader = null;
        try
        {
            final InputStream inputStream = context.getResources().openRawResource(contentResourceId);
            if (inputStream != null)
            {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                return toString(reader);
            }
            throw new IOException("Error opening license file.");
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
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON))
                    {
                        finish();
                    }
                    else if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        mWriteSettingPermissionHandler.getPermission();
                    }
                    break;
                
                case InitActivityParameters.ALERT_DIALOG_LICENCES_PERMISSION:
                    
                    if (message.get("message").equals(AlertDialogParameters.ONCLICK_POSITIVE_BUTTON))
                    {
                        //### no oobe -> go to oobe class
                        
                        
                        //
                        if (checkOobe())
                        {
                            
                        }
                        else
                        {
                            startMainActivity();
                        }
                    }
                    
                    
                    break;
                
                
            }
        }
    }
    
    private boolean checkOobe()
    {
        return false;
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
    
    public void runtimePermissionCheck()
    {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
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
            Logs.showTrace("[InitActivity] start to confirm Connect Device Server!");
            
            
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
