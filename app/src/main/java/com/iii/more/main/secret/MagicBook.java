package com.iii.more.main.secret;

import android.app.Activity;
import android.content.Intent;
import android.util.ArrayMap;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;

import com.iii.more.emotion.EmotionParameters;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptHandler;
import com.iii.more.emotion.interrupt.FaceEmotionInterruptParameters;
import com.iii.more.main.MainActivity;
import com.iii.more.main.MainApplication;
import com.iii.more.oobe.OobeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sdk.ideas.common.Logs;

/**
 * 小秘方
 */
public class MagicBook
{
    private static final String LOG_TAG = "MagicBook";

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
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_ENGAGEMENT, "-100");
        simFaceEmotionEventBase.put(EmotionParameters.STRING_EMOTION_VALENCE, "-100");
        simFaceEmotionEventBase.put("This is", "fake");
    }

    /**
     * 根據 emotionName 從 emotionHandler 產生一個辨識到臉部情緒時的模擬訊息
     */
    public static HashMap<String, String> cookFaceEmotionDetectedEvent(
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
                message.put(FaceEmotionInterruptParameters.STRING_EMOTION_NAME, emotionName);
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

    public static void jumpToActivity(String from, String to)
    {
        try
        {
            Class fromClazz = Class.forName(from);
            Class toClazz = Class.forName(to);

            if (!Activity.class.isAssignableFrom(fromClazz))
            {
                Log.d(LOG_TAG, "jumpToActivity() 'from' (" + to + ") is not an Activity");
                return;

            }
            else if (!Activity.class.isAssignableFrom(toClazz))
            {
                Log.d(LOG_TAG, "jumpToActivity() 'to' (" + to + ") is not an Activity");
                return;
            }

            jumpToActivity(fromClazz, toClazz);
        }
        catch (ClassNotFoundException e)
        {
            Log.d(LOG_TAG, "jumpToActivity() class for name not found (from: " + from + ", to: " + to);
        }
    }
    /**
     * start Activity 'to' only if the top activity in back stack is 'from'
     */
    public static void jumpToActivity(Class<? extends Activity> from, Class<? extends Activity> to)
    {
        Activity now = getActivity();
        if (now == null)
        {
            Log.d(LOG_TAG, "jumpToActivity() now == null");
            return;
        }
        else if (now.getClass() != from)
        {
            Log.d(LOG_TAG, "jumpToActivity() class of now (" + now.getClass().getName()
                + ")is not equal to from (" + from.getName() + ")");
            return;
        }

        if (from == OobeActivity.class && to == MainActivity.class)
        {
            Log.w(LOG_TAG, "jumpToActivity() get ready to jump from " + from + " to " + to);
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startMain.setClass(now, MainActivity.class);
            now.startActivity(startMain);
            now.finish();
        }
        else
        {
            Log.d(LOG_TAG, "jumpToActivity() have no idea how to jump from " + from + " to " + to);
        }
    }

    public static Activity getActivity()
    {
        try
        {
            Class clazz = Class.forName("android.app.ActivityThread");
            Object obj = clazz.getMethod("currentActivityThread").invoke(null);
            Field field = clazz.getDeclaredField("mActivities");
            field.setAccessible(true);

            Object activitiesObj = field.get(obj);
            if (activitiesObj == null || !(activitiesObj instanceof Map))
            {
                Log.d(LOG_TAG, "getTopActivity() cannot get expected activities map");
                return null;
            }

            Map activities =  (Map)activitiesObj;
            for (Object activityRecord : activities.values())
            {
                Class activityRecordClazz = activityRecord.getClass();
                Field pausedField = activityRecordClazz.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord))
                {
                    Field activityField = activityRecordClazz.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }

            return null;
        }
        catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private MagicBook()
    {

    }
}
