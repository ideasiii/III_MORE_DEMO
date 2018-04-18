package com.iii.more.main;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cyberon.utility.CReaderPlayer;

import java.util.Locale;

import sdk.ideas.tool.speech.tts.TextToSpeechHandler;

/**
 * TTSVoicePool 類別整合 Google, 賽微等 TTS engine，提供單一的呼叫方式。<br>
 * 此類別使用 Handler 非同步地傳送事件，故使用前須先呼叫 {@link TTSVoicePool#setHandler(Handler)}
 * 方法設定接收事件的 Handler 物件。<br>
 * 使用 {@link TTSVoicePool#init()} } 方法初始化內部的 TTS 引擎，注意賽微 TTS 在初始化時會進行的其他動作。
 * 這個類別最好搭配 {@link TTSEventListenerBridge}，將原本要由 Handler 處理的、不同 TTS 引擎丟過來的各種事件
 * 改由 {@link com.iii.more.main.listeners.TTSEventListener} 統一進行處理
 */
public final class TTSVoicePool
{
    // TTS 引擎的編號

    /** Google TTS 的編號 */
    public static final byte TTS_VOICE_GOOGLE = 1;
    /** 賽微 TTS (kid, 女) 的編號 */
    public static final byte TTS_VOICE_CYBERON_KID_FEMALE = 2;
    /** 賽微 TTS (kid, 男) 的編號 */
    public static final byte TTS_VOICE_CYBERON_KID_MALE = 3;
    /** 有幾種聲音能用 */
    static final int TTS_VOICE_SIZE = TTS_VOICE_CYBERON_KID_MALE;

    private static final String LOG_TAG = "TTSVoicePool";

    private final Context mContext;
    private Handler mHandler;

    private TextToSpeechHandler mGoogleTtsHandler; // Google TTS
    private CReaderAdapter mCyberonTtsAdapter_KidMale; // 賽微 TTS: kid, 女性
    private CReaderAdapter mCyberonTtsAdapter_KidFemale; // 賽微 TTS: kid, 男性

    // 目前正在使用的 TTS 語音的編號
    private byte mActiveVoice = TTS_VOICE_CYBERON_KID_FEMALE;

    // 偏好使用的 TTS 語音的編號，但由於資源等現實狀況的限制，不一定等於 mActiveVoice
    private byte mPreferredVoice;

    public TTSVoicePool(Context context, byte preferredVoice)
    {
        mContext = context;
        mPreferredVoice = preferredVoice;
        mActiveVoice = TTS_VOICE_GOOGLE;
    }

    public void setHandler(Handler handler)
    {
        mHandler = handler;
    }

    /**
     * 初始化 TTSVoicePool 需要使用的資源。注意賽微 TTS 初始化時所進行的動作
     */
    public void init()
    {
        mGoogleTtsHandler = new TextToSpeechHandler(mContext);
        mGoogleTtsHandler.setHandler(mHandler);
        mGoogleTtsHandler.init();

        // init CReader using existing data (if any) to prevent any NullPointerException
        initCReader();
        downloadCReaderData();
    }

    /**
     * 下載賽微 TTS 需要的額外資料
     */
    private void downloadCReaderData()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                boolean getNewData = CyberonAssetsExtractor.extract(
                    mContext.getAssets(), mContext.getFilesDir().getAbsolutePath());

                if (getNewData)
                {
                    Log.d(LOG_TAG, "downloaded new data, reinit CReader");
                    initCReader();
                }
                else
                {
                    Log.d(LOG_TAG, "no new data, skip reinit CReader");
                }
            }
        }.start();
    }

    /** 初始化賽微 TTS 所需的資源 */
    private void initCReader()
    {
        String cReaderDataRoot = mContext.getFilesDir().getAbsolutePath() + "/cyberon/CReader";

        if (mCyberonTtsAdapter_KidMale != null)
        {
            mCyberonTtsAdapter_KidMale.shutdown();
        }
        mCyberonTtsAdapter_KidMale = new CReaderAdapter(mContext, cReaderDataRoot);
        mCyberonTtsAdapter_KidMale.setHandler(mHandler);
        mCyberonTtsAdapter_KidMale.setVoiceName(CReaderPlayer.VoiceNameConstant
            .TRADITIONAL_CHINESE_KID_MALE_VOICE_NAME);
        mCyberonTtsAdapter_KidMale.setSpeechRate(85);
        mCyberonTtsAdapter_KidMale.setVolume(200);
        mCyberonTtsAdapter_KidMale.init();

        if (mCyberonTtsAdapter_KidFemale != null)
        {
            mCyberonTtsAdapter_KidFemale.shutdown();
        }
        mCyberonTtsAdapter_KidFemale = new CReaderAdapter(mContext, cReaderDataRoot);
        mCyberonTtsAdapter_KidFemale.setHandler(mHandler);
        mCyberonTtsAdapter_KidFemale.setVoiceName(CReaderPlayer.VoiceNameConstant
            .TRADITIONAL_CHINESE_KID_FEMALE_VOICE_NAME);
        mCyberonTtsAdapter_KidFemale.setPitch(85);
        mCyberonTtsAdapter_KidFemale.setSpeechRate(85);
        mCyberonTtsAdapter_KidFemale.setVolume(200);
        mCyberonTtsAdapter_KidFemale.init();

        // switch to CReader if we wanted it but couldn't be done
        if (mPreferredVoice != mActiveVoice)
        {
            setVoice(mPreferredVoice);
        }
    }

    /** 取得目前正在使用中的 TTS 引擎編號 */
    public byte getActiveVoice()
    {
        return mActiveVoice;
    }

    /** 設定要使用的 TTS 引擎 */
    public void setVoice(byte who)
    {
        switch (who)
        {
            case TTS_VOICE_GOOGLE:
                mActiveVoice = who;
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                if (mCyberonTtsAdapter_KidFemale.hasInitialized())
                {
                    mActiveVoice = who;
                }
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                if (mCyberonTtsAdapter_KidMale.hasInitialized())
                {
                    mActiveVoice = who;
                }
                break;
            default:
                Log.d(LOG_TAG, "setVoice() unknown who '" + who + "'");
                return;
        }

        mPreferredVoice = who;

        if (mPreferredVoice != mActiveVoice)
        {
            Log.d(LOG_TAG, "setVoice() preferred voice " + mPreferredVoice
                + " cannot be used, using " + mActiveVoice + " instead");
        }
        else
        {
            Log.d(LOG_TAG, "setVoice() preferred voice " + mPreferredVoice
                + " has set active");
        }
    }

    /**
     * 使用賽微 TTS 的數值範圍設定音高
     * @param pitch 音高，範圍為 50~200，預設為 100
     */
    public void setPitchCyberonScaling(int pitch)
    {
        float googleMappedPitch = pitch / 100.0f;

        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                mGoogleTtsHandler.setPitch(googleMappedPitch);
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setPitch(pitch);
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setPitch(pitch);
                break;
            default:
                Log.d(LOG_TAG, "Unknown mActiveVoice: " + mActiveVoice);
        }
    }

    /**
     * 使用賽微 TTS 的數值範圍設定語速
     * @param rate 語速，範圍為 50~200，預設為 100
     */
    public void setSpeechRateCyberonScaling(int rate)
    {
        float googleMappedSpeed = rate / 100.0f;

        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                mGoogleTtsHandler.setSpeechRate(googleMappedSpeed);
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setSpeechRate(rate);
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setSpeechRate(rate);
                break;
            default:
                Log.d(LOG_TAG, "Unknown mActiveVoice: " + mActiveVoice);
        }
    }

    /**
     * 使用賽微 TTS 的數值範圍設定音高 & 速度
     * @param pitch 音高，範圍為 50~200，預設為 100
     * @param rate 語速，範圍為 50~200，預設為 100
     */
    public void setPitchCyberonScaling(int pitch, int rate)
    {
        setPitchCyberonScaling(pitch);
        setSpeechRateCyberonScaling(rate);
    }

    /**
     * 使用 Google TTS 的數值範圍設定音高 & 速度
     * @param pitch 音高，預設為 1.0
     * @param rate 語速，預設為 1.0
     */
    public void setPitchGoogleScaling(float pitch, float rate)
    {
        int cyberonMappedPitch = (int) ((pitch - 0.5) * 120) + 70;
        int cyberonMappedSpeed = (int) ((rate - 0.5) * 60) + 70;

        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                mGoogleTtsHandler.setPitch(pitch);
                mGoogleTtsHandler.setSpeechRate(rate);
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setPitch(cyberonMappedPitch);
                mCyberonTtsAdapter_KidFemale.setSpeechRate(cyberonMappedSpeed);
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setPitch(cyberonMappedPitch);
                mCyberonTtsAdapter_KidMale.setSpeechRate(cyberonMappedSpeed);
                break;
            default:
                Log.d(LOG_TAG, "Unknown mActiveVoice: " + mActiveVoice);
        }
    }

    /**
     * 使用 Google TTS 的數值範圍設定音高 & 速度
     * This method is left for backward compatibility.
     * @param pitch 音高，預設為 1.0
     * @param rate 語速，預設為 1.0
     */
    public void setPitch(float pitch, float rate)
    {
        setPitchGoogleScaling(pitch, rate);
    }

    /**
     * 設定 TTS 的輸出語言。
     * I'm afraid if this will have any effect to the TTS engines.
     */
    public void setLanguage(Locale language)
    {
        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                mGoogleTtsHandler.setLocale(language);
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.setLanguage(language);
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.setLanguage(language);
                break;
            default:
                Log.d(LOG_TAG, "Unknown mCurrentUsingTts: " + mActiveVoice);
        }
    }

    /**
     * 取得 TTS 的輸出語言
     */
    public Locale getLanguage()
    {
        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                return mGoogleTtsHandler.getLocale();
            case TTS_VOICE_CYBERON_KID_FEMALE:
                return mCyberonTtsAdapter_KidFemale.getLanguage();
            case TTS_VOICE_CYBERON_KID_MALE:
                return mCyberonTtsAdapter_KidMale.getLanguage();
            default:
                Log.d(LOG_TAG, "Unknown mCurrentUsingTts: " + mActiveVoice);
                return null;
        }
    }

    /**
     * 將 text 轉為語音輸出，開始發聲、結束發聲、將要結束發聲 (如果引擎支援) 等事件會傳遞至 Handler。
     */
    public void speak(String text, String textId)
    {
        switch (mActiveVoice)
        {
            case TTS_VOICE_GOOGLE:
                mGoogleTtsHandler.textToSpeech(text, textId);
                break;
            case TTS_VOICE_CYBERON_KID_FEMALE:
                mCyberonTtsAdapter_KidFemale.speak(text, textId);
                break;
            case TTS_VOICE_CYBERON_KID_MALE:
                mCyberonTtsAdapter_KidMale.speak(text, textId);
                break;
            default:
                Log.d(LOG_TAG, "Unknown mCurrentUsingTts: " + mActiveVoice);
        }
    }

    /**
     * 設定好 TTS pitch & speech rate 後再將 text 轉為語音輸出
     */
    public void speak(String text, String textId, float pitch, float rate)
    {
        setPitch(pitch, rate);
        speak(text, textId);
    }

    /**
     * 停止正在進行的 TTS
     */
    public void stop()
    {
        mGoogleTtsHandler.stop();
        mCyberonTtsAdapter_KidMale.stop();
        mCyberonTtsAdapter_KidFemale.stop();
    }
}
