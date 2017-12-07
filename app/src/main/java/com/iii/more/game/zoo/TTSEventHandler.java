package com.iii.more.game.zoo;

import com.iii.more.main.listeners.TTSEventListener;

import android.os.Handler;

import org.json.JSONObject;

import sdk.ideas.common.Logs;


/**
 * Created by jugo on 2017/12/5
 */

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
            final int nIndex = Integer.valueOf(utteranceId);
            int nNext = -1;
            
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
                    handlerScenarize.sendEmptyMessage(nNext);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_MONKEY:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_BANANA, 2000);
                    break;
                case SCEN.SCEN_INDEX_BANANA:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_BANANA_NON, 1000);
                    break;
                case SCEN.SCEN_INDEX_BANANA_NON:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_ANIMAL_ELEPHONE,                 2000);
                    break;
                case SCEN.SCEN_INDEX_FOOD_MENU:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EAT_HAMBERB, 6000);
                    break;
                case SCEN.SCEN_INDEX_EAT_HAMBERB:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_HAMBERB,
                    // 1000);
                    break;
                case SCEN.SCEN_INDEX_EAT_DNUTE:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_DNUTE, 1000);
                    break;
                case SCEN.SCEN_INDEX_EAT_ICECREAME:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_ICECREAME,  1000);
                    break;
                case SCEN.SCEN_INDEX_EATED_HAMBERB:
                case SCEN.SCEN_INDEX_EATED_DNUTE:
                case SCEN.SCEN_INDEX_EATED_ICECREAME:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_GAME_OVER, 1000);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_ELEPHONE:
                //    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_VEGETABLE, 1000);
                    break;
                case SCEN.SCEN_INDEX_VEGETABLE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_VEGETABLE_NON, 2000);
                    break;
                case SCEN.SCEN_INDEX_VEGETABLE_NON:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_LEMUR, 1000);
                    break;
                case SCEN.SCEN_INDEX_LEMUR:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_APPLE, 1000);
                    break;
                case SCEN.SCEN_INDEX_APPLE:
                //    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_APPLE_NON, 2000);
                    break;
                case SCEN.SCEN_INDEX_APPLE_NON:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FOOD_MENU, 1000);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_KONG:
                 //   handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FAV_ANIMAL, 1000);
                    break;
                case SCEN.SCEN_INDEX_FAV_ANIMAL:
                //    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_FAV_ANIMAL_SPEECH);
                    break;
                case SCEN.SCEN_INDEX_GAME_OVER:
                //    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FINISH, 2000);
                    break;
                
            }
        }
    };
}
