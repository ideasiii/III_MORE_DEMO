package com.iii.more.game.parktour;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.PictureCallback;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iii.more.main.MainApplication;
import com.iii.more.main.R;
import com.iii.more.main.listeners.TTSEventListener;

import sdk.ideas.common.Logs;

@SuppressWarnings("deprecation")
public class CameraActivity extends Activity
{
    private MainApplication application = null;
    private Camera mCamera;
    private CameraPreview mPreview;
    private String mstrPicPath = null;
    private ImageView imgMask = null;
    private ImageView imgCounter = null;
    private int mnIndex = 0;
    private String mstrAnimal = "動物";
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);
        imgMask = findViewById(R.id.imageViewCameraFrame);
        imgCounter = findViewById(R.id.imageViewCounter);
        if (checkCameraHardware(this))
        {
            mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_FRONT, this);
            mCamera.setFaceDetectionListener(new MyFaceDetectionListener());
            mCamera.setDisplayOrientation(90);
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
            findViewById(R.id.imageViewCameraCapture).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mCamera.takePicture(null, null, mPicture);
                }
            });
        }
        
        Bundle b = this.getIntent().getExtras();
        mnIndex = b.getInt("index");
        Logs.showTrace("[CameraActivity] onCreate get intent index: " + mnIndex);
        mstrAnimal = setFrame(mnIndex);
        
        application = (MainApplication) getApplication();
        application.stopFaceEmotion();
        registerService();
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }
    
    private String setFrame(final int nIndex)
    {
        switch (nIndex)
        {
            case Scenarize.SCEN_END_PHOTO_BEAR:
                imgMask.setImageResource(R.drawable.iii_zoo_bear_frame);
                return "黑熊";
            case Scenarize.SCEN_END_PHOTO_LION:
                imgMask.setImageResource(R.drawable.iii_zoo_lion_frame);
                return "獅子";
            case Scenarize.SCEN_END_PHOTO_LEOPARD:
                imgMask.setImageResource(R.drawable.iii_zoo_leopard_frame);
                return "花豹";
            case Scenarize.SCEN_END_PHOTO_MONKEY:
                imgMask.setImageResource(R.drawable.iii_zoo_monkey_frame);
                return "猴子";
        }
        return "動物";
    }
    
    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context)
    {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
        {
            // this device has a camera
            return true;
        }
        else
        {
            // no camera on this device
            return false;
        }
    }
    
    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance(int nType, Context context)
    {
        Camera c = null;
        try
        {
            c = Camera.open(nType); // attempt to get a Camera instance
            Camera.Parameters parameters = c.getParameters();
            parameters.setPreviewSize(800, 1280);
            parameters.setPictureSize(800, 1280);
            parameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);
            parameters.setPictureFormat(ImageFormat.JPEG);
            parameters.setPreviewFrameRate(29);
            parameters.setJpegQuality(100);
            if (isSupportZoom(c))
            {
                parameters.setZoom(1);
            }
            
            c.setParameters(parameters);
            Logs.showTrace("Camera Opened");
        }
        catch (Exception e)
        {
            Logs.showError("Camera is not available (in use or does not exist) Exception: " + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }
    
    /**
     * A basic Camera preview class
     */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
    {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        
        public CameraPreview(Context context, Camera camera)
        {
            super(context);
            mCamera = camera;
            
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        
        public void surfaceCreated(SurfaceHolder holder)
        {
            try
            {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                Logs.showTrace("[CameraActivity] surfaceCreated Start");
                application.playTTS("那我們來跟" + mstrAnimal + "拍張照吧,準備好了嗎", String.valueOf(Scenarize.SCEN_END_CAMERA_OPENED));
            }
            catch (IOException e)
            {
                Logs.showError("Error setting camera preview: " + e.getMessage());
            }
        }
        
        public void surfaceDestroyed(SurfaceHolder holder)
        {
        
        }
        
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
        {
            if (mHolder.getSurface() == null)
            {
                return;
            }
            
            try
            {
                mCamera.stopPreview();
            }
            catch (Exception e)
            {
                Logs.showError("Error stopping camera preview: " + e.getMessage());
            }
            
            try
            {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            }
            catch (Exception e)
            {
                Logs.showError("Error starting camera preview: " + e.getMessage());
            }
        }
    }
    
    public Bitmap convertBmp(Bitmap bmp)
    {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1); // 镜像水平翻转
        matrix.setRotate(270);
        Bitmap convertBmp = Bitmap.createBitmap(bmp, 0, 0, w, h, matrix, true);
        
        return convertBmp;
    }
    
    private PictureCallback mPicture = new PictureCallback()
    {
        
        @Override
        public void onPictureTaken(byte[] data, Camera camera)
        {
            Bitmap bmPhoto = convertBmp(BitmapFactory.decodeByteArray(data, 0, data.length));
            Bitmap bmFrame = null;
            
            switch (mnIndex)
            {
                case Scenarize.SCEN_END_PHOTO_BEAR:
                    bmFrame = BitmapFactory.decodeResource(getResources(), R.drawable.iii_zoo_bear_frame);
                    break;
                case Scenarize.SCEN_END_PHOTO_LION:
                    bmFrame = BitmapFactory.decodeResource(getResources(), R.drawable.iii_zoo_lion_frame);
                    break;
                case Scenarize.SCEN_END_PHOTO_LEOPARD:
                    bmFrame = BitmapFactory.decodeResource(getResources(), R.drawable.iii_zoo_leopard_frame);
                    break;
                case Scenarize.SCEN_END_PHOTO_MONKEY:
                    bmFrame = BitmapFactory.decodeResource(getResources(), R.drawable.iii_zoo_monkey_frame);
                    break;
                default:
                    bmFrame = BitmapFactory.decodeResource(getResources(), R.drawable.iii_photo_frame);
                    break;
            }
            
            
            Bitmap bmCombine = combineBitmap(bmPhoto, bmFrame);
            SaveImage(bmCombine);
            bmCombine.recycle();
            bmFrame.recycle();
            bmPhoto.recycle();
            // close();
            theHandler.sendEmptyMessageDelayed(666, 3000);
            
        }
    };
    
    private void SaveImage(Bitmap finalBitmap)
    {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        Logs.showTrace("[CameraActivity] SaveImage path: " + root);
        File myDir = new File(root + "/edubot");
        if (!myDir.exists())
        {
            myDir.mkdirs();
        }
        
        myDir.setExecutable(true);
        myDir.setReadable(true);
        myDir.setWritable(true);
        
        String strFileName = "edubot_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        File file = new File(myDir, strFileName);
        if (file.exists())
        {
            file.delete();
        }
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            mstrPicPath = file.getPath();
            MediaScannerConnection.scanFile(this, new String[]{mstrPicPath}, null, null);
            
        }
        catch (Exception e)
        {
            Logs.showError("Save Image Exception: " + e.getMessage());
        }
    }
    
    private void releaseCamera()
    {
        if (mCamera != null)
        {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
            Logs.showTrace("Camera Released");
        }
    }
    
    class MyFaceDetectionListener implements Camera.FaceDetectionListener
    {
        @Override
        public void onFaceDetection(Face[] faces, Camera camera)
        {
            if (faces.length > 0)
            {
                Logs.showTrace("face detected: " + faces.length + " Face 1 Location X: " + faces[0].rect.centerX() + "Y: " + faces[0].rect.centerY());
            }
            
        }
    }
    
    public void startFaceDetection()
    {
        // Try starting Face Detection
        Camera.Parameters params = mCamera.getParameters();
        
        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0)
        {
            // camera supports face detection, so can start it:
            mCamera.startFaceDetection();
        }
    }
    
    private static boolean isSupportZoom(Camera camera)
    {
        boolean isSuppport = false;
        if (null != camera && camera.getParameters().isZoomSupported())
        {
            isSuppport = true;
        }
        return isSuppport;
    }
    
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground)
    {
        if (background == null)
        {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        //canvas.drawBitmap(foreground, (bgWidth - fgWidth), (bgHeight - fgHeight), null);
        canvas.drawBitmap(foreground, 0, 0, null);
        
        //this is died
        //canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.restore();
        return newmap;
    }
    
    public static Bitmap combineBarBitmap(Bitmap background, Bitmap foreground)
    {
        if (background == null)
        {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgHeight = foreground.getHeight();
        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, 5, (bgHeight - fgHeight) - 5, null);
        //canvas.save(Canvas.ALL_SAVE_FLAG);
        canvas.save();
        canvas.restore();
        return newmap;
    }
    
    private void close()
    {
        Bundle bundle = new Bundle();
        bundle.putString("picture", mstrPicPath);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
    
    private void registerService()
    {
        application.setTTSEventListener(new TTSEventListener()
        {
            @Override
            public void onInitSuccess()
            {
            
            }
            
            @Override
            public void onInitFailed(int status, String message)
            {
            
            }
            
            @Override
            public void onUtteranceStart(String utteranceId)
            {
            
            }
            
            //=========== TTS 講完幹話後 =============// ,3,2,1,笑一個
            @Override
            public void onUtteranceDone(String utteranceId)
            {
                Logs.showTrace("[CameraActivity] onUtteranceDone utteranceId: " + utteranceId);
                int nIndex = Integer.valueOf(utteranceId);
                switch (nIndex)
                {
                    case Scenarize.SCEN_END_CAMERA_OPENED:
                        imgCounter.setImageResource(R.drawable.iii_counter_3);
                        imgCounter.setVisibility(View.VISIBLE);
                        application.playTTS("3", String.valueOf(Scenarize.SCEN_END_COUNT_THREE));
                        break;
                    case Scenarize.SCEN_END_COUNT_THREE:
                        imgCounter.setImageResource(R.drawable.iii_counter_2);
                        application.playTTS("2", String.valueOf(Scenarize.SCEN_END_COUNT_TWO));
                        break;
                    case Scenarize.SCEN_END_COUNT_TWO:
                        imgCounter.setImageResource(R.drawable.iii_counter_1);
                        application.playTTS("1,笑一個", String.valueOf(Scenarize.SCEN_END_COUNT_ONE));
                        break;
                    case Scenarize.SCEN_END_COUNT_ONE:
                        imgCounter.setVisibility(View.INVISIBLE);
                        mCamera.takePicture(null, null, mPicture);
                        break;
                }
            }
        });
    }
    
    private Handler theHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 666:
                    close();
                    break;
            }
        }
    };
}
