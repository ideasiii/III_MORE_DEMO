package com.iii.more.main.listeners;

/**
 * CockpitService 各種事件的 listener，只有 MainApplication 類別會接受此 listener。
 * CockpitService 本身使用 Handler 傳送事件至 MainApplication，
 * MainApplication 再將 Handler 的資料轉給 listener。
 */
public interface CockpitConnectionEventListener
{
    /** 使用者允許裝置的使用時的回呼。注意此時尚未與目標建立連線 */
    void onPermissionGranted(Object arg);

    /** 使用者不允許裝置的使用時的回呼 */
    void onPermissionNotGranted(Object arg);

    /** 找不到可用的裝置時的回呼，通常發生在應用程式剛啟動而裝置未插入時 */
    void onNoDevice(Object arg);

    /** USB 裝置斷線時的回呼 */
    void onDisconnected(Object arg);

    /** 已與 USB 裝置建立連線，準備接收資料時的回呼 */
    void onReady(Object arg);

    /** 不支援此種裝置連接方式時的回呼 (應該很少發生吧?) */
    void onProtocolNotSupported(Object arg);
}
