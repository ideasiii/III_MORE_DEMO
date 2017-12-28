package com.cyberon.utility;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.cyberon.utility.WaveFile;
import com.cyberon.engine.CReader;
import com.cyberon.engine.CReaderVersionInfo;
import com.cyberon.utility.WavePlay;

/**
 * <u>The CReaderPlayer class is the main(high level) SDK class.</u><br>
 * It generates TTS data(CReader class) and plays the data with a audio track object(WavePlay class).<br>
 * <br>
 * @version 1.0.4
 * <p align="left">Update date: 2016/04/12</p>
 * <p align="right">Cyberon Corporation</p>
 */
public class CReaderPlayer implements CReader.AudioCallback
{
	/**
	 * The language ID list for first parameter of init(...) API
	 */
	public static class LangIdConstant
	{
		public static final short LANG_CHINESE_TRADITIONAL      =1;    // Chinese (Taiwan Region)
		public static final short LANG_CHINESE_SIMPLIFIED       =2;    // Chinese (PR China)
		public static final short LANG_CANTONESE                =3;    // Cantonese (HK)
		public static final short LANG_ENGLISH_US               =4;    // English (USA)
		public static final short LANG_ENGLISH_UK               =5;    // English (UK)
		public static final short LANG_GERMAN	               	=6;
		public static final short LANG_SPANISH_SA	            =7;	   //Spanish (South America)
		public static final short LANG_FRENCH	               	=8;
		public static final short LANG_ITALY	               	=9;
		public static final short LANG_KOREA	               	=10;
		public static final short LANG_RUSSIA      				=11;
		public static final short LANG_PROTUGUESE_BR			=12;
		public static final short LANG_THAI						=14;
		public static final short LANG_DUTCH	               	=16;
		public static final short LANG_JAPAN	               	=17;
		public static final short LANG_POLISH	               	=19;
		public static final short LANG_TURKISH	               	=21;
		public static final short LANG_DANISH	               	=22;
		public static final short LANG_SWEDISH	               	=23;
		public static final short LANG_NORWEGIAN               	=24;
		public static final short LANG_FINNISH	               	=25;
		public static final short LANG_GREEK	               	=26;
		public static final short LANG_PROTUGUESE_EU           	=27;
		public static final short LANG_ENGLISH_AU              	=31;
		public static final short LANG_SPANISH_EU             	=33;   //Spanish  (Europe)
		public static final short LANG_CHINESE_MINNAN          	=34;
		public static final short LANG_VIETNAMESE_VIE          	=35;
		public static final short LANG_BAHASA_IDN              	=38;
	}

	/**
	 * The voice name list for forth parameter of init(...) API
	 */
	public static class VoiceNameConstant
	{
		public static final String TRADITIONAL_CHINESE_FEMALE_VOICE_NAME = "Ally_TW";
		public static final String TRADITIONAL_CHINESE_KID_MALE_VOICE_NAME = "KidM_TW";
		public static final String TRADITIONAL_CHINESE_KID_FEMALE_VOICE_NAME = "KidF_TW";
		public static final String TRADITIONAL_CHINESE_MALE_VOICE_NAME = "Andy_TW";
		public static final String SIMPLIFIED_CHINESE_FEMALE_VOICE_NAME = "Amber_CN";
		public static final String CANTONESE_FEMALE_VOICE_NAME = "Alice_HK";
		public static final String ENGLISH_US_FEMALE_VOICE_NAME = "Anita_USA";//"Jacob_USA";
		public static final String ENGLISH_UK_FEMALE_VOICE_NAME = "Anita_UK";
		public static final String GERMAN_FEMALE_VOICE_NAME = "Gerda_DE";
		public static final String FRENCH_FEMALE_VOICE_NAME = "Anna_FR";
		public static final String ITALY_FEMALE_VOICE_NAME = "Sally_IT";
		public static final String RUSSIAN_FEMALE_VOICE_NAME = "Anfisa_RU";
		public static final String PORTUGUESE_BRAZILIAN_FEMALE_VOICE_NAME = "Belle_BR";
		public static final String THAI_FEMALE_VOICE_NAME = "Liani_TH";
		public static final String JAPANESE_FEMALE_VOICE_NAME = "Yuki_JP";
		public static final String KOREAN_FEMALE_VOICE_NAME = "Nara_KR";
		public static final String TURKISH_FEMALE_VOICE_NAME = "Aydan_TR";
		public static final String EUROPE_SPANISH_FEMALE_VOICE_NAME = "Drina_ES";
		public static final String SA_SPANISH_FEMALE_VOICE_NAME = "Claudia_MX";

		public static final String ENGLISH_AU_FEMALE_VOICE_NAME = "Anita_USA";
		public static final String GREEK_FEMALE_VOICE_NAME = "Alexandra_GR";
		public static final String FINNISH_FEMALE_VOICE_NAME = "Heli_FI";

		public static final String DANISH_FEMALE_VOICE_NAME = "Aase_DK";
		public static final String DUTCH_FEMALE_VOICE_NAME = "Nancy_NL";
		public static final String NORWEGIAN_FEMALE_VOICE_NAME = "Ada_NO";
		public static final String POLISH_FEMALE_VOICE_NAME = "Pamela_PL";
		public static final String PORTUGUESE_EUROPE_FEMALE_VOICE_NAME = "Tatiana_PT";
		public static final String SWEDISH_FEMALE_VOICE_NAME = "Katherine_SE";

		public static final String MINNAN_FEMALE_VOICE_NAME = "Triss_NAN";
		public static final String VIETNAMESE_FEMALE_VOICE_NAME = "Tierra_VN";
		public static final String BAHASA_INDONESIA_FEMALE_VOICE_NAME = "Agatha_ID";
	}

	/**
	 * The error code list for return value of SDK API
	 */
	public static class ErrorCodeConstant
	{
		public static final int CREADER_RET_OK = 0;
		public static final int CREADER_RET_PARAM_ERROR = -10;
		public static final int	CREADER_RET_MEMORY_FAIL = -9;
		public static final int	CREADER_RET_FILE_ERROR = -8;
		public static final int	CREADER_RET_WAV_FMT_ERROR = -7;
		public static final int	CREADER_RET_DONE = -6;
		public static final int	CREADER_RET_NO_TTS = -5;
		public static final int	CREADER_RET_WAV_OUT_STOP = -4;
		public static final int	CREADER_RET_FAIL_LOAD_UTILS_DLL = -501;
		public static final int	CREADER_RET_FAIL_LOAD_TTS_DLL = -502;
		public static final int	CREADER_RET_FAIL_GET_FP_FROM_TTS_DLL = -503;
		public static final int	CREADER_RET_FAIL_LOAD_NLP_DLL = -504;
		public static final int	CREADER_RET_FAIL_GET_FP_FROM_NLP_DLL = -505;
		public static final int	CREADER_RET_CREATE_NLP_HANDLE = -506;
		public static final int	CREADER_RET_FAIL_LOAD_PROSODY_BIN = -507;
		public static final int	CREADER_RET_FAIL_LOAD_VOICE_BIN = -508;
		public static final int	CREADER_RET_FAIL_LOAD_TTS_BIN = -509;
		public static final int	CREADER_RET_FAIL_CREATE_TTS_HANDLE = -510;
		public static final int	CREADER_RET_FAIL_CREATE_LEXMGR_HANDLE = -511;
		public static final int	CREADER_RET_FAIL_TTS_LANG_NOT_SUPPORT = -512;
		public static final int	CREADER_RET_INVALID_TTS_INDEX = -513;
		public static final int	CREADER_RET_API_NOT_SUPPORT = -514;
		public static final int	CREADER_RET_FAIL_LOAD_NLP_BIN = -515;
		public static final int	CREADER_RET_FAIL_LOAD_TN_BIN = -516;
		public static final int	CREADER_RET_FAIL_ADD_LANG = -517;
		public static final int	CREADER_RET_FAIL_ADD_VOICE = -518;
		public static final int	CREADER_RET_EXCEED_MAX_PUNC_MARK_NUM = -519;
		public static final int	CREADER_RET_FAIL_CREATE_ALPHA_DIGIT_PRERECORD_BUF = -520;
		public static final int	CREADER_RET_FAIL_LOAD_LICENSE = -521;
		public static final int	CREADER_RET_FAIL_SDK_NOT_SUPPORT = -522;
		public static final int	CREADER_RET_FAIL_LANGUAGE_NOT_SUPPORT = -523;
		public static final int	CREADER_RET_FAIL_EXCEED_MAX_HANDLE_NUM = -524;
		public static final int	CREADER_RET_EXPIRED = -1000;
		public static final int	CREADER_RET_LICENSE_FAIL = -1001;
		public static final int	CREADER_RET_UNKNOW_ERR = -3000;
	}

	/**
	 * The TTS type for second parameter of addTTSAudioItem(...) API
	 * <br>Current only support CREADER_TYPE_NORMAL
	 */
	public static class TTSTypeConstant
	{
		public static final int CREADER_TYPE_NORMAL = 0;
		public static final int CREADER_TYPE_NAME = 1;
		public static final int CREADER_TYPE_TIME = 2;
		public static final int CREADER_TYPE_DATE = 3;
		public static final int CREADER_TYPE_WEEKDAY = 4;
		public static final int CREADER_TYPE_ORDINAL = 5;
		public static final int CREADER_TYPE_WORD = 6;
		public static final int CREADER_TYPE_PROMPT = 7;
		public static final int CREADER_TYPE_NUMBER = 8;
		public static final int CREADER_TYPE_DIGIT = 9;
	}

	/**
	 * The status which is transfered back by ICReaderListener
	 */
	public static class StatusConstant
	{
	    public static final int CREADER_PLAY_START = 	0;
	    public static final int CREADER_PLAY_STOP = 	1;
	    public static final int CREADER_PLAY_ERR = 	2; // error stop
	    public static final int CREADER_PLAY_END = 		3;
	}

	/**
	 * The synthesize status which is transfered back by ICReaderListener
	 */
	public static class SynthesizeConstant
	{
	    public static final int CREADER_SYNTHESIZE_TEXT = 	0;
	    public static final int CREADER_SYNTHESIZE_WAVE = 	1;
	    public static final int CREADER_SYNTHESIZE_DONE = 	2;
	}

	/**
	 * For callback Text
	 */
	private class TextSampleSize
	{
		String Text = null;
		int size = 0;
	}

	private static final String LOG_TAG = "CReader";
	/**Identify of playing task.*/
	private static int m_iId = 0;

	private static boolean m_bLogWaveFile = false;

	private CReader mStreamMgr;
	private CReaderVersionInfo mCReaderVersionInfo = null;
	private long mTTSHandle = 0;
	private WavePlay mWavePlay = new WavePlay();
	private WaveFile mWaveFile = null;
	//private Vector<Object> mItems;
	private ConcurrentLinkedQueue<Object> mItems;
	//private LinkedList<byte []> mDataItems = new LinkedList<byte []>();
	private ConcurrentLinkedQueue<byte []> mDataItems = new ConcurrentLinkedQueue<byte []>();
	private Thread mPlayThread = null;
	private Thread mDataThread = null;

	//For callback Text
	//private LinkedList<TextSampleSize> mTextSampleItems = new LinkedList<TextSampleSize>();
	private ConcurrentLinkedQueue<TextSampleSize> mTextSampleItems = new ConcurrentLinkedQueue<TextSampleSize>();
	private boolean bSynthesize;

	private boolean mIsPlaying = false;
	private boolean mIsPaused = false;

	private static int mSampleRate = 16000;
	private int mCountFrames = 0;
	private int mSleepFrame = 0;
	private int mAudioStreamType = AudioManager.STREAM_MUSIC;
	private String mLibPath = null;
	private String mDataPath = null;

	//For callback function
	private ICReaderListener mCallback = null;

	public CReaderPlayer()
	{
		//mContext = context;
		mItems = new ConcurrentLinkedQueue<Object>();
	}


	/**
	 * initialize the CReaderPlayer object with voice name of a person.
	 *
	 * @param nLangID the language ID, please reference "CReaderLangIdConstant" class
	 * @param sLibPath the library(*.so) path, ie. /data/data/xxx/lib
	 * @param sDataPath the data path. Please assign it be the previous folder of any language folder.
	 * @param sVoiceName voice name of TTS bin files, ie. "Ally_TW" for traditional Chinese, "Anita_USA" for English, ...etc
	 * @return CREADER_RET_OK indicates success, else indicates error code (please reference "CReaderErrorCodeConstant" class)
	 */
	public synchronized int init(Context context, short nLangID, String sLibPath, String sDataPath, String sVoiceName)
	{
		if(mStreamMgr != null)
			return ErrorCodeConstant.CREADER_RET_OK;

		mStreamMgr = new CReader(this);
		Log.d(LOG_TAG, String.format("nLangID = %d", nLangID));
		Log.d(LOG_TAG, String.format("sLibPath = %s", sLibPath));
		Log.d(LOG_TAG, String.format("sDataPath = %s", sDataPath));
		Log.d(LOG_TAG, String.format("sVoiceName = %s", sVoiceName));

		//Set language ID of English AU to English USA
		if(nLangID == LangIdConstant.LANG_ENGLISH_AU)
			nLangID = LangIdConstant.LANG_ENGLISH_US;

		//Initialize
		int[] arrayRes = new int[1];
		mTTSHandle = mStreamMgr.initialize(context, nLangID, sLibPath, sDataPath, sVoiceName, arrayRes);

		if(arrayRes[0]==CReader.CREADER_RET_OK)
		{
			mCReaderVersionInfo = new CReaderVersionInfo();
			//hardcode license bin path now
			CReader.GetVersionInfo(sDataPath + "/CReaderLicense.bin",mCReaderVersionInfo);
		}

		mLibPath = sLibPath;
		mDataPath = sDataPath;

		return arrayRes[0];
	}

	/**
	 * release resource of CReaderPlayer object.
	 */
	public synchronized void release()
	{
		if(mStreamMgr == null)
			return;

		stop(0L);
		mStreamMgr.release(mTTSHandle);
		mTTSHandle = 0;
		mStreamMgr = null;

		mWavePlay.setStopFlag(true);
		mWavePlay.stop();
		mWavePlay.release();
	}

	/**
	 * set TTS speed
	 * @param nSpeed the TTS speed, and from slow to fast is 50 to 200 (default is 100).
	 */
	public void setSpeed(int nSpeed)
	{
		if(mStreamMgr == null)
			return;

		mStreamMgr.setSpeed(mTTSHandle, nSpeed);
	}

	/**
	 * set TTS volume
	 * @param nVolume the TTS volume(unit is %), and the range is 0 ~ 500 (default is 100).
	 */
	public void setVolume(int nVolume)
	{
		if(mStreamMgr == null)
			return;
		mStreamMgr.setVolume(mTTSHandle, nVolume);
	}

	/**
	 * set TTS pitch
	 * @param nPitch the TTS pitch, and from low to high is 50 to 200 (default is 100).
	 */
	public void setPitch(int nPitch)
	{
		if(mStreamMgr == null)
			return;

		mStreamMgr.setPitch(mTTSHandle, nPitch);
	}

	/**
	 * set TTS Short Delay
	 * @param nShortDelay the TTS Short Delay.(default is 20 [millisecond]).
	 */
	public void setShortDelay(int nShortDelay)
	{
		if(mStreamMgr == null)
			return;

		mStreamMgr.setShortDelay(mTTSHandle, nShortDelay);
	}

	/**
	 * set TTS Long Delay
	 * @param nLongDelay the TTS Long Delay.(default is 300 [millisecond]).
	 */
	public void setLongDelay(int nLongDelay)
	{
		if(mStreamMgr == null)
			return;

		mStreamMgr.setLongDelay(mTTSHandle, nLongDelay);
	}

	/**
	 * clear the all audio items.
	 */
	public void clearItems()
	{
		/*synchronized (mItems)
		{*/
			mItems.clear();
		//}
	}


	/**
	 * add the string as TTS audio item to play.
	 * use the currently language setting to generate TTS audio.
	 * @param msg the TTS message string
	 */
	public void addTTSAudioItem(String msg)
	{
		addTTSAudioItem(msg, TTSTypeConstant.CREADER_TYPE_NORMAL);
	}


	/**
	 * add the string as TTS audio item to play.
	 * use the currently language setting to generate TTS audio.
	 * @param msg the TTS message string
	 * @param ttsType the type of message, please reference "CReaderTTSTypeConstant" class
	 */
	public void addTTSAudioItem(String msg, int ttsType) {
		int ttsID = -1; //decide by engine

		// make new string with TTS language-code and type
		StringBuffer sb = new StringBuffer();
		sb.append((char)('A' + ttsID));
		sb.append((char)('A' + ttsType));
		sb.append((String)msg);
		String msg2 = sb.toString();

		addAudioItem(CReader.STREAM_AUDIO_TYPE_TTS, msg2);
	}


	/**
	 * add wave file as audio item to play. Support 8k or 16k sampling rate, 16-bits PCM format.
	 * @param filePath file-path to play
	 */
	public void addWaveFileAudioItem(String filePath)
	{
		addAudioItem(CReader.STREAM_AUDIO_TYPE_FILE, filePath);
	}

	/**
	 * add wave buffer as audio item to play.
	 * @param samples 16-bits PCM wave buffer
	 * @param is16k true for 16k, false for 8k.
	 */
	public void addWaveBufferAudioItem(short[] samples, boolean is16k) {
		if (samples.length <= 0)
			return;

		// the first sample is the indication for 16k or 8k sampling-rate
		// 0 for 16k, otherwise 8k
		samples[0] = (short)((is16k) ? 0 : 1);

		addAudioItem(CReader.STREAM_AUDIO_TYPE_BUFFER, samples);
	}


	/**
	 * add silence as audio item to play
	 * @param len the length(millisecond) of silence
	 */
	public void addSilenceAudioItem(int len) {
		int[] lens = new int[1];
		lens[0] = len;
		addAudioItem(CReader.STREAM_AUDIO_TYPE_SIL, lens);
	}

	/**Launch asynchronous playing.
	 * @param callback the callback function for status's feedback
	 * @param nAudioStreamType the audio stream type for audio playing, for example, AudioManager.STREAM_MUSIC
	 * @return Return identify of playing task if succeeded, return zero if failed.*/
	public int play(ICReaderListener callback)
	{
		bSynthesize = false;
		return play(false, callback, AudioManager.STREAM_MUSIC);
	}

	/**Launch synthesize.
	 * @param callback the callback function for status's feedback
	 * @return Return identify of playing task if succeeded, return zero if failed.*/
	public int synthesize(ICReaderListener callback)
	{
		bSynthesize = true;
		return play(false, callback, AudioManager.STREAM_MUSIC);
	}

	/**Launch playing.
	 *
	 * @param sync Set true to waiting for playing done.
	 * @param callback the callback function for status's feedback
	 * @param nAudioStreamType the audio stream type for audio playing, for example, AudioManager.STREAM_MUSIC
	 * @return             Return identify of playing task if succeeded, return zero if failed or
	 *                     sync is true.*/
	public synchronized int play(boolean sync, ICReaderListener callback, int nAudioStreamType)
	{
		int returnVal = 0;

		if(mStreamMgr == null)
			return ErrorCodeConstant.CREADER_RET_NO_TTS;

		if(callback == null)
			Log.d(LOG_TAG, "callback function is null!");

		mAudioStreamType = nAudioStreamType;

		if (mItems.isEmpty())
		{
			Log.w(LOG_TAG, "Without playing item");
			//mHandler.sendMessage(mHandler.obtainMessage(TTS_PLAY_END, TTS_PLAY_STATUS_OK,
			//		m_iId));
			if (callback != null)
				callback.onCReaderStatusChanged(StatusConstant.CREADER_PLAY_END);
		}
		else
		{
			// stop currently playing if any
			stop(0L);
			if (!sync)
				returnVal = (m_iId += 1);

			mCallback = callback;

			mIsPlaying = true;
			mIsPaused = false;
			mDataItems.clear();
			mTextSampleItems.clear();
			mCountFrames = 0;
			mDataThread = new Thread(mDataRunnable);
			mDataThread.start();
			if(!bSynthesize)
			{
				mPlayThread = new Thread(mPlaybackRunnable);
				mPlayThread.start();
			}

			if (sync)
			{
				// Waiting for playback thread to end
				try
				{
					if (mDataThread != null)
					{
						mDataThread.join();
						mDataThread = null;
					}
					if(!bSynthesize){
						if (mPlayThread != null)
						{
							mPlayThread.join();
							mPlayThread = null;
						}
					}
				}
				catch (InterruptedException ex)
				{
				}

				mCallback = null;
			}
		}
		return returnVal;
	}

	/**Stop playing.
	 *
	*/
	public synchronized void stop()
	{
		if(mStreamMgr == null)
			return;

		stop(0L);
	}

	/**Stop playing.
	 *
	 * @param lPend the length of waiting time after stopping playing. The unit is millisecond. Usually set it to 0.
	*/
	public synchronized void stop(long lPend)
	{
		mIsPlaying = false;
		mIsPaused = false;
		mWavePlay.setStopFlag(true);
		mStreamMgr.stop(mTTSHandle);
		try
		{
			if (mDataThread != null)
			{
				int nWait = 0;
				while (true)
				{
					mDataThread.join(3000);
					if (!mDataThread.isAlive())
					{
						mDataThread = null;
						break;
					}
					if (nWait++ > 3)
					{
						Log.e(LOG_TAG, "Fail to wait data thread end.");
						mDataThread.interrupt();
						mDataThread.join(600);
						mDataThread = null;
						break;
					}
				}
			}
			if (mPlayThread != null)
			{
				int nWait = 0;
				while (true)
				{
					mPlayThread.join(3000);
					if (!mPlayThread.isAlive())
					{
						mPlayThread = null;
						break;
					}
					if (nWait++ > 3)
					{
						Log.e(LOG_TAG, "Fail to wait play thread end.");
						mPlayThread.interrupt();
						mPlayThread.join(600);
						mPlayThread = null;
						break;
					}
				}
			}
			// make silence feeling to next sound clip
			if (lPend > 0L)
				Thread.sleep(lPend);
		}
		catch (Exception ex)
		{
		}

		mDataItems.clear();
		mTextSampleItems.clear();
		mWavePlay.stop();
		mWavePlay.release();
	}

	/**
	 * pause play thread
	 */
	public void pause()
	{
		mIsPaused = true;
		mWavePlay.pause();
	}

	/**
	 * resume paused play thread
	 */
	public void resume()
	{
		mIsPaused = false;
		mWavePlay.resume();
	}

	/**
	 * return play thread is alive or not
	 */
	public boolean IsPlaying()
	{
		if(mPlayThread != null && mPlayThread.isAlive())
			return true;
		else
			return false;
	}

	/**
	 * return play thread is paused or not
	 */
	public boolean IsPaused()
	{
		return mIsPaused;
	}

	private void addAudioItem(int type, Object obj) {

		int[] typeArray = new int[1];
		typeArray[0] = type;
		/*synchronized (mItems)
		{*/
			mItems.add(typeArray);
			mItems.add(obj);
		//}
	}

	private Runnable mPlaybackRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			int returnValue = StatusConstant.CREADER_PLAY_ERR;

			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
			Log.i(LOG_TAG, "playback thread into");
			while (true)
			{
				//Big BUG!
				//We can't set flag in here, it may cause multi-thread timing issue
				//mIsPlaying = true;

				mWavePlay.stop();
				// We must RELEASE it, otherwise, it may no sound at sometimes.
				mWavePlay.release();

				if (!mWavePlay.init(mAudioStreamType, 1, 16, mSampleRate))
				{
					Log.e(LOG_TAG, "fail to create AudioTrack object.");
					break;
				}

				// check user break playback
				if (!mIsPlaying)
				{
					returnValue = StatusConstant.CREADER_PLAY_STOP;
					break;
				}

				// start to play AudioTrack object
				//mCountFrames = 0;
				mWavePlay.start(null, 0, 0);

				if(mCallback != null)
					mCallback.onCReaderStatusChanged(StatusConstant.CREADER_PLAY_START);

				// For HTC Vivo, we need to sleep 100 ms at here to avoid some problems
				// Play 100 ms silence is useless.
				// This delay can also let data thread to prepare more play data.
				try
				{
					Thread.sleep(100);
				}
				catch (Exception ex)
				{
				}

				while (mIsPlaying)
				{
					byte [] byBuf = null;

					/*synchronized (mDataItems)
					{*/
						if (!mDataItems.isEmpty())
							byBuf = mDataItems.remove();
						else
							ToolKit.sleep(10);
					//}

					if (byBuf == null)
					{
						if (mDataThread != null && mDataThread.isAlive())
							ToolKit.sleep(10);
						else
							break;
					}
					else
					{
						while (mIsPaused && mIsPlaying)
						{
							ToolKit.sleep(100);
						}

						//add TextCallBack
						if(!mTextSampleItems.isEmpty())
						{
							TextSampleSize temp;
							temp = mTextSampleItems.peek();
							//judge play time when audio play time bigger then current text callback text
							if((mWavePlay.getTotalPlayBytes() >> 1) >= temp.size)
							{
								//callback text and remove first linklist
								if(mCallback!=null)
									mCallback.onCReaderPlayText(temp.Text);
								mTextSampleItems.remove();
							}
						}

						mWavePlay.write(byBuf);
					}
				}

				// check if we got empty audio buffer
				Log.d(LOG_TAG, "mCountFrames= " + mCountFrames);

				/*
				if (mIsPlaying)
				{
					mWavePlay.waitPlayFinish();
					returnValue = CReaderStatusConstant.CREADER_PLAY_END;
				}
				*/
				mWavePlay.finishWrite();
				while (mIsPlaying)
				{
					if (!mWavePlay.isPlaying())
					{
						returnValue = StatusConstant.CREADER_PLAY_END;
						break;
					}
					ToolKit.sleep(100);
				}

				break;
			}
			mWavePlay.stop();

			if (mCallback != null)
			{
				if(mIsPlaying)
					mCallback.onCReaderStatusChanged(returnValue);
				else
					mCallback.onCReaderStatusChanged(StatusConstant.CREADER_PLAY_STOP);
			}
			Log.i(LOG_TAG, "playback thread leave");
		}
	};

	private Runnable mDataRunnable = new Runnable()
	{
		@Override
		public void run()
		{
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);
			Log.i(LOG_TAG, "Data thread into");
			while (mIsPlaying)
			{
				if (m_bLogWaveFile)
				{
					SimpleDateFormat oFmt = new SimpleDateFormat("yyyyMMdd_HHmmss");

					try
					{
						mWaveFile = new WaveFile(null, String.format("TTS_%s.wav", oFmt
								.format(new Date())));
						mWaveFile.setFormat(16, 1, mSampleRate);
					}
					catch (Exception e)
					{
					}
				}

				// start to write buffer
				Object [] oItems;
				/*synchronized (mItems)
				{*/
					oItems = mItems.toArray();
				//}
				mStreamMgr.process(mTTSHandle, oItems); // this is blocking function

				if(bSynthesize && mCallback!=null)
					mCallback.onCReaderSynthesizeData(null,SynthesizeConstant.CREADER_SYNTHESIZE_DONE);

				try
				{
					if (mWaveFile != null)
					{
						mWaveFile.close();
						mWaveFile = null;
					}
				}
				catch (Exception ex)
				{
				}

				// check if we got empty audio buffer
				Log.d(LOG_TAG, "mCountFrames=" + mCountFrames);
				break;
			}

			Log.i(LOG_TAG, "Data thread leave");
		}
	};


	@Override
	public int audioCallbackMethod(int nCallbackType, int lpVoid, byte[] wavBuf, int size)
	{
		if(!bSynthesize)
		{
			if(nCallbackType == CReader.CREADER_CALLBACKE_TYPE_TEXT)
			{
				//byte[] to UTF16
				String out = new String(wavBuf,Charset.forName("UTF-16LE"));
				TextSampleSize temp = new TextSampleSize();
				temp.Text = out;
				temp.size = mCountFrames;
				mTextSampleItems.add(temp);
				//Log.d("PlayStreamMgr callback %s text.", out);
				return 0;
			}

			// This function is called in thread
			if (size > 0 && mIsPlaying)
			{


				//Log.d("PlayStreamMgr callback %d ms data.", size/32);
				mCountFrames += size >> 1; // one sample is one frame

			//To improve UI response time, we sleep 1 ms per 100 ms TTS data;
		/*
			if (mCountFrames / (mSampleRate/10) > mSleepFrame)
			{
				ToolKit.sleep(1);
				mSleepFrame = mCountFrames / (mSampleRate/10);
			}
		*/

				// Because AudioTrack.write() is too slow, so we use the API at another thread
				/*synchronized (mDataItems)
				{*/
					mDataItems.add(wavBuf);
				//}

				//Limit TTS data queue size to save memory usage when read large text.
				while (mDataItems.size() > 200 && mIsPlaying)
					ToolKit.sleep(100);

				//Don't synthesis TTS data during pause state.
				while (mIsPaused && mIsPlaying)
				{
					ToolKit.sleep(100);
				}

				if (mWaveFile != null)
				{
					try
					{
						mWaveFile.writeData(wavBuf);
					}
					catch (Exception e)
					{
					}
				}
			}
			return 0;
		}
		else	//Synthesize
		{
			while(mIsPaused)
			{
				ToolKit.sleep(100);
			}
			if(mCallback!=null)
			{
				if(nCallbackType == CReader.CREADER_CALLBACKE_TYPE_TEXT)
					mCallback.onCReaderSynthesizeData(wavBuf,SynthesizeConstant.CREADER_SYNTHESIZE_TEXT);
				else
					mCallback.onCReaderSynthesizeData(wavBuf,SynthesizeConstant.CREADER_SYNTHESIZE_WAVE);
			}
			return 0;
		}
	}

	public boolean IsTrial()
	{
		return mCReaderVersionInfo.bTrialVersion;
	}

	public String GetReleaseTo()
	{
		return mCReaderVersionInfo.szReleaseTo;
	}

	public int[] GetLanguage()
	{
		int LangNum = 0;
		int[] arrayRes = new int[1];
		//put get Language Speaker here for testing
		LangNum = CReader.GetAvailableLangID(mDataPath,null,arrayRes);
		int langArrayID[] = new int[LangNum];
		CReader.GetAvailableLangID(mDataPath,langArrayID,arrayRes);

		//debug
		/*for(int i=0;i<LangNum;i++)
			Log.d(LOG_TAG, String.format("nLangID = %d", langArrayID[i]));*/

		return langArrayID;
	}
}
