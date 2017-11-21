package com.iii.more.main;

import java.util.HashMap;

/**
 * Created by joe on 2017/11/9.
 */

public interface FaceEmotionEventListener
{
    void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String, String> tts,
            HashMap<String, String> image, Object extendData);
    
    void onFaceDetectResult(boolean isDetectFace);
}
