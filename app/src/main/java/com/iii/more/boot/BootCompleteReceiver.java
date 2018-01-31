package com.iii.more.boot;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iii.more.clock.setting.AlarmHandler;
import com.iii.more.setting.Pref;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/12/29
 */

public class BootCompleteReceiver extends BroadcastReceiver
{
    
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Logs.showTrace("[BootCompleteReceiver] reboot system");
        
        
        Logs.showTrace("[BootCompleteReceiver] start to set Alarms");
        
        Pref mPref = new Pref(context);
    
        AlarmHandler mAlarmHandler = new AlarmHandler(context);
        mAlarmHandler.init();
        
        mAlarmHandler.clearAlarmData();
        mAlarmHandler.addAlarmData(mPref.getSleep());
        mAlarmHandler.addAlarmData(mPref.getBrush());
        mAlarmHandler.startSetAlarms();
        
    }
}
