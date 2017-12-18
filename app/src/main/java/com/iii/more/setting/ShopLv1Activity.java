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

public class ShopLv1Activity extends SettingBaseActivity {

    private String TAG = ShopLv1Activity.class.getSimpleName();
    private Context mCtx;
    public static Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("商城");
        setLeftRes(R.drawable.ic_school_white_24dp);
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_shop_lv1;
    }

    @Override
    public void onBackPressed() {
        if( ShopLv1Activity.mActivity != null ) {
            ShopLv1Activity.mActivity.finish();
        }
        if( AlarmLv1Activity.mActivity != null ) {
            AlarmLv1Activity.mActivity.finish();
        }
        if( SettingLv1Activity.mActivity != null ) {
            SettingLv1Activity.mActivity.finish();
        }
    }

    private void init_UI() {

    }
}
