package com.iii.more.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.SparseArray;

/**
 * 通用音效的聚集地，透過減少產生相同的 MediaPlayer 以期節省資源
 */
public class SoundEffectsPool
{
    private Context mContext;
    private SparseArray<MediaPlayer> mCachedPlayers = new SparseArray<>();

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
