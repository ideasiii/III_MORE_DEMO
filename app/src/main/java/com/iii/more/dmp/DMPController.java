package com.iii.more.dmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

import sdk.ideas.common.Logs;

/**
 * Created by joe on 2017/8/22.
 */

public abstract class DMPController
{
    
    
    private static boolean validSocket(Socket mSocket)
    {
        if (null == mSocket || mSocket.isClosed())
        {
            return false;
        }
        return true;
    }
    
    public static int dmpSend(final int nCommand, final String strBody, DMPParameters.DMP_PACKET sendPacket
            , Socket mSocket)
    {
        int nDmpStatus = DMPParameters.STATUS_SUCCESS;
        int nLength = 0;
        try
        {
            if (!validSocket(mSocket))
            {
                return DMPParameters.STATUS_ERR_SOCKET_INVALID;
            }
            
            OutputStream outSocket = mSocket.getOutputStream();
            
            // header + body + endChar
            nLength = DMPParameters.DMP_HEADER_SIZE;
            
            if (null != strBody && 0 < strBody.length())
            {
                //maybe error 需不需要加一呢?
                
                nLength += strBody.getBytes(DMPParameters.BODY_CODE_TYPE).length + 1;
                sendPacket.dmpBody = strBody;
            }
            ByteBuffer buf = ByteBuffer.allocate(nLength);
            buf.putInt(nLength);
            buf.putInt(nCommand);
            buf.putInt(nDmpStatus);
            
            sendPacket.dmpHeader.command_id = nCommand;
            sendPacket.dmpHeader.command_length = nLength;
            sendPacket.dmpHeader.command_status = nDmpStatus;
            
            // debug using start
            Logs.showTrace("##Request Command## ");
            Logs.showTrace("Command ID: " + String.valueOf(nCommand));
            Logs.showTrace("Command Length: " + String.valueOf(nLength));
            Logs.showTrace("Command Status: " + String.valueOf(nDmpStatus));
            Logs.showTrace("Command Body: " + strBody);
            Logs.showTrace("##Request Command## ");
            // debug using end
            
            if (null != strBody && 0 < strBody.length())
            {
                buf.put(strBody.getBytes(DMPParameters.BODY_CODE_TYPE));
                //maybe wrong 可能會出錯
                // add endChar
                buf.put((byte) 0);
            }
            
            buf.flip();
            // Send Request
            
            outSocket.write(buf.array());
            // debug using
            Logs.showTrace("##Request Command## SUCCESS!!");
            
            buf.clear();
            buf = null;
            
        }
        catch (IOException e)
        {
            // debug using start
            Logs.showTrace("@@Request Command@@ ");
            Logs.showTrace("Command ID: " + String.valueOf(nCommand));
            Logs.showTrace("Command Length: " + String.valueOf(nLength));
            Logs.showTrace("Command Status: " + String.valueOf(nDmpStatus));
            Logs.showTrace("Command Body: " + strBody);
            Logs.showTrace("@@Request Command@@ ");
            // debug using end
            
            Logs.showError(e.toString());
            nDmpStatus = DMPParameters.STATUS_ERR_IO_EXCEPTION;
        }
        
        // debug using start
        Logs.showTrace("### DMP Status" + String.valueOf(nDmpStatus));
        return nDmpStatus;
    }
    
    
    public static int dmpReceive(DMPParameters.DMP_PACKET receivePacket, Socket mSocket)
    {
        int nDmpStatus = DMPParameters.STATUS_SUCCESS;
        
        if (null == receivePacket)
        {
            System.out.println("Parameter CMP_PACKET invalid");
            nDmpStatus = DMPParameters.STATUS_ERR_INVALID_PARAM;
            return nDmpStatus;
        }
        
        if (!validSocket(mSocket))
        {
            nDmpStatus = DMPParameters.STATUS_ERR_SOCKET_INVALID;
            return nDmpStatus;
        }
        
        try
        {
            InputStream inSocket = mSocket.getInputStream();
            
            ByteBuffer buf = ByteBuffer.allocate(DMPParameters.DMP_HEADER_SIZE);
            
            // Receive Response
            int nLength = inSocket.read(buf.array(), 0, DMPParameters.DMP_HEADER_SIZE);
            buf.rewind();
            
            //debug using
            //Logs.showTrace("[Controller]&&&&& BUF"+String.valueOf(buf));
            
            if (DMPParameters.DMP_HEADER_SIZE <= nLength)
            {
                buf.order(ByteOrder.BIG_ENDIAN);
                //Logs.showTrace("buf getString"+ buf);
                receivePacket.dmpHeader.command_length = buf.getInt(0); // offset
                receivePacket.dmpHeader.command_id = buf.getInt(4);
                receivePacket.dmpHeader.command_status = buf.getInt(8);
                
                
                nDmpStatus = receivePacket.dmpHeader.command_status;
                int nBodySize = receivePacket.dmpHeader.command_length - DMPParameters.DMP_HEADER_SIZE;
                
                //debug using
                Logs.showTrace("[Controller] Body Size:" + String.valueOf(nBodySize));
                
                //debug using
                if (0 < nBodySize)
                {
                    int count = 0;
                    buf.clear();
                    buf = ByteBuffer.allocate(nBodySize);
                    
                    while (count < nBodySize)
                    {
                        nLength = inSocket.read(buf.array(), count, nBodySize - count); //have end of char '\0'
                        count += nLength;
                    }
                    
                    if (count == nBodySize)
                    {
                        byte[] bytes = new byte[nBodySize];
                        buf.get(bytes);
                        receivePacket.dmpBody = new String(bytes, Charset.forName(DMPParameters.BODY_CODE_TYPE));
                        Logs.showTrace("&^^^^ body data " + receivePacket.dmpBody);
                    }
                    
                    // debugging use Start
                    Logs.showTrace("@@Response Command@@ ");
                    Logs.showTrace("Command ID: " + String.valueOf(receivePacket.dmpHeader.command_id));
                    Logs.showTrace("Command Length: " + String.valueOf(receivePacket.dmpHeader.command_length));
                    Logs.showTrace("Command Status: " + String.valueOf(receivePacket.dmpHeader.command_status));
                    if (null != receivePacket.dmpBody)
                    {
                        Logs.showTrace("Response Message: " + receivePacket.dmpBody);
                    }
                    Logs.showTrace("@@Response Command@@ ");
                    // debugging use End
                    
                }
                buf.clear();
                
            }
            else
            {
                nDmpStatus = DMPParameters.STATUS_ERR_PACKET_LENGTH;
            }
            buf = null;
        }
        catch (IOException e)
        {
            nDmpStatus = DMPParameters.STATUS_ERR_IO_EXCEPTION;
            Logs.showError(e.toString());
        }
        catch (IndexOutOfBoundsException e)
        {
            nDmpStatus = DMPParameters.STATUS_ERR_PACKET_CONVERT;
            Logs.showError(e.toString());
        }
        catch (Exception e)
        {
            nDmpStatus = DMPParameters.STATUS_ERR_EXCEPTION;
            Logs.showError(e.toString());
            
        }
        
        return nDmpStatus;
        
    }
}
