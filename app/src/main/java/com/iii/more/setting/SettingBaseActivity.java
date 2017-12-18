package com.iii.more.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iii.more.main.R;
import com.iii.more.setting.Api.Core;
import com.iii.more.setting.Api.Table;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;

import static com.iii.more.setting.Api.Table.HTTP_SUCCESS;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public abstract class SettingBaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    private final String TAG = SettingBaseActivity.class.getSimpleName();
    private Context mCtx;
    private Activity mActivity;

    public static final String PREFS_NAME = "iii.more.demo.setting";
    public static SharedPreferences PrefSettings;
    public static SharedPreferences.Editor PrefEditor;
    public static int maxUsedCount;

    public static final String key1itemType = "itemType";
    public static final String key2 = "alarmType";
    public static final String key3name = "name";
    public static final String key4time = "time";
    public static final String key5story = "story";
    public static final String key6recur = "recur";
    public static final String key7prefIndex = "prefIndex";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        mCtx = this;
        mActivity = this;

        if (PrefSettings == null) {
            TriggerCreate();
            PrefSettings = mCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            PrefEditor = PrefSettings.edit();
        }

        init_UI();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Table.Response response) {
        Log.e(TAG, response.getPath());
        Log.e(TAG, String.valueOf(response.httpCode));
        Log.e(TAG, response.httpBody);
        if (response.httpCode == HTTP_SUCCESS) {
            switch (response.function_id) {
                case Table.device_create_id: {
                    try {
                        JSONObject jsonObject = new JSONObject(response.httpBody);
                        boolean success = jsonObject.optBoolean("success");
                        if (success) {
                            Log.e(TAG, response.getPath() + " success");
                        } else {
                            String error = jsonObject.optString("error");
                            String message = jsonObject.optString("message");
                            String messageInTable = response.getErrorDescription(error);
                            Log.e(TAG, response.getPath() + " " + messageInTable);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void TriggerCreate() {
        String android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        String device_os = Build.VERSION.RELEASE;
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String mac_address = wInfo.getMacAddress();

        Table.device_id = android_id;
        Table.Request request = new Table.Request(Table.device_create_id);
        FormBody formBody = new FormBody.Builder()
            .add("device_id", Table.device_id)
            .add("device_os", device_os)
            .add("mac_address", mac_address)
            .build();
        request.formBody = formBody;
        new Core().TriggerApiTask(request);
    }

    protected void setTitle(String value) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(value);
        }
    }

    protected void setFooterVisible(int value) {
        LinearLayout llFooter = (LinearLayout) findViewById(R.id.llFooter);
        if (llFooter != null) {
            llFooter.setVisibility(value);
        }
    }

    protected void setLeftRes(@DrawableRes int res) {
        ImageView ivLeft = (ImageView) findViewById(R.id.ivLeft);
        if (ivLeft != null) {
            ivLeft.setImageDrawable(ContextCompat.getDrawable(this, res));
            ivLeft.setTag(res);
        }
    }

    private boolean bChildHandleLeft = false;

    protected void letMeHandleLeft() {
        bChildHandleLeft = true;
    }

    private void init_UI() {
        final ImageView ivLeft = (ImageView) findViewById(R.id.ivLeft);
        if (ivLeft != null) {
            ivLeft.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int imageRes = getImageResource(ivLeft);
                    if (imageRes == R.drawable.ic_arrow_back_white_24dp) {
                        if (bChildHandleLeft == false) {
                            finish();
                        }
                    }
                }
            });
        }

        LinearLayout llMenuL = (LinearLayout) findViewById(R.id.llMenuL);
        if (llMenuL != null) {
            llMenuL.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mCtx, ShopLv1Activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
            });
        }
        LinearLayout llMenuM = (LinearLayout) findViewById(R.id.llMenuM);
        if (llMenuM != null) {
            llMenuM.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mCtx, AlarmLv1Activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
            });
        }
        LinearLayout llMenuR = (LinearLayout) findViewById(R.id.llMenuR);
        if (llMenuR != null) {
            llMenuR.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mCtx, SettingLv1Activity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(i);
                }
            });
        }

        TextView tvRight = (TextView) findViewById(R.id.tvRight);
        if (tvRight != null) {
            tvRight.setVisibility(View.GONE);
        }
    }

    private int getImageResource(ImageView iv) {
        int iRet = 0;
        Object obj = iv.getTag();
        if (obj instanceof String) {
            String strTag = (String) obj;
            strTag = strTag.toLowerCase();
            if (strTag.contains("back")) {
                iRet = R.drawable.ic_arrow_back_white_24dp;
            }
        } else if (obj instanceof Integer) {
            iRet = (Integer) iv.getTag();
        }
        return iRet;
    }
}
