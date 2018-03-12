package com.iii.more.main;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cyberon.utility.CReaderPlayer;

import java.util.Locale;

import sdk.ideas.tool.speech.tts.TextToSpeechHandler;

/**
 * 不同 TTS 聲音的集中地
 */
public final class TTSVoicePool
{
    public static final byte TTS_VOICE_GOOGLE = 1;
    public static final byte TTS_VOICE_CYBERON_KID_FEMALE = 2;
    public static final byte TTS_VOICE_CYBERON_KID_MALE = 3;
    public static final int TTS_VOICE_SIZE = TTS_VOICE_CYBERON_KID_MALE;
    private static final String LOG_TAG = "TTSVoicePool";

    private final Context mContext;
    private Handler mHandler;
    private String cReaderDataRoot;

    private TextToSpeechHandler mGoogleTtsHandler; // Google TTS (女性)
    private CReaderAdapter mCyberonTtsAdapter_KidMale; // 賽微 TTS: 女性
    private CReaderAdapter mCyberonTtsAdapter_KidFemale; // 賽微 TTS: 男性

    // 目前正在使用的 TTS 語音的編號
    private byte mActiveVoice = TTS_VOICE_CYBERON_KID_FEMALE;

    // 偏好使用的 TTS 語音的編號
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

    public void init()
    {
        mGoogleTtsHandler = new TextToSpeechHandler(mContext);
        mGoogleTtsHandler.setHandler(mHandler);
        mGoogleTtsHandler.init();

        // init CReader using existing data (if exists) to prevent any NullPointerException
        initCReader();
        downloadCReaderData();
    }

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

    private void initCReader()
    {
        cReaderDataRoot = mContext.getFilesDir().getAbsolutePath() + "/cyberon/CReader";

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

        if (mPreferredVoice != mActiveVoice)
        {
            setVoice(mPreferredVoice);
        }
    }

    public byte getActiveVoice()
    {
        return mActiveVoice;
    }

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
     * 設定 TTS pitch & speech rate
     * 參數是賽微 TTS 的 scaling (i.e., 預設值是 100)
     */
    public void setPitchCyberonScaling(int pitch, int rate)
    {
        setPitchCyberonScaling(pitch);
        setSpeechRateCyberonScaling(rate);
    }

    /**
     * 設定 TTS pitch & speech rate
     * 參數是 google TTS 的 scaling (i.e., 預設值是 1.0)
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

    public void setPitch(float pitch, float rate)
    {
        setPitchGoogleScaling(pitch, rate);
    }

    /**
     * 設定 TTS 的輸出語言
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
     * 將 text 轉為語音輸出
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
