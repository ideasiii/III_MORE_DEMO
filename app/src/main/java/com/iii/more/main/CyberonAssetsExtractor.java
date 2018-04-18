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

    /** 儲存資料包版本的檔案的檔名 */
    private static final String ZIP_VERSION_FILENAME = "cyberon_creader_zip_version";
    /** 儲存資料包版本的檔案的暫存檔名 */
    private static final String TMP_ZIP_VERSION_FILENAME = ZIP_VERSION_FILENAME + ".tmp";
    /** 資料包檔名 */
    private static final String ZIP_FILENAME = "cyberon_creader.zip";
    /** 伺服器網址 */
    private static final String HOST = "https://ryejuice.sytes.net";
    /** 儲存資料包版本的檔案的 URL */
    private static final String ZIP_VERSION_URL = HOST + "/edubot/cyberon-tts-data/" + ZIP_VERSION_FILENAME;
    /** 資料包 URL */
    private static final String ZIP_URL = HOST + "/edubot/cyberon-tts-data/" + ZIP_FILENAME;

    /**
     * retrieve CReader data files
     * @return whether newer data is extracted
     */
    static boolean extract(AssetManager assetManager, String dstDir)
    {
        // AssetManager.list() is taking too much time!!!
        //copyAssetToDataDir(assetManager, "cyberon", dstDir);

        // This is faster, using a hardcoded list instead of dynamic scanning
        //copyAssetToDataDirHardCoded(assetManager, dstDir);

        // Download CReader data files from the Internet
        return downloadDataIfNewer(dstDir);
    }

    /**
     * 此方法會先從伺服器下載一個 metadata 確認伺服器上的資料包版本，若本地的資料包較舊，則下載新的。
     * @param dstDir 資料包要解壓縮到哪裡
     * @return 若伺服器的資料較新，且資料包的資料「全部解壓縮成功」時，回傳 true; 否則回傳 false
     */
    private static boolean downloadDataIfNewer(String dstDir)
    {
        if (!needReDownloadData(dstDir))
        {
            Log.d(LOG_TAG, "zip is latest, skip download");
            return false;
        }

        Log.d(LOG_TAG, "zip is not latest, need refresh");

        boolean dataDownload = downloadDataFromServer(ZIP_URL, dstDir);
        if (dataDownload)
        {
            File f = new File(dstDir + "/" + TMP_ZIP_VERSION_FILENAME);
            if (f.exists())
            {
                f.renameTo(new File(dstDir + "/" + ZIP_VERSION_FILENAME));
            }
        }

        return dataDownload;
    }

    /**
     * 從伺服器下載一個標記伺服器資料包版本的 metadata，用此 metadata 檢查本地的資料是否較舊，需要更新。
     * @param dstDir metadata 要下載到哪裡
     * @return 若需要下載更新資料，回傳 true，否則回傳 false
     */
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

        Log.d(LOG_TAG, "try downloading version info from " + ZIP_VERSION_URL);

        try
        {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(1000);
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
            Log.d(LOG_TAG, "remote data version = " + verOnServer + ", local data version = " + verLocal);
            return verLocal < verOnServer;
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, e.getMessage());
            return true;
        }
    }

    /**
     * data archive and extract to dstDir
     * @return whether data is download and fully extracted.
     *         Even false is returned, there may be some files already extracted to dstDir.
     */
    private static boolean downloadDataFromServer(String urlStr, String dstDir)
    {
        URL url;
        try
        {
            url = new URL(urlStr);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        }

        Log.d(LOG_TAG, "try downloading data archive from " + urlStr);

        try
        {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setReadTimeout(2000);

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
            return false;
        }

        return true;
    }

    /**
     * 從 apk 複製賽微 TTS 的資料包到指定目錄，要複製的內容已經寫死在方法內
     * @param am
     * @param dstDir 解壓縮到哪裡
     */
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

    /**
     * 從 apk 複製賽微 TTS 的資料包到指定目錄，要複製的內容使用動態方式檢查
     * @param am
     * @param dstDir 解壓縮到哪裡
     */
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

    /**
     * 將 apk 內的檔案解壓縮到 dstDir
     * @param am
     * @param assetPath
     * @param dstDir
     */
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

    /**
     * 將 in 的內容複製到 out
     * @param in
     * @param out
     * @throws IOException
     */
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
