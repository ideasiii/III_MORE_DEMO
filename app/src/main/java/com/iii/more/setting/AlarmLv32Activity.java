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

public class AlarmLv32Activity extends SettingBaseActivity {

    private String TAG = AlarmLv32Activity.class.getSimpleName();
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
    private String recur = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            recur = bundle.getString("recur");
        }

        setTitle("重複");
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
        return R.layout.activity_alarm_lv32;
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
                    CheckedTextView me = (CheckedTextView)v;
                    me.toggle();
                }
            });
        }

        String[] arrRecur = recur.split(",");
        if (arrRecur != null && arrRecur.length > 0) {
            for (int i = 0; i < arrRecur.length; i++) {
                String decimal = getDecimal(arrRecur[i]);
                int index = Integer.parseInt(decimal);
                arrCtv[index].setChecked(true);
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

    private String getDecimal(String value){
        String strRet = "";
        if( value.equals("日") ) {
            strRet = "0";
        }
        if( value.equals("一") ) {
            strRet = "1";
        }
        if( value.equals("二") ) {
            strRet = "2";
        }
        if( value.equals("三") ) {
            strRet = "3";
        }
        if( value.equals("四") ) {
            strRet = "4";
        }
        if( value.equals("五") ) {
            strRet = "5";
        }
        if( value.equals("六") ) {
            strRet = "6";
        }
        return strRet;
    }

    private String getChinese(int value){
        String strRet = "";
        if( value == 0 ) {
            strRet = "日";
        }
        if( value == 1 ) {
            strRet = "一";
        }
        if( value == 2 ) {
            strRet = "二";
        }
        if( value == 3 ) {
            strRet = "三";
        }
        if( value == 4 ) {
            strRet = "四";
        }
        if( value == 5 ) {
            strRet = "五";
        }
        if( value == 6 ) {
            strRet = "六";
        }
        return strRet;
    }

    private void prepareFinish() {
        String recur = "";
        for( int i=0;i<arrCtv.length;i++ ){
            if( arrCtv[i].isChecked() ) {
                recur = recur + getChinese(i) + ",";
            }
        }

        if( recur.length() > 0 ) {
            recur = recur.substring(0, recur.length() - 1);
            Intent i = new Intent();
            i.putExtra("recur", recur);
            setResult(Activity.RESULT_OK, i);
        }

        finish();
    }
}
