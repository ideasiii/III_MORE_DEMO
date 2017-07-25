package com.iii.more.sensor;

import sdk.ideas.common.BaseHandler;
import sdk.ideas.common.ListenReceiverAction;
import sdk.ideas.common.Logs;
import sdk.ideas.common.ResponseCode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joe on 2017/7/21.
 */

public class SensorHandler extends BaseHandler implements SensorEventListener, ListenReceiverAction
{
    private SensorManager mSensorManager = null;
    private ArrayList<Sensor> mSensors = null;
    private boolean isListening = false;
    
    public SensorHandler(@NonNull Context context)
    {
        super(context);
    }
    
    
    public void init(@NonNull ArrayList<Integer> sensorTypes)
    {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mSensors = new ArrayList<>();
        for (int i = 0; i < sensorTypes.size(); i++)
        {
            Sensor m = null;
            switch (sensorTypes.get(i))
            {
                case SensorParameters.TYPE_PROXIMITY:
                    m = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    break;
                
                case SensorParameters.TYPE_LIGHT:
                    m = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                    break;
                
                
                default:
                    break;
                
            }
            if (null == m)
            {
                Logs.showError("ERROR Sensor");
            }
            else
            {
                mSensors.add(m);
            }
            
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        HashMap<String, String> message = new HashMap<>();
        
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_LIGHT:
                Logs.showTrace("[SensorHandler] sensor TYPE_LIGHT: " + String.valueOf(event.values[0]));
                message.put("type", String.valueOf(SensorParameters.TYPE_LIGHT));
                message.put("value", String.valueOf(event.values[0]));
                break;
            case Sensor.TYPE_PROXIMITY:
                Logs.showTrace("[SensorHandler] sensor TYPE_PROXIMITY: " + String.valueOf(event.values[0]));
                message.put("type", String.valueOf(SensorParameters.TYPE_PROXIMITY));
                message.put("value", String.valueOf(event.values[0]));
                break;
            default:
                
                break;
        }
        callBackMessage(ResponseCode.ERR_SUCCESS, SensorParameters.CLASS_SENSOR, SensorParameters.METHOD_SENSOR_CHANGE, message);
        
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        
    }
    
    @Override
    public void startListenAction()
    {
        if (!isListening && null != mSensorManager && null != mSensors)
        {
            for (int i = 0; i < mSensors.size(); i++)
            {
                mSensorManager.registerListener(this, mSensors.get(i), SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
        
    }
    
    @Override
    public void stopListenAction()
    {
        if (isListening && null != mSensorManager)
        {
            mSensorManager.unregisterListener(this);
        }
    }
}
