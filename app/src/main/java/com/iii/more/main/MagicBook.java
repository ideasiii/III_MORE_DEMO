package com.iii.more.main;

import com.iii.more.emotion.EmotionParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptHandler;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 小秘方
 */
class MagicBook
{
    // cookFaceEmotionDetectedEvent() 所回傳內容的基礎
    private static final HashMap<String, String> simFaceEmotionEventBase = new HashMap<>();
    private static final Random random = new Random();

    static
    {
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_ANGER, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_DISGUST, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_FEAR, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_JOY, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_SADNESS, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_SURPRISE, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_CONTEMPT, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EXPRESSION_ATTENTION, "-100");
        simFaceEmotionEventBase.put("This is", "fake");
    }

    /**
     * 根據 emotionName 從 emotionHandler 產生一個辨識到臉部情緒時的模擬訊息
     */
    static HashMap<String, String> cookFaceEmotionDetectedEvent(
            FaceEmotionInterruptHandler emotionHandler, String emotionName)
    {
        try
        {
            Method method = FaceEmotionInterruptHandler.class.getDeclaredMethod(
                            "getEmotionBrainElementByEmotionName", String.class);
            method.setAccessible(true);
            Object emotionBrainElement = method.invoke(emotionHandler, emotionName);

            if (emotionBrainElement != null)
            {
                Class<?> clazz = Class.forName("com.iii.more.emotion.interrupt.FaceEmotionInterruptHandler$EmotionBrainElement");
                Field imageNameField = clazz.getField("emotionMappingImageName");
                Field ttsField = clazz.getField("emotionMappingTTS");

                String emotionMappingImageName = (String) imageNameField.get(emotionBrainElement);
                List<JSONObject> emotionMappingTTS = (List<JSONObject>) ttsField.get(emotionBrainElement);

                HashMap<String, String> message = new HashMap<>(simFaceEmotionEventBase);
                message.put(FaceEmotionInterruptParameters.STRING_IMG_FILE_NAME,
                        emotionMappingImageName);

                if (emotionMappingTTS.size() > 0)
                {
                    int pickedTextIndex = random.nextInt(emotionMappingTTS.size());

                    JSONObject ttsData = emotionMappingTTS.get(pickedTextIndex);
                    message.put(FaceEmotionInterruptParameters.STRING_TTS_TEXT, ttsData
                            .getString("tts"));
                    message.put(FaceEmotionInterruptParameters.STRING_TTS_PITCH, ttsData
                            .getString("pitch"));
                    message.put(FaceEmotionInterruptParameters.STRING_TTS_SPEED, ttsData
                            .getString("speed"));
                }

                return message;
            }
        }
        catch (ReflectiveOperationException | JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private MagicBook()
    {

    }
}
