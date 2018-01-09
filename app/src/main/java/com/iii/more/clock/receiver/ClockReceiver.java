package com.iii.more.clock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iii.more.clock.ClockActivity;
import com.iii.more.clock.ClockParameters;

import java.util.Calendar;

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
        Bundle bundleData = intent.getBundleExtra("Bundle");
        if (null != bundleData)
        {
            Long oldTimeMill = bundleData.getLong(ClockParameters.KEY_CLOCK_TIME);
            Calendar cal = Calendar.getInstance();
            
            Long timeDiff = cal.getTimeInMillis() - oldTimeMill;
            Logs.showTrace("[ClockReceiver] timeDiff: " + String.valueOf(timeDiff));
            
            if (false)
            {
                Intent activityIntent = new Intent();
                
                activityIntent.putExtra(ClockParameters.KEY_BUNDLE_NAME, bundleData);
                activityIntent.setClass(context, ClockActivity.class);
                activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //start clock Activity
                context.startActivity(activityIntent);
            }
        }
    }
}
