package com.cyberon.engine;

import java.io.File;

import com.cyberon.utility.ToolKit;

import android.content.Context;

/**
 * <u>The CReader class is the SDK API class of SDK library.</u><br>
 * The class functions come from SDK library(libCReader.so)<br>
 * <br>
 * <br>
 * @version 1.0.0 <br> 2016/04/12
 * <p align="right">Cyberon Corporation</p>
 */
public class CReader
{

	/** Stream Audio Type */
	public static final int STREAM_AUDIO_TYPE_ERROR = -1;
	public static final int STREAM_AUDIO_TYPE_TTS = 1;
	public static final int STREAM_AUDIO_TYPE_FILE = 3;
	public static final int STREAM_AUDIO_TYPE_BUFFER = 4;
	public static final int STREAM_AUDIO_TYPE_SIL = 5;

	/** TTS_CALLBACK_TYPE */
	public static final int CREADER_CALLBACKE_TYPE_WAVE = 0;
	public static final int CREADER_CALLBACKE_TYPE_TEXT = 1;

	/** TTS_TYPE */
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
	public static final int CREADER_TYPE_DIGIT2 = 10; // special pronunciation

	/** TTS_ERROR_CODE */
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
	public static final int CREADER_RET_FAIL_CREATE_ALPHA_DIGIT_PRERECORD_BUF = -520;
	public static final int	CREADER_RET_FAIL_LOAD_LICENSE = -521;
	public static final int	CREADER_RET_FAIL_SDK_NOT_SUPPORT = -522;
	public static final int	CREADER_RET_FAIL_LANGUAGE_NOT_SUPPORT = -523;
	public static final int	CREADER_RET_FAIL_EXCEED_MAX_HANDLE_NUM = -524;

	public static final int	CREADER_RET_EXPIRED = -1000;
	public static final int CREADER_RET_LICENSE_FAILED = -1001;
	public static final int	CREADER_RET_UNKNOW_ERR = -3000;

	//For load SDK library
	private static boolean g_mbLoadOK = false;

	public static boolean LoadSDKLibrary(Context context, String sLibPath)
    {
		String strSDKLibName = "CReader";
		String sFile;
		File file;

        if (g_mbLoadOK)
            return true;

        //Load library by assigned path
        sFile = sLibPath + "/lib" + strSDKLibName+ ".so";
        file = new File(sFile);

        try
        {
            if (file.exists())
            {
                System.load(sFile);
                g_mbLoadOK = true;
                //Debug.d("CReader", "System load 1!");
                return true;
            }
        }
        catch (Exception ex)
        {
        }


       sFile = ToolKit.getNativeLibPath(context) + "/lib" + strSDKLibName+ ".so";
       file = new File(sFile);
       try
       {
           if (file.exists())
           {
        	   //Load library by App path
        	   System.load(sFile);
        	   //Debug.d("CReader", "System load 2!");
           }
           else
           {
        	   System.loadLibrary(strSDKLibName);
        	   //Debug.d("CReader", "System load 3!");
           }
           g_mbLoadOK = true;
       }
       catch (Exception ex)
       {
       }

        return g_mbLoadOK;
    }
	//


	public interface AudioCallback
	{
		public int audioCallbackMethod(int nCallBackType, int lpVoid, byte[] wavBuf, int nSize);
	}

	private AudioCallback mCallback = null;

	public CReader(AudioCallback cb)
	{
		mCallback = cb;
	}

	@SuppressWarnings("unused")
	private int runCallbackMethod(int nCallBackType, int lpVoid, byte[] wavBuf, int nSize)
	{
		if (mCallback != null)
			return mCallback.audioCallbackMethod(nCallBackType, lpVoid, wavBuf, nSize);
		return 0;
	}

	/**
	 * initialize CReader object
	 * @return true if initialization successfully, otherwise false.
	 */
	public long initialize(Object context, short nLangID, String sLibPath, String sDataPath, String sVoiceName, int[] nArrayErr)
	{
		boolean bLoad;
		//Load SDK library
		bLoad = LoadSDKLibrary((Context)context, sLibPath);

		//Init
		if(bLoad)
			return init(context, nLangID, sLibPath, sDataPath, sVoiceName, nArrayErr);
		else
			return 0;
	}

	public long initialize(Object context, short nLangID, String sLibPath, String sDataPath, String sLicensePath, String sVoiceName, int[] nArrayErr)
	{
		boolean bLoad;
		//Load SDK library
		bLoad = LoadSDKLibrary((Context)context, sLibPath);

		//Init
		if(bLoad)
			return initLicense(context, nLangID, sLibPath, sDataPath, sLicensePath, sVoiceName, nArrayErr);
		else
			return 0;
	}

	/**
	 * create CReader object
	 */
	public native long init(Object context, short nLangID, String sLibPath, String sDataPath, String sVoiceName, int[] nArrayErr);

	public native long initLicense(Object context, short nLangID, String sLibPath, String sDataPath, String sLicensePath, String sVoiceName, int[] nArrayErr);

	/**
	 * release CReader object
	 */
	public native void release(long lHandle);

	/**
	 * process the audio data in object list.
	 * the object array is the pair combination of int[1] type and audio object.
	 * The function is blocking.
	 * @param objs audio object array
	 * @return true if process fully, not break by user, otherwise false.
	 */
	public native int process(long lHandle, Object[] objs);

	/**
	 * force process stop
	 */
	public native void stop(long lHandle);
	/**
	 * check if processing currently.
	 * @return true if process now, otherwise false.
	 */
	public native boolean isProcessing(long lHandle);

	//----------------------------------------------------
	//Setting APIs

	/**
	 * set TTS speed. [50 ~ 200] (slow to fast)
	 * @return true if success, otherwise false.
	 */
	public native boolean setSpeed(long lHandle, int nSpeed);

	/**
	 * set TTS Pitch. [50 ~ 200]
	 * @return true if success, otherwise false.
	 */
	public native boolean setPitch(long lHandle, int nPitch);

	/**
	 * set TTS Volume. [0 ~ 500]
	 * @return true if success, otherwise false.
	 */
	public native boolean setVolume(long lHandle, int nVolume);

	/**
	 * set TTS Long Delay.Punctuation like ",.?;" [millisecond]
	 * @return true if success, otherwise false.
	 */
	public native boolean setLongDelay(long lHandle, int nLongDelay);

	/**
	 * set TTS Short Delay. [millisecond]
	 * @return true if success, otherwise false.
	 */
	public native boolean setShortDelay(long lHandle, int nShortDelay);

	/**Get SDK Version.
     * @return              String of version.*/
	public static native int GetVersionInfo(String sLicensePath,CReaderVersionInfo oCReaderVersionInfo);

	/**Get support LangID.
     * @return              String of version.*/
	public static native int GetAvailableLangID(String sDataPath, int[] nArrayLangID, int[] nArrayErr);

	/**Get support Speaker number.
     * @return              String of version.*/
	public static native int GetAvailableSpeakerNum(String sDataPath, int nLangID, int[] nArrayErr);

	/**Get support Speaker name.
     * @return              String of version.*/
	public static native int GetAvailableSpeaker(String sLibPath, String sDataPath, int nLangID, int nSpeaker, byte[] szSpeaker, int[] nArrayErr);

	/*static
	{
		System.loadLibrary("CReader");
	}*/
}
