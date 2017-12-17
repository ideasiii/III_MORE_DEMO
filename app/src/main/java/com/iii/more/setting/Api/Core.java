package com.iii.more.setting.Api;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.iii.more.setting.Api.Table.HTTP_EXCEPTION;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class Core {

    private final String TAG = Core.class.getSimpleName();
    private ApiTask mApiTask;

    public void TriggerApiTask(@NonNull Table.Request apiRequest) {
        mApiTask = new ApiTask(apiRequest);
        mApiTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
    }

    private class ApiTask extends AsyncTask<Void, Void, Integer> {

        private Table.Request apiRequest;
        private Table.Response apiResponse;

        public ApiTask(Table.Request apiRequest) {
            this.apiRequest = apiRequest;
            apiResponse = new Table.Response(apiRequest.function_id);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if( apiRequest.formBody == null ) {
                cancel(true);
                Log.e(TAG, apiRequest.getPath() + ": not accept null formBody");
            }
        }

        @Override
        protected Integer doInBackground(Void... param) {

            String url = Table.mServer + apiRequest.getPath();
            Log.e(TAG, url);

            Request request = new Request.Builder()
                .url(url)
                .post(apiRequest.formBody)
                .headers(Table.getHeader())
                .build();

            try {
                OkHttpClient client = new OkHttpClient();
                Response response = client.newCall(request).execute();
                apiResponse.httpBody = response.body().string();
                apiResponse.httpCode = response.code();
            } catch (IOException e) {
                e.printStackTrace();
                apiResponse.httpCode = HTTP_EXCEPTION;
            }

            return apiResponse.httpCode;
        }

        @Override
        protected void onPostExecute(final Integer apiResponseCode) {
            mApiTask = null;
            EventBus.getDefault().post(apiResponse);
        }

        @Override
        protected void onCancelled() {
            mApiTask = null;
        }
    }

}
