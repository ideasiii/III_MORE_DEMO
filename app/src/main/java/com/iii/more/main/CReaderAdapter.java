package com.iii.more.main;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.cyberon.utility.CReaderPlayer;
import com.cyberon.utility.ICReaderListener;
import com.iii.more.main.secret.ClassConstantValueReverseLookup;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 讓 Cyberon CReader 能與 Google TTS 使用上盡量貼近的 adapter
 */
class CReaderAdapter
{
    static final int MSG_WHAT = 842143816;

    /** 是否在 TTS speaking 快結束時送出 UTTERANCE_ALMOST_DONE 事件 */
    private static final boolean EARLY_TRIGGER_DONE_EVENT = true;

    static class Event
    {
        static final int INIT_FAILED = 0;
        static final int INIT_OK = 1;
        static final int UTTERANCE_START = 2;
        static final int UTTERANCE_DONE = 3;
        static final int UTTERANCE_ALMOST_DONE = 4;
        static final int UTTERANCE_STOP = 5;
    }

    private static final String LOG_TAG = "CReaderAdapter";
    private static final Map<String, Short> langIdLocaleMap;
    private static final ClassConstantValueReverseLookup statusRevLookup;
    private static final ClassConstantValueReverseLookup errorCodeRevLookup;
    private static final ClassConstantValueReverseLookup synthesizeRevLookup;

    private Context mContext;
    private CReaderPlayer mCReader;
    private Handler mHandler;
    private String mDataDir;

    private Locale mLocale = Locale.TAIWAN;
    private String mVoiceName = CReaderPlayer.VoiceNameConstant.TRADITIONAL_CHINESE_FEMALE_VOICE_NAME;

    // 要切換但尚未切換的語言，當執行 TextToSpeech() 方法時會檢查是否需重新產生一個不同語言的 TTS 物件
    private Locale mPostponedChangingLocale = null;

    /** 真的設定到 CReader 的音調數值 */
    private int mPitch;
    /**  真的設定到 CReader 的語速數值 */
    private int mRate;
    /**  真的設定到 CReader 的音量數值 */
    private int mVolume;

    /** 為了讓計量單位與 Google TTS 相近所儲存的音調數值 */
    //private float mPitchForGetter;
    /** 為了讓計量單位與 Google TTS 相近所儲存的語速數值 */
    //private float mRateForGetter;

    // 尚未成功處理的TTS要求，尚未說完的原因可能有：正在初始化引擎、正在切換語言、正在講...等
    private SpeakBundle mWorkingSpeaking;

    // 是否已完成初始化
    private boolean mDoneInitialization = false;

    CReaderAdapter(Context context, String dataDir)
    {
        this(context, dataDir, Locale.TAIWAN);
    }

    CReaderAdapter(Context context, String dataDir, Locale locale)
    {
        mContext = context;
        mLocale = locale;
        mDataDir = dataDir;

        setPitch(100);
        setSpeechRate(100);
        setVolume(300);
    }

    public boolean hasInitialized()
    {
        return mDoneInitialization;
    }

    public boolean isTrial()
    {
        if (mCReader != null)
        {
            return mCReader.IsTrial();
        }

        Log.w(LOG_TAG, "isTrail() CReader is null");
        return true;
    }

    public boolean isSpeaking()
    {
        return mCReader.IsPlaying();
    }

    public boolean isPaused()
    {
        return mCReader.IsPaused();
    }

    public String getReleaseTo()
    {
        return mCReader.GetReleaseTo();
    }

    /** 取得目前作用中的 Locale */
    public Locale getLanguage()
    {
        return mLocale;
    }

    public short getLanguageId()
    {
        return mLocale == null ? -1 : langIdLocaleMap.get(mLocale.toLanguageTag());
    }

    public int[] getAvailableLangID()
    {
        return mCReader.GetLanguage();
    }

    public void setHandler(Handler h)
    {
        mHandler = h;
    }

    public void setLanguage(Locale loc)
    {
        if (loc == null || mLocale.equals(loc))
        {
            return;
        }

        String langTag = loc.toLanguageTag();
        if (!langIdLocaleMap.containsKey(langTag))
        {
            Log.w(LOG_TAG, "setLanguage() Specified locale '" + langTag + "' is not supported");
        }
        else if (langIdLocaleMap.get(langTag).shortValue() == langIdLocaleMap.get(mLocale.toLanguageTag()).shortValue())
        {
            Log.d(LOG_TAG, "setLanguage() Language IDs are identical between locales, skip reinit");
            mLocale = loc;
        }
        else
        {
            mPostponedChangingLocale = loc;
        }
    }

    public void setVoiceName(String name)
    {
        mVoiceName = name;
    }

    /*public void setPitch(float pitch)
    {
        // CReader 的 pitch 範圍為 50~200，default 100
        int mappedVal = (int)((pitch-0.5)*120)+70;
        if (mappedVal > 200)
        {
            mappedVal = 200;
        }
        else if (mappedVal < 50)
        {
            mappedVal = 50;
        }

        if (mCReader != null)
        {
            mCReader.setPitch(mappedVal);
        }

        mPitch = mappedVal;
        mPitchForGetter = pitch;
    }*/

    public void setPitch(int pitch)
    {
        // CReader 的 pitch 範圍為 50~200，default 100
        if (pitch > 200)
        {
            pitch = 200;
        }
        else if (pitch < 50)
        {
            pitch = 50;
        }

        if (mCReader != null)
        {
            mCReader.setPitch(pitch);
        }

        mPitch = pitch;
        //mPitchForGetter = -1;
    }

    /*public void setSpeechRate(float speechRate)
    {
        // CReader 的 speed 範圍為 50~200，default 100
        int mappedVal = (int)((speechRate-0.5)*60)+70;
        if (mappedVal > 200)
        {
            mappedVal = 200;
        }
        else if (mappedVal < 50)
        {
            mappedVal = 50;
        }

        if (mCReader != null)
        {
            mCReader.setSpeed(mappedVal);
        }

        mRate = mappedVal;
        mRateForGetter = speechRate;
    }*/

    public void setSpeechRate(int speechRate)
    {
        // CReader 的 speed 範圍為 50~200，default 100
        if (speechRate > 200)
        {
            speechRate = 200;
        }
        else if (speechRate < 50)
        {
            speechRate = 50;
        }

        if (mCReader != null)
        {
            mCReader.setSpeed(speechRate);
        }

        mRate = speechRate;
        //mRateForGetter = -1;
    }

    public void setVolume(int vol)
    {
        if (vol > 500)
        {
            vol = 500;
        }
        else if (vol < 0)
        {
            vol = 0;
        }

        if (mCReader != null)
        {
            mCReader.setVolume(vol);
        }

        mVolume = vol;
    }

    public void stop()
    {
        if (mCReader != null && mDoneInitialization)
        {
            mCReader.stop();
            mCReader.clearItems();
        }
    }

    public void shutdown()
    {
        if (mCReader != null)
        {
            Log.d(LOG_TAG, "shutdown()");

            mCReader.stop();
            mCReader.release();
            mCReader = null;
            mDoneInitialization = false;
        }
    }

    public void pause()
    {
        mCReader.pause();
    }

    public void resume()
    {
        mCReader.resume();
    }

    public void init()
    {
        Log.d(LOG_TAG, "init()");
        shutdown();

        if (mPostponedChangingLocale != null)
        {
            mLocale = mPostponedChangingLocale;
            mPostponedChangingLocale = null;
        }

        short langId = langIdLocaleMap.get(mLocale.toLanguageTag());
        String strLibPath = com.cyberon.utility.ToolKit.getNativeLibPath(mContext);

        Log.d(LOG_TAG, "init() strLibPath=" + strLibPath);
        Log.d(LOG_TAG, "init() strDataPath=" + mDataDir);

        mCReader = new CReaderPlayer();
        HashMap<String, String> message = new HashMap<>();
        int nRes = mCReader.init(mContext, langId, strLibPath, mDataDir, mVoiceName);
        if (nRes != CReaderPlayer.ErrorCodeConstant.CREADER_RET_OK)
        {
            Log.d(LOG_TAG, "init() init fail");

            message.put("nRes", Integer.toString(nRes));
            message.put("message", "ERROR! result is " + nRes + " while initializing CReader");

            if (mHandler != null)
            {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT, Event.INIT_FAILED, 0, message));
            }
        }
        else
        {
            Log.d(LOG_TAG, "init() init OK");

            mDoneInitialization = true;

            Log.d(LOG_TAG, "init() setSpeed(" + mRate + ")");
            Log.d(LOG_TAG, "init() setPitch(" + mPitch + ")");
            Log.d(LOG_TAG, "init() setVolume(" + mVolume + ")");

            mCReader.setSpeed(mRate);
            mCReader.setPitch(mPitch);
            mCReader.setVolume(mVolume);

            if (mWorkingSpeaking != null)
            {
                if (mWorkingSpeaking.retryCount <= 1)
                {
                    Log.d(LOG_TAG, "init() TTSCache hold a pending request, do it");
                    setPitch(mWorkingSpeaking.pitch);
                    setSpeechRate(mWorkingSpeaking.rate);
                    speak(mWorkingSpeaking.text, mWorkingSpeaking.utteranceId);
                }
                else
                {
                    mWorkingSpeaking = null;
                }
            }

            message.put("message", "init success");
            if (mHandler != null)
            {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT, Event.INIT_OK, 0, message));
            }
        }
    }

    /**
     * 說出 text，呼叫此方法時會停止正在進行的說話動作
     * @param text String to be spoken
     * @param utteranceId Identifier for this request
     */
    public void speak(String text, String utteranceId)
    {
        if (mWorkingSpeaking != null)
        {
            stop();
        }

        mWorkingSpeaking = new SpeakBundle(text, utteranceId, mPitch, mRate);

        if (mPostponedChangingLocale != null)
        {
            Log.d(LOG_TAG, "A restart is required to switch language, " +
                "queue this request until reinitialization is done: `" + text + "`");

            init();
            return;
        }
        else if (mCReader == null || !mDoneInitialization)
        {
            Log.w(LOG_TAG, "speak() CReader is not initialized, dropping request");
            return;
        }

        mCReader.clearItems();
        mCReader.addTTSAudioItem(text);
        //mCReader.addSilenceAudioItem(5000);
        int ret = mCReader.play(mCReaderListener);
        //mCReader.synthesize(mCallback);

        if (ret < 0)
        {
            Log.w(LOG_TAG, "speak() error, ret = " + ret + " (" + errorCodeRevLookup.getName(ret) + ")");
        }
    }

    private ICReaderListener mCReaderListener = new ICReaderListener()
    {
        private long speakTimeTrace = 0;

        public void onCReaderStatusChanged(int nStatus)
        {
            Log.d(LOG_TAG, "onCReaderStatusChanged() status="
                + nStatus + " (" + statusRevLookup.getName(nStatus) + ")");

            HashMap<String, String> message = new HashMap<>();
            int event;

            if(nStatus == CReaderPlayer.StatusConstant.CREADER_PLAY_START)
            {
                message.put("text", mWorkingSpeaking.text);
                message.put("utteranceId", mWorkingSpeaking.utteranceId);
                event = Event.UTTERANCE_START;
            }
            else if (nStatus == CReaderPlayer.StatusConstant.CREADER_PLAY_STOP)
            {
                String utteranceId = "unknown???";

                if (mWorkingSpeaking != null)
                {
                    utteranceId = mWorkingSpeaking.utteranceId;
                    mWorkingSpeaking = null;
                }

                message.put("utteranceId", utteranceId);
                message.put("interrupted", "true");
                event = Event.UTTERANCE_STOP;
            }
            else if(nStatus == CReaderPlayer.StatusConstant.CREADER_PLAY_END)
            {
                Log.d(LOG_TAG, "spoken elapsed: " +  (System.currentTimeMillis()- speakTimeTrace));
                this.speakTimeTrace = System.currentTimeMillis();

                String text = "unknown???";
                String utteranceId = "unknown???";

                if (mWorkingSpeaking != null)
                {
                    text = mWorkingSpeaking.text;
                    utteranceId = mWorkingSpeaking.utteranceId;
                }

                message.put("text", text);
                message.put("utteranceId", utteranceId);
                event = Event.UTTERANCE_DONE;

                if (!mWorkingSpeaking.hasEarlyTriggeredDoneEvent)
                {
                    // precaution, if early trigger did not work
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT, Event.UTTERANCE_ALMOST_DONE, 0, message));
                }

                mWorkingSpeaking = null;
            }
            else
            {
                Log.w(LOG_TAG, "onCReaderStatusChanged() unhandled status " + nStatus
                + " (" + synthesizeRevLookup.getName(nStatus) + ")");
                return;
            }

            if (mHandler != null)
            {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT, event, 0, message));
            }
        }

        public void onCReaderPlayText(String text)
        {
            Log.d(LOG_TAG, "spoken elapsed: " +  (System.currentTimeMillis()- speakTimeTrace));
            this.speakTimeTrace = System.currentTimeMillis();

            if (mWorkingSpeaking == null || text == null)
            {
                return;
            }

            Log.d(LOG_TAG, "onCReaderPlayText() Text callback=" + text);

            mWorkingSpeaking.textSpokenLengthWithoutPunctuation += text.length();
            Log.d(LOG_TAG, "text.length = " + text.length()
                + ", total spoken (w/o punctuation) = " + mWorkingSpeaking.textSpokenLengthWithoutPunctuation);

            // TODO early event trigger is too early for 2 words, 3 words...
            if (EARLY_TRIGGER_DONE_EVENT
                && mWorkingSpeaking.textSpokenLengthWithoutPunctuation >= mWorkingSpeaking.textWithoutPunctuation.length())
            {
                Log.d(LOG_TAG, "about to finish TTS, trigger UTTERANCE_DONE event");

                String fullText = mWorkingSpeaking.text;
                String utteranceId = mWorkingSpeaking.utteranceId;
                mWorkingSpeaking.hasEarlyTriggeredDoneEvent = true;

                HashMap<String, String> message = new HashMap<>();
                message.put("text", fullText);
                message.put("utteranceId", utteranceId);
                int event = Event.UTTERANCE_ALMOST_DONE;

                if (mHandler != null)
                {
                    //long delayMillis = 130 * text.length();
                    long delayMillis = 50;
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_WHAT, event, 0, message), delayMillis);
                }
            }
        }

        public void onCReaderSynthesizeData(byte[] lpData, int nType)
        {
            switch(nType)
            {
                case CReaderPlayer.SynthesizeConstant.CREADER_SYNTHESIZE_TEXT:
                    /*String temp = new String(lpData, Charset.forName("UTF-16LE"));
                    Log.d(LOG_TAG, "Text callback=" + temp);*/
                    break;
                case CReaderPlayer.SynthesizeConstant.CREADER_SYNTHESIZE_WAVE:
                    //Log.d(LOG_TAG, "Wave callback=" + lpData.length);
                    break;
                case CReaderPlayer.SynthesizeConstant.CREADER_SYNTHESIZE_DONE:
                    //Log.d(LOG_TAG, "Done callback=");
                    /*if (mHandler != null)
                    {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("text", mWorkingSpeaking.text);
                        message.put("utteranceId", mWorkingSpeaking.utteranceId);
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_WHAT, EVENT_UTTERANCE_DONE, 0, message));
                    }

                    mWorkingSpeaking = null;*/
                    break;
                default:
                    Log.w(LOG_TAG, "onCReaderSynthesizeData() unknown nType " + nType
                        + " (" + synthesizeRevLookup.getName(nType) + ")");
            }
        }
    };

    static
    {
        langIdLocaleMap = new HashMap<>();

        langIdLocaleMap.put("zh", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-TW", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-Hant", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-Hant-TW", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-Hant-MO", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-Hant-HK", CReaderPlayer.LangIdConstant.LANG_CHINESE_TRADITIONAL);
        langIdLocaleMap.put("zh-Hans", CReaderPlayer.LangIdConstant.LANG_CHINESE_SIMPLIFIED);
        langIdLocaleMap.put("zh-Hans-SG", CReaderPlayer.LangIdConstant.LANG_CHINESE_SIMPLIFIED);
        langIdLocaleMap.put("zh-Hans-MO", CReaderPlayer.LangIdConstant.LANG_CHINESE_SIMPLIFIED);
        langIdLocaleMap.put("zh-Hans-HK", CReaderPlayer.LangIdConstant.LANG_CHINESE_SIMPLIFIED);
        langIdLocaleMap.put("zh-Hans-CN", CReaderPlayer.LangIdConstant.LANG_CHINESE_SIMPLIFIED);

        langIdLocaleMap.put("en", CReaderPlayer.LangIdConstant.LANG_ENGLISH_US);
        langIdLocaleMap.put("en-US", CReaderPlayer.LangIdConstant.LANG_ENGLISH_US);
        langIdLocaleMap.put("en-GB", CReaderPlayer.LangIdConstant.LANG_ENGLISH_UK);
        langIdLocaleMap.put("en-AU", CReaderPlayer.LangIdConstant.LANG_ENGLISH_AU);

        statusRevLookup =  new ClassConstantValueReverseLookup(CReaderPlayer.StatusConstant.class);
        errorCodeRevLookup = new ClassConstantValueReverseLookup(CReaderPlayer.ErrorCodeConstant.class);
        synthesizeRevLookup = new ClassConstantValueReverseLookup(CReaderPlayer.SynthesizeConstant.class);
    }

    /** 每個 speak 事件的元素 */
    private static class SpeakBundle
    {
        String text;
        String textWithoutPunctuation;
        String utteranceId;
        int textSpokenLengthWithoutPunctuation;
        int pitch;
        int rate;
        byte retryCount;
        boolean hasEarlyTriggeredDoneEvent;

        private SpeakBundle(String text, String utteranceId, int pitch, int rate)
        {
            this.text = text;
            this.utteranceId = utteranceId;
            this.pitch = pitch;
            this.rate = rate;

            this.textWithoutPunctuation = Tools.removePunctuations(text).replaceAll("\\s+","");
            this.textSpokenLengthWithoutPunctuation = 0;
            this.retryCount = 0;
            this.hasEarlyTriggeredDoneEvent = false;
        }
    }
}
