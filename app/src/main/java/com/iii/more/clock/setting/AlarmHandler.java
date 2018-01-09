package com.iii.more.clock.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.iii.more.clock.ClockParameters;
import com.iii.more.clock.receiver.ClockReceiver;
import com.iii.more.clock.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.Calendar;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/12/29
 */

public class AlarmHandler extends BaseHandler
{
    
    private ArrayList<AlarmElement> mAlarmElementList = null;
    
    public AlarmHandler(Context context)
    {
        super(context);
    }
    
    public void setAlarmData(@NonNull String alarmData)
    {
        mAlarmElementList = convertData(alarmData);
        if (null != mAlarmElementList)
        {
            Logs.showTrace("[AlarmHandler] set data successful");
        }
        else
        {
            Logs.showError("[AlarmHandler] convert ERROR while setting AlarmData");
        }
    }
    
    public void startAll()
    {
        if (null != mAlarmElementList)
        {
            //cancel all alarms
            Intent intent = new Intent(mContext, ClockReceiver.class);
            AlarmUtils.cancelAllAlarms(mContext, intent);
            
            //set all alarms
            for (int i = 0; i < mAlarmElementList.size(); i++)
            {
                AlarmUtils.addAlarm(mContext, mAlarmElementList.get(i).mDataIntent, mAlarmElementList.get
                    (i).mID, mAlarmElementList.get(i).mCalendar, false);
            }
        }
    }
    
    private ArrayList<AlarmElement> convertData(@NonNull String alarmData)
    {
        //###
        // convert ready style to joe style
        if (AlarmParameters.isTest)
        {
            Intent dataIntent = getDataIntentData(ClockReceiver.class, "三隻小豬", AlarmParameters
                .TYPE_BEFORE_SLEEP);
            ArrayList<AlarmElement> test = new ArrayList<>();
            test.add(new AlarmElement(1, 5, 15, 10, dataIntent));
            return test;
        }
        
        return null;
    }
    
    
    private Intent getDataIntentData(@NonNull Class<?> whichClass, @NonNull String storyName, int alarmType)
    {
        Intent dataIntent = new Intent(mContext, ClockReceiver.class);
        Bundle activityBundle = new Bundle();
        activityBundle.putString(ClockParameters.KEY_PLAY_STREAM_NAME, storyName);
        activityBundle.putInt(ClockParameters.KEY_ALARM_TYPE, alarmType);
        dataIntent.putExtra(ClockParameters.KEY_BUNDLE_NAME, activityBundle);
        return dataIntent;
    }
    
    public class AlarmElement
    {
        Calendar mCalendar = null;
        Intent mDataIntent = null;
        int mID = -1;
        
        public AlarmElement(int id, int day, int hour, int minute, @NonNull Intent dataIntent)
        {
            this.mID = id;
            mCalendar = getCalendarData(getCalendarDayOfWeek(day), hour, minute);
            mDataIntent = dataIntent;
            
            mDataIntent.getBundleExtra(ClockParameters.KEY_BUNDLE_NAME).putLong(ClockParameters
                .KEY_CLOCK_TIME, mCalendar.getTimeInMillis());
        }
        
        /**
         * @param day    is like: Calendar.Monday , Calendar.THURSDAY
         * @param hour   is like: 0 ~ 23
         * @param minute is like: 0 ~ 59
         * @return Calendar Data Type
         */
        private Calendar getCalendarData(int day, int hour, int minute)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.DAY_OF_WEEK, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            return calendar;
        }
        
        private int getCalendarDayOfWeek(int day)
        {
            int returnDate;
            switch (day)
            {
                case 1:
                    returnDate = Calendar.MONDAY;
                    break;
                case 2:
                    returnDate = Calendar.TUESDAY;
                    break;
                case 3:
                    returnDate = Calendar.WEDNESDAY;
                    break;
                case 4:
                    returnDate = Calendar.THURSDAY;
                    break;
                case 5:
                    returnDate = Calendar.FRIDAY;
                    break;
                case 6:
                    returnDate = Calendar.SATURDAY;
                    break;
                case 7:
                    returnDate = Calendar.SUNDAY;
                    break;
                default:
                    returnDate = Calendar.SUNDAY;
                    break;
                
            }
            return returnDate;
        }
        
        
    }
    
    
}
