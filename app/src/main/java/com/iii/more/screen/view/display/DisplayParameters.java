package com.iii.more.screen.view.display;

import android.graphics.Color;
import android.os.Environment;

import com.iii.more.main.R;

/**
 * Created by joe on 2017/7/12.
 */

public class DisplayParameters
{
    public static final int CLASS_DISPLAY = 4557;
    public static final int METHOD_CLICK = 0;
    
    
    public static final int RELATIVE_LAYOUT_ID = 0;
    public static final int IMAGE_VIEW_ID = 1;
    public static final int TEXT_VIEW_ID = 2;
    public static final int RESULT_TEXT_VIEW_ID = 3;
    
    
    public static final int DEFAULT_BACKGROUND_COLOR = R.color.default_app_color;
    
    
    public static final String STRING_JSON_KEY_TIME = "time";
    public static final String STRING_JSON_KEY_HOST = "host";
    public static final String STRING_JSON_KEY_FILE = "file";
    public static final String STRING_JSON_KEY_COLOR = "color";
    public static final String STRING_JSON_KEY_ANIMATION = "animation";
    public static final String STRING_JSON_KEY_TEXT = "text";
    public static final String STRING_JSON_KEY_DESCRIPTION = "description";
    public static final String STRING_JSON_KEY_ENABLE = "enable";
    public static final String STRING_JSON_KEY_SHOW = "show";
    
    public static final String STRING_PATH_IMAGE = Environment.getExternalStorageDirectory() + "/more/image/";
    
    
    public static final String JSON_DISPLAY_STORY_DEFAULT = "{\"enable\":1,\"show\":[{\"host\":\"https" +
        ":\\/\\/ryejuice.sytes.net\\/edubot\\/OCTOBO_Expressions\\/\",\"color\":\"#6d94d5\"," +
        "\"animation\":{\"type\":5,\"duration\":1000,\"repeat\":1,\"interpolate\":1},\"text\":{\"type\":0}," +
        "\"description\":\"default\",\"time\":0,\"file\":\"g_o_speak.gif\"}," +
        "{\"host\":\"https:\\/\\/ryejuice.sytes.net\\/edubot\\/OCTOBO_Expressions\\/\"," +
        "\"color\":\"#6d94d5\",\"animation\":{\"type\":5,\"duration\":1000,\"repeat\":1," +
        "\"interpolate\":1},\"text\":{\"type\":0},\"description\":\"default\",\"time\":900000," +
        "\"file\":\"g_o_speak.gif\"}]}";
    
    
}
