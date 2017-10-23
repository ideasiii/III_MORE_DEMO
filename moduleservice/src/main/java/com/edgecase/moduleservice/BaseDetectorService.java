package com.edgecase.moduleservice;

import android.app.Service;

abstract public class BaseDetectorService extends Service {

    static protected OnDetectionListener mOnDetectionListener;

    public void setOnEventListener(OnDetectionListener listener) {
        mOnDetectionListener = listener;
    }
}
