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

public class AccountLv1Activity extends SettingBaseActivity {

    private String TAG = AccountLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("帳號");
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_account_lv1;
    }

    private void init_UI()
    {
        LinearLayout llItem1 = (LinearLayout)findViewById(R.id.llItem1);
        llItem1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AccountLv2Activity.class);
                startActivity(i);
            }
        });
    }

}
