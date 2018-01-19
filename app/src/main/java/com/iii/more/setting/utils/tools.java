package com.iii.more.setting.utils;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class tools {

    public static boolean[] recurConvertAsc(String value) {
        boolean[] bRet = new boolean[7];
        String[] strArr = value.split(",");
        for (int i = 0; i < strArr.length; i++) {
            int iRet = getDecimal(strArr[i]);
            bRet[iRet] = true;
        }
        return bRet;
    }

    public static String recurConvertDesc(boolean[] recur) {
        String strRecur = "";
        for (int i = 0; i < recur.length; i++) {
            if( recur[i] ) {
                strRecur = strRecur + getChinese(i) + ",";
            }
        }
        if( strRecur.length() > 0 ) {
            strRecur = strRecur.substring(0, strRecur.length() - 1);
        }
        return strRecur;
    }

    private static int getDecimal(String value) {
        int iRet = 0;
        if (value.equals("日")) {
            iRet = 0;
        }
        if (value.equals("一")) {
            iRet = 1;
        }
        if (value.equals("二")) {
            iRet = 2;
        }
        if (value.equals("三")) {
            iRet = 3;
        }
        if (value.equals("四")) {
            iRet = 4;
        }
        if (value.equals("五")) {
            iRet = 5;
        }
        if (value.equals("六")) {
            iRet = 6;
        }
        return iRet;
    }

    private static String getChinese(int value) {
        String strRet = "";
        if (value == 0) {
            strRet = "日";
        }
        if (value == 1) {
            strRet = "一";
        }
        if (value == 2) {
            strRet = "二";
        }
        if (value == 3) {
            strRet = "三";
        }
        if (value == 4) {
            strRet = "四";
        }
        if (value == 5) {
            strRet = "五";
        }
        if (value == 6) {
            strRet = "六";
        }
        return strRet;
    }
}
