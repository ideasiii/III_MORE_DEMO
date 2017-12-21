package com.cyberon.utility;

public interface ICReaderListener
{
    public void onCReaderStatusChanged( int nStatus );

    public void onCReaderPlayText(String text);

    public void onCReaderSynthesizeData(byte[] lpData, int nType);
}

