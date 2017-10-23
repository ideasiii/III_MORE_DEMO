package com.edgecase.moduleservice;

import android.util.Log;

public class LOG {

    final static private boolean bShowClassName = true;
    final static private boolean bShowMethodName = true;
    final static private boolean bShowLineNumberName = true;

    public static void d(String tag, Object obj2)
    {
        if(DetectorService.enableLog)
        {
            String strOut = "";
            if( bShowMethodName ) {
                strOut = getLogTagWithMethod()+ String.valueOf(obj2);
            } else {
                strOut = String.valueOf(obj2);
            }
            Log.d(saveLength(tag), strOut);
        }
    }
    public static void e(String tag, Object obj2)
    {
        if(DetectorService.enableLog)
        {
            String strOut = "";
            if( bShowMethodName ) {
                strOut = getLogTagWithMethod()+ String.valueOf(obj2);
            } else {
                strOut = String.valueOf(obj2);
            }
            Log.e(saveLength(tag), strOut);
        }
    }
    public static void i(String tag, Object obj2)
    {
        if(DetectorService.enableLog)
        {
            String strOut = "";
            if( bShowMethodName ) {
                strOut = getLogTagWithMethod()+ String.valueOf(obj2);
            } else {
                strOut = String.valueOf(obj2);
            }
            Log.i(saveLength(tag), strOut);
        }
    }
    public static void v(String tag, Object obj2)
    {
        if(DetectorService.enableLog)
        {
            String strOut = "";
            if( bShowMethodName ) {
                strOut = getLogTagWithMethod()+ String.valueOf(obj2);
            } else {
                strOut = String.valueOf(obj2);
            }
            Log.v(saveLength(tag), strOut);
        }
    }
    public static void w(String tag, Object obj2)
    {
        if(DetectorService.enableLog)
        {
            String strOut = "";
            if( bShowMethodName ) {
                strOut = getLogTagWithMethod()+ String.valueOf(obj2);
            } else {
                strOut = String.valueOf(obj2);
            }
            Log.w(saveLength(tag), strOut);
        }
    }

    static private String saveLength(String tag){
        if( tag != null ) {
            if( tag.length() > 23 ) {
                tag = tag.substring(0, 23);
            }
        } else {
            tag = "";
        }
        return tag;
    }

    static private String getLogTagWithMethod() {
        Throwable stack = new Throwable().fillInStackTrace();
        StackTraceElement[] trace = stack.getStackTrace();
        //String strOut = trace[2].getClassName() + "." + trace[2].getMethodName() + ":" + trace[2].getLineNumber() + ":";
        //String strOut = trace[2].getClass().getSimpleName() + "." + trace[2].getMethodName() + ":" + trace[2].getLineNumber() + ":";
        String strOut = trace[2].getMethodName() + ": " + trace[2].getLineNumber() + ": ";
        return strOut;
    }
}
