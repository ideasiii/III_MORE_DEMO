package com.iii.more.game.zoo;

import com.iii.more.main.listeners.TTSEventListener;

import android.os.Handler;


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
            switch (Integer.valueOf(utteranceId))
            {
                case SCEN.SCEN_INDEX_START:
                    // handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ANIMAL_RFID);
                    //handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_MRT_MAP); // 測試
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_RFID:
                    //handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_HOLD_HAND, 2000);
                    break;
                case SCEN.SCEN_INDEX_HOLD_HAND: // 孩子挑選交通工具RFID
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_TRAFFIC_BUS, 6000);
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_BUS: // 孩子將悠遊卡RFID放上盤子
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_TRAFFIC_CARD, 6000);
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_MRT: // 孩子將悠遊卡RFID放上盤子
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_TRAFFIC_CARD, 6000);
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_CAR:
                    break;
                case SCEN.SCEN_INDEX_TRAFFIC_CARD:
                    switch (mnTraffic)
                    {
                        case SCEN.SCEN_INDEX_TRAFFIC_BUS:
                            handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_BUS_INSIDE,
                                1000);
                            break;
                        case SCEN.SCEN_INDEX_TRAFFIC_MRT:
                            handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_MRT_MAP, 1000);
                            break;
                    }
                    
                    break;
                case SCEN.SCEN_INDEX_BUS_INSIDE:     // 等待拉人去座位
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_DROP_CUSTOM, 6000);
                    break;
                case SCEN.SCEN_INDEX_DROP_CUSTOM_IDLE: // 等待拉人去座位 第二次
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_DROP_CUSTOM_IDLE2,
                        3000);
                    break;
                case SCEN.SCEN_INDEX_DROP_CUSTOM:    // 好棒！!!我們出發囉！
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_BUS_DRIVE);
                    break;
                case SCEN.SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ZOO_DOOR);
                    break;
                case SCEN.SCEN_INDEX_ZOO_DOOR:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_ANIMAL_MONKEY);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_MONKEY:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_BANANA, 2000);
                    break;
                case SCEN.SCEN_INDEX_BANANA:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_BANANA_NON, 1000);
                    break;
                case SCEN.SCEN_INDEX_BANANA_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_ANIMAL_ELEPHONE, 2000);
                    break;
                case SCEN.SCEN_INDEX_FOOD_MENU:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EAT_HAMBERB, 6000);
                    break;
                case SCEN.SCEN_INDEX_EAT_HAMBERB:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_HAMBERB, 1000);
                    break;
                case SCEN.SCEN_INDEX_EAT_DNUTE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_DNUTE, 1000);
                    break;
                case SCEN.SCEN_INDEX_EAT_ICECREAME:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_EATED_ICECREAME, 1000);
                    break;
                case SCEN.SCEN_INDEX_EATED_HAMBERB:
                case SCEN.SCEN_INDEX_EATED_DNUTE:
                case SCEN.SCEN_INDEX_EATED_ICECREAME:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_GAME_OVER, 1000);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_ELEPHONE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_VEGETABLE, 1000);
                    break;
                case SCEN.SCEN_INDEX_VEGETABLE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_VEGETABLE_NON, 2000);
                    break;
                case SCEN.SCEN_INDEX_VEGETABLE_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_LEMUR, 1000);
                    break;
                case SCEN.SCEN_INDEX_LEMUR:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_APPLE, 1000);
                    break;
                case SCEN.SCEN_INDEX_APPLE:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_APPLE_NON, 2000);
                    break;
                case SCEN.SCEN_INDEX_APPLE_NON:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FOOD_MENU, 1000);
                    break;
                case SCEN.SCEN_INDEX_ANIMAL_KONG:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FAV_ANIMAL, 1000);
                    break;
                case SCEN.SCEN_INDEX_FAV_ANIMAL:
                    handlerScenarize.sendEmptyMessage(SCEN.SCEN_INDEX_FAV_ANIMAL_SPEECH);
                    break;
                case SCEN.SCEN_INDEX_GAME_OVER:
                    handlerScenarize.sendEmptyMessageDelayed(SCEN.SCEN_INDEX_FINISH, 2000);
                    break;
                case SCEN.SCEN_INDEX_FACE_EMONTION:
                   // handlerScenarize.sendEmptyMessageDelayed(mnScenarizeIndex, 100);
                    break;
            }
        }
    };
}
