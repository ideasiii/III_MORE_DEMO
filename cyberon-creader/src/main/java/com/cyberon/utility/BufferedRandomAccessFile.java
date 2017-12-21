package com.cyberon.utility;
//package edu.northwestern.at.utils;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UTFDataFormatException;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;



/**
*  Extension fo the RandomAccessFile to use buffered I/O as much as
*  possible. Usable with the <code>com.objectwave.persist.FileBroker</code> .
*  Publically identical to <code>java.io.RandomAccessFile</code> , except for
*  the constuctor and <code>flush()</code> . <p>
*
*  <b>Note:</b> This class is not threadsafe.
*
* @author  Steven Sinclair
* @version  $Date: 2005/02/20 17:27:32 $ $Revision: 2.3 $
* @see  java.io.RandomAccessFile
*
*	<p>
*	Modifications by Philip R. Burns.  2007/05/08.
*	</p>
*
*	Modifications by Ming Wu  2009/11/12.
*		1. Fix readInt()... "signed" bug
*		2. Fix bug in read(byte[] b, int pos, int len)
*		3. Fix bug in write(byte[] b, int pos, int len)
*		4. Fix bug in setLength()
*		5. Remove un-used code
*		6. Speed up by use data member directly, ex. currBuf.pos => currBuf_pos
*		7. Speed up readInt(), readShort(), readUnsignedShort() by use temp byte buffer
*		8. Speed up writeInt(), writeLong() by use temp byte buffer
*		9. Improve commitBuffer() speed by use two currBuf_modified flag
*		10. Performance is better than BufferedReader and BufferedWriter
*
*/

public class BufferedRandomAccessFile implements DataInput, DataOutput
{
	protected byte[] currBuf_bytes = null;			// cache buffer
	protected int currBuf_pos = 0;					// current file position in the buffer [0, currBuf_bytes.length()-1]
	protected int currBuf_dataLen = 0;				// how many bytes of the buffer are valid? ( < BUFFER_SIZE near end of file)
	protected boolean currBuf_modified_before_pos = false;		// buffer needs to be written back to disk? (partial buffer write flag)
	protected boolean currBuf_modified_after_pos = false;		// buffer needs to be written back to disk? (full buffer write flag)
	protected long currBuf_filePos = 0;				// what file offset does this buffer start at?
	protected byte[] temp_bytes = null;

	RandomAccessFile delegate;

	public static boolean copy(File source, File dest)
	{
		FileChannel in = null, out = null;
		FileInputStream fin = null;
		FileOutputStream fout = null;
		boolean bRet = false;

		try
		{
			fin = new FileInputStream(source);
			fout = new FileOutputStream(dest);
			in = fin.getChannel();
			out = fout.getChannel();

			long size = in.size();
			MappedByteBuffer buf = in.map(FileChannel.MapMode.READ_ONLY, 0, size);

			out.write(buf);
			bRet = true;
		}
		catch (Exception ex)
		{
		}
		finally
		{
			try
			{
				if (fin != null)
					fin.close();
				if (fout != null)
				{
					fout.flush();
					fout.close();
				}
			}
			catch (Exception ex)
			{
			}
		}

		return bRet;
	}

	/**
	*  Constructor for the BufferedRandomAccessFile object
	*
	* @param  file Description of Parameter
	* @param  mode Description of Parameter
	* @param  bufferSize Description of Parameter
	* @exception  IOException Description of Exception
	*/
	public BufferedRandomAccessFile(File file, String mode, int bufferSize) throws IOException
	{
		delegate = new RandomAccessFile(file, mode);

		if (bufferSize < 1)
		{
			throw new Error("Buffer size must be at least 1");
		}
		currBuf_bytes = new byte [bufferSize];
		currBuf_filePos = delegate.getFilePointer();
		temp_bytes = new byte [8];

		//We don't always prepare cache buffer
		//fillBuffer();
	}

	/**
	*  Constructor for the BufferedRandomAccessFile object
	*
	* @param  file Description of Parameter
	* @param  mode Description of Parameter
	* @exception  IOException Description of Exception
	*/
	public BufferedRandomAccessFile(File file, String mode) throws IOException
	{
		this(file, mode, 8192);
	}

	/**
	*  Sets the Length attribute of the BufferedRandomAccessFile object
	*
	* @param  newLength The new Length value
	* @exception  IOException Description of Exception
	*/
	public void setLength(long newLength) throws IOException
	{
		delegate.setLength(newLength);
		if (newLength < currBuf_filePos)
		{
			currBuf_filePos = newLength;
			currBuf_pos = 0;
			currBuf_dataLen = 0;
		}
		else if (newLength < currBuf_filePos + currBuf_dataLen)
		{
			currBuf_dataLen = (int) (newLength - currBuf_filePos);
			if (currBuf_pos > currBuf_dataLen)
				currBuf_pos = currBuf_dataLen;
		}
	}

	/////////////////////////////  Support Reader & Writer

	/**
	*  Gets the Reader attribute of the BufferedRandomAccessFile object
	*
	* @return  The Reader value
	*/
	public Reader getReader()
	{
		return
		new Reader()
		{
			/**
			*  Description of the Method
			*
			* @exception  IOException Description of Exception
			*/
			@Override
            public void close() throws IOException
			{
				BufferedRandomAccessFile.this.close();
			}
			/**
			*  Description of the Method
			*
			* @param  readAhreadLimit Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void mark(int readAhreadLimit) throws IOException
			{
				throw new IOException("mark not supported");
			}
			/**
			*  Description of the Method
			*
			* @return  Description of the Returned Value
			*/
			@Override
            public boolean markSupported()
			{
				return false;
			}
			/**
			*  Description of the Method
			*
			* @return  Description of the Returned Value
			* @exception  IOException Description of Exception
			*/
			@Override
            public int read() throws IOException
			{
				return BufferedRandomAccessFile.this.readChar();
			}
			/**
			*  Description of the Method
			*
			* @param  buf Description of Parameter
			* @return  Description of the Returned Value
			* @exception  IOException Description of Exception
			*/
			@Override
            public int read(char[] buf) throws IOException
			{
				return read(buf, 0, buf.length);
			}
			/**
			*  Description of the Method
			*
			* @param  buf Description of Parameter
			* @param  pos Description of Parameter
			* @param  len Description of Parameter
			* @return  Description of the Returned Value
			* @exception  IOException Description of Exception
			*/
			@Override
            public int read(char[] buf, int pos, int len) throws IOException
			{
				for(int i = 0; i < len; i++)
				{
					buf[pos + i] = readChar();
				}
				return len;
			}
			/**
			*  Description of the Method
			*
			* @return  Description of the Returned Value
			* @exception  IOException Description of Exception
			*/
			@Override
            public boolean ready() throws IOException
			{
				return (currBuf_pos < currBuf_dataLen) ||
				(length() < currBuf_filePos + currBuf_pos);
			}
			/**
			*  Description of the Method
			*
			* @param  n Description of Parameter
			* @return  Description of the Returned Value
			* @exception  IOException Description of Exception
			*/
			@Override
            public long skip(long n) throws IOException
			{
				skipBytes(n);
				return n;
			}
		};
	}

	/**
	*  Gets the Writer attribute of the BufferedRandomAccessFile object
	*
	* @return  The Writer value
	*/
	public Writer getWriter()
	{
		return
		new Writer()
		{
			/**
			*  Description of the Method
			*
			* @exception  IOException Description of Exception
			*/
			@Override
            public void close() throws IOException
			{
				BufferedRandomAccessFile.this.close();
			}
			/**
			*  Description of the Method
			*
			* @exception  IOException Description of Exception
			*/
			@Override
            public void flush() throws IOException
			{
				BufferedRandomAccessFile.this.flush();
			}
			/**
			*  Description of the Method
			*
			* @param  ch Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void write(int ch) throws IOException
			{
				writeChar(ch);
			}
			/**
			*  Description of the Method
			*
			* @param  ch Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void write(char[] ch) throws IOException
			{
				write(ch, 0, ch.length);
			}
			/**
			*  Description of the Method
			*
			* @param  ch Description of Parameter
			* @param  pos Description of Parameter
			* @param  len Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void write(char[] ch, int pos, int len) throws IOException
			{
				for(int i = 0; i < len; i++)
				{
					writeChar(ch[pos + i]);
				}
			}
			/**
			*  Description of the Method
			*
			* @param  str Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void write(String str) throws IOException
			{
				write(str, 0, str.length());
			}
			/**
			*  Description of the Method
			*
			* @param  str Description of Parameter
			* @param  pos Description of Parameter
			* @param  len Description of Parameter
			* @exception  IOException Description of Exception
			*/
			@Override
            public void write(String str, int pos, int len) throws IOException
			{
				for(int i = 0; i < len; i++)
				{
					writeChar(str.charAt(pos + i));
				}
			}
		};
	}

	/**
	*  Gets the FD attribute of the BufferedRandomAccessFile object
	*
	* @return  The FD value
	* @exception  IOException Description of Exception
	*/
	public FileDescriptor getFD() throws IOException
	{
		return delegate.getFD();
	}

	/**
	*  Gets the FilePointer attribute of the BufferedRandomAccessFile object
	*
	* @return  The FilePointer value
	*/
	public long getFilePointer()
	{
		return currBuf_filePos + currBuf_pos;
	}

	//////////////////////////////  BEGIN CUT & PASTE FROM RandomAccessFile

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public boolean readBoolean() throws IOException
	{
		return readByte() != 0;
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public int readUnsignedByte() throws IOException
	{
		int b = read();
		if (b < 0)
		{
			throw new EOFException();
		}
		return b & 0xff;
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public byte readByte() throws IOException
	{
		int b = read();
		if(b < 0)
		{
			throw new EOFException();
		}
		return (byte) b;
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public short readShort() throws IOException
	{
		read(temp_bytes, 0, 2);
		int ch1 = ((int)temp_bytes[0] & 0xFF);
		int ch2 = ((int)temp_bytes[1] & 0xFF);
		if((ch1 | ch2) < 0)
		{
			throw new EOFException();
		}
		return (short) ((ch1 << 8) + (ch2 << 0));
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public int readUnsignedShort() throws IOException
	{
		read(temp_bytes, 0, 2);
		int ch1 = ((int)temp_bytes[0] & 0xFF);
		int ch2 = ((int)temp_bytes[1] & 0xFF);
		if((ch1 | ch2) < 0)
		{
			throw new EOFException();
		}
		return (ch1 << 8) + (ch2 << 0);
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public char readChar() throws IOException
	{
		return (char) readUnsignedShort();
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public int readInt() throws IOException
	{
		read(temp_bytes, 0, 4);
		int ch1 = ((int)temp_bytes[0] & 0xFF);
		int ch2 = ((int)temp_bytes[1] & 0xFF);
		int ch3 = ((int)temp_bytes[2] & 0xFF);
		int ch4 = ((int)temp_bytes[3] & 0xFF);
		if((ch1 | ch2 | ch3 | ch4) < 0)
		{
			throw new EOFException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public long readLong() throws IOException
	{
		return ((long) (readInt()) << 32) + (readInt() & 0xFFFFFFFFL);
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public float readFloat() throws IOException
	{
		return Float.intBitsToFloat(readInt());
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public double readDouble() throws IOException
	{
		return Double.longBitsToDouble(readLong());
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public String readLine() throws IOException
	{
		//This program is same as RandomAccessFile.readLine
		StringBuilder input = new StringBuilder();
		int c = -1;
		boolean eol = false;

		while(!eol)
		{
			switch (c = read())
			{
				case -1:
				case '\n':
				eol = true;
				break;
				case '\r':
				eol = true;
				long cur = getFilePointer();
				if((read()) != '\n')
				{
					seek(cur);
				}
				break;
				default:
				input.append((char) c);
			}
		}

		if((c == -1) && (input.length() == 0))
		{
			return null;
		}
		return input.toString();
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public String readUTF() throws IOException
	{
		//This program is same as DataInputStream.readUTF
		int utflen = readUnsignedShort();
		byte[] bytearr = null;
		char[] chararr = null;

		bytearr = new byte[utflen];
		chararr = new char[utflen];

		int c, char2, char3;
		int count = 0;
		int chararr_count=0;

		readFully(bytearr, 0, utflen);

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			if (c > 127) break;
			count++;
			chararr[chararr_count++]=(char)c;
		}

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
				case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				/* 0xxxxxxx*/
				count++;
				chararr[chararr_count++]=(char)c;
				break;
				case 12: case 13:
				/* 110x xxxx   10xx xxxx*/
				count += 2;
				if (count > utflen)
				throw new UTFDataFormatException(
				"malformed input: partial character at end");
				char2 = (int) bytearr[count-1];
				if ((char2 & 0xC0) != 0x80)
				throw new UTFDataFormatException(
				"malformed input around byte " + count);
				chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
				(char2 & 0x3F));
				break;
				case 14:
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				count += 3;
				if (count > utflen)
				throw new UTFDataFormatException(
				"malformed input: partial character at end");
				char2 = (int) bytearr[count-2];
				char3 = (int) bytearr[count-1];
				if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
				throw new UTFDataFormatException(
				"malformed input around byte " + (count-1));
				chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
				((char2 & 0x3F) << 6)  |
				((char3 & 0x3F) << 0));
				break;
				default:
				/* 10xx xxxx,  1111 xxxx */
				throw new UTFDataFormatException(
				"malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}

	public String readString() throws IOException
	{
		int n = readInt();
		byte[] b = new byte[n];
		read(b);
		return new String(b);
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeBoolean(boolean b) throws IOException
	{
		write(b ? 1 : 0);
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeByte(int b) throws IOException
	{
		write(b);
	}

	/**
	*  Description of the Method
	*
	* @param  s Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeShort(int s) throws IOException
	{
		temp_bytes[0] = (byte)(s >>> 8);
		temp_bytes[1] = (byte)(s);
		write(temp_bytes, 0, 2);
	}

	/**
	*  Description of the Method
	*
	* @param  ch Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeChar(int ch) throws IOException
	{
		writeShort(ch);
	}

	/**
	*  Description of the Method
	*
	* @param  i Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeInt(int i) throws IOException
	{
		temp_bytes[0] = (byte)(i >>> 24);
		temp_bytes[1] = (byte)(i >>> 16);
		temp_bytes[2] = (byte)(i >>> 8);
		temp_bytes[3] = (byte)(i);
		write(temp_bytes, 0, 4);
		/*
		write((i >>> 24) & 0xFF);
		write((i >>> 16) & 0xFF);
		write((i >>> 8) & 0xFF);
		write((i >>> 0) & 0xFF);
		*/
	}

	/**
	*  Description of the Method
	*
	* @param  l Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeLong(long l) throws IOException
	{
		temp_bytes[0] = (byte)(l >>> 56);
		temp_bytes[1] = (byte)(l >>> 48);
		temp_bytes[2] = (byte)(l >>> 40);
		temp_bytes[3] = (byte)(l >>> 32);
		temp_bytes[4] = (byte)(l >>> 24);
		temp_bytes[5] = (byte)(l >>> 16);
		temp_bytes[6] = (byte)(l >>> 8);
		temp_bytes[7] = (byte)(l);
		write(temp_bytes, 0, 8);
		/*
		write((int) (l >>> 56) & 0xFF);
		write((int) (l >>> 48) & 0xFF);
		write((int) (l >>> 40) & 0xFF);
		write((int) (l >>> 32) & 0xFF);
		write((int) (l >>> 24) & 0xFF);
		write((int) (l >>> 16) & 0xFF);
		write((int) (l >>> 8) & 0xFF);
		write((int) (l >>> 0) & 0xFF);
		*/
	}

	/**
	*  Description of the Method
	*
	* @param  f Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeFloat(float f) throws IOException
	{
		writeInt(Float.floatToIntBits(f));
	}

	/**
	*  Description of the Method
	*
	* @param  f Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeDouble(double f) throws IOException
	{
		writeLong(Double.doubleToLongBits(f));
	}

	/**
	*  Description of the Method
	*
	* @param  str Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeUTF(String str) throws IOException
	{
		int strlen = str.length();
		int utflen = 0;

		for(int i = 0; i < strlen; i++)
		{
			int c = str.charAt(i);
			if((c >= 0x0001) && (c <= 0x007F))
			{
				utflen++;
			}
			else if(c > 0x07FF)
			{
				utflen += 3;
			}
			else
			{
				utflen += 2;
			}
		}
		if(utflen > 65535)
		{
			throw new UTFDataFormatException();
		}
		write((utflen >>> 8) & 0xFF);
		write((utflen >>> 0) & 0xFF);
		for(int i = 0; i < strlen; i++)
		{
			int c = str.charAt(i);
			if((c >= 0x0001) && (c <= 0x007F))
			{
				write(c);
			}
			else if(c > 0x07FF)
			{
				write(0xE0 | ((c >> 12) & 0x0F));
				write(0x80 | ((c >> 6) & 0x3F));
				write(0x80 | ((c >> 0) & 0x3F));
			}
			else
			{
				write(0xC0 | ((c >> 6) & 0x1F));
				write(0x80 | ((c >> 0) & 0x3F));
			}
		}
	}

	public void writeString(String str) throws IOException
	{
		byte[] b = str.getBytes();
		writeInt(b.length);
		write(b, 0, b.length);
	}

	public void newLine() throws IOException
	{
		writeByte('\r');
		writeByte('\n');
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void readFully(byte[] b) throws IOException
	{
		//readFully(b, 0, b.length);
		read(b, 0, b.length);
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @param  pos Description of Parameter
	* @param  len Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void readFully(byte[] b, int pos, int len) throws IOException
	{
		read(b, pos, len);
		/*
		int n = 0;
		while(n < len)
		{
			int count = this.read(b, pos + n, len - n);
			if(count < 0)
			{
				throw new EOFException();
			}
			n += count;
		}
		*/
	}

	/**
	*  Description of the Method
	*
	* @param  s Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeBytes(String s) throws IOException
	{
		byte[] b = s.getBytes();
		write(b, 0, b.length);
	}

	/**
	*  Description of the Method
	*
	* @param  s Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void writeChars(String s) throws IOException
	{
		//This program is same as RandomAccessFile.readUTF
		int clen = s.length();
		int blen = 2 * clen;
		byte[] b = new byte[blen];
		char[] c = new char[clen];
		s.getChars(0, clen, c, 0);
		for(int i = 0, j = 0; i < clen; i++)
		{
			b[j++] = (byte) (c[i] >>> 8);
			b[j++] = (byte) (c[i] >>> 0);
		}
		write(b, 0, blen);
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	public long length() throws IOException
	{
		long fileLen = delegate.length();

		if (currBuf_filePos + currBuf_dataLen > fileLen)
		{
			return currBuf_filePos + currBuf_dataLen;
		}
		else
		{
			return fileLen;
		}
	}

	/**
	*  Description of the Method
	*
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	public int read() throws IOException
	{
		if (currBuf_pos < currBuf_dataLen)
		{
			// at least one byte is available in the buffer
			return currBuf_bytes[currBuf_pos++]&0xff;
		}
		else
		{
			syncBuffer(currBuf_filePos + currBuf_pos);
			if(currBuf_dataLen == 0)
			{
				throw new EOFException();
			}
			return read();
			// recurse: should be trivial this time.
		}
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @param  pos Description of Parameter
	* @param  len Description of Parameter
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	public int read(byte[] b, int pos, int len) throws IOException
	{
		int n = 0;
		while (len > 0)
		{
			int bufferAvailable = currBuf_dataLen - currBuf_pos;
			int thiscopy = Math.min(bufferAvailable, len);
			if (thiscopy == 0)	//len > 0, so it is bufferAvailable == 0
			{
				syncBuffer(currBuf_filePos + currBuf_pos);
				if (currBuf_dataLen == 0)
					break;
				else
					continue;
			}
			System.arraycopy(currBuf_bytes, currBuf_pos, b, pos, thiscopy);
			currBuf_pos += thiscopy;
			pos += thiscopy;
			len -= thiscopy;
			n += thiscopy;
		}
		return n;
	}

	/**
	*  Description of the Method
	*
	* @param  pos Description of Parameter
	* @exception  IOException Description of Exception
	*/
	public void seek(long pos) throws IOException
	{
		long newBufPos = pos - currBuf_filePos;
		if (newBufPos >= 0 && newBufPos < currBuf_dataLen)
		{
			// when buffer already modified, set currBuf_modified_after_pos
			// if seek to front position
			if (currBuf_modified_before_pos)
			{
				if (newBufPos < currBuf_pos)
					currBuf_modified_after_pos = true;
			}

			// it falls within the buffer
			currBuf_pos = (int) newBufPos;
		}
		else
		{
			//syncBuffer(pos);
			commitBuffer();
			currBuf_filePos = pos;
			currBuf_dataLen = 0;
			currBuf_pos = 0;
		}
	}

	/**
	*  Description of the Method
	*
	* @param  n Description of Parameter
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	@Override
    public int skipBytes(int n) throws IOException
	{
		return (int) skipBytes((long) n);
	}

	/**
	*  Description of the Method
	*
	* @param  n Description of Parameter
	* @return  Description of the Returned Value
	* @exception  IOException Description of Exception
	*/
	public long skipBytes(long n) throws IOException
	{
		try
		{
			seek(currBuf_filePos + currBuf_pos + n);
			return n;
		}
		catch(EOFException ex)
		{
			return -1;
		}
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void write(byte[] b) throws IOException
	{
		write(b, 0, b.length);
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @param  pos Description of Parameter
	* @param  len Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void write(byte[] b, int pos, int len) throws IOException
	{
		if (currBuf_pos + len <= currBuf_bytes.length)
		{
			//We must check and initialize cache buffer for synchronization
			if (currBuf_dataLen == 0)
				syncBuffer(currBuf_filePos);
			System.arraycopy(b, pos, currBuf_bytes, currBuf_pos, len);
			currBuf_pos += len;
			currBuf_modified_before_pos = true;
			if (currBuf_pos > currBuf_dataLen)
				currBuf_dataLen = currBuf_pos;
		}
		else
		{
			if (len <= currBuf_bytes.length)
			{
				syncBuffer(currBuf_filePos + currBuf_pos);
				write(b, pos, len);
				// recurse: it should succeed trivially this time.
			}
			else
			{
				// write more than the buffer can contain: use delegate
				delegate.seek(currBuf_filePos + currBuf_pos);
				delegate.write(b, pos, len);
				syncBuffer(currBuf_filePos + currBuf_pos + len);
			}
		}
	}

	/**
	*  Description of the Method
	*
	* @param  b Description of Parameter
	* @exception  IOException Description of Exception
	*/
	@Override
    public void write(int b) throws IOException
	{
		if (currBuf_pos < currBuf_bytes.length)
		{
			//We must check and initialize cache buffer for synchronization
			if (currBuf_dataLen == 0)
				syncBuffer(currBuf_filePos);
			// trivial write
			currBuf_bytes[currBuf_pos++] = (byte) b;
			currBuf_modified_before_pos = true;
			if (currBuf_pos > currBuf_dataLen)
				currBuf_dataLen = currBuf_pos;
		}
		else
		{
			syncBuffer(currBuf_filePos + currBuf_pos);
			write(b);
			// recurse: should succeed trivially this time.
		}
	}

	// This will do more when dual buffers are implemented.
	//
	/**
	*  Description of the Method
	*
	* @exception  IOException Description of Exception
	*/
	public void flush() throws IOException
	{
		commitBuffer();
	}

	/**
	*  Description of the Method
	*
	* @exception  IOException Description of Exception
	*/
	public void close() throws IOException
	{
		flush();
		delegate.close();
	}

	/**
	*  Save any changes and re-read the currBuf_bytes from the given position.
	*  Note that the read(byte[],int,int) method assumes that this method sets
	*  currBuf_pos to 0.
	*
	* @param  new_FP Description of Parameter
	* @return  int - the number of bytes available for reading
	* @exception  IOException Description of Exception
	*/
	protected int syncBuffer(long new_FP) throws IOException
	{
		commitBuffer();
		delegate.seek(new_FP);
		currBuf_filePos = new_FP;
		fillBuffer();
		return currBuf_dataLen;
	}

	/**
	*  Description of the Method
	*
	* @exception  IOException Description of Exception
	*/
	protected void fillBuffer() throws IOException
	{
		currBuf_dataLen = delegate.read(currBuf_bytes);
		currBuf_pos = 0;
		if (currBuf_dataLen < 0)
		{
			currBuf_dataLen = 0;
		}
	}

	/**
	*  If modified, write buffered bytes to the delegate file
	*
	* @exception  IOException Description of Exception
	*/
	protected void commitBuffer() throws IOException
	{
		if (currBuf_modified_before_pos)
		{
			delegate.seek(currBuf_filePos);

			if (currBuf_modified_after_pos)
			{
				delegate.write(currBuf_bytes, 0, currBuf_dataLen);
				currBuf_modified_after_pos = false;
			}
			else
			{
				delegate.write(currBuf_bytes, 0, currBuf_pos);
			}

			currBuf_modified_before_pos = false;
		}
	}

}
