package com.iii.more.main;

/**
 * 監聽駕駛艙各種傳感器的事件的 listener
 */
public interface CockpitSensorEventListener
{
    /** 判定到握手事件時的回呼 */
    void onShakeHands(Object sender);

    /** 判定到拍手事件時的回呼 */
    void onClapHands(Object sender);

    /** 判定到捏臉頰事件時的回呼 */
    void onPinchCheeks(Object sender);

    /** 判定到拍頭事件時的回呼 */
    void onPatHead(Object sender);

    /** RFID 掃描到東西時的回呼 */
    void onScannedRfid(Object sensor, String scannedResult);
}
