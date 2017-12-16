package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class LanguageLv1Activity extends SettingBaseActivity {

    private String TAG = LanguageLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    CheckedTextView ctv1;
    CheckedTextView ctv2;
    CheckedTextView ctv3;
    CheckedTextView ctv4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("語言");

        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_language_lv1;
    }

    private void init_UI()
    {
        ctv1 = (CheckedTextView) findViewById(R.id.ctv1);
        ctv1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() == false ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv2 = (CheckedTextView) findViewById(R.id.ctv2);
        ctv2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() == false ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv3 = (CheckedTextView) findViewById(R.id.ctv3);
        ctv3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() == false ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv4 = (CheckedTextView) findViewById(R.id.ctv4);
        ctv4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() == false ) {
                    ctv4.toggle();
                }
            }
        });
    }

}
