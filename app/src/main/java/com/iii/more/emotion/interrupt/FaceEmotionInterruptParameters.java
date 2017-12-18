package com.iii.more.emotion.interrupt;

/**
 * Created by joe on 2017/11/13.
 */

public abstract class FaceEmotionInterruptParameters
{
    public static final int CLASS_FACE_EMOTION_INTERRUPT = 4584;
    public static final int METHOD_RULE = 0;
    public static final int METHOD_EVENT = 1;
    public static final int METHOD_RECORD = 2;
    
    public static final String JSON_STRING_EMOTION_ID = "emotion_id";
    public static final String JSON_STRING_EMOTION_MAPPING_IMAGE_NAME = "img_name";
    public static final String JSON_STRING_EMOTION_NAME = "emotion_name";
    public static final String JSON_STRING_ID = "id";
    public static final String JSON_STRING_DATA_TYPE = "data_type";
    
    public static final String JSON_STRING_TRIGGER_TIME = "trigger_time";
    public static final String JSON_STRING_TRIGGER_VALUE = "trigger_value";
    public static final String JSON_STRING_CONTENTS = "contents";
    public static final String JSON_STRING_EMOTION_TYPE = "emotion_type";
    public static final String JSON_STRING_PRIORITY = "priority";
    
    public static final String STRING_IMG_FILE_NAME = "IMG_FILE_NAME";
    public static final String STRING_TTS_TEXT = "TTS_TEXT";
    public static final String STRING_TTS_PITCH = "TTS_PITCH";
    public static final String STRING_TTS_SPEED = "TTS_SPEED";
    
    public static final String STRING_EMOTION_NAME = "EMOTION_NAME";
    
    public static final String STRING_NATURAL = "NATURAL";
    public static final int INT_NATURAL_RULE = 999;
    
    public  static final boolean isDebugging = true;
    public static final int attentionTriggerTime = 120;
    
}
