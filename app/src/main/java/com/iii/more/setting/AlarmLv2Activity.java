package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.more.main.R;

import java.util.Calendar;

import static com.iii.more.setting.AlarmLv1Activity.ITEM_BODY;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AlarmLv2Activity extends SettingBaseActivity {

    private String TAG = AlarmLv2Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private AlarmLv1Activity.Alarm alarm;

    private ImageView ivLeft;
    private TextView tvRight;

    private TextView tvBigTime;
    private EditText etName;
    private TextView tvTime;
    private TextView tvRecur;
    private TextView tvStory;

    private LinearLayout llTime;
    private LinearLayout llRecur;
    private LinearLayout llStory;

    final int REQUEST_TIME = 1111;
    final int REQUEST_RECUR = 2222;
    final int REQUEST_STORY = 3333;

    private String title = "";
    private int alarmType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int position = bundle.getInt("position");
            title = bundle.getString("title", "");
            alarmType = bundle.getInt("alarmType", -1);
            setTitle(title);

            if (title.equals("編輯")) {
                alarm = AlarmLv1Activity.alarms.get(position);
            }
        }
        setFooterVisible(View.GONE);
        letMeHandleLeft();
        init_UI();
    }

    @Override
    public void onBackPressed() {
        exitConfirm();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_alarm_lv2;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TIME) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String time = bundle.getString("time");
                    tvBigTime.setText(time);
                    tvTime.setText(time);
                }
            }
        }
        if (requestCode == REQUEST_RECUR) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String recur = bundle.getString("recur");
                        tvRecur.setText(recur);
                    }
                }
            }
        }
        if (requestCode == REQUEST_STORY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        String story = bundle.getString("story");
                        tvStory.setText(story);
                    }
                }
            }
        }
    }

    private void init_UI() {

        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitConfirm();
            }
        });

        tvRight = (TextView) findViewById(R.id.tvRight);
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareFinish();
            }
        });

        tvBigTime = (TextView) findViewById(R.id.tvBigTime);
        etName = (EditText) findViewById(R.id.etName);
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvRecur = (TextView) findViewById(R.id.tvRecur);
        tvStory = (TextView) findViewById(R.id.tvStory);

        llTime = (LinearLayout) findViewById(R.id.llTime);
        llRecur = (LinearLayout) findViewById(R.id.llRecur);
        llStory = (LinearLayout) findViewById(R.id.llStory);

        if (alarm != null) {
            tvBigTime.setText(alarm.time);
            tvTime.setText(alarm.time);
            etName.setText(alarm.name);
            tvRecur.setText(alarm.recur);
            tvStory.setText(alarm.story);
        } else {
            Calendar c = Calendar.getInstance();
            int iHH = c.get(Calendar.HOUR_OF_DAY);
            int iMM = c.get(Calendar.MINUTE);
            String defValue = String.format("%02d:%02d", iHH, iMM);
            tvBigTime.setText(defValue);
            tvTime.setText(defValue);
        }

        llTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AlarmLv31Activity.class);
                i.putExtra("time", tvTime.getText());
                startActivityForResult(i, REQUEST_TIME);
            }
        });
        llRecur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AlarmLv32Activity.class);
                i.putExtra("recur", tvRecur.getText());
                startActivityForResult(i, REQUEST_RECUR);
            }
        });
        llStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AlarmLv33Activity.class);
                i.putExtra("story", tvStory.getText());
                startActivityForResult(i, REQUEST_STORY);
            }
        });
    }

    private void exitConfirm(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setMessage("尚未儲存");
        builder.setNegativeButton("直接離開(不儲存)", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.setPositiveButton("離開+儲存", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                prepareFinish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void prepareFinish() {

        String name = etName.getText().toString();
        if (name.length() == 0) {
            etName.setError("請輸入名稱");
            return;
        }

        if( title.equals("新增") ){
            String suffix = String.valueOf(maxUsedCount);
            AlarmLv1Activity.Alarm newAlarm = new AlarmLv1Activity.Alarm();
            newAlarm.alarmType = alarmType;
            newAlarm.time = tvTime.getText().toString();
            newAlarm.name = name;
            newAlarm.recur = tvRecur.getText().toString();
            newAlarm.story = tvStory.getText().toString();

            PrefEditor.putInt(key1itemType + suffix, ITEM_BODY);
            PrefEditor.putInt(key2 + suffix, newAlarm.alarmType);
            PrefEditor.putString(key3name + suffix, newAlarm.name);
            PrefEditor.putString(key4time + suffix, newAlarm.time);
            PrefEditor.putString(key5story + suffix, newAlarm.story);
            PrefEditor.putString(key6recur + suffix, newAlarm.recur);
            PrefEditor.putString(key7prefIndex + suffix, suffix);
            PrefEditor.commit();

            maxUsedCount = maxUsedCount + 1;
            PrefEditor.putInt("maxUsedCount", maxUsedCount );
            PrefEditor.commit();
        }
        if( title.equals("編輯") ){
            String suffix = alarm.prefIndex;
            alarm.time = tvTime.getText().toString();
            alarm.name = name;
            alarm.recur = tvRecur.getText().toString();
            alarm.story = tvStory.getText().toString();

            //PrefEditor.putInt(key1itemType + suffix, ITEM_BODY);
            //PrefEditor.putInt(key2 + suffix, alarm.alarmType);
            PrefEditor.putString(key3name + suffix, alarm.name);
            PrefEditor.putString(key4time + suffix, alarm.time);
            PrefEditor.putString(key5story + suffix, alarm.story);
            PrefEditor.putString(key6recur + suffix, alarm.recur);
            //PrefEditor.putString(key7prefIndex + suffix, alarm.prefIndex);
            PrefEditor.commit();
        }

        setResult(RESULT_OK);
        finish();
    }
}
