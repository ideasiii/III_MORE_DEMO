package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.iii.more.main.R;
import com.iii.more.setting.utils.RecyclerViewAlarmAdapter;
import com.iii.more.setting.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.iii.more.setting.Pref.KEY_ALARM;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AlarmLv1Activity extends SettingBaseActivity implements
    RecyclerViewAlarmAdapter.ItemClickListener,
    RecyclerViewAlarmAdapter.DelToggleListener,
    RecyclerViewAlarmAdapter.AddToggleListener,
    RecyclerViewAlarmAdapter.DelClickListener {

    private String TAG = AlarmLv1Activity.class.getSimpleName();
    private Context mCtx;
    public static Activity mActivity;

    private final int REQUEST_EDIT = 5555;

    private RecyclerViewAlarmAdapter adapter;
    private RecyclerView rvFriends;

    public static final int ITEM_HEADER = 0;
    public static final int ITEM_BODY = 1;

    public static final int ALARM_SLEEP = 0;
    public static final int ALARM_BRUSH = 1;

    public static class Alarm {
        public int itemType = 1; // 0 = header , 1 = body
        public int alarmType; // 0 = sleep , 1 = brush
        public String name;
        public String time;
        public boolean[] recur = new boolean[7];
        public String story;
        public boolean bShowDel = false;
    }

    public static List<Alarm> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("生活作息");
        setLeftRes(R.drawable.ic_alarm_white_24dp);
        init_data();
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_alarm_lv1;
    }

    @Override
    public void onBackPressed() {
        if (ShopLv1Activity.mActivity != null) {
            ShopLv1Activity.mActivity.finish();
        }
        if (AlarmLv1Activity.mActivity != null) {
            AlarmLv1Activity.mActivity.finish();
        }
        if (SettingLv1Activity.mActivity != null) {
            SettingLv1Activity.mActivity.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT) {
            if (resultCode == RESULT_OK) {
                tinyInnerDB.putListObject(KEY_ALARM, (ArrayList<?>) alarms);
                init_data();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void init_data() {

        if (alarms == null) {
            alarms = new ArrayList<>();
        } else {
            alarms.clear();
        }

        ArrayList<? extends Object> ret = tinyInnerDB.getListObject(KEY_ALARM, Alarm.class);
        List<Alarm> alarm = (List<Alarm>) ret;

        List<Alarm> alarmsTmpSleep = new ArrayList<>();
        List<Alarm> alarmsTmpBrush = new ArrayList<>();
        for (int i = 0; i < alarm.size(); i++) {
            Alarm each = alarm.get(i);
            if (each.alarmType == ALARM_SLEEP) {
                each.bShowDel = false;
                alarmsTmpSleep.add(each);
            }
            if (each.alarmType == ALARM_BRUSH) {
                each.bShowDel = false;
                alarmsTmpBrush.add(each);
            }
        }

        for (int i = 0; i < alarmsTmpSleep.size(); i++) {
            alarms.add(alarmsTmpSleep.get(i));
        }

        for (int i = 0; i < alarmsTmpBrush.size(); i++) {
            alarms.add(alarmsTmpBrush.get(i));
        }
    }

    private void init_dataFake() {
        alarms = new ArrayList<>();
        int i1Max = 9;
        for (int i = 0; i < i1Max; i++) {
            Alarm each = new Alarm();
            if (i == 0) {
                each.itemType = 0;
                each.alarmType = ALARM_SLEEP;
                each.name = "睡眠時間";
            } else {
                each.itemType = 1;
                each.alarmType = ALARM_SLEEP;
                each.name = String.format("name %d", i);
                each.time = String.format("%d%d:%d%d", i, i, i, i);
                each.story = String.format("story %d", i);
                Random r = new Random();
                for (int b = 0; b < each.recur.length; b++) {
                    each.recur[b] = r.nextBoolean();
                }
            }
            alarms.add(each);
        }

        int i2Max = 5;
        for (int i = 0; i < i2Max; i++) {
            Alarm each = new Alarm();
            if (i == 0) {
                each.itemType = 0;
                each.alarmType = ALARM_BRUSH;
                each.name = "刷牙時間";
            } else {
                each.itemType = 1;
                each.alarmType = ALARM_BRUSH;
                each.name = String.format("b name %d", i);
                each.time = String.format("%d%d:%d%d", i, i, i, i);
                each.story = String.format("b story %d", i);
                Random r = new Random();
                for (int b = 0; b < each.recur.length; b++) {
                    each.recur[b] = r.nextBoolean();
                }
            }
            alarms.add(each);
        }
    }

    private void init_UI() {
        adapter = new RecyclerViewAlarmAdapter(this, alarms);
        adapter.setItemClickListener(this);
        adapter.setDelToggleListener(this);
        adapter.setAddToggleListener(this);
        adapter.setDelClickListener(this);

        rvFriends = (RecyclerView) findViewById(R.id.rvFriends);
        rvFriends.setLayoutManager(new LinearLayoutManager(mCtx));
        rvFriends.addItemDecoration(new SimpleDividerItemDecoration(mCtx));
        rvFriends.setAdapter(adapter);
    }

    @Override
    public void onDelToggle(View view, int position) {
        Log.d(TAG, alarms.get(position).name);
        Alarm alarm = alarms.get(position);
        int iAlarmType = alarm.alarmType;
        for (int i = 0; i < alarms.size(); i++) {
            if (alarms.get(i).alarmType == iAlarmType) {
                alarms.get(i).bShowDel = !alarms.get(i).bShowDel;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAddToggle(View view, int position) {
        Alarm alarm = alarms.get(position);

        Log.d(TAG, alarm.name);
        Intent i = new Intent(mCtx, AlarmLv2Activity.class);
        i.putExtra("position", position);
        i.putExtra("title", "新增");
        i.putExtra("alarmType", alarm.alarmType);
        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    public void onItemClick(View view, int position) {
        Alarm alarm = alarms.get(position);

        Log.d(TAG, alarm.name);
        Intent i = new Intent(mCtx, AlarmLv2Activity.class);
        i.putExtra("position", position);
        i.putExtra("title", "編輯");
        startActivityForResult(i, REQUEST_EDIT);
    }

    @Override
    public void onDelClick(View view, int position) {
        Log.d(TAG, "Del confirm :" + alarms.get(position).name);
        Alarm delAlarm = alarms.get(position);
        delConfirm(delAlarm);
    }

    private void delConfirm(final Alarm delAlarm) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setMessage("確定刪除");
        builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("刪除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                doDel(delAlarm);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doDel(Alarm delAlarm) {
        alarms.remove(delAlarm);
        tinyInnerDB.putListObject(KEY_ALARM, (ArrayList<?>) alarms);
        adapter.notifyDataSetChanged();
    }
}