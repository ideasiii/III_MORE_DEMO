package com.iii.more.main;

/**
 * Created by joe on 2017/7/11.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;

import java.io.File;

import sdk.ideas.common.Logs;

/**
 * Created by Keval on 11-Nov-16.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */

public class DemoCamService extends HiddenCameraService
{
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            if (HiddenCameraUtils.canOverDrawOtherApps(this))
            {
                try
                {
                    for (int i = 0; i < 100; i++)
                    {
                        CameraConfig cameraConfig = new CameraConfig()
                                .getBuilder(DemoCamService.this)
                                .setImageFile(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/test" + String.valueOf(i) + ".jpg"))
                                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                                .build();
                        startCamera(cameraConfig);
                        
                        new android.os.Handler().post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                takePicture();
                            }
                        });
                        
                        Thread.sleep(40);
                        
                    }
                }
                catch (InterruptedException e)
                {
                    Logs.showError(e.toString());
                }
                catch (Exception e)
                {
                    Logs.showError(e.toString());
                }
            }
            else
            {
                
                //Open settings to grant permission for "Draw other apps".
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
            }
        }
        else
        {
            //TODO Ask your parent activity for providing runtime permission
            Toast.makeText(this, "Camera permission not available", Toast.LENGTH_SHORT).show();
        }
        return START_NOT_STICKY;
    }
    
    
    @Override
    public void onImageCapture(@NonNull File imageFile)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        //Do something with the bitmap
        
        Log.d("Image capture", imageFile.length() + "");
        stopSelf();
    }
    
    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode)
    {
        switch (errorCode)
        {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                Toast.makeText(this, "Cannot open camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                Toast.makeText(this, "Cannot write image captured by camera.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                Toast.makeText(this, "Camera permission not available.", Toast.LENGTH_LONG).show();
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                Toast.makeText(this, "Your device does not have front camera.", Toast.LENGTH_LONG).show();
                break;
        }
        
        stopSelf();
    }
    
    
}