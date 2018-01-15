package com.iii.more.game.zoo;

/**
 * Created by jugo on 2017/12/5
 */


abstract class SCEN
{
    public static final int SCEN_INDEX_NO_ACTION = 0;         // 無動作
    public static final int SCEN_INDEX_START = 100;           // 等待動物園圖案的RFID
    public static final int SCEN_INDEX_ANIMAL_RFID = 101;     // 取得動物園圖案的RFID
    public static final int SCEN_INDEX_HOLD_HAND = 102;       // 孩子抓住章魚寶的手
    public static final int SCEN_INDEX_TRAFFIC_BUS = 103;     // 孩子選擇搭公車
    public static final int SCEN_INDEX_TRAFFIC_MRT = 104;     // 孩子選擇搭捷運
    public static final int SCEN_INDEX_TRAFFIC_CAR = 105;     // 孩子選擇坐汽車
    public static final int SCEN_INDEX_TRAFFIC_CARD_BUS = 106;    // 孩子將BUS悠遊卡RFID放上盤子
    public static final int SCEN_INDEX_BUS_INSIDE = 107;      // 章魚寶眼睛螢幕畫面轉成公車內部
    public static final int SCEN_INDEX_DROP_CUSTOM = 108;     // 孩子直接用手指在畫面上拉乘客到座位上
    public static final int SCEN_INDEX_BUS_DRIVE = 111;
    public static final int SCEN_INDEX_ZOO_DOOR = 112;
    public static final int SCEN_INDEX_MRT_MAP = 137;
    public static final int SCEN_INDEX_TRAFFIC_CARD_MRT = 138;  // // 孩子將MRT悠遊卡RFID放上盤子
    public static final int SCEN_INDEX_CHOICE_ZOO = 139;    // 選動物區
    public static final int SCEN_INDEX_ZOO_TAIWAN = 140;    // 台灣動物區
    public static final int SCEN_INDEX_ZOO_BIRD = 141;      // 鳥園
    public static final int SCEN_INDEX_ZOO_RAIN = 142;      // 熱帶雨林動物區
    public static final int SCEN_INDEX_ZOO_CUT = 143;       // 可愛動物區
    public static final int SCEN_INDEX_ZOO_AFFICA = 144;    // 非洲動物區
    public static final int SCEN_INDEX_ANIMAL_END = 145;
    public static final int SCEN_INDEX_FOOD_STORE = 146;
    public static final int SCEN_INDEX_FOOD_CHOICE = 147;
    public static final int SCEN_INDEX_FOOD_EAT = 148;
    public static final int SCEN_INDEX_CAR_RUN = 149;
    public static final int SCEN_INDEX_CAR_OUTSIDE = 150;
    public static final int SCEN_INDEX_CAR_START = 151;         // 握著我的手轉一下，要發動囉！
    public static final int SCEN_INDEX_CAR_FIX = 152;           // 車子故障了，請幫忙修補輪胎
    public static final int SCEN_INDEX_CAR_FIX_SUCCESS = 153;   // 太好了，車子修好了，謝謝你的幫忙
    //public static final int SCEN_INDEX_EMOTION_RESP = 154;      // 情緒偵測回應
    public static final int SCEN_INDEX_BUS_EMOTION_RESP = 155;
    public static final int SCEN_INDEX_MRT_EMOTION_RESP = 156;
    public static final int SCEN_INDEX_CAR_EMOTION_RESP = 157;
    public static final int SCEN_INDEX_MRT_RUN = 158;
    public static final int SCEN_INDEX_GAME_OVER = 666;
    public static final int SCEN_INDEX_FINISH = 999;
    public static final int SENSOR_FACE_EMOTION = 1005;
    
    public static final int MSG_TTS_PLAY = 2000;
    
    public static final String GAME_ZOO = "22418120115";
    public static final String TRAFFIC_BUS = "3268108115";
    public static final String TRAFFIC_MRT = "6487127115";
    public static final String TRAFFIC_CAR = "208145104115";
    public static final String TRAFFIC_CARD = "2287191147";
    public static final String CAR_KEY = "112199103115";
    
    public static final int MAX_ZOO_VISIT = 2;
}
