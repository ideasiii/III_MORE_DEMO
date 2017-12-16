package com.iii.more.setting;

import android.os.Bundle;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class SettingLv1Activity extends SettingBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

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

    }

}
