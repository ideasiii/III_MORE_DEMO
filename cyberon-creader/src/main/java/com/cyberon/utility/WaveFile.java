package com.cyberon.utility;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.Context;


public class WaveFile
{
	private BufferedRandomAccessFile mBufferedRandomAccessFile = null;
	private int mSampleSizeInByte = 0;

	/**
	 * Constructor a new WaveFile using specified name for the specified
	 * context.
	 *
	 * @param context
	 *        [in] The context for the file.
	 * @param name
	 *        [in] The file name to be contained for the context. Can not
	 *        contain path separators
	 */
	public WaveFile(Context context, String name) throws IOException
	{
		String szPath;

		//ToolKit.GetStorageCardStatus() has bug for some internal flash device.
//	    if (ToolKit.GetStorageCardStatus() == ToolKit.ID_SD_OK)
    	    szPath = (name.startsWith(File.separator)? name: String.format("%s/%s", ToolKit.getExternalStorageDirectory(), name));
//	    else
//    	    szPath = (name.startsWith(File.separator)? name: String.format("%s/%s", context.getFilesDir().getAbsolutePath(), name));

		mBufferedRandomAccessFile = new BufferedRandomAccessFile(new File(szPath), "rw", 8192);
	}

	/**
	 * Close the wave file.
	 *
	 * @throws IOException
	 */
	public void close() throws IOException
	{
		setDataSize(mSampleSizeInByte);
		mBufferedRandomAccessFile.flush();
		mBufferedRandomAccessFile.close();
	}

	/**
	 * Set wave file format.
	 *
	 * @param bitsPerSample
	 *        [in] Bits per sample. 8 or 16.
	 * @param channelsPerFrame
	 *        [in] Number of channels. 1 or 2.
	 * @param sampleRate
	 *        [in] Sample rate. This value should be 8000, 16000, 11025, 22050,
	 *        or 44100.
	 * @throws IOException
	 */
	public void setFormat(
		int bitsPerSample,
		int channelsPerFrame,
		int sampleRate) throws IOException
	{
		mBufferedRandomAccessFile.seek(0);
		mBufferedRandomAccessFile.writeByte('R');
		mBufferedRandomAccessFile.writeByte('I');
		mBufferedRandomAccessFile.writeByte('F');
		mBufferedRandomAccessFile.writeByte('F');
		mBufferedRandomAccessFile.writeInt(0);
		mBufferedRandomAccessFile.writeByte('W');
		mBufferedRandomAccessFile.writeByte('A');
		mBufferedRandomAccessFile.writeByte('V');
		mBufferedRandomAccessFile.writeByte('E');
		mBufferedRandomAccessFile.writeByte('f');
		mBufferedRandomAccessFile.writeByte('m');
		mBufferedRandomAccessFile.writeByte('t');
		mBufferedRandomAccessFile.writeByte(' ');
		mBufferedRandomAccessFile.writeInt(Integer.reverseBytes(16));
		mBufferedRandomAccessFile.writeShort(Short.reverseBytes((short)1));
		mBufferedRandomAccessFile.writeShort(Short.reverseBytes((short)channelsPerFrame));
		mBufferedRandomAccessFile.writeInt(Integer.reverseBytes(sampleRate));
		mBufferedRandomAccessFile.writeInt(Integer.reverseBytes(sampleRate * channelsPerFrame
			* bitsPerSample / 8));
		mBufferedRandomAccessFile.writeShort(Short.reverseBytes((short)(channelsPerFrame
			* bitsPerSample / 8)));
		mBufferedRandomAccessFile.writeShort(Short.reverseBytes((short)bitsPerSample));
		mBufferedRandomAccessFile.writeByte('d');
		mBufferedRandomAccessFile.writeByte('a');
		mBufferedRandomAccessFile.writeByte('t');
		mBufferedRandomAccessFile.writeByte('a');
		mBufferedRandomAccessFile.writeInt(0);
		mBufferedRandomAccessFile.flush();
	}

	/**
	 * Write wave data to WaveFile object.
	 *
	 * @param data
	 *        [in] Byte array data.
	 *
	 * @throws IOException
	 */
	public void writeData(byte[] data) throws IOException
	{
		mBufferedRandomAccessFile.seek(44 + mSampleSizeInByte);
		mBufferedRandomAccessFile.write(data);
		mSampleSizeInByte += data.length;
	}

	public void writeData(byte[] data, int nSize) throws IOException
	{
		mBufferedRandomAccessFile.seek(44 + mSampleSizeInByte);
		mBufferedRandomAccessFile.write(data, 0, nSize);
		mSampleSizeInByte += data.length;
	}
	/**
     * Write wave data to WaveFile object.
     *
     * @param data
     *        [in] Short array data.
     *
     * @throws IOException
     */
	public void writeData(short[] data) throws IOException
	{
		mBufferedRandomAccessFile.seek(44 + mSampleSizeInByte);
		ByteBuffer byteBuf = ByteBuffer.allocate(data.length * (Short.SIZE / 8));
		byteBuf.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < data.length; i++)
			byteBuf.putShort(data[i]);
		mBufferedRandomAccessFile.write(byteBuf.array());
		mSampleSizeInByte += (byteBuf.array().length);
	}

	/**Write audio data into wave file by NIO interface.
	 * @param data         [in] Data to be write.
	 * @throws IOException If some other I/O error occurs.*/
	public void writeData(ByteBuffer data) throws IOException
    {
		mBufferedRandomAccessFile.seek(44 + mSampleSizeInByte);
		int nSize = data.limit();
		byte byBuf[] = new byte [nSize];
		data.rewind();
		data.get(byBuf);
		mBufferedRandomAccessFile.write(byBuf);
		mSampleSizeInByte += nSize;
    }

	/**
	 * Returns the wave data size in bytes that this object currently has.
	 *
	 * @return The wave data size in bytes.
	 */
	public int getSize()
	{
		return mSampleSizeInByte;
	}

	private void setDataSize(int size) throws IOException
	{
		int chunkSize = 44 + size - 8;

		mBufferedRandomAccessFile.flush();
		mBufferedRandomAccessFile.seek(4);
		mBufferedRandomAccessFile.writeInt(Integer.reverseBytes(chunkSize));
		mBufferedRandomAccessFile.flush();
		mBufferedRandomAccessFile.seek(40);
		mBufferedRandomAccessFile.writeInt(Integer.reverseBytes(size));
		mBufferedRandomAccessFile.flush();
	}
}
