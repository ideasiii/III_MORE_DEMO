package com.iii.more.screen.view.alterdialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

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
    private String id = "";
    private boolean editable = false;
    
    private DialogInterface.OnClickListener positiveOnClickListener = null;
    private DialogInterface.OnClickListener negativeOnClickListener = null;
    
    private EditText editText = null;
    
    public AlertDialogHandler(@NonNull Context context)
    {
        super(context);
    }
    
    public void setText(String id, String title, String content, String positiveButtonString, String negativeButtonString, boolean editable)
    {
        this.id = id;
        this.title = title;
        this.content = content;
        this.positiveButtonString = positiveButtonString;
        this.negativeButtonString = negativeButtonString;
        this.editable = editable;
    }
    
    
    public void init()
    {
        positiveOnClickListener = new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                HashMap<String, String> message = new HashMap<>();
                message.put("id", id);
                message.put("message", AlertDialogParameters.ONCLICK_POSITIVE_BUTTON);
                if (editable)
                {
                    message.put("edit", editText.getText().toString());
                }
                
                callBackMessage(ResponseCode.ERR_SUCCESS, AlertDialogParameters.CLASS_ALERT_DIALOG, AlertDialogParameters.METHOD_SHOW, message);
                
            }
        };
        negativeOnClickListener = new DialogInterface.OnClickListener()
        {
            
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                HashMap<String, String> message = new HashMap<>();
                message.put("id", id);
                message.put("message", AlertDialogParameters.ONCLICK_NEGATIVE_BUTTON);
                callBackMessage(ResponseCode.ERR_SUCCESS, AlertDialogParameters.CLASS_ALERT_DIALOG, AlertDialogParameters.METHOD_SHOW, message);
                
            }
        };
        editText = new EditText(mContext);
    }
    
    public void setEditText(String text)
    {
        if (null != editText)
        {
            editText.setText(text);
        }
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
    
    public void show(int contentSize)
    {
        try
        {
            setAlertDialogEvent(title, content, contentSize, mContext,
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
        AlertDialog.Builder tmp = new AlertDialog.Builder(context);
        
        tmp.setTitle(title);
        tmp.setMessage(message);
        tmp.setCancelable(false);
        
        if (null != positiveString && positiveString.length() != 0)
        {
            tmp.setPositiveButton(positiveString, positiveOnClickListener);
        }
        if (null != negativeString && negativeButtonString.length() != 0)
        {
            tmp.setNegativeButton(negativeString, negativeOnClickListener);
        }
        if (editable)
        {
            tmp.setView(editText);
        }
        
        tmp.show();
        
        
    }
    
    private void setAlertDialogEvent(String title, String message, int contentSize, Context context,
            String positiveString, DialogInterface.OnClickListener positiveOnClickListener,
            String negativeString, DialogInterface.OnClickListener negativeOnClickListener)
    {
        AlertDialog.Builder tmp = new AlertDialog.Builder(context);
        
        tmp.setTitle(title);
        
        tmp.setCancelable(false);
        
        if (null != positiveString && positiveString.length() != 0)
        {
            tmp.setPositiveButton(positiveString, positiveOnClickListener);
        }
        if (null != negativeString && negativeButtonString.length() != 0)
        {
            tmp.setNegativeButton(negativeString, negativeOnClickListener);
        }
    
    
        final TextView myView = new TextView(context.getApplicationContext());
        myView.setText(message);
        myView.setTextSize(contentSize);
        myView.setVerticalScrollBarEnabled(true);
        myView.setMovementMethod(new LinkMovementMethod());
        myView.setFocusable(false);
        tmp.setView(myView);
        
        
        tmp.show();
        
        
    }
    
    
}
