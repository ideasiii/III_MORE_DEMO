package com.iii.more.game.zoo;

import android.os.Handler;
import android.os.Message;

import com.iii.more.main.listeners.FaceEmotionEventListener;

import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/12/6
 */

public class FaceEmotionEventHandler
{
    private Handler handlerScenarize = null;
    
    public FaceEmotionEventHandler(Handler handler)
    {
        handlerScenarize = handler;
    }
    
    public FaceEmotionEventListener getFaceEmotionEventListener()
    {
        return faceEmotionEventListener;
    }
    
    private FaceEmotionEventListener faceEmotionEventListener = new FaceEmotionEventListener()
    {
        
        @Override
        public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String,
            String> ttsEmotionData, HashMap<String, String> imageEmotionData, Object extendData)
        {
            JSONObject jsonRoot = new JSONObject();
            try
            {
                if (null != faceEmotionData)
                {
                    jsonRoot.put("EMOTION_NAME", faceEmotionData.get("EMOTION_NAME"));
                    
                    if (null != ttsEmotionData)
                    {
                        jsonRoot.put("TTS_SPEED", ttsEmotionData.get("TTS_SPEED"));
                        jsonRoot.put("TTS_PITCH", ttsEmotionData.get("TTS_PITCH"));
                        jsonRoot.put("TTS_TEXT", ttsEmotionData.get("TTS_TEXT"));
                    }
                    
                    if (null != imageEmotionData)
                    {
                        jsonRoot.put("IMG_FILE_NAME", imageEmotionData.get("IMG_FILE_NAME"));
                    }
                    
                    Logs.showTrace("[ZooActivity] onFaceEmotionResult EMOTION_NAME:" + jsonRoot
                        .toString());
                }
            }
            catch (Exception e)
            {
                Logs.showError("[FaceEmotionEventHandler] onFaceEmotionResult Exception:" + e
                    .getMessage());
            }
            
            Message message = new Message();
            message.what = SCEN.SENSOR_FACE_EMOTION;
            message.obj = jsonRoot.toString();
           // handlerScenarize.sendMessage(message);
        }
        
        @Override
        public void onFaceDetectResult(boolean isDetectFace)
        {
        
        }
    };
}
