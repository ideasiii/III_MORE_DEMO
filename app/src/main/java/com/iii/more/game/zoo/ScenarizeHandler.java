package com.iii.more.game.zoo;

import android.content.Context;
import android.util.SparseArray;
import android.widget.ImageView;

import com.iii.more.main.R;

import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;


/**
 * Created by Jugo on 2017/12/6
 */

public class ScenarizeHandler
{
    public static enum FRONT
    {
        FACE, OBJECT
    }
    
    private Context theContext = null;
    
    public ScenarizeHandler(Context context)
    {
        theContext = context;
    }
    
    public void createScenarize(SparseArray<JSONObject> scenarize)
    {
        try
        {
            scenarize.clear();
            
            setScenarize(scenarize, SCEN.SCEN_INDEX_START, SCEN.SCEN_INDEX_ANIMAL_RFID, true,
                false, R.drawable.octobo16, R.drawable.zoo_map, "octobo16.png", ImageView
                    .ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_CROP, FRONT.FACE, "嗨! 你好 "
                    + "來玩遊戲吧");
            
            setScenarize(scenarize, SCEN.SCEN_INDEX_ANIMAL_RFID, SCEN.SCEN_INDEX_HOLD_HAND, true,
                true, R.drawable.noeye, R.drawable.zoo, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, FRONT.OBJECT, "哈囉，" + GLOBAL
                    .ChildName + "今天我們一起去動物園玩！牽著我的手，出發囉！");
            
            setScenarize(scenarize, SCEN.SCEN_INDEX_HOLD_HAND, SCEN.SCEN_INDEX_NO_ACTION, true,
                true, R.drawable.noeye, R.drawable.traffic, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, FRONT.OBJECT,
                "抓緊喔！今天，你想要坐什麼交通工具去呢");
            
            // 幹 是坐公車
            setScenarize(scenarize, SCEN.SCEN_INDEX_TRAFFIC_BUS, SCEN.SCEN_INDEX_TRAFFIC_CARD_BUS,
                true,
                true, R.drawable.noeye, R.drawable.bus, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, FRONT.OBJECT, "請刷悠遊卡");
    
    
            setScenarize(scenarize, SCEN.SCEN_INDEX_TRAFFIC_CARD_BUS, SCEN.SCEN_INDEX_BUS_INSIDE,
                true,
                true, R.drawable.noeye, R.drawable.bus, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, FRONT.OBJECT, "逼，，逼");
    
            setScenarize(scenarize, SCEN.SCEN_INDEX_BUS_INSIDE, SCEN.SCEN_INDEX_NO_ACTION,
                true,
                false,
                R.drawable.businside,
                R.drawable.bus,
                "businside.png",
                ImageView.ScaleType.CENTER_INSIDE,
                ImageView.ScaleType.CENTER_INSIDE,
                FRONT.FACE,
                "請你幫忙讓大家都有座位坐");
            /*
              
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setObjectImg(R.drawable.man, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.addView(ivMan);*/
            
            // 坐捷運
            setScenarize(scenarize, SCEN.SCEN_INDEX_TRAFFIC_MRT, SCEN.SCEN_INDEX_TRAFFIC_CARD_MRT,
                true,
                true, R.drawable.noeye, R.drawable.mrt, "noeye.png", ImageView.ScaleType
                    .CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE, FRONT.OBJECT, "請刷悠遊卡");
            
            // 坐車子
        }
        catch (Exception es)
        {
            Logs.showError("[ScenarizeHandler] createScenarize Exception:" + es.getMessage());
        }
        
    }
    
    private void setScenarize(SparseArray<JSONObject> scenarize, int index, int next, boolean
        face_show, boolean object_show, int face_id, int object_id, String face_image, ImageView
        .ScaleType scaleType, ImageView.ScaleType scaleTypeObj, ScenarizeHandler.FRONT front,
        String tts_text)
    {
        try
        {
            scenarize.put(index, new JSONObject().put("index", index).put("next", next).put
                ("face_show", face_show).put("object_show", object_show).put("face_id", face_id)
                .put("object_id", object_id).put("face_image", face_image).put("face_scale_type",
                    scaleType).put("object_scale_type", scaleTypeObj).put("front", front).put
                    ("tts_text", tts_text));
        }
        catch (Exception e)
        {
            Logs.showError("[ScenarizeHandler] setScenarize Exception:" + e.getMessage());
        }
    }
}

/*
        switch (nIndex)
        {
          
           
         
            case SCEN.SCEN_INDEX_TRAFFIC_MRT:
                mnTraffic = SCEN.SCEN_INDEX_TRAFFIC_MRT;
                strTTS = "請刷悠遊卡";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.mrt_train, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN.SCEN_INDEX_TRAFFIC_CAR:
                mnTraffic = SCEN.SCEN_INDEX_TRAFFIC_CAR;
                break;
            case SCEN.SCEN_INDEX_TRAFFIC_CARD:   // 孩子將悠遊卡RFID放上盤子
                strTTS = "逼，，逼";
                break;
            case SCEN.SCEN_INDEX_BUS_INSIDE:     // 章魚寶眼睛螢幕畫面轉成公車內部
                strTTS = strName + "，，請你幫忙讓大家都有座位坐";
                robotHead.bringObjImgtoFront();
                nFace = R.drawable.businside;
                strFaceImg = "businside.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.setObjectImg(R.drawable.man, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(false);
                robotHead.showFaceImg(true);
                robotHead.addView(ivMan);
                break;
            case SCEN.SCEN_INDEX_DROP_CUSTOM:    // 孩子直接用手指在畫面上拉乘客到座位上，完成
                robotHead.removeView(ivMan);
                strTTS = "好棒！!!我們出發囉！";
                robotHead.showObjectImg(false);
                Logs.showTrace("dropped X = " + String.valueOf(mnDroppedX));
                if (550 < mnDroppedX)
                {
                    nFace = R.drawable.businside_right;
                    strFaceImg = "businside_right.png";
                }
                else
                {
                    nFace = R.drawable.businside_left;
                    strFaceImg = "businside_left.png";
                }
                
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN.SCEN_INDEX_DROP_CUSTOM_IDLE:
                strTTS = "快一點呀，，公車要開囉！";
                break;
            case SCEN.SCEN_INDEX_DROP_CUSTOM_IDLE2:
                strTTS = "啊啊 ，，來不及了，，公車開動囉！";
                break;
            case SCEN.SCEN_INDEX_BUS_DRIVE:      // 公車開始移動
                strTTS = "噗噗噗噗噗噗噗噗噗噗";
                robotHead.bringFaceImgtoFront();
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.busmoving, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN.SCEN_INDEX_MRT_MAP:
                robotHead.showObjectImg(false);
                nFace = R.drawable.noeye;
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.addView(mrtMap);
                break;
            case SCEN.SCEN_INDEX_ZOO_DOOR:       // 顯示出動物園的大門
                strTTS = "到囉，，讓我們一起來參觀動物吧";
                nFace = R.drawable.zoodoor;
                strFaceImg = "zoodoor.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                break;
            case SCEN.SCEN_INDEX_ANIMAL_MONKEY:
                strTTS = "看，是猴子";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.monkey, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN.SCEN_INDEX_BANANA:
                strTTS = "猴子 最愛吃香蕉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN.SCEN_INDEX_BANANA_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.banana_non, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN.SCEN_INDEX_FOOD_MENU:
                strTTS = strName + "我們來吃東西休息一下吧！選你想吃得食物吧";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(false);
                robotHead.addView(linearFood);
                break;
            case SCEN.SCEN_INDEX_EAT_HAMBERB:
                robotHead.removeView(linearFood);
                strTTS = "來吃漢堡囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.burger, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN.SCEN_INDEX_EATED_HAMBERB:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.burger_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN.SCEN_INDEX_EAT_DNUTE:
                robotHead.removeView(linearFood);
                strTTS = "來吃甜甜圈囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.donut, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN.SCEN_INDEX_EATED_DNUTE:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.donut_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN.SCEN_INDEX_EAT_ICECREAME:
                robotHead.removeView(linearFood);
                strTTS = "來吃冰淇淋囉！";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.icecream, ImageView.ScaleType.CENTER_INSIDE);
                robotHead.showObjectImg(true);
                robotHead.showFaceImg(true);
                robotHead.bringObjImgtoFront();
                break;
            case SCEN.SCEN_INDEX_EATED_ICECREAME:
                strTTS = "嗯 好吃！,, 我們下次再來玩";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.icecream_non, ImageView.ScaleType.CENTER_INSIDE);
                break;
            case SCEN.SCEN_INDEX_ANIMAL_ELEPHONE:
                strTTS = "快看，，是大象";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.elephone2, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_VEGETABLE:
                strTTS = "大象最喜歡吃草跟樹葉!!";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_VEGETABLE_NON:
                strTTS = "啊嗯嗯嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.vegetable_non, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_LEMUR:
                strTTS = "是狐猴";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.lemur, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_APPLE:
                strTTS = "蘋果是狐猴最愛吃的食物";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_APPLE_NON:
                strTTS = "啊嗯";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.apple_non, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_ANIMAL_KONG:
                strTTS = "哈哈，，是猩猩，，猩猩最喜歡吃香蕉喔!!";
                robotHead.setObjectImg(R.drawable.kong, ImageView.ScaleType.FIT_XY);
                break;
            case SCEN.SCEN_INDEX_FAV_ANIMAL:
                strTTS = "今天，真好玩，請告訴我，你最喜歡什麼動物呢";
                robotHead.showObjectImg(false);
                nFace = R.drawable.octobo31;
                strFaceImg = "octobo31.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                break;
            case SCEN.SCEN_INDEX_FAV_ANIMAL_SPEECH:
                // mVoiceRecognition.startListen();
                break;
            case SCEN.SCEN_INDEX_GAME_OVER:
                strTTS = "再見囉";
                nFace = R.drawable.noeye;
                strFaceImg = "noeye.png";
                robotHead.setFace(nFace, ImageView.ScaleType.CENTER_CROP);
                robotHead.setObjectImg(R.drawable.zoodoor, ImageView.ScaleType.FIT_XY);
                robotHead.showObjectImg(true);
                break;
            case SCEN.SCEN_INDEX_FINISH:
                finish();
                break;
            case SCEN.SCEN_INDEX_FACE_EMONTION:
                
                break;
            default:
                return;
        }
        application.setTTSPitch(1.0f, 1.0f);
        application.playTTS(strTTS, String.valueOf(nIndex));
        
        // 傳送Tracker Data
        trackerHandler.setRobotFace(strFaceImg).setSensor("", "").setScene(String.valueOf(GLOBAL
            .mnScenarizeIndex)).setMicrophone("").setSpeaker("tts", strTTS, "1", "1", "").send();
            */