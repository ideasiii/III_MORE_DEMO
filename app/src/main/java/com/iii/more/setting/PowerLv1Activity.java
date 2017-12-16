package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class PowerLv1Activity extends SettingBaseActivity {

    private String TAG = PowerLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    private Switch switch1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

    private void init_UI() {
        switch1 = (Switch) findViewById(R.id.switch1);
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TriggerSetting();
            }
        });
        TriggerQuery();
    }

    private void TriggerQuery() {
        Table.Request request = new Table.Request();
        request.api_id = Table.setting_option_lowpower_id;
        request.function_path = Table.setting_option_lowpower_url;
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    private void TriggerSetting() {
        String action = switch1.isChecked() ? "0" : "1";
        Table.Request request = new Table.Request();
        request.api_id = Table.setting_lowpower_id;
        request.function_path = Table.setting_lowpower_url;
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .add("action", action)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    @Override
    public void onEventBus(Table.Response response) {
        Log.e(TAG, String.valueOf(response.api_id));
        Log.e(TAG, String.valueOf(response.httpCode));
        Log.e(TAG, response.httpBody);
        if (response.httpCode == HTTP_SUCCESS) {
            switch (response.api_id) {
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
                            Toast.makeText(mCtx, error + "\n" + message, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(mCtx, error + "\n" + message, Toast.LENGTH_SHORT).show();
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
