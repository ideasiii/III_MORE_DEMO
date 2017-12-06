package com.iii.more.game.zoo;

/**
 * Created by jugo on 2017/12/5
 */


abstract class SCEN
{
    public static final int SCEN_INDEX_START = 100;           // 等待動物園圖案的RFID
    public static final int SCEN_INDEX_ANIMAL_RFID = 101;     // 取得動物園圖案的RFID
    public static final int SCEN_INDEX_HOLD_HAND = 102;       // 孩子抓住章魚寶的手
    public static final int SCEN_INDEX_TRAFFIC_BUS = 103;     // 孩子選擇搭公車
    public static final int SCEN_INDEX_TRAFFIC_MRT = 104;     // 孩子選擇搭捷運
    public static final int SCEN_INDEX_TRAFFIC_CAR = 105;     // 孩子選擇坐汽車
    public static final int SCEN_INDEX_TRAFFIC_CARD = 106;    // 孩子將悠遊卡RFID放上盤子
    public static final int SCEN_INDEX_BUS_INSIDE = 107;      // 章魚寶眼睛螢幕畫面轉成公車內部
    public static final int SCEN_INDEX_DROP_CUSTOM = 108;     // 孩子直接用手指在畫面上拉乘客到座位上
    public static final int SCEN_INDEX_DROP_CUSTOM_IDLE = 109;     // 孩子很久未在畫面上拉乘客到座位上
    public static final int SCEN_INDEX_DROP_CUSTOM_IDLE2 = 110;     //
    // 孩子很久未在畫面上拉乘客到座位上part2
    public static final int SCEN_INDEX_BUS_DRIVE = 111;
    public static final int SCEN_INDEX_ZOO_DOOR = 112;
    public static final int SCEN_INDEX_ANIMAL_MONKEY = 113;
    public static final int SCEN_INDEX_FOOD_MENU = 118;
    public static final int SCEN_INDEX_EAT_HAMBERB = 119;
    public static final int SCEN_INDEX_EATED_HAMBERB = 120;
    public static final int SCEN_INDEX_ANIMAL_ELEPHONE = 122;
    public static final int SCEN_INDEX_ANIMAL_KONG = 123;
    public static final int SCEN_INDEX_FAV_ANIMAL = 124;
    public static final int SCEN_INDEX_FAV_ANIMAL_SPEECH = 125;
    public static final int SCEN_INDEX_BANANA = 126;
    public static final int SCEN_INDEX_BANANA_NON = 127;
    public static final int SCEN_INDEX_VEGETABLE = 128;
    public static final int SCEN_INDEX_VEGETABLE_NON = 129;
    public static final int SCEN_INDEX_LEMUR = 130;
    public static final int SCEN_INDEX_APPLE = 131;
    public static final int SCEN_INDEX_APPLE_NON = 132;
    public static final int SCEN_INDEX_EAT_DNUTE = 133;
    public static final int SCEN_INDEX_EAT_ICECREAME = 134;
    public static final int SCEN_INDEX_EATED_DNUTE = 135;
    public static final int SCEN_INDEX_EATED_ICECREAME = 136;
    public static final int SCEN_INDEX_MRT_MAP = 137;
    public static final int SCEN_INDEX_FACE_EMONTION = 777;
    public static final int SCEN_INDEX_GAME_OVER = 666;
    public static final int SCEN_INDEX_FINISH = 999;
    
    public static final int SENSOR_SHACK_HAND = 1000;
    public static final int SENSOR_CLAP_HAND = 1001;
    public static final int SENSOR_PINCH_CHEEK = 1002;
    public static final int SENSOR_PAT_HEAD = 1003;
    public static final int SENSOR_RFID = 1004;
}
