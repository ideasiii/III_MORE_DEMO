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
    }

    private void TriggerCreate() {
        Table.Request request = new Table.Request(Table.device_info_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    private void TriggerGetInfo() {
        Table.Request request = new Table.Request(Table.device_info_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    @Override
    public void onEventBus(Table.Response response) {
        Log.e(TAG, response.getPath());
        Log.e(TAG, String.valueOf(response.httpCode));
        Log.e(TAG, response.httpBody);
        if (response.httpCode == HTTP_SUCCESS) {
            switch (response.function_id) {
                case Table.device_info_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            String mac_address = jsonObject.optString("mac_address");
                            String device_os = jsonObject.optString("device_os");
                            Log.d(TAG, mac_address);
                            Log.d(TAG, device_os);
                        } else {
                            String error = jsonObject.optString("error");
                            String message = jsonObject.optString("message");
                            String messageInTable = response.getErrorDescription(error);
                            Toast.makeText(mCtx, error + "\n" + messageInTable, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case Table.device_create_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            // TODO success parser
                        } else {
                            String error = jsonObject.optString("error");
                            String message = jsonObject.optString("message");
                            String messageInTable = response.getErrorDescription(error);
                            Toast.makeText(mCtx, error + "\n" + messageInTable, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }
}
