package com.iii.more.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

/**
 * 通用音效的聚集地，減少產生相同的 MediaPlayer 以節省資源
 */
public class SoundEffectsPool
{
    private Context mContext;
    private final SparseArray<MediaPlayer> mCachedPlayers = new SparseArray<>();

    public SoundEffectsPool(Context context)
    {
        mContext = context;
    }

    /**
     * 重頭撥放指定的音效檔案
     * @param id 音效檔的 resource ID
     */
    public void replay(final int id)
    {
        MediaPlayer player = mCachedPlayers.get(id);
        if (player == null)
        {
            player = MediaPlayer.create(mContext, id);
            mCachedPlayers.put(id, player);
        }

        replayMediaPlayer(player);
    }

    /**
     * 停止撥放指定的音效檔案
     * @param id 音效檔的 resource ID
     */
    public void stop(final int id)
    {
        MediaPlayer player = mCachedPlayers.get(id);
        if (player != null)
        {
            player.stop();
        }
    }

    /**
     * 暫停撥放指定的音效檔案
     * @param id 音效檔的 resource ID
     */
    public void pause(final int id)
    {
        MediaPlayer player = mCachedPlayers.get(id);
        if (player != null)
        {
            player.pause();
        }
    }

    /**
     * 將不再使用的音效移除。
     * @param id 音效檔的 resource ID
     */
    public void removeFromPool(final int id)
    {
        mCachedPlayers.delete(id);
    }

    /**
     * 預先讀入音效
     * @param id 音效檔的 resource ID
     * @return 指定音效是否已經存在 cache 內，是回傳 true，否回傳 false
     */
    public boolean cache(final int id)
    {
        boolean cached = mCachedPlayers.get(id) != null;
        if (!cached)
        {
            MediaPlayer player = MediaPlayer.create(mContext, id);
            mCachedPlayers.put(id, player);
        }

        return cached;
    }

    /** 將 MediaPlayer 重頭播放 */
    private static void replayMediaPlayer(MediaPlayer mp)
    {
        if (mp.isPlaying())
        {
            mp.seekTo(0);
        }
        else
        {
            mp.start();
        }
    }
}
