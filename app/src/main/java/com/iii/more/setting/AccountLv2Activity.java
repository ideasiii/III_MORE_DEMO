package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.iii.more.main.R;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class AccountLv2Activity extends SettingBaseActivity {

    private String TAG = AccountLv2Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("帳號");
        setFooterVisible(View.GONE);
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_account_lv2;
    }

    private void init_UI()
    {

    }

}
