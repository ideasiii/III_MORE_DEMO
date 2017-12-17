package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.iii.more.main.R;
import com.iii.more.setting.utils.RecyclerViewFriendsAdapter;
import com.iii.more.setting.utils.SimpleDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AlarmLv1Activity extends SettingBaseActivity implements
    RecyclerViewFriendsAdapter.ItemClickListener,
    RecyclerViewFriendsAdapter.DelToggleListener,
    RecyclerViewFriendsAdapter.AddToggleListener,
    RecyclerViewFriendsAdapter.DelClickListener {

    private String TAG = AlarmLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private RecyclerViewFriendsAdapter adapter;
    private RecyclerView rvFriends;

    private final int SLEEP = 0;
    private final int BRUSH = 1;

    public static class Alarm {
        public int itemType = 1; // 0 = header , 1 = body
        public int alarmType; // 0 = sleep , 1 = brush
        public String hhmm;
        public String title;
        public String description;
        public boolean bShowDel = false;
    }

    private List<Alarm> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("生活作息");
        init_data();
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_alarm_lv1;
    }

    private void init_data() {
        alarms = new ArrayList<>();
        int i1Max = 9;
        for (int i = 0; i < i1Max; i++) {
            Alarm each = new Alarm();
            if (i == 0) {
                each.itemType = 0;
                each.alarmType = SLEEP;
                each.title = "睡眠時間";
            } else {
                each.itemType = 1;
                each.alarmType = SLEEP;
                each.hhmm = String.format("%d%d:%d%d", i, i, i, i);
                each.title = String.format("title %d", i);
                each.description = String.format("description %d", i);
            }
            alarms.add(each);
        }

        int i2Max = 5;
        for (int i = 0; i < i2Max; i++) {
            Alarm each = new Alarm();
            if (i == 0) {
                each.itemType = 0;
                each.alarmType = BRUSH;
                each.title = "刷牙時間";
            } else {
                each.itemType = 1;
                each.alarmType = BRUSH;
                each.hhmm = String.format("%d%d:%d%d", i, i, i, i);
                each.title = String.format("b title %d", i);
                each.description = String.format("b description %d", i);
            }
            alarms.add(each);
        }
    }

    private void init_UI() {
        adapter = new RecyclerViewFriendsAdapter(this, alarms);
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
        Log.d(TAG, alarms.get(position).title);
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
        Log.d(TAG, alarms.get(position).title);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, alarms.get(position).title);
    }

    @Override
    public void onDelClick(View view, int position) {
        Log.d(TAG, "Del confirm :" + alarms.get(position).title);
    }

}