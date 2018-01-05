package com.iii.more.game.zoo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iii.more.game.module.Utility;
import com.iii.more.main.listeners.FaceEmotionEventListener;

import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;

/**
 * Created by jugo on 2017/12/6
 */

public class FaceEmotionEventHandler
{
    private Context theContext = null;
    private Handler handlerScenarize = null;
    JSONObject jsonRoot = null;
    
    public FaceEmotionEventHandler(Context context, Handler handler)
    {
        theContext = context;
        handlerScenarize = handler;
        jsonRoot = new JSONObject();
        try
        {
            jsonRoot.put("IMG_FILE_RES_ID", Utility.getResourceId(theContext, "octobo16",
                "drawable"));
            jsonRoot.put("EMOTION_NAME", "ATTENTION");
            jsonRoot.put("TTS_TEXT", "你很專心喔");
            jsonRoot.put("TTS_SPEED", "1.0");
            jsonRoot.put("TTS_PITCH", "1.0");
        }
        catch (Exception e)
        {
            Logs.showTrace("Exception " + e.getMessage());
        }
        
    }
    
    public FaceEmotionEventListener getFaceEmotionEventListener()
    {
        return faceEmotionEventListener;
    }
    
    public JSONObject getEmotion()
    {
        return jsonRoot;
    }
    
    private FaceEmotionEventListener faceEmotionEventListener = new FaceEmotionEventListener()
    {
        String strImage = null;
        
        @Override
        public void onFaceEmotionResult(HashMap<String, String> faceEmotionData, HashMap<String,
            String> ttsEmotionData, HashMap<String, String> imageEmotionData, Object extendData)
        {
            try
            {
                if (null != faceEmotionData)
                {
                    jsonRoot.put("EMOTION_NAME", faceEmotionData.get("EMOTION_NAME"));
                    jsonRoot.put("EMOTION_TIME", System.currentTimeMillis());
                    
                    if (null != ttsEmotionData)
                    {
                        jsonRoot.put("TTS_SPEED", ttsEmotionData.get("TTS_SPEED"));
                        jsonRoot.put("TTS_PITCH", ttsEmotionData.get("TTS_PITCH"));
                        jsonRoot.put("TTS_TEXT", ttsEmotionData.get("TTS_TEXT"));
                    }
                    else
                    {
                        if (0 == faceEmotionData.get("EMOTION_NAME").compareTo("ATTENTION"))
                        {
                            // 幹! 不會來點表情喔
                            jsonRoot.put("TTS_TEXT", "你很不專心喔");
                        }
                        jsonRoot.put("TTS_SPEED", "1.0");
                        jsonRoot.put("TTS_PITCH", "1.0");
                    }
                    
                    if (null != imageEmotionData)
                    {
                        jsonRoot.put("IMG_FILE_NAME", imageEmotionData.get("IMG_FILE_NAME"));
//                        strImage = imageEmotionData.get("IMG_FILE_NAME").substring
//                            (imageEmotionData.get("IMG_FILE_NAME").indexOf('-') + 1
//                                , imageEmotionData.get("IMG_FILE_NAME").lastIndexOf('.'));
                        strImage = "octobo" + imageEmotionData.get("IMG_FILE_NAME").substring
                            (imageEmotionData.get("IMG_FILE_NAME").indexOf('-') + 1,
                                imageEmotionData.get("IMG_FILE_NAME").lastIndexOf('.'));
                        int nId = Utility.getResourceId(theContext, strImage, "drawable");
                        Logs.showTrace("[ZooActivity] onFaceEmotionResult image: " + strImage +
                            " resource id" + nId);
                        jsonRoot.put("IMG_FILE_RES_ID", nId);
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
