package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AlarmLv31Activity extends SettingBaseActivity {

    private String TAG = AlarmLv2Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private ImageView ivLeft;
    private TimePicker timePicker;
    private String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if( bundle != null ) {
            time = bundle.getString("time", "defValue");
        }

        setTitle("時間設定");
        setFooterVisible(View.GONE);
        letMeHandleLeft();
        init_UI();
    }

    @Override
    public void onBackPressed() {
        prepareFinish();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_alarm_lv31;
    }

    private void init_UI() {

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        String[] arrTime = time.split(":");
        if( arrTime != null && arrTime.length > 0 ) {
            int iHH = Integer.parseInt(arrTime[0]);
            timePicker.setCurrentHour(iHH);
            if( arrTime.length > 1 ) {
                int iMM = Integer.parseInt(arrTime[1]);
                timePicker.setCurrentMinute(iMM);
            }
        }

        ivLeft = (ImageView) findViewById(R.id.ivLeft);
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareFinish();
            }
        });
    }

    private void prepareFinish(){
        String HHMM = String.format("%02d:%02d", timePicker.getCurrentHour(), timePicker.getCurrentMinute() );
        Intent i = new Intent();
        i.putExtra("time", HHMM);
        setResult(Activity.RESULT_OK, i);
        finish();
    }
}
