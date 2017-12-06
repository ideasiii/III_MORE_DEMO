package com.iii.more.game.zoo;

import com.iii.more.main.listeners.CockpitSensorEventListener;

import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

import static com.iii.more.game.zoo.ZooActivity.trackerHandler;

/**
 * Created by jugo on 2017/12/5
 */

class SensorEventHandler
{
    private Handler handlerScenarize = null;
    
    SensorEventHandler(Handler handler)
    {
        handlerScenarize = handler;
    }
    
    CockpitSensorEventListener getSensorEventListener()
    {
        return cockpitSensorEventListener;
    }
    
    private CockpitSensorEventListener cockpitSensorEventListener = new CockpitSensorEventListener()
    {
        @Override
        public void onShakeHands(Object sender)
        {
            switch (GLOBAL.mnScenarizeIndex)
            {
                case SCEN.SCEN_INDEX_ANIMAL_RFID:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_HOLD_HAND);
                    break;
            }
            
            Logs.showTrace("onShakeHands");
            trackerHandler.setRobotFace("").setSensor("shake_hand", "1").setScene(String.valueOf
                (GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "")
                .send();
        }
        
        @Override
        public void onClapHands(Object sender)
        {
            switch (GLOBAL.mnScenarizeIndex)
            {
                case SCEN.SCEN_INDEX_ANIMAL_RFID:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_HOLD_HAND);
                    break;
            }
            
            Logs.showTrace("onClapHands");
            trackerHandler.setRobotFace("").setSensor("clap_hand", "1").setScene(String.valueOf
                (GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "")
                .send();
        }
        
        @Override
        public void onPinchCheeks(Object sender)
        {
            // 捏臉頰
            
            Logs.showTrace("onPinchCheeks");
            trackerHandler.setRobotFace("").setSensor("pinch_cheeks", "1").setScene(String
                .valueOf(GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1",
                "1", "").send();
        }
        
        @Override
        public void onPatHead(Object sender)
        {
            
            Logs.showTrace("onPatHead");
            trackerHandler.setRobotFace("").setSensor("pat_hat", "1").setScene(String.valueOf
                (GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "")
                .send();
        }
        
        @Override
        public void onScannedRfid(Object sensor, String scannedResult)
        {
            Logs.showTrace("[ZooActivity] onScannedRfid GLOBAL.mnScenarizeIndex=" + String
                .valueOf(GLOBAL.mnScenarizeIndex) + " Result:" + scannedResult);
            
            
            if (GLOBAL.scenarize.indexOfKey(GLOBAL.mnScenarizeIndex) < 0)
            {
                return;
            }
            
            Logs.showTrace("[ZooActivity] onScannedRfid GLOBAL.mnScenarizeIndex=" + String
                .valueOf(GLOBAL.mnScenarizeIndex) + " Result:" + scannedResult);
            
            int nNext = -1;
            JSONObject jsonScenarize = GLOBAL.scenarize.get(GLOBAL.mnScenarizeIndex);
            
            try
            {
                nNext = jsonScenarize.getInt("next");
            }
            catch (Exception e)
            {
                Logs.showError("[SensorEventHandler] onScannedRfid Exception:" + e.getMessage());
            }
            
            switch (GLOBAL.mnScenarizeIndex)
            {
                case SCEN.SCEN_INDEX_START:
                    handlerScenarize.sendEmptyMessage(nNext);
                    break;
                case SCEN.SCEN_INDEX_HOLD_HAND:
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_TRAFFIC_BUS);
                    if (0 == scannedResult.compareTo("1"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_BUS);
                    }
                    else if (0 == scannedResult.compareTo("2"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_MRT);
                    }
                    else if (0 == scannedResult.compareTo("3"))
                    {
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_CAR);
                    }
                    else
                    {
                        handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_BUS);
                    }
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_BUS:
                    GLOBAL.mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_TRAFFIC_CARD);
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_CARD);
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_MRT:
                    GLOBAL.mnScenarizeIndex = -1;
                    handlerScenarize.removeMessages(SCEN.SCEN_INDEX_TRAFFIC_CARD);
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_TRAFFIC_CARD);
                    break;
            }
            
            trackerHandler.setRobotFace("").setSensor("rfid", scannedResult).setScene(String
                .valueOf(GLOBAL.mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1",
                "1", "").send();
            
        }
    };
}
