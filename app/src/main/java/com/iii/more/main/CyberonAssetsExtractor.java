package com.iii.more.main;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * CyberonAssetsExtractor copies assets other directory,
 * which can be seen and loaded by Cyberon TTS library
 */
final class CyberonAssetsExtractor
{
    private static final String LOG_TAG = "CyberonAssetsExtractor";

    private static final String ZIP_VERSION_FILENAME = "cyberon_creader_zip_version";
    private static final String TMP_ZIP_VERSION_FILENAME = ZIP_VERSION_FILENAME + ".tmp";
    private static final String ZIP_FILENAME = "cyberon_creader.zip";
    private static final String HOST = "https://ryejuice.sytes.net";
    private static final String ZIP_VERSION_URL = HOST + "/edubot/cyberon-tts-data/" + ZIP_VERSION_FILENAME;
    private static final String ZIP_URL = HOST + "/edubot/cyberon-tts-data/" + ZIP_FILENAME;

    static void extract(AssetManager assetManager, String dstDir)
    {
        // AssetManager.list() is taking too long!!!
        //copyAssetToDataDir(assetManager, "cyberon", dstDir);

        //copyAssetToDataDirHardCoded(assetManager, dstDir);
        downloadDataIfNewer(dstDir);
    }

    private static void downloadDataIfNewer(String dstDir)
    {
        if (!needReDownloadData(dstDir))
        {
            Log.d(LOG_TAG, "zip is latest, skip download");
            return;
        }

        Log.d(LOG_TAG, "zip is not latest, need refresh");

        downloadDataFromServer(ZIP_URL, dstDir);

        File f = new File(dstDir + "/" + TMP_ZIP_VERSION_FILENAME);
        if (f.exists())
        {
            f.renameTo(new File(dstDir + "/" + ZIP_VERSION_FILENAME));
        }
    }

    private static boolean needReDownloadData(String dstDir)
    {
        URL url;
        int verOnServer;
        try
        {
            url = new URL(ZIP_VERSION_URL);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return true;
        }

        try
        {
            URLConnection urlConnection = url.openConnection();
            InputStream in = urlConnection.getInputStream();
            OutputStream fileOut = new FileOutputStream(dstDir + "/" + TMP_ZIP_VERSION_FILENAME);
            ByteArrayOutputStream memoryOut = new ByteArrayOutputStream();

            copyStream(in, memoryOut);
            String inStr = memoryOut.toString("UTF-8");
            fileOut.write(memoryOut.toByteArray());

            memoryOut.close();
            fileOut.close();
            in.close();

            verOnServer = Integer.valueOf(inStr.trim());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return true;
        }

        try
        {
            File f = new File(dstDir + "/" + ZIP_VERSION_FILENAME);
            if (!f.exists() || !f.isFile())
            {
                return true;
            }

            InputStream in = new FileInputStream(f);

            byte[] buffer = new byte[1024];
            int read = in.read(buffer);

            if (read == -1)
            {
                f.delete();
                return true;
            }

            in.close();
            int verLocal = Integer.valueOf((new String(buffer, 0, read).trim()));
            return verLocal < verOnServer;
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getMessage());
            return true;
        }
    }

    private static void downloadDataFromServer(String urlStr, String dstDir)
    {
        URL url;
        try
        {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return;
        }

        try
        {
            URLConnection urlConnection = url.openConnection();
            InputStream webIn = urlConnection.getInputStream();
            ZipInputStream zipIn = new ZipInputStream(webIn);
            ZipEntry zipEntry;

            while((zipEntry = zipIn.getNextEntry()) != null)
            {
                Log.d(LOG_TAG, "Extract " + zipEntry.getName() + " to " + dstDir);

                if (zipEntry.isDirectory())
                {
                    File f = new File(dstDir + "/" + zipEntry.getName());
                    if (f.exists())
                    {
                        f.delete();
                    }

                    f.mkdirs();
                }
                else
                {
                    FileOutputStream extractOut = new FileOutputStream(dstDir + "/" + zipEntry.getName());
                    BufferedOutputStream bufOut = new BufferedOutputStream(extractOut);
                    copyStream(zipIn, bufOut);

                    bufOut.close();
                    extractOut.close();
                    zipIn.closeEntry();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
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

            copyStream(in, out);
            in.close();
            out.close();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException
    {
        byte[] buffer = new byte[16 * 1024];
        int read;
        while ((read = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, read);
        }

        out.flush();
    }

}
