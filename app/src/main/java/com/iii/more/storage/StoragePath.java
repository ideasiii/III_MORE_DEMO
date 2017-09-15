package com.iii.more.storage;

import android.os.Environment;

import java.io.File;

/**
 * Created by joe on 2017/8/22.
 */

public abstract class StoragePath
{
    private static final String DIR_NAME = "/more";
    
    public static String getSavePath()
    {
        File folder = new File(Environment.getExternalStorageDirectory() + DIR_NAME);
        boolean success = false;
        if (!folder.exists())
        {
            success = folder.mkdir();
            if (!success)
            {
                //bug maybe
                return null;
            }
            else
            {
                return Environment.getExternalStorageDirectory() + DIR_NAME;
            }
        }
        else
        {
            return Environment.getExternalStorageDirectory() + DIR_NAME;
        }
        
    }
    
}
