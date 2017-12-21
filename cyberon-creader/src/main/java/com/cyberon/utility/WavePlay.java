package com.cyberon.utility;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class WavePlay implements AudioTrack.OnPlaybackPositionUpdateListener
{
	private static final String LOG_TAG = "WavePlay";

	public static final byte[] k_bySilence =
	{
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00
	};

	private static final int k_nDataPieceDuration = 80;	//ms

	/** The following objects are passed from parent. */
	protected Handler m_oHandler = null;
	protected volatile boolean m_bStopPlay = false;

	protected AudioTrack m_oAudioTrack = null;
	protected int m_nChannelNum = 1;
	protected int m_nBitsPerSample = 16;
	protected int m_nSampleRate = 8000;
	protected int m_nAudioStreamType = AudioManager.STREAM_SYSTEM;
	protected int m_nMinBufSize = 0;
	protected long m_lPlayStartTime = 0;
	protected long m_lPauseStartTime = 0;
	protected long m_lTotalPauseTime = 0;
	protected int m_nPlayStartHeadPosition = 0;
	protected int m_nTotalPlayBytes = 0;
	protected int m_nPlayNotifyMsg = 0;
	protected int m_nPlayedPeriod = 0;
	protected boolean m_bReachEnd = false;

	public WavePlay()
	{
	}

	/**
	 * Init AudioTrack object
	 *
	 * @param nAudioStreamType
	 *        [in] Audio stream type, defined in AudioManager.
	 * @param nChannelNum
	 *        [in] Mono or stereo. 1 0r 2.
	 * @param nBitsPerSample
	 *        [in] 8 or 16.
	 * @param nSampleRate
	 *        [in] 8000, 11025, 16000, 22010, 44100.
	 * @return Return true if succeeded, return false if failed.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean init(int nAudioStreamType, int nChannelNum, int nBitsPerSample, int nSampleRate)
	{
		return init(nAudioStreamType, nChannelNum, nBitsPerSample, nSampleRate, 120);
	}

	@SuppressWarnings("deprecation")
	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean init(int nAudioStreamType, int nChannelNum, int nBitsPerSample, int nSampleRate, int nMinBufferTime)
	{
		int nAudioFormat;

		release();

		m_nChannelNum = nChannelNum;
		m_nBitsPerSample = nBitsPerSample;
		m_nSampleRate = nSampleRate;
		m_nAudioStreamType = nAudioStreamType;

		if (Build.VERSION.SDK_INT >= 5)
			nAudioFormat = (nChannelNum == 1) ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO;
		else
			nAudioFormat = (nChannelNum == 1) ? AudioFormat.CHANNEL_CONFIGURATION_MONO : AudioFormat.CHANNEL_CONFIGURATION_STEREO;

		// We must get nMinSize at every play time, because play path may change (device, Bluetooth).
		m_nMinBufSize = AudioTrack.getMinBufferSize(
				nSampleRate,
				nAudioFormat,
				(nBitsPerSample == 8) ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT);
		Log.d(LOG_TAG, "AudioTrack min buffer size = " + m_nMinBufSize + ", nAudioStreamType = " + nAudioStreamType);
		// Android 4.1 Galaxy Nexus have noise for 16KHz when use default getMinBufferSize().
		// More buffer for AudioTrack is more safe for stream play
		if (nMinBufferTime > 0)
		{
			int nMinBufferTimeBytes = m_nSampleRate * m_nChannelNum * m_nBitsPerSample/8 * nMinBufferTime / 1000;
			if (m_nMinBufSize < nMinBufferTimeBytes)
			{
				int n = (nMinBufferTimeBytes + m_nMinBufSize - 1) / m_nMinBufSize;
				if (n > 20) // n can't set too large, otherwise, no sound without error
					n = 20;
				else if (n < 2)
					n = 2;
				m_nMinBufSize *= n;
			}
		}
		else
		{
			m_nMinBufSize *= 2;
		}
		Log.d(LOG_TAG, "set AudioTrack buffer size = " + m_nMinBufSize);
		// create AudioTract object
		//The play data size must over m_nMinBufSize, otherwise, it won't play.
		m_oAudioTrack = new AudioTrack(
				nAudioStreamType,
				nSampleRate,
				nAudioFormat,
				(nBitsPerSample == 8) ? AudioFormat.ENCODING_PCM_8BIT : AudioFormat.ENCODING_PCM_16BIT,
				m_nMinBufSize,
				AudioTrack.MODE_STREAM);

		if (waitState(AudioTrack.STATE_INITIALIZED, 10, 5))
		{
			m_oAudioTrack.setPlaybackPositionUpdateListener(this);
		}
		else
		{
			Log.d(LOG_TAG, "Fail to to waiting AudioTrack init state ready");
			m_oAudioTrack.release();
			m_oAudioTrack = null;
		}

		return (m_oAudioTrack != null);
	}

	/**
	 * Release resource of WavePlay object.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized void release()
	{
		stop();
		if (m_oAudioTrack != null)
		{
			m_oAudioTrack.setPlaybackPositionUpdateListener(null);
			m_oAudioTrack.release();
			waitState(AudioTrack.STATE_UNINITIALIZED, 10, 5);
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		release();
		super.finalize();
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean isInitOK()
	{
		if (m_oAudioTrack == null)
			return false;

		return (m_oAudioTrack.getState() ==	AudioTrack.STATE_INITIALIZED);
	}

	/**
	 * Start the playing.
	 *
	 * @param oNotifyHandler
	 *            [in] Use this handler to send play notify message.
	 * @param nPlayNotifyMsg
	 *            [in] The value of play notify message.
	 * @param nNotifyPeriodInMS
	 *            [in] The notify period in ms. Must great then 0, otherwise we can't stop it in real time
	 * @return Return true if succeeded, return false if failed.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean start(Handler oNotifyHandler, int nPlayNotifyMsg, int nNotifyPeriodInMS)
	{
		if (m_oAudioTrack == null)
			return false;
		if (m_oAudioTrack.getState() != AudioTrack.STATE_INITIALIZED)
			return false;

		m_oHandler = oNotifyHandler;
		m_nPlayNotifyMsg = nPlayNotifyMsg;
		m_lPlayStartTime = 0;
		m_lPauseStartTime = 0;
		m_lTotalPauseTime = 0;
		m_nPlayedPeriod = 0;
		m_nTotalPlayBytes = 0;
		m_nPlayStartHeadPosition = 0;
		m_bReachEnd = false;
		m_bStopPlay = false;

		try
		{
			if (m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
			{
				m_oAudioTrack.play();
				waitPlayState(AudioTrack.PLAYSTATE_PLAYING, 10, 5);
			}

			if (nNotifyPeriodInMS > 0)
				m_oAudioTrack.setPositionNotificationPeriod(m_nSampleRate * nNotifyPeriodInMS / 1000);
		}
		catch (Exception ex)
		{
			return false;
		}

		return true;
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean write(short []saOutWaveBuf)
	{
		return write(saOutWaveBuf, 0, saOutWaveBuf.length);
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean write(short []saOutWaveBuf, int nOffsetInFrame, int nSizeInFrame)
	{
		if (m_oAudioTrack == null)
			return false;
		if (m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
			return false;

		if (m_lPlayStartTime == 0)
		{
			//m_lPlayStartPosition may not 0, if we call start(), pause(), stop() and start() again
			m_nPlayStartHeadPosition = getPlaybackHeadPosition();
			m_lPlayStartTime = SystemClock.elapsedRealtime();
			Log.d(LOG_TAG, "Write first data at PlaybackHeadPosition = " + m_nPlayStartHeadPosition);
			Log.d(LOG_TAG, "m_lPlayStartTime = " + m_lPlayStartTime);
		}

		// m_oAudioTrack.write() is very slow
		// To avoid long time non-stoppable, we split to 80 ms
		//m_oAudioTrack.write(saOutWaveBuf, 0, saOutWaveBuf.length);
		m_nTotalPlayBytes += (saOutWaveBuf.length * 2);

		int nDataPieceFrameLen =
				k_nDataPieceDuration * m_nSampleRate * m_nChannelNum * (m_nBitsPerSample / 8) / 1000 /2;
		int iLen, iRes;
		int iIdx = nOffsetInFrame;
		while (!m_bStopPlay && nSizeInFrame > 0)
		{
			iLen = Math.min(nDataPieceFrameLen, nSizeInFrame);
			iRes = 0;
			if ((iRes = m_oAudioTrack.write(saOutWaveBuf, iIdx, iLen)) != iLen)
				Log.d(LOG_TAG, "Write data fail: " + iRes);
			nSizeInFrame -= iLen;
			iIdx += iLen;
		}
		return true;
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean write(byte []baOutWaveBuf)
	{
		return write(baOutWaveBuf, 0, baOutWaveBuf.length);
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean write(byte []baOutWaveBuf, int nOffsetInBytes, int nSizeInBytes)
	{
		if (m_oAudioTrack == null)
			return false;
		if (m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
			return false;

		if (m_lPlayStartTime == 0)
		{
			m_nPlayStartHeadPosition = getPlaybackHeadPosition();
			m_lPlayStartTime = SystemClock.elapsedRealtime();
			Log.d(LOG_TAG, "Write first data at PlaybackHeadPosition = " + m_nPlayStartHeadPosition);
			Log.d(LOG_TAG, "m_lPlayStartTime = " + m_lPlayStartTime);
		}
		// m_oAudioTrack.write() is very slow
		// To avoid long time non-stoppable, we split to 80 ms
		//m_oAudioTrack.write(baOutWaveBuf, nOffsetInBytes, nSizeInBytes);
		m_nTotalPlayBytes += nSizeInBytes;

		int nDataPieceByteLen =
			m_nSampleRate * k_nDataPieceDuration * m_nChannelNum * (m_nBitsPerSample / 8) / 1000;
		int iLen, iRes;
		int iIdx = nOffsetInBytes;
		while (!m_bStopPlay && nSizeInBytes > 0)
		{
			iLen = Math.min(nDataPieceByteLen, nSizeInBytes);
			iRes = 0;
			if ((iRes = m_oAudioTrack.write(baOutWaveBuf, iIdx, iLen)) != iLen)
				Log.d(LOG_TAG, "Write data fail: " + iRes);
			nSizeInBytes -= iLen;
			iIdx += iLen;
		}

		return true;
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean pause()
	{
		if (m_oAudioTrack == null || m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING)
			return false;

		try
		{
			m_oAudioTrack.pause();
			m_lPauseStartTime = SystemClock.elapsedRealtime();
		}
		catch (Exception ex)
		{
			return false;
		}
		return true;
	}

	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean resume()
	{
		if (m_oAudioTrack == null || m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PAUSED)
			return false;

		try
		{
			m_oAudioTrack.play();
			if (m_lPauseStartTime != 0)
			{
				m_lTotalPauseTime += (SystemClock.elapsedRealtime() - m_lPauseStartTime);
				m_lPauseStartTime = 0;
			}
		}
		catch (Exception ex)
		{
			return false;
		}
		return true;
	}

	public void setStopFlag(boolean bStop)
	{
		m_bStopPlay = bStop;
	}

	/**
	 * When no more play data, please call this function.
	 *
	 * @return Return true if succeeded, return false if failed.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized void finishWrite()
	{
		int nSize;

		if (m_oAudioTrack == null)
			return;

		// To fix Galaxy Nexus repeat voice bug, we always add 80 ms silence at the end
		// Always append 120 ms silence at the end to fix cut end voice problem on some devices.
		// For example, 80 ms is useless on HTC Pyramid with 4.0, it must be 120 ms.
		Log.d(LOG_TAG, "Append 120 ms silence.");
		int nSilenceSize = 120 * m_nSampleRate * m_nChannelNum * (m_nBitsPerSample/8) / 1000;
		byte wavBuf[] = new byte [nSilenceSize];
		fillSilence(wavBuf);
		m_oAudioTrack.write(wavBuf, 0, nSilenceSize);

		int nTotalPlayFrames = m_nTotalPlayBytes / (m_nChannelNum * m_nBitsPerSample/8);

		// If we had used pause(), then HeadPosition may not 0 when we call start() again.
		// So we must add m_nPlayStartHeadPosition to compute the the last position.
		m_oAudioTrack.setNotificationMarkerPosition(nTotalPlayFrames + m_nPlayStartHeadPosition);
		Log.d(LOG_TAG, "nTotalPlayFrames = " + nTotalPlayFrames + ", m_nPlayStartHeadPosition = " + m_nPlayStartHeadPosition);
		Log.d(LOG_TAG, "setNotificationMarkerPosition() at " + (nTotalPlayFrames + m_nPlayStartHeadPosition));
		Log.d(LOG_TAG, "m_nTotalPlayBytes = " + m_nTotalPlayBytes +  ", nTotalPlayFrames = " + nTotalPlayFrames);

		// We must play m_nMinBufSize data at least, otherwise AudioTrack won't play
		// m_nMinBufSize is not the value get from AudioTrack.getMinBufferSize()
		// m_nMinBufSize is the value we set in "new AudioTrack()"
		nSize = m_nMinBufSize - m_nTotalPlayBytes - nSilenceSize;
		// But we don't change m_nTotalPlayBytes and MarkerPosition for faster response time.
		if (nSize < wavBuf.length)
			nSize = wavBuf.length;
		while (nSize > 0)
		{
			int n = Math.min(nSize, wavBuf.length);
			// We can't use write(), because it will change m_nTotalPlayBytes.
			if (m_oAudioTrack != null)
				m_oAudioTrack.write(wavBuf, 0, n);
			else
				break;
			nSize -= n;
		}

		// Adjust m_lPlayStartTime first
		sleep(10);
		int nPlaybackHeadPosition = getPlaybackHeadPosition();
		long lCurrTime = SystemClock.elapsedRealtime();
		Log.d(LOG_TAG, "m_lPlayStartTime = " + m_lPlayStartTime + ", nPlaybackHeadPosition = " + nPlaybackHeadPosition);
		m_lPlayStartTime = lCurrTime - (nPlaybackHeadPosition - m_nPlayStartHeadPosition)*1000/m_nSampleRate - m_lTotalPauseTime;
		Log.d(LOG_TAG, "lCurrTime = " + lCurrTime + ". Adjust m_lPlayStartTime to " + m_lPlayStartTime);
	}

	/**
	 * Check whether is playing. It can only be called after start().
	 *
	 * @return Return true if playing, return false if play end.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean isPlaying()
	{
		if (m_oAudioTrack == null)
			return false;

		long lPlayBufferTime = 1000L * m_nTotalPlayBytes * 8 / m_nChannelNum / m_nBitsPerSample / m_nSampleRate;
		if (lPlayBufferTime <= 0)
			return false;

		int nTotalPlayFrames = m_nTotalPlayBytes / (m_nChannelNum * m_nBitsPerSample/8);
		int nPlaybackHeadPosition = getPlaybackHeadPosition();
		if (nPlaybackHeadPosition >= nTotalPlayFrames + m_nPlayStartHeadPosition)
		{
			if (m_bReachEnd)
				return false;

			long lElapsedTime;
			lElapsedTime = SystemClock.elapsedRealtime() - m_lPlayStartTime - m_lTotalPauseTime;

			long lWaitTime = getWaitTime();

			if (lElapsedTime > lWaitTime)
				return false;
			else
				return true;
		}
		else
		{
			return true;
		}
	}

	/**
	 * Wait until all data played. It can not be called in UI thread, because UI thread call into
	 * onMarkerReached() and cause waitPlayFinish() timeout
	 *
	 * @param bWaitPlaybackFinish
	 *            [in] Set true to waiting for playing done.
	 * @return Return true if succeeded, return false if failed.
	 */
	public boolean waitPlayFinish()
	{
		boolean bRet = true;
		long lWaitTime;
		long lElapsedTime;
		int nPlaybackHeadPosition;

		if (m_oAudioTrack == null)
			return true;

		finishWrite();

		long lPlayBufferTime = 1000L * m_nTotalPlayBytes * 8 / m_nChannelNum / m_nBitsPerSample / m_nSampleRate;
		int nTotalPlayFrames = m_nTotalPlayBytes / (m_nChannelNum * m_nBitsPerSample/8);
		Log.d(LOG_TAG, "lPlayBufferTime = " + lPlayBufferTime + ", m_nPlayStartHeadPosition = " + m_nPlayStartHeadPosition);

		// Maybe we can only use m_oAudioTrack.flush() to wait play finish
		// => No, it can't fix this problem, and it cause more bad result:
		//    onMarkerReached() won't be called

		while (m_oAudioTrack.getState() == AudioTrack.STATE_INITIALIZED &&
				m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED)
		{
			lElapsedTime = SystemClock.elapsedRealtime() - m_lPlayStartTime - m_lTotalPauseTime;

			nPlaybackHeadPosition = getPlaybackHeadPosition();

			// lWaitTime may be changed when m_lPlayStartRealTime is set in onPeriodicNotification().
			// So we must update it.
			lWaitTime = getWaitTime();

			Log.d(LOG_TAG, "m_bStopPlay = " + m_bStopPlay + ", nPlaybackHeadPosition = " +	nPlaybackHeadPosition);
			Log.d(LOG_TAG, "lElapsedTime = " + lElapsedTime + ", lWaitTime = " + lWaitTime);
			if (m_bStopPlay)
				break;

			if (nPlaybackHeadPosition >= nTotalPlayFrames + m_nPlayStartHeadPosition)
			{
				if (m_bReachEnd)
				{
					Log.d(LOG_TAG, "PlaybackHeadPosition AND onMarkerReached() reach end");
					break;
				}
				else
				{
					if (lElapsedTime > lWaitTime)
					{
						Log.d(LOG_TAG, "Timeout for waiting AudioTrack playback stop!");
						break;
					}

					if (nPlaybackHeadPosition >= nTotalPlayFrames + m_nSampleRate*120/1000 &&
						lElapsedTime > lPlayBufferTime + 50)
					{
						Log.d(LOG_TAG, "Reach total play and silence frame and wait enough time!");
						break;
					}

					Log.d(LOG_TAG, "Only PlaybackHeadPosition reach end");
				}
			}

			if (lPlayBufferTime - lElapsedTime > 1000)
				sleep(300);
			else if (lPlayBufferTime - lElapsedTime > 100)
				sleep(50);
			else
				sleep(10);
		}

		return bRet;
	}

	/**
	 * Start the playing.
	 *
	 * @param bWaitPlaybackFinish
	 *            [in] Set true to waiting for playing done.
	 * @return Return true if succeeded, return false if failed.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized boolean stop()
	{
		boolean bRet = true;

		if (m_oAudioTrack == null)
			return true;

		if (m_oAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED)
		{
			try
			{
				m_oAudioTrack.stop();
				waitPlayState(AudioTrack.PLAYSTATE_STOPPED, 10, 5);

				if (m_bStopPlay)
				{
					// 1. stop() won't STOP the playing immediately
					//    So we MUST call pause()
					// 2. pause() MUST call after stop(), otherwise the PlaybackHeadPosition is not 0
					//    at next AudioTrack.play()
					m_oAudioTrack.pause();
					waitPlayState(AudioTrack.PLAYSTATE_PAUSED, 10, 5);
				}
			}
			catch (Exception ex)
			{
				bRet = false;
			}
		}

		return bRet;
	}

	public int getMinFrameSize()
	{
		return m_nMinBufSize;
	}

	public int getTotalPlayBytes()
	{
		return m_nTotalPlayBytes;
	}

	/**
	 * Get the current play time in millisecond.
	 * @return The current play time.
	 */
	// synchronized THIS to protect m_oAudioTrack
	public synchronized int getCurrentPlaybackTime()
	{
		int nPlaybackPosition;

		if (m_oAudioTrack == null)
			return -1;

		nPlaybackPosition = getPlaybackHeadPosition();

		return (nPlaybackPosition * 1000 / m_nSampleRate);
	}

	//AudioTrack.OnPlaybackPositionUpdateListener
	@Override
	public void onMarkerReached(AudioTrack track)
	{
		// It is not reliable :
		// 1. If we don't say and let CVSD time-out, it won't be called after play beep twice.
		// 2. If say correct command and then CVSD play voice tag, it will be called twice.
		m_bReachEnd = true;
		if (m_oAudioTrack != null)
		{
			// Some Android 5.0 device (HTC M9, Zenfone 5) may get java.lang.IllegalStateException
			// at track.getPlaybackHeadPosition(), so we add try-catch.
			try
			{
				// Don't use m_oAudioTrack, use "track", because we didn't use
				// synchronized THIS to protect the following code.
				int nPlaybackHeadPosition = track.getPlaybackHeadPosition();
				Log.d(LOG_TAG, "onMarkerReached() when PlaybackHeadPosition = " + nPlaybackHeadPosition);
			}
			catch (Exception ex)
			{
				Log.w(LOG_TAG, ex.toString());
			}
		}
		else
		{
			Log.i(LOG_TAG, "Get onMarkerReached() when m_oAudioTrack is null!");
		}

		// We should use the following code in general case, but it cause waitPlayFinish()
		// getPlaybackHeadPosition() return 0, so we don't use it
		/*
		try
		{
			if (track.getPlayState() == AudioTrack.PLAYSTATE_PLAYING)
				track.stop();
		}
		catch (Exception ex)
		{
		}
		*/
	}

	//AudioTrack.OnPlaybackPositionUpdateListener
	@Override
	public void onPeriodicNotification(AudioTrack track)
	{
		if (m_oHandler != null && m_oAudioTrack != null)
			m_oHandler.sendMessage(m_oHandler.obtainMessage(m_nPlayNotifyMsg, m_nPlayedPeriod++, 0));
	}

	// Only called in init() and release(), so don't need synchronized THIS.
	protected boolean waitState(int nWaitState, int nWaitDuration, int nMaxWaitCount)
	{
		int nWaitCount = 0;

		if ( (nWaitState != AudioTrack.STATE_INITIALIZED) &&
			 (nWaitState != AudioTrack.STATE_UNINITIALIZED) )
				return false;

		while (m_oAudioTrack.getState() != nWaitState)
		{
			if (nWaitCount++ > nMaxWaitCount)
				break;
			sleep(nWaitDuration);
		}
		Log.d(LOG_TAG, "Waiting AudioTrack state " + nWaitState + " use " + nWaitCount*nWaitDuration + " ms");

		return (nWaitCount <= nMaxWaitCount);
	}

	// Only called in start() and stop(), so don't need synchronized THIS.
	protected boolean waitPlayState(int nWaitPlayState, int nWaitDuration, int nMaxWaitCount)
	{
		int nWaitCount = 0;

		if ( (nWaitPlayState != AudioTrack.PLAYSTATE_PLAYING) &&
			 (nWaitPlayState != AudioTrack.PLAYSTATE_PAUSED) &&
			 (nWaitPlayState != AudioTrack.PLAYSTATE_STOPPED) )
			return false;

		while (m_oAudioTrack.getPlayState() != nWaitPlayState)
		{
			if (nWaitCount++ > nMaxWaitCount)
				break;
			sleep(nWaitDuration);
		}
		Log.d(LOG_TAG, "Waiting AudioTrack play state " + nWaitPlayState + " use " + nWaitCount*nWaitDuration + " ms");

		return (nWaitCount <= nMaxWaitCount);
	}

	protected void sleep(long lTimeInMS)
	{
		try
		{
			if (lTimeInMS > 0)
				Thread.sleep(lTimeInMS);
			else
				Thread.sleep(10);
		}
		catch (Exception ex)
		{
		}
	}

	protected long getWaitTime()
	{
		long lPlayBufferTime = 1000L * m_nTotalPlayBytes * 8 / m_nChannelNum / m_nBitsPerSample / m_nSampleRate;
		long lWaitTime = lPlayBufferTime;

		if (m_nAudioStreamType == 6) //BtHeadset.getSTREAM_BLUETOOTH_SCO()
		{
			lWaitTime = lPlayBufferTime + 2000;
		}
		else
		{
			if (lPlayBufferTime < 300)
				lWaitTime = lPlayBufferTime + 125;
			else if (lPlayBufferTime < 500)
				lWaitTime = lPlayBufferTime + 150;
			else if (lPlayBufferTime < 750)
				lWaitTime = lPlayBufferTime + 200;
			else if (lPlayBufferTime < 1000)
				lWaitTime = lPlayBufferTime + 300;
			else if (lPlayBufferTime < 2000)
				lWaitTime = lPlayBufferTime + 400;
			else if (lPlayBufferTime < 3000)
				lWaitTime = lPlayBufferTime + 500;
			else if (lPlayBufferTime < 4000)
				lWaitTime = lPlayBufferTime + 600;
			else
				lWaitTime = lPlayBufferTime + 700;
		}

		return lWaitTime;
	}

	protected int getPlaybackHeadPosition()
	{
		int nPlaybackHeadPosition = 0;
		try
		{
			nPlaybackHeadPosition = m_oAudioTrack.getPlaybackHeadPosition();
		}
		catch (Exception ex)
		{
			Log.w(LOG_TAG, ex.toString());
		}
		return nPlaybackHeadPosition;
	}

	public static void fillSilence(byte[] byBuffer)
	{
		if (byBuffer != null)
		{
			int nBufferIndex = 0;
			while (nBufferIndex < byBuffer.length)
			{
				System.arraycopy(k_bySilence, 0, byBuffer, nBufferIndex, Math.min(k_bySilence.length, byBuffer.length - nBufferIndex));
				nBufferIndex += k_bySilence.length;
			}
		}
	}
}
