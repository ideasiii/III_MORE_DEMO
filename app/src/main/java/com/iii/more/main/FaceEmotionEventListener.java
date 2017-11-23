package com.iii.more.main;

import java.util.HashMap;

/**
 * Created by joe on 2017/11/9.
 */

public interface FaceEmotionEventListener
{
    void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String, String> ttsEmotionData,
            HashMap<String, String> imageEmotionData, Object extendData);
    
    void onFaceDetectResult(boolean isDetectFace);
}
