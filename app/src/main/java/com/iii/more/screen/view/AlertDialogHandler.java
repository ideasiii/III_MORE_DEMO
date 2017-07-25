package com.iii.more.screen.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;

import java.util.HashMap;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

/**
 * Created by joe on 2017/7/20.
 */

public class AlertDialogHandler extends BaseHandler
{
    private String content = "";
    private String title = "";
    private String positiveButtonString = "";
    private String negativeButtonString = "";
    private DialogInterface.OnClickListener positiveOnClickListener = null;
    private DialogInterface.OnClickListener negativeOnClickListener = null;
    
    public AlertDialogHandler(@NonNull Context context)
    {
        super(context);
    }
    
    public void setText(String title, String content, String positiveButtonString, String negativeButtonString)
    {
        this.title = title;
        this.content = content;
        this.positiveButtonString = positiveButtonString;
        this.negativeButtonString = negativeButtonString;
        
    }
    
    public void init()
    {
        positiveOnClickListener = new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                HashMap<String, String> message = new HashMap<>();
                
                message.put("message", AlertDialogParameters.ONCLICK_POSITIVE_BUTTON);
                callBackMessage(ResponseCode.ERR_SUCCESS, AlertDialogParameters.CLASS_ALERT_DIALOG, AlertDialogParameters.METHOD_SHOW, message);
                
            }
        };
        negativeOnClickListener = new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                HashMap<String, String> message = new HashMap<>();
                
                message.put("message", AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON);
                callBackMessage(ResponseCode.ERR_SUCCESS, AlertDialogParameters.CLASS_ALERT_DIALOG, AlertDialogParameters.METHOD_SHOW, message);
                
            }
        };
    }
    
    public void show()
    {
        try
        {
            setAlertDialogEvent(title, content, mContext,
                    positiveButtonString, positiveOnClickListener,
                    negativeButtonString, negativeOnClickListener);
        }
        catch (Exception e)
        {
            Logs.showError("[AlertDialogHandler] something ERROR" + e.toString());
            
        }
    }
    
    
    private void setAlertDialogEvent(String title, String message, Context context,
            String positiveString, DialogInterface.OnClickListener positiveOnClickListener,
            String negativeString, DialogInterface.OnClickListener negativeOnClickListener)
    {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveString, positiveOnClickListener)
                .setNegativeButton(negativeString, negativeOnClickListener)
                .show();
    }
    
    
}
