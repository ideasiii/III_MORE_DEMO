package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.iii.more.main.R;
import com.iii.more.setting.Api.Core;
import com.iii.more.setting.Api.Table;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

import static com.iii.more.setting.Api.Table.HTTP_SUCCESS;
import static com.iii.more.setting.Pref.KEY_IS_LOW_POWER;
import static com.iii.more.setting.Pref.KEY_LANGUAGE;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class SettingLv1Activity extends SettingBaseActivity {

    private String TAG = SettingLv1Activity.class.getSimpleName();
    private Context mCtx;
    public static Activity mActivity;

    LinearLayout llAccount;
    LinearLayout llBought;
    LinearLayout llLanguage;
    LinearLayout llPower;
    LinearLayout llReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("設定");
        setLeftRes(R.drawable.ic_settings_white_24dp);

        init_UI();
    }

    @Override
    protected void onResume(){
        super.onResume();

        // 取得設定資料的 範本
//        Pref pref = new Pref(mCtx);
//        List<Brush> brushes = pref.getBrush();
//        List<Sleep> sleeps = pref.getSleep();
//        int iLang = pref.getLanguage();
//        boolean bLowPower = pref.isLowPower();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_setting_lv1;
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
        llAccount = (LinearLayout) findViewById(R.id.llAccount);
        llAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, AccountLv1Activity.class);
                startActivity(i);
            }
        });
        
        llBought = (LinearLayout) findViewById(R.id.llBought);
        llBought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, BoughtLv1Activity.class);
                startActivity(i);
            }
        });

        llLanguage = (LinearLayout) findViewById(R.id.llLanguage);
        llLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, LanguageLv1Activity.class);
                startActivity(i);
            }
        });

        llPower = (LinearLayout) findViewById(R.id.llPower);
        llPower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mCtx, PowerLv1Activity.class);
                startActivity(i);
            }
        });

        llReset = (LinearLayout) findViewById(R.id.llReset);
        llReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriggerReset();
            }
        });
    }

    private void TriggerReset() {
        tinyInnerDB.putBoolean(KEY_IS_LOW_POWER, false);
        tinyInnerDB.putInt(KEY_LANGUAGE, 0);

        Table.Request request = new Table.Request(Table.setting_reset_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    @Override
    public void onEventBus(Table.Response response) {
        super.onEventBus(response);
        Log.e(TAG, response.getPath());
        Log.e(TAG, String.valueOf(response.httpCode));
        Log.e(TAG, response.httpBody);
        if (response.httpCode == HTTP_SUCCESS) {
            switch (response.function_id) {
                case Table.setting_reset_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            Toast.makeText(mCtx, response.getPath() + " SUCCESS", Toast.LENGTH_SHORT).show();
                        } else {
                            String error = jsonObject.optString("error");
                            String message = jsonObject.optString("message");
                            String messageInTable = response.getErrorDescription(error);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
