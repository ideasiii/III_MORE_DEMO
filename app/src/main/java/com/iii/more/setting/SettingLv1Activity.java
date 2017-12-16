package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class SettingLv1Activity extends SettingBaseActivity {

    private String TAG = SettingLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    LinearLayout llAccount;
    LinearLayout llBought;
    LinearLayout llLanguage;
    LinearLayout llPower;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("設定");
        setLeftRes(R.drawable.ic_home_white_24dp);

        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_setting_lv1;
    }

    private void init_UI()
    {
        llAccount = (LinearLayout) findViewById(R.id.llAccount);
        llAccount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AccountLv1Activity.class);
                startActivity(i);
            }
        });
        llBought = (LinearLayout) findViewById(R.id.llBought);
        llBought.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, BoughtLv1Activity.class);
                startActivity(i);
            }
        });

        llLanguage = (LinearLayout) findViewById(R.id.llLanguage);
        llLanguage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, LanguageLv1Activity.class);
                startActivity(i);
            }
        });

        llPower = (LinearLayout) findViewById(R.id.llPower);
        llPower.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, PowerLv1Activity.class);
                startActivity(i);
            }
        });
    }

}
