package com.iii.more.main;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * CyberonAssetsExtractor copies assets other directory,
 * which can be seen and loaded by Cyberon TTS library
 */
final class CyberonAssetsExtractor
{
    private static final String LOG_TAG = "CyberonAssetsExtractor";

    static void extract(AssetManager assetManager, String dstDir)
    {
        // AssetManager.list() is taking too long!!!
        //copyAssetToDataDir(assetManager, "cyberon", dstDir);

        copyAssetToDataDirHardCoded(assetManager, dstDir);
    }

    private static void copyAssetToDataDirHardCoded(AssetManager am, String dstDir)
    {
        // AssetManager.list() is very very slow, so predefine a list needed to
        String[] dirs =
        {
            "cyberon", "cyberon/CReader", "cyberon/CReader/en-US", "cyberon/CReader/zh-TW"
        };

        String[] files =
        {
            "cyberon/CReader/CReaderLicense.bin",
            "cyberon/CReader/en-US/NLP.0409.bin",
            "cyberon/CReader/en-US/TN.0409.bin",
            "cyberon/CReader/zh-TW/NLP.0404.bin",
            "cyberon/CReader/zh-TW/Sentence.0404.txt",
            "cyberon/CReader/zh-TW/TN.0404.bin",
            "cyberon/CReader/zh-TW/TTS_Voice.0404.KidF_TW.bin",
            "cyberon/CReader/zh-TW/TTS_Voice.0404.KidM_TW.bin",
            "cyberon/CReader/zh-TW/TTS_Voice.0409.KidF_TW.bin",
            "cyberon/CReader/zh-TW/TTS_Voice.0409.KidM_TW.bin"
        };

        for (String dirStr : dirs)
        {
            String dirAbsPath = dstDir + "/" + dirStr;
            File dstFile = new File(dirAbsPath);
            dstFile.mkdirs();
        }

        for (String fileStr : files)
        {
            copyFile(am, fileStr, dstDir);
        }
    }

    private static void copyAssetToDataDir(AssetManager am, String assetPath, String dstDir)
    {
        try
        {
            String[] assetsList = am.list(assetPath);
            if (assetsList.length < 1)
            {
                copyFile(am, assetPath, dstDir);
                return;
            }

            String dstAbsPath = dstDir + "/" + assetPath;
            Log.d(LOG_TAG, "dir path = " + dstAbsPath);
            File dstFile = new File(dstAbsPath);
            if (!dstFile.exists())
            {
                Log.d(LOG_TAG, "mkdir `" + dstFile.getAbsolutePath() + "`");
                dstFile.mkdirs();
            }

            for (String f : assetsList)
            {
                Log.d(LOG_TAG, "asset path = " + assetPath + "/" + f);
                copyAssetToDataDir(am, assetPath + "/" + f, dstDir);
            }
        }
        catch (IOException e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private static void copyFile(AssetManager am, String assetPath, String dstDir)
    {
        try
        {
            InputStream in = am.open(assetPath);
            String dstPath =  dstDir + "/" + assetPath;
            OutputStream out = new FileOutputStream(dstPath);

            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1)
            {
                out.write(buffer, 0, read);
            }

            in.close();
            out.flush();
            out.close();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
    }
}
