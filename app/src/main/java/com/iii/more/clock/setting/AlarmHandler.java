package com.iii.more.clock.setting;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.iii.more.clock.receiver.ClockReceiver;
import com.iii.more.clock.utils.AlarmUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;

import com.iii.more.setting.struct.Brush;
import com.iii.more.setting.struct.Sleep;

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
    
    public void init()
    {
        mAlarmElementList = new ArrayList<>();
    }
    
    public void clearAlarmData()
    {
        if (null != mAlarmElementList)
        {
            mAlarmElementList.clear();
        }
        
    }
    
    
    public void addAlarmData(@NonNull List<?> alarmListData)
    {
        if (alarmListData.size() != 0)
        {
            int flag = AlarmParameters.TYPE_BEFORE_SLEEP;
            if (alarmListData.get(0) instanceof Sleep)
            {
                flag = AlarmParameters.TYPE_BEFORE_SLEEP;
            }
            else if (alarmListData.get(0) instanceof Brush)
            {
                flag = AlarmParameters.TYPE_BRUSH_TEETH;
            }
            addListData(mAlarmElementList, convertData(alarmListData, flag));
        }
        
    }
    
    
    private <T> void addListData(ArrayList<T> src, ArrayList<T> des)
    {
        if (null != src && null != des)
        {
            src.addAll(des);
        }
    }
    
    
    public void startSetAlarms()
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
    
    private ArrayList<AlarmElement> convertData(List<?> alarmRowData, int listType)
    {
        //###
        //Data Intent
        Intent intent = new Intent(mContext, ClockReceiver.class);
        
        //alarm id
        int alarmID = 1;
        
        // convert ready style to joe style
        if (AlarmParameters.isTest)
        {
            Intent dataIntent = getDataIntentData(ClockReceiver.class, "三隻小豬", AlarmParameters
                .TYPE_BEFORE_SLEEP);
            ArrayList<AlarmElement> test = new ArrayList<>();
            test.add(new AlarmElement(1, 5, 15, 2, dataIntent));
            return test;
        }
        else
        {
            ArrayList<AlarmElement> alarmList = new ArrayList<>();
            
            switch (listType)
            {
                case AlarmParameters.TYPE_BEFORE_SLEEP:
                    List<Sleep> sleepData = (List<Sleep>) alarmRowData;
                    
                    //每一筆Sleep資料包含以下資訊
                    for (Sleep data : sleepData)
                    {
                        for (int i = 0; i < data.recur.length; i++)
                        {
                            //day is set is true
                            if (data.recur[i])
                            {
                                String[] timeData = data.time.split(":");
                                alarmList.add(new AlarmElement(alarmID++, i, Integer.valueOf(timeData[0]),
                                    Integer.valueOf(timeData[1]), intent));
                            }
                        }
                        
                    }
                    
                    
                    break;
                case AlarmParameters.TYPE_BRUSH_TEETH:
                    List<Brush> brushData = (List<Brush>) alarmRowData;
                    
                    
                    for (Brush data : brushData)
                    {
                        for (int i = 0; i < data.recur.length; i++)
                        {
                            //day is set is true
                            if (data.recur[i])
                            {
                                String[] timeData = data.time.split(":");
                                alarmList.add(new AlarmElement(alarmID++, i, Integer.valueOf(timeData[0]),
                                    Integer.valueOf(timeData[1]), intent));
                            }
                        }
                        
                    }
                    
                    break;
                
                
            }
            return alarmList;
            
        }
        
        
    }
    
    
    private Intent getDataIntentData(@NonNull Class<?> whichClass, @NonNull String storyName, int alarmType)
    {
        Intent dataIntent = new Intent(mContext, ClockReceiver.class);
       /* Bundle activityBundle = new Bundle();
        activityBundle.putString(ClockParameters.KEY_PLAY_STREAM_NAME, storyName);
        activityBundle.putInt(ClockParameters.KEY_ALARM_TYPE, alarmType);
        dataIntent.putExtra(ClockParameters.KEY_BUNDLE_NAME, activityBundle);*/
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
            mCalendar = getCalendarData(day, hour, minute);
            mDataIntent = dataIntent;
            
            //mDataIntent.getBundleExtra(ClockParameters.KEY_BUNDLE_NAME).putLong(ClockParameters
            //    .KEY_CLOCK_TIME, mCalendar.getTimeInMillis());
        }
        
        /**
         * @param day    is like: Calendar.Monday , Calendar.THURSDAY
         * @param hour   is like: 0 ~ 23
         * @param minute is like: 0 ~ 59
         * @return Calendar Data Type
         */
        private Calendar getCalendarData(int day, int hour, int minute)
        {     //todo 時區有問題啊!!!!
            Calendar calendar = Calendar.getInstance();
            
            Logs.showTrace("[AlarmHandler] System.currentTimeMillis():" + System.currentTimeMillis());
            
            //todo 可能是day差一天
            calendar.set(Calendar.DAY_OF_WEEK, day);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            
            Logs.showTrace("[AlarmHandler] calendar.getTimeInMillis(): " + calendar.getTimeInMillis());
            
            if (calendar.getTimeInMillis() < System.currentTimeMillis())
            {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + 7 * 24 * 60 * 60 * 1000L);
                Logs.showTrace("[AlarmHandler] after Set calendar.getTimeInMillis():" + calendar
                    .getTimeInMillis());
            }
            
            
            return calendar;
        }
        
        
    }
    
    
}
