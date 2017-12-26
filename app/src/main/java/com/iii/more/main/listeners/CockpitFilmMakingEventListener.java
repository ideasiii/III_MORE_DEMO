package com.iii.more.main.listeners;

/**
 * 監聽駕駛艙拍片事件的 listener
 */
public interface CockpitFilmMakingEventListener
{
    /** 判定到 TTS 事件時的回呼 */
    void onTTS(Object sender, String text, String language);

    /** 判定到更換大臉事件時的回呼 */
    void onEmotionImage(Object sender, String imageFilename);
}
