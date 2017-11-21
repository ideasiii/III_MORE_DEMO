package com.iii.more.main.globaltts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sdk.ideas.common.BaseHandler;

public class TextToSpeechHandler extends BaseHandler
{
    private TextToSpeech tts = null;
    private Locale mLocale;
    private static int textID = 0;
    private boolean returnInitValue;

    public TextToSpeechHandler(Context mContext)
    {
        super(mContext);
        this.mLocale = Locale.TAIWAN;
        this.returnInitValue = false;
    }

    public TextToSpeechHandler(Context mContext, Locale mLocale)
    {
        super(mContext);
        this.mLocale = Locale.TAIWAN;
        this.returnInitValue = false;
        this.mLocale = mLocale;
    }

    public void setLocale(Locale mLocale)
    {
        this.mLocale = mLocale;
    }

    public Locale getLocale()
    {
        return this.mLocale;
    }

    public void init()
    {
        if (!this.checkPackageExist(this.mContext, "com.google.android.tts"))
        {
            HashMap<String, String> message = new HashMap();
            message.put("message", "package not found error");
            message.put("packageName", "com.google.android.tts");
            this.callBackMessage(-11, 1046, 0, message);
        }
        else
        {
            try
            {
                if (this.tts != null)
                {
                    this.tts.shutdown();
                    this.tts = null;
                }

                this.tts = new TextToSpeech(this.mContext, mOnInitListener);
            }
            catch (Exception var3)
            {
                HashMap<String, String> message = new HashMap();
                message.put("message", var3.toString());
                this.callBackMessage(0, 1046, 0, message);
            }
        }

    }

    public static synchronized int getTextID()
    {
        ++textID;
        return textID;
    }

    public void downloadTTS()
    {
        String var1 = "com.google.android.tts";

        try
        {
            ((Activity) this.mContext).startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.tts")));
        }
        catch (ActivityNotFoundException var3)
        {
            ((Activity) this.mContext).startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")));
        }
    }

    public void textToSpeech(String text, String textID)
    {
        if (this.returnInitValue && this.tts != null)
        {
            if (text == null)
            {
                text = "";
            }

            if (Build.VERSION.SDK_INT >= 21)
            {
                this.ttsGreater21(text, textID);
            }
            else
            {
                this.ttsUnder20(text, textID);
            }
        }
        else
        {
            HashMap<String, String> message = new HashMap();
            message.put("message", "init frist");
            this.callBackMessage(-6, 1046, 1, message);
        }

    }

    public void stop()
    {
        if (this.returnInitValue && this.tts != null)
        {
            this.tts.stop();
        }
        else
        {
            HashMap<String, String> message = new HashMap();
            message.put("message", "init frist");
            this.callBackMessage(-6, 1046, 1, message);
        }
    }

    public void shutdown()
    {
        if (this.returnInitValue && this.tts != null)
        {
            this.tts.shutdown();
            this.returnInitValue = false;
        }
    }

    public void setPitch(float fpitch, float frate)
    {
        tts.setPitch(fpitch);
        tts.setSpeechRate(frate);
    }

    private void ttsUnder20(String text, String textID)
    {
        HashMap<String, String> params = new HashMap();
        if (textID == null)
        {
            textID = String.valueOf(getTextID());
        }

        params.put("utteranceId", textID);
        this.tts.speak(text, 0, params);
        params.clear();
        params = null;
    }

    @TargetApi(21)
    private void ttsGreater21(String text, String textID)
    {
        if (textID == null)
        {
            textID = String.valueOf(getTextID());
        }

        this.tts.speak(text, 0, null, textID);
    }

    private boolean checkPackageExist(Context mContext, String packageName)
    {
        if (mContext != null && packageName != null)
        {
            boolean getSysPackages = false;
            List<PackageInfo> packs = mContext.getPackageManager().getInstalledPackages(0);

            for (int i = 0; i < packs.size(); ++i)
            {
                PackageInfo p = (PackageInfo) packs.get(i);
                if ((getSysPackages || p.versionName != null) && packageName.equals(p.packageName))
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    private OnInitListener mOnInitListener = new OnInitListener()
    {
        public void onInit(int status)
        {
            if (status == 0)
            {
                int result = tts.setLanguage(mLocale);
                HashMap message;
                if (result == -1)
                {
                    message = new HashMap();
                    message.put("message", "this Language is missing data, please download it");
                    callBackMessage(-13, 1046, 0, message);
                }
                else if (result == -2)
                {
                    message = new HashMap();
                    message.put("message", "this Language is not supported, please try another ones");
                    callBackMessage(-13, 1046, 0, message);
                }
                else
                {
                    returnInitValue = true;
                    message = new HashMap();
                    message.put("message", "init success");
                    callBackMessage(1, 1046, 0, message);
                    tts.setOnUtteranceProgressListener(mUtteranceProgressListener);
                }
            }
            else
            {
                HashMap<String, String> messagex = new HashMap();
                messagex.put("message", "ERROR status is" + status + " while new TextToSpeech method");
                callBackMessage(0, 1046, 0, messagex);
            }
        }
    };

    private UtteranceProgressListener mUtteranceProgressListener = new UtteranceProgressListener()
    {
        public void onStart(String utteranceId)
        {
            HashMap<String, String> message = new HashMap();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "START");
            message.put("message", "the speech is started");
            callBackMessage(1, 1046, 1, message);
        }

        public void onDone(String utteranceId)
        {
            HashMap<String, String> message = new HashMap();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "DONE");
            message.put("message", "the speech is done");
            callBackMessage(1, 1046, 1, message);
        }

        public void onError(String utteranceId)
        {
            HashMap<String, String> message = new HashMap();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "ERROR");
            message.put("message", "the speech occurred error");
            callBackMessage(0, 1046, 1, message);
        }

        @SuppressLint({"NewApi"})
        public void onError(String utteranceId, int errorCode)
        {
            HashMap<String, String> message = new HashMap();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "ERROR");
            message.put("message", "ERROR code is" + errorCode + ", check Google TextToSpeech Class ERROR Code");
            callBackMessage(0, 1046, 1, message);
            super.onError(utteranceId, errorCode);
        }

        @SuppressLint({"NewApi"})
        public void onStop(String utteranceId, boolean interrupted)
        {
            HashMap<String, String> message = new HashMap();
            message.put("TextID", utteranceId);
            message.put("TextStatus", "STOP");
            message.put("message", "the speech is stopped");
            callBackMessage(1, 1046, 1, message);
            super.onStop(utteranceId, interrupted);
        }
    };
}
