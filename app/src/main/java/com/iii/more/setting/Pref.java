package com.iii.more.setting;

import android.content.Context;
import android.support.annotation.NonNull;

import com.iii.more.setting.struct.Brush;
import com.iii.more.setting.struct.Sleep;
import com.iii.more.setting.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

import static com.iii.more.setting.AlarmLv1Activity.ALARM_BRUSH;
import static com.iii.more.setting.AlarmLv1Activity.ALARM_SLEEP;
import static com.iii.more.setting.AlarmLv1Activity.ITEM_BODY;

/**
 * TODO: 此頁說明
 *
 *  取得設定資料的 範本

    Pref pref = new Pref(mCtx);
    List<Brush> brushes = pref.getBrush();  // sleeps.recur : boolean[7] , index 0 = 星期天 , 1 = 一 .. 6 = 六
    List<Sleep> sleeps = pref.getSleep();  // sleeps.recur : boolean[7] , index 0 = 星期天 , 1 = 一 .. 6 = 六
    int iLang = pref.getLanguage();  // 0 = 國語 , 1 = 英文
    boolean bLowPower = pref.isLowPower(); false = 省電模式 OFF , true = 省電模式 ON

 * @author ReadyChen
 */

public class Pref {

    private static Context ctx;
    private static TinyDB tinyOutterDB;

    public static final String KEY_ALARM = "Alarm";
    public static final String KEY_IS_LOW_POWER = "isLowPower";
    public static final String KEY_LANGUAGE = "language";

    public Pref(@NonNull Context ctx) {
        this.ctx = ctx;
        if (tinyOutterDB == null) {
            tinyOutterDB = new TinyDB(this.ctx);

            ArrayList<? extends Object> ret = tinyOutterDB.getListObject(KEY_ALARM, AlarmLv1Activity.Alarm.class);
            List<AlarmLv1Activity.Alarm> alarm = (List<AlarmLv1Activity.Alarm>) ret;

            if( alarm.size() == 0 ) {
                // 表示 尚未建立 header for db，我們進行一次性的 初始化
                AlarmLv1Activity.Alarm headerSleep = new AlarmLv1Activity.Alarm();
                headerSleep.itemType = 0;
                headerSleep.alarmType = ALARM_SLEEP;
                headerSleep.name = "睡眠時間";
                AlarmLv1Activity.Alarm headerBrush = new AlarmLv1Activity.Alarm();
                headerBrush.itemType = 0;
                headerBrush.alarmType = ALARM_BRUSH;
                headerBrush.name = "刷牙時間";

                List<AlarmLv1Activity.Alarm> alarms = new ArrayList<>();
                alarms.add(headerSleep);
                alarms.add(headerBrush);
                tinyOutterDB.putListObject(KEY_ALARM, (ArrayList<?>) alarms);
                tinyOutterDB.putBoolean(KEY_IS_LOW_POWER, false);
                tinyOutterDB.putInt(KEY_LANGUAGE, 0);
            }
        }
    }

    public boolean isLowPower() {
        boolean isLowPower = tinyOutterDB.getBoolean(KEY_IS_LOW_POWER);
        return isLowPower;
    }

    public int getLanguage() {
        int language = tinyOutterDB.getInt(KEY_LANGUAGE);
        return language;
    }

    public List<Brush> getBrush() {
        ArrayList<? extends Object> ret = tinyOutterDB.getListObject(KEY_ALARM, AlarmLv1Activity.Alarm.class);
        List<AlarmLv1Activity.Alarm> alarm = (List<AlarmLv1Activity.Alarm>) ret;

        List<Brush> retBrush = new ArrayList<>();
        for (AlarmLv1Activity.Alarm each : alarm) {
            if( each.itemType == ITEM_BODY && each.alarmType == ALARM_BRUSH ) {
                Brush eachBrush = new Brush();
                eachBrush.name = each.name;
                eachBrush.story = each.story;
                eachBrush.time = each.time;
                eachBrush.recur = each.recur;
                retBrush.add(eachBrush);
            }
        }
        return retBrush;
    }

    public List<Sleep> getSleep() {
        ArrayList<? extends Object> ret = tinyOutterDB.getListObject(KEY_ALARM, AlarmLv1Activity.Alarm.class);
        List<AlarmLv1Activity.Alarm> alarm = (List<AlarmLv1Activity.Alarm>) ret;

        List<Sleep> retSleep = new ArrayList<>();
        for (AlarmLv1Activity.Alarm each : alarm) {
            if( each.itemType == ITEM_BODY && each.alarmType == ALARM_SLEEP ) {
                Sleep eachSleep = new Sleep();
                eachSleep.name = each.name;
                eachSleep.story = each.story;
                eachSleep.time = each.time;
                eachSleep.recur = each.recur;
                retSleep.add(eachSleep);
            }
        }
        return retSleep;
    }
}
