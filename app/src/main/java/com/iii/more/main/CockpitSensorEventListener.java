package com.iii.more.main;

/**
 * CockpitService 傳感器事件的 listener
 */
public interface CockpitSensorEventListener
{
    /** 握手 */
    void onShakeHands(Object sender);

    /** 拍手 */
    void onClapHands(Object sender);

    /** 擠壓臉頰 */
    void onPinchCheeks(Object sender);

    /** 拍頭 */
    void onPatHead(Object sender);

    /** RFID 掃描到東西 */
    void onScannedRfid(Object sensor, String scannedResult);
}
