package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AlarmLv33Activity extends SettingBaseActivity {

    private String TAG = AlarmLv33Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private ImageView ivLeft;
    private CheckedTextView ctv0;
    private CheckedTextView ctv1;
    private CheckedTextView ctv2;
    private CheckedTextView ctv3;
    private CheckedTextView ctv4;
    private CheckedTextView ctv5;
    private CheckedTextView ctv6;
    private CheckedTextView[] arrCtv;
    private String story = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            story = bundle.getString("story");
        }

        setTitle("睡前故事/音樂");
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
        return R.layout.activity_alarm_lv33;
    }

    private void init_UI() {

        ctv0 = (CheckedTextView) findViewById(R.id.ctv0);
        ctv1 = (CheckedTextView) findViewById(R.id.ctv1);
        ctv2 = (CheckedTextView) findViewById(R.id.ctv2);
        ctv3 = (CheckedTextView) findViewById(R.id.ctv3);
        ctv4 = (CheckedTextView) findViewById(R.id.ctv4);
        ctv5 = (CheckedTextView) findViewById(R.id.ctv5);
        ctv6 = (CheckedTextView) findViewById(R.id.ctv6);
        arrCtv = new CheckedTextView[7];
        arrCtv[0] = ctv0;
        arrCtv[1] = ctv1;
        arrCtv[2] = ctv2;
        arrCtv[3] = ctv3;
        arrCtv[4] = ctv4;
        arrCtv[5] = ctv5;
        arrCtv[6] = ctv6;

        for( int i=0;i<arrCtv.length;i++ ){
            arrCtv[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v)
                {
                    for( int i=0;i<arrCtv.length;i++ ){
                        arrCtv[i].setChecked(false);
                    }
                    CheckedTextView me = (CheckedTextView)v;
                    me.setChecked(true);
                }
            });
        }

        for( int i=0;i<arrCtv.length;i++ ){
            if( arrCtv[i].getText().toString().equals(story) ) {
                arrCtv[i].setChecked(true);
                break;
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

    private void prepareFinish() {

        String story = "";
        for( int i=0;i<arrCtv.length;i++ ){
            if( arrCtv[i].isChecked() ) {
                story = arrCtv[i].getText().toString();
                break;
            }
        }

        if( story.length() > 0 ) {
            Intent i = new Intent();
            i.putExtra("story", story);
            setResult(Activity.RESULT_OK, i);
        }

        finish();
    }
}
