package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class PowerLv1Activity extends SettingBaseActivity {

    private String TAG = PowerLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("電源");

        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_power_lv1;
    }

    private void init_UI()
    {

    }

}
