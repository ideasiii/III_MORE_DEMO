package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.iii.more.main.R;
import com.iii.more.setting.Api.Core;
import com.iii.more.setting.Api.Table;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

import static com.iii.more.setting.Api.Table.HTTP_SUCCESS;
import static com.iii.more.setting.Pref.KEY_IS_LOW_POWER;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class PowerLv1Activity extends SettingBaseActivity {

    private String TAG = PowerLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private Switch switch1;
    private boolean isLowPower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        isLowPower = tinyInnerDB.getBoolean(KEY_IS_LOW_POWER);
        setTitle("電源");
        setFooterVisible(View.GONE);
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_power_lv1;
    }

    private void init_UI() {
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean checked = ((Switch)v).isChecked();
                String action = switch1.isChecked() ? "1" : "0";
                TriggerSetting(action);
            }
        });

        // TriggerQuery();
        // API 暫時 視為 儲存副本的功能。非，取出資料使用。
        // Setting Pref 採用本機為主

        String action = "0";
        if( isLowPower ) {
            switch1.setChecked(true);
            action = "1";
        }
        TriggerSetting(action);
    }

    private void TriggerQuery() {
        Table.Request request = new Table.Request(Table.setting_option_lowpower_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    private void TriggerSetting(String action) {
        boolean bValue = false;
        if( action.equals("0") ) {
            bValue = false;
        }
        if( action.equals("1") ) {
            bValue = true;
        }
        tinyInnerDB.putBoolean(KEY_IS_LOW_POWER, bValue);

        Table.Request request = new Table.Request(Table.setting_lowpower_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .add("action", action)
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
                case Table.setting_option_lowpower_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            int result = jsonObject.optInt("result");
                            Log.d(TAG, String.valueOf(result));
                            if (result == 0) {
                                switch1.setChecked(false);
                            }
                            if (result == 1) {
                                switch1.setChecked(true);
                            }
                        } else {
                            String error = jsonObject.optString("error");
                            String message = jsonObject.optString("message");
                            String messageInTable = response.getErrorDescription(error);
                            //Toast.makeText(mCtx, error + "\n" + messageInTable, Toast.LENGTH_SHORT).show();
                            if( error.equals("ER0100") ) {
                                switch1.setChecked(false);
                                TriggerSetting("0");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case Table.setting_lowpower_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {

                        } else {
                            String error = jsonObject.optString("error");
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
