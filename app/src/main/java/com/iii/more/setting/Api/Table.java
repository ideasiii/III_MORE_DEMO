package com.iii.more.setting.Api;

import android.util.Log;

import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.Headers;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class Table {

    private static final String TAG = Table.class.getSimpleName();

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//<editor-fold desc="internet define">

    public static final String mServer = "https://www.more.org.tw/api/";

    public static final int NOT_VALID = -1000;
    public static final int HTTP_EXCEPTION = -2000;
    public static final int HTTP_SUCCESS = 200;

//</editor-fold>

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//<editor-fold desc="for server parameter">

    public static String device_id = "readytest999";

    private static HashMap<String, String> errorMap;

    private static String getErrorDescript(String errorType) {

        if (errorMap == null) {
            generateErrorMap();
        }

        String functionPath = errorMap.get(errorType);
        if (functionPath == null) {
            Log.e(TAG, "errorType not exist in errorMap");
        }

        return functionPath;
    }

    private static void generateErrorMap() {
        errorMap = new HashMap<>();
        errorMap.put("ER0100", "找不到指定的資料");
        errorMap.put("ER0120", "缺少必要參數");
        errorMap.put("ER0200", "輸入的參數名稱不在規範中");
        errorMap.put("ER0210", "輸入的參數內容格式錯誤");
        errorMap.put("ER0220", "輸入的參數內容不在規範中");
        errorMap.put("ER0230", "輸入的參數內容中，欄位名稱不存在");
        errorMap.put("ER0240", "輸入的參數內容抵觸");
        errorMap.put("ER0500", "系統錯誤");
    }

//</editor-fold>

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//<editor-fold desc="for api header request response">

    private static Headers headers;

    public static Headers getHeader() {

        if (headers == null) {
            Headers.Builder builder = new Headers.Builder();
            builder.add("content-type", "application/x-www-form-urlencoded");
            headers = builder.build();
        }
        return headers;
    }

    public static class Request {

        public int function_id = 0;
        public FormBody formBody;

        public Request(int function_id) {
            this.function_id = function_id;
        }

        public String getPath() {
            return getFunctionPath(function_id);
        }
    }

    public static class Response {

        public int function_id;
        public int httpCode = NOT_VALID;
        public String httpBody = "";

        public Response(int value) {
            function_id = value;
        }

        public String getPath() {
            return getFunctionPath(function_id);
        }
        public String getErrorDescription(String errorType) {
            return getErrorDescript(errorType);
        }
    }

//</editor-fold>

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//<editor-fold desc="for api id , api function path">

    public static final int device_info_id = 1001;
    public static final int device_create_id = 1002;
    public static final int setting_reset_id = 2002;
    public static final int setting_lowpower_id = 2003;
    public static final int setting_option_lowpower_id = 2004;
    public static final int setting_language_id = 2005;
    public static final int setting_option_language_id = 2006;

    private static HashMap<Integer, String> functionMap;

    private static String getFunctionPath(int functionId) {

        if (functionMap == null) {
            generateFunctionPath();
        }

        String functionPath = functionMap.get(functionId);
        if (functionPath == null) {
            Log.e(TAG, "functionId not exist in functionMap");
        }

        return functionPath;
    }

    private static void generateFunctionPath() {
        functionMap = new HashMap<>();
        functionMap.put(device_info_id, "device/info.jsp");
        functionMap.put(device_create_id, "device/create.jsp");
        functionMap.put(setting_reset_id, "setting/reset.jsp");
        functionMap.put(setting_lowpower_id, "setting/low-power.jsp");
        functionMap.put(setting_option_lowpower_id, "setting/option/low-power.jsp");
        functionMap.put(setting_language_id, "setting/language.jsp");
        functionMap.put(setting_option_language_id, "setting/option/language.jsp");
    }

//</editor-fold>

}
