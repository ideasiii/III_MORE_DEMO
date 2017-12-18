package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
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

public class LanguageLv1Activity extends SettingBaseActivity {

    private String TAG = LanguageLv1Activity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    CheckedTextView ctv1;
    CheckedTextView ctv2;
    CheckedTextView ctv3;
    CheckedTextView ctv4;
    private int LanguageId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mCtx = this;
        mActivity = this;

        setTitle("語言");
        setFooterVisible(View.GONE);
        init_UI();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_language_lv1;
    }

    private void init_UI()
    {
        ctv1 = (CheckedTextView) findViewById(R.id.ctv1);
        ctv1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() == false ) {
                    ctv1.toggle();
                    LanguageId = 0;
                    TriggerSetting();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv2 = (CheckedTextView) findViewById(R.id.ctv2);
        ctv2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() == false ) {
                    ctv2.toggle();
                    LanguageId = 1;
                    TriggerSetting();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv3 = (CheckedTextView) findViewById(R.id.ctv3);
        ctv3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() == false ) {
                    ctv3.toggle();
                    LanguageId = 2;
                    TriggerSetting();
                }
                if( ctv4.isChecked() ) {
                    ctv4.toggle();
                }
            }
        });
        ctv4 = (CheckedTextView) findViewById(R.id.ctv4);
        ctv4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if( ctv1.isChecked() ) {
                    ctv1.toggle();
                }
                if( ctv2.isChecked() ) {
                    ctv2.toggle();
                }
                if( ctv3.isChecked() ) {
                    ctv3.toggle();
                }
                if( ctv4.isChecked() == false ) {
                    ctv4.toggle();
                    LanguageId = 3;
                    TriggerSetting();
                }
            }
        });
        TriggerQuery();
    }
    private void TriggerQuery() {
        Table.Request request = new Table.Request(Table.setting_option_language_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    private void TriggerSetting() {
        String action = String.valueOf(LanguageId);
        Table.Request request = new Table.Request(Table.setting_language_id);
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
                case Table.setting_option_language_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            int result = jsonObject.optInt("result");
                            Log.d(TAG, String.valueOf(result));
                            if (result == 0) {
                                ctv1.setChecked(true);
                            }
                            if (result == 1) {
                                ctv2.setChecked(true);
                            }
                            if (result == 2) {
                                ctv3.setChecked(true);
                            }
                            if (result == 3) {
                                ctv4.setChecked(true);
                            }
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
                case Table.setting_language_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {

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
