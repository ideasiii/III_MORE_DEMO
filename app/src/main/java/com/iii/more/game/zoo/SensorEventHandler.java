package com.iii.more.game.zoo;

import com.iii.more.main.listeners.CockpitSensorEventListener;

import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

import static com.iii.more.game.zoo.ZooActivity.trackerHandler;

/**
 * Created by jugo on 2017/12/5
 */

class SensorEventHandler
{
    private Handler handlerScenarize = null;
    
    private class SensorEvent
    {
        public int NextScenarize;
        public ScenarizeDefine.NEXT_TRIGER nextTriger;
        
        public SensorEvent(final int nNextScenarize, final ScenarizeDefine.NEXT_TRIGER next_triger)
        {
            NextScenarize = nNextScenarize;
            nextTriger = next_triger;
        }
    }
    
    private SparseArray<SensorEvent> listSensorEvent = null;
    
    
    SensorEventHandler(Handler handler)
    {
        handlerScenarize = handler;
        listSensorEvent = new SparseArray<SensorEvent>();
    }
    
    public void setHandler(Handler handler)
    {
        handlerScenarize = handler;
    }
    
    public void goScenarize(final int nScenarizeIndex)
    {
        handlerScenarize.sendEmptyMessage(nScenarizeIndex);
    }
    
    public void addSensorEvent(final int nCurrentScenarize, final int nNextScenarize, final
    ScenarizeDefine.NEXT_TRIGER next_triger)
    {
        SensorEvent sensorEvent = new SensorEvent(nNextScenarize, next_triger);
        listSensorEvent.append(nCurrentScenarize, sensorEvent);
    }
    
    CockpitSensorEventListener getSensorEventListener()
    {
        return cockpitSensorEventListener;
    }
    
    private CockpitSensorEventListener cockpitSensorEventListener = new CockpitSensorEventListener()
    {
        private void eventHandle(final int nScenarizeIndex, final String strSensorName)
        {
            if (-1 < listSensorEvent.indexOfKey(nScenarizeIndex))
            {
                SensorEvent sensorEvent = listSensorEvent.get(nScenarizeIndex);
                if (ScenarizeDefine.NEXT_TRIGER.SENSOR_ALL == sensorEvent.nextTriger ||
                    ScenarizeDefine.NEXT_TRIGER.HAND_SHAKE == sensorEvent.nextTriger)
                {
                    goScenarize(sensorEvent.NextScenarize);
                }
            }
            trackerHandler.setRobotFace("").setSensor(strSensorName, "1").setScene(String.valueOf
                (nScenarizeIndex)).setMicrophone("").setSpeaker("tts", "", "1", "1", "").send();
            Logs.showTrace(strSensorName);
        }
        
        @Override
        public void onShakeHands(Object sender)
        {
            Logs.showTrace("[CockpitSensorEventListener] onShakeHands ScenarizeIndex=" + GLOBAL.scenarizeCurr.ScenarizeIndex);
            eventHandle(GLOBAL.scenarizeCurr.ScenarizeIndex, "onShakeHands");
        }
        
        @Override
        public void onClapHands(Object sender)
        {
            eventHandle(GLOBAL.scenarizeCurr.ScenarizeIndex, "onClapHands");
        }
        
        @Override
        public void onPinchCheeks(Object sender)
        {
            // 捏臉頰
            eventHandle(GLOBAL.scenarizeCurr.ScenarizeIndex, "onPinchCheeks");
        }
        
        @Override
        public void onPatHead(Object sender)
        {
            eventHandle(GLOBAL.scenarizeCurr.ScenarizeIndex, "onPatHead");
        }
        
        @Override
        public void onScannedRfid(Object sensor, String scannedResult)
        {
            Logs.showTrace("[ZooActivity] onScannedRfid GLOBAL.mnScenarizeIndex=" + String
                .valueOf(GLOBAL.scenarizeCurr.ScenarizeIndex) + " Result:" + scannedResult);
            
            
            if (GLOBAL.scenarize.indexOfKey(GLOBAL.scenarizeCurr.ScenarizeIndex) < 0)
            {
                return;
            }
            
            Logs.showTrace("[ZooActivity] onScannedRfid GLOBAL.mnScenarizeIndex=" + String
                .valueOf(GLOBAL.scenarizeCurr.ScenarizeIndex) + " Result:" + scannedResult);
            
            int nNext = -1;
            JSONObject jsonScenarize = GLOBAL.scenarize.get(GLOBAL.scenarizeCurr.ScenarizeIndex);
            
            try
            {
                nNext = jsonScenarize.getInt("next");
            }
            catch (Exception e)
            {
                Logs.showError("[SensorEventHandler] onScannedRfid Exception:" + e.getMessage());
            }
            
            switch (GLOBAL.scenarizeCurr.ScenarizeIndex)
            {
                case SCEN.SCEN_INDEX_START:
                    if (0 == scannedResult.compareTo(SCEN.GAME_ZOO))
                    {
                        goScenarize(SCEN.SCEN_INDEX_ANIMAL_RFID);
                    }
                    break;
                case SCEN.SCEN_INDEX_HOLD_HAND: // "抓緊喔！今天，你想要坐什麼交通工具去呢"
                    if (0 == scannedResult.compareTo(SCEN.TRAFFIC_BUS))
                    {
                        goScenarize(SCEN.SCEN_INDEX_TRAFFIC_BUS);
                    }
                    else if (0 == scannedResult.compareTo(SCEN.TRAFFIC_MRT))
                    {
                        goScenarize(SCEN.SCEN_INDEX_TRAFFIC_MRT);
                    }
                    else if (0 == scannedResult.compareTo(SCEN.TRAFFIC_CAR))
                    {
                        goScenarize(SCEN.SCEN_INDEX_TRAFFIC_CAR);
                    }
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_BUS:
                case SCEN.SCEN_INDEX_TRAFFIC_MRT:
                    if (0 == scannedResult.compareTo(SCEN.TRAFFIC_CARD)) // 刷悠遊卡
                    {
                        goScenarize(nNext);
                    }
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_CAR:
                    if (0 == scannedResult.compareTo(SCEN.CAR_KEY))  // 鑰匙RFID放上盤子
                    {
                        goScenarize(nNext);
                    }
                    break;
                
            }
            
            trackerHandler.setRobotFace("").setSensor("rfid", scannedResult).setScene(String
                .valueOf(GLOBAL.scenarizeCurr.ScenarizeIndex)).setMicrophone("").setSpeaker
                ("tts", "", "1", "1", "").send();
            
        }
    };
}
