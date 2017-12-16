package com.iii.more.setting;

import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iii.more.main.R;
import com.iii.more.setting.Api.Table;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public abstract class SettingBaseActivity extends AppCompatActivity {

    protected abstract int getLayoutResourceId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
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

    }

    protected void setTitle(String value) {
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(value);
        }
    }

    protected void setLeftRes(@DrawableRes int res) {
        ImageView ivLeft = (ImageView) findViewById(R.id.ivLeft);
        if (ivLeft != null) {
            ivLeft.setImageDrawable(ContextCompat.getDrawable(this, res));
            ivLeft.setTag(res);
        }
    }

    private void init_UI() {
        final ImageView ivLeft = (ImageView) findViewById(R.id.ivLeft);
        if (ivLeft != null) {
            ivLeft.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int imageRes = getImageResource(ivLeft);
                    if (imageRes == R.drawable.ic_arrow_back_white_24dp) {
                        finish();
                    }
                }
            });
        }
    }

    private int getImageResource(ImageView iv) {
        int iRet = 0;
        Object obj = iv.getTag();
        if (obj instanceof String) {
            String strTag = (String)obj;
            strTag = strTag.toLowerCase();
            if( strTag.contains("back") ) {
                iRet = R.drawable.ic_arrow_back_white_24dp;
            }
        } else if (obj instanceof Integer) {
            iRet = (Integer) iv.getTag();
        }
        return iRet;
    }
}
