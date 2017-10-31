package com.iii.more.screen.view.progressDialog;

import android.content.Context;
import android.graphics.Color;

import com.kaopiz.kprogresshud.KProgressHUD;

import sdk.ideas.common.BaseHandler;

/**
 * Created by joe on 2017/8/11.
 */

public class ProgressDialog extends BaseHandler
{
    private KProgressHUD mKProgressHUD = null;
    
    public ProgressDialog(Context context)
    {
        super(context);
    }
    
    public void init()
    {
        
        mKProgressHUD = KProgressHUD.create(mContext)
                .setStyle(com.kaopiz.kprogresshud.KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setDetailsLabel("Downloading Image Data & Init Connecting...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.BLACK);
    }
    
    public void init(String showData)
    {
        
        mKProgressHUD = KProgressHUD.create(mContext)
                .setStyle(com.kaopiz.kprogresshud.KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please Wait")
                .setDetailsLabel(showData)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .setBackgroundColor(Color.BLACK);
    }
    
    public void show()
    {
        if (null != mKProgressHUD && !mKProgressHUD.isShowing())
        {
            mKProgressHUD.show();
        }
        
    }
    
    public void dismiss()
    {
        if (null != mKProgressHUD && mKProgressHUD.isShowing())
        {
            mKProgressHUD.dismiss();
            
        }
    }
    
    
}
