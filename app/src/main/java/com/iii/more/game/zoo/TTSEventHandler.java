package com.iii.more.game.zoo;

import com.iii.more.main.listeners.TTSEventListener;

import android.os.Handler;

import org.json.JSONObject;

import sdk.ideas.common.Logs;

class TTSEventHandler
{
    private Handler handlerScenarize = null;
    
    TTSEventHandler(Handler handler)
    {
        handlerScenarize = handler;
    }
    
    TTSEventListener getTTSEventListener()
    {
        return ttsEventListener;
    }
    
    private int mnTraffic;
    
    private TTSEventListener ttsEventListener = new TTSEventListener()
    {
        
        @Override
        public void onInitSuccess()
        {
        
        }
        
        @Override
        public void onInitFailed(int status, String message)
        {
        
        }
        
        @Override
        public void onUtteranceStart(String utteranceId)
        {
        
        }
        
        @Override
        public void onUtteranceDone(String utteranceId)
        {
            int nIndex;
            int nNext = -1;
            
            try
            {
                nIndex = Integer.valueOf(utteranceId);
            }
            catch (Exception e)
            {
                return;
            }
            
            
            if (GLOBAL.scenarize.indexOfKey(nIndex) < 0)
            {
                Logs.showError("[TTSEventHandler] onUtteranceDone invalid Index:" + nIndex);
                return;
            }
            JSONObject jsonScenarize = GLOBAL.scenarize.get(nIndex);
            try
            {
                nNext = jsonScenarize.getInt("next");
            }
            catch (Exception e)
            {
                Logs.showError("[TTSEventHandler] onUtteranceDone Exception:" + e.getMessage());
            }
            
            
            switch (Integer.valueOf(utteranceId))
            {
                
                case SCEN.SCEN_INDEX_TRAFFIC_CARD_BUS:
                case SCEN.SCEN_INDEX_DROP_CUSTOM:          // 好棒！!!我們出發囉！
                case SCEN.SCEN_INDEX_BUS_DRIVE:            // 公車開始移動
                case SCEN.SCEN_INDEX_ZOO_DOOR:             // 到動物園門口
                case SCEN.SCEN_INDEX_FOOD_STORE:          // 我們來吃東西休息一下吧！
                case SCEN.SCEN_INDEX_TRAFFIC_CARD_MRT:
                case SCEN.SCEN_INDEX_CAR_RUN:
                    handlerScenarize.sendEmptyMessage(nNext);
                    break;
                case SCEN.SCEN_INDEX_FOOD_EAT:
                case SCEN.SCEN_INDEX_GAME_OVER:
                    handlerScenarize.sendEmptyMessageDelayed(nNext, 2000);
                    break;
            }
        }
    };
}
