package com.iii.more.clock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iii.more.clock.ClockActivity;
import com.iii.more.clock.ClockParameters;
import com.iii.more.clock.setting.AlarmParameters;
import com.iii.more.setting.Pref;
import com.iii.more.setting.struct.Brush;
import com.iii.more.setting.struct.Sleep;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2018/1/3
 */

public class ClockReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Logs.showTrace("[ClockReceiver] onReceive new message");
        Bundle bundleData = getClockPref(context);
        
        if (null != bundleData)
        {
            Intent activityIntent = new Intent();
            
            activityIntent.putExtra(ClockParameters.KEY_BUNDLE_NAME, bundleData);
            activityIntent.setClass(context, ClockActivity.class);
            activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start clock Activity
            
            context.startActivity(activityIntent);
        }
    }
    
    private int getNowCalendarHourAndMinute()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        
        return hour * 60 + min;
    }
    
    private int getHourAndMinute(String time)
    {
        String[] timeData = time.split(":");
        int hour = Integer.valueOf(timeData[0]);
        int min = Integer.valueOf(timeData[1]);
        return hour * 60 + min;
    }
    
    private int getWeekDay()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
        
    }
    
    private int diffRange(int a, int b)
    {
        return Math.abs(a - b);
    }
    
    private Bundle getClockPref(Context context)
    {
        Bundle bundleData = new Bundle();
        
        int nowTime = getNowCalendarHourAndMinute();
        
        Pref mPref = new Pref(context);
        
        //check sleep
        List<Sleep> sleepList = mPref.getSleep();
        
        for (Sleep data : sleepList)
        {
            if (diffRange(getHourAndMinute(data.time), nowTime) < ClockParameters.ALARM_TIME_ERROR_RANGE)
            {
                // check day
                if (data.recur[getWeekDay()])
                {
                    // That's it!
                    bundleData.putString(ClockParameters.KEY_PLAY_STREAM_NAME, data.story);
                    bundleData.putInt(ClockParameters.KEY_ALARM_TYPE, AlarmParameters.TYPE_BEFORE_SLEEP);
                    
                    return bundleData;
                }
            }
        }
        
        //check brush
        List<Brush> brushList = mPref.getBrush();
        for (Brush data : brushList)
        {
            if (diffRange(getHourAndMinute(data.time), nowTime) < ClockParameters.ALARM_TIME_ERROR_RANGE)
            {
                // check day
                if (data.recur[getWeekDay()])
                {
                    // That's it!
                    bundleData.putString(ClockParameters.KEY_PLAY_STREAM_NAME, data.story);
                    bundleData.putInt(ClockParameters.KEY_ALARM_TYPE, AlarmParameters.TYPE_BRUSH_TEETH);
                    
                    return bundleData;
                }
            }
        }
        
        
        return null;
        
        
    }
    
    
}
