package com.iii.more.emotion;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.edgecase.moduleservice.OnDetectionListener;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

import com.edgecase.moduleservice.DetectorService;

/**
 * Created by joe on 2017/10/18.
 */

/**
 * ### is mean need to add code
 */

public class EmotionHandler extends BaseHandler implements OnDetectionListener
{
    
    private ArrayDeque<HashMap<String, String>> emotionCallBackQueue = null;
    private Thread handleEmotionDataThread = null;
    private volatile boolean isCloseThread = true;
    DetectorService mDetectorService = null;
    private boolean mBound = false;
    private volatile HashMap<String, String> emotionVolatileHashMap = null;
    private boolean isTest = true;
    
    
    public EmotionHandler(Context context)
    {
        super(context);
    }
    
    
    private ServiceConnection mConnection = new ServiceConnection()
    {
        
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            DetectorService.MyBinder binder = (DetectorService.MyBinder) iBinder;
            mDetectorService = binder.getService();
            mDetectorService.setEnableLog(false);
            mDetectorService.setOnEventListener(EmotionHandler.this);
            mBound = true;
        }
        
        @Override
        public void onServiceDisconnected(ComponentName arg0)
        {
            mBound = false;
        }
    };
    
    public void init()
    {
        emotionCallBackQueue = new ArrayDeque<HashMap<String, String>>();
        handleEmotionDataThread = new Thread(new ReadEmotionQueueRunnable(EmotionParameters.ONE_TIME_COUNT,
                EmotionParameters.THREAD_SLEEP_TIME));
        //###need to add ready code new constructor
        
    }
    
    public void start()
    {
        if (isCloseThread == true)
        {
            
            handleEmotionDataThread.start();
            isCloseThread = false;
            //###need to add ready code startService
            Intent intent = new Intent(this.mContext, DetectorService.class);
            boolean bRet = mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }
    
    public void stop()
    {
        if (!isCloseThread)
        {
            isCloseThread = true;
            
            //###need to add ready code stopService
            if (mBound)
            {
                mContext.unbindService(mConnection);
                mBound = false;
            }
        }
    }
    
    
    private void pushBackQueue(HashMap<String, String> emotionData)
    {
        emotionCallBackQueue.push(emotionData);
    }
    
    @Override
    public void onFaceDetected(boolean bDetected)
    {
        HashMap<String, String> message = new HashMap<>();
        if (bDetected)
        {
            Logs.showTrace("onFaceDetected have Face now detect emotion...");
            
            message.put("message", "detect face");
            callBackMessage(ResponseCode.ERR_SUCCESS, EmotionParameters.CLASS_EMOTION, EmotionParameters.METHOD_FACE_DETECT, message);
            
        }
        else
        {
            Logs.showTrace("onFaceDetected no Face detect....");
            emotionVolatileHashMap = null;
            message.put("message", "detect no face");
            callBackMessage(ResponseCode.ERR_SUCCESS, EmotionParameters.CLASS_EMOTION, EmotionParameters.METHOD_NO_FACE_DETECT, message);
            
        }
    }
    
    @Override
    public void onImageResults(List<Face> faces, Frame image, float timestamp)
    {
        if (faces == null || faces.size() == 0)
        {
            return; //frame was not processed or no face found
        }
        
        Face face = null;
        //For each face found
        for (int i = 0; i < faces.size(); i++)
        {
            face = faces.get(i);
            break;
            
            /* get face id
            int faceId = face.getId();
            */
            /*Appearance
            Face.GLASSES glassesValue = face.appearance.getGlasses();
            Face.GENDER genderValue = face.appearance.getGender();
            Face.AGE ageValue = face.appearance.getAge();
            Face.ETHNICITY ethnicityValue = face.appearance.getEthnicity();
            
            String textValue = "";
            switch (glassesValue)
            {
                case NO:
                    textValue = "no";
                    break;
                case YES:
                    textValue = "yes";
                    break;
            }
            switch (genderValue)
            {
                case UNKNOWN:
                    textValue = textValue + " unknown";
                    break;
                case FEMALE:
                    textValue = textValue + " female";
                    break;
                case MALE:
                    textValue = textValue + " male";
                    break;
            }
            switch (ageValue)
            {
                case AGE_UNKNOWN:
                    textValue = textValue + " unknown";
                    break;
                case AGE_UNDER_18:
                    textValue = textValue + " under 18";
                    break;
                case AGE_18_24:
                    textValue = textValue + " 18-24";
                    break;
                case AGE_25_34:
                    textValue = textValue + " 25-34";
                    break;
                case AGE_35_44:
                    textValue = textValue + " 35-44";
                    break;
                case AGE_45_54:
                    textValue = textValue + " 45-54";
                    break;
                case AGE_55_64:
                    textValue = textValue + " 55-64";
                    break;
                case AGE_65_PLUS:
                    textValue = textValue + " 65+";
                    break;
            }
            switch (ethnicityValue)
            {
                case UNKNOWN:
                    textValue = textValue + " unknown";
                    break;
                case CAUCASIAN:
                    textValue = textValue + " caucasian";
                    break;
                case BLACK_AFRICAN:
                    textValue = textValue + " black african";
                    break;
                case EAST_ASIAN:
                    textValue = textValue + " east asian";
                    break;
                case SOUTH_ASIAN:
                    textValue = textValue + " south asian";
                    break;
                case HISPANIC:
                    textValue = textValue + " hispanic";
                    break;
            }
             Log.d(TAG, textValue);
            */
            
           
            
            /*Some Emoji
            float smiley = face.emojis.getSmiley();
            float laughing = face.emojis.getLaughing();
            float wink = face.emojis.getWink();
            */
           
            
            /*Some Expressions
            float smile = face.expressions.getSmile();
            float brow_furrow = face.expressions.getBrowFurrow();
            float brow_raise = face.expressions.getBrowRaise();
            */
            /*Measurements
            float interocular_distance = face.measurements.getInterocularDistance();
            float yaw = face.measurements.orientation.getYaw();
            float roll = face.measurements.orientation.getRoll();
            float pitch = face.measurements.orientation.getPitch();
            */
            /*Face feature points coordinates
            PointF[] points = face.getFacePoints();
            */
        }
        
        //Some Emotions
        if (null != face)
        {
            HashMap<String, String> faceHashMap = new HashMap<>();
            
            faceHashMap.put(EmotionParameters.STRING_EMOTION_ANGER, String.valueOf(face.emotions.getAnger()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_CONTEMPT, String.valueOf(face.emotions.getContempt()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_DISGUST, String.valueOf(face.emotions.getDisgust()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_FEAR, String.valueOf(face.emotions.getFear()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_JOY, String.valueOf(face.emotions.getJoy()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_SADNESS, String.valueOf(face.emotions.getSadness()));
            faceHashMap.put(EmotionParameters.STRING_EMOTION_SURPRISE, String.valueOf(face.emotions.getSurprise()));
            faceHashMap.put(EmotionParameters.STRING_EXPRESSION_ATTENTION, String.valueOf(face.expressions.getAttention()));
            
            Logs.showTrace("[EmotionHandler] get face Emotion:" + faceHashMap);
            
            if (isTest == true)
            {
                emotionVolatileHashMap = faceHashMap;
            }
            else
            {
                pushBackQueue(faceHashMap);
            }
        }
    }
    
    
    private class ReadEmotionQueueRunnable implements Runnable
    {
        private int pollCounts = -1;
        private int sleepSecond = -1;
        
        
        public ReadEmotionQueueRunnable(int pollCounts, int sleepSecond)
        {
            this.sleepSecond = sleepSecond;
            this.pollCounts = pollCounts;
            
        }
        
        @Override
        public void run()
        {
            while (!isCloseThread)
            {
                try
                {
                    if (isTest == false)
                    {
                        if (null != emotionCallBackQueue)
                        {
                            ArrayList<HashMap<String, String>> emotionArrayListData = new ArrayList<>();
                            for (int i = 0; i < pollCounts; i++)
                            {
                                if (emotionCallBackQueue.size() > 0)
                                {
                                    emotionArrayListData.add(emotionCallBackQueue.poll());
                                }
                            }
                            
                            //### get last one to show this person emotion
                            if (emotionArrayListData.size() > 0)
                            {
                                HashMap<String, String> message = emotionArrayListData.get(emotionArrayListData.size() - 1);
                                callBackMessage(ResponseCode.ERR_SUCCESS, EmotionParameters.CLASS_EMOTION, EmotionParameters.METHOD_EMOTION_DETECT, message);
                            }
                        }
                    }
                    else
                    {
                        if (null != emotionVolatileHashMap)
                        {
                            HashMap<String, String> message = new HashMap<>(emotionVolatileHashMap);
                            callBackMessage(ResponseCode.ERR_SUCCESS, EmotionParameters.CLASS_EMOTION, EmotionParameters.METHOD_EMOTION_DETECT, message);
                        }
                    }
                    
                    
                    Thread.sleep(sleepSecond);
                }
                catch (InterruptedException e)
                {
                    Logs.showTrace("[EmotionHandler] Queue Thread stop!");
                }
                catch (Exception e)
                {
                    Logs.showError("[EmotionHandler] some ERROR in Queue Thread: " + e.toString());
                }
                
                
            }
            
            
        }
    }
    
    
}
