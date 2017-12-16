package com.iii.more.setting.Api;

import okhttp3.FormBody;
import okhttp3.Headers;

/**
 * TODO: 此頁說明
 *
 * @author ReadyChen
 */

public class Table {

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//  internet define

    public static final String mServer = "https://www.more.org.tw/api/";

    public static final int NOT_VALID = -1000;
    public static final int HTTP_EXCEPTION = -2000;
    public static final int HTTP_SUCCESS = 200;

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//  for server parameter

    public static String device_id = "readytest999";


// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//  for api header request response

    private static Headers headers;
    public static Headers getHeader(){

        if( headers == null ) {
            Headers.Builder builder = new Headers.Builder();
            builder.add("content-type", "application/x-www-form-urlencoded");
            headers = builder.build();
        }
        return headers;
    }

    public static class Request{

        public int api_id = 0;
        public String function_path;
        public FormBody formBody;

        public Request(){

        }
        public Request(int api_id, String function_path){
            this.api_id = api_id;
            this.function_path = function_path;
        }
    }

    public static class Response {

        public int api_id;
        public int httpCode = NOT_VALID;
        public String httpBody = "";

        public Response(int value){
            api_id = value;
        }
    }

// = = = = = = = = = = = = = = = = = = = = = = = = = = = =
//  for api id , api function path

    public static final int device_info_id = 1001;
    public static final String device_info_url = "device/info.jsp";

    public static final int device_create_id = 1002;
    public static final String device_create_url = "device/create.jsp";

    public static final int setting_reset_id = 2002;
    public static final String setting_reset_url = "setting/reset.jsp";

    public static final int setting_lowpower_id = 2003;
    public static final String setting_lowpower_url = "setting/low-power.jsp";

    public static final int setting_option_lowpower_id = 2004;
    public static final String setting_option_lowpower_url = "setting/option/low-power.jsp";

    public static final int setting_language_id = 2005;
    public static final String setting_language_url = "setting/language.jsp";

    public static final int setting_option_language_id = 2006;
    public static final String setting_option_language_url = "setting/option/language.jsp";

}
