package com.edgecase.moduleservice;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.affectiva.android.affdex.sdk.detector.FrameDetector;

import java.util.List;

public class DetectorService extends BaseDetectorService {

    static private final String TAG = DetectorService.class.getSimpleName();
    private HandlerThread detectionThread;
    private DetectionHandler detectionHandler;
    public MyBinder myBinder = new MyBinder();
    static public boolean enableLog = false;

    public boolean getEnableLog() {
        return enableLog;
    }
    public void setEnableLog(boolean value) {
        enableLog = value;
    }

    // 綁定此 Service 的物件
    public class MyBinder extends Binder {
        public DetectorService getService() {
            LOG.e(TAG, "getService");
            return DetectorService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        LOG.e(TAG, "onBind");

        if (!CameraHelper.checkPermission(this)) {
            LOG.w(TAG, "app does not have camera permission, stopping service");
            stopSelf();
        } else if (detectionThread == null) {
            // fire up the background thread
            detectionThread = new DetectionThread();
            detectionThread.start();
            detectionHandler = new DetectionHandler(getApplicationContext(), detectionThread);
            detectionHandler.sendStartMessage();
        }

        return myBinder;
    }

    @Override
    public void onDestroy() {
        LOG.e(TAG, "onDestroy");
        if (detectionHandler != null) {
            detectionHandler.sendStopMessage();
            try {
                detectionThread.join();
                detectionThread = null;
                detectionHandler = null; // facilitate GC
            } catch (InterruptedException ignored) {
            }
        }
        super.onDestroy();
    }

    private static class DetectionThread extends HandlerThread {
        private DetectionThread() {
            super("DetectionThread");
        }
    }

    /**
     * A handler for the DetectionThread.
     */
    private static class DetectionHandler extends Handler {
        //Incoming message codes
        private static final int START = 0;
        private static final int STOP = 1;

        private CameraHelper cameraHelper;
        private FrameDetector frameDetector;
        private SurfaceTexture surfaceTexture;
        private DetectorListener listener;

        private DetectionHandler(Context context, HandlerThread detectionThread) {
            // note: getLooper will block until the the thread's looper has been prepared
            super(detectionThread.getLooper());

            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            cameraHelper = new CameraHelper(context, display, new CameraHelperListener());
            surfaceTexture = new SurfaceTexture(0); // a dummy texture

            // Set up the FrameDetector.  For the purposes of this sample app, we'll just request
            // listen for face events and request valence scores.
            frameDetector = new FrameDetector(context);
            frameDetector.setDetectAllEmotions(true);
            frameDetector.setDetectAllExpressions(true);
            frameDetector.setDetectAllAppearances(true);
            frameDetector.setDetectAllEmojis(false);
            frameDetector.setDetectGender(true);

            listener = new DetectorListener(context);
            frameDetector.setImageListener(listener);
            frameDetector.setFaceListener(listener);
        }

        /**
         * asynchronously start processing on the background thread
         */
        private void sendStartMessage() {
            sendMessage(obtainMessage(START));
        }

        /**
         * asynchronously stop processing on the background thread
         */
        private void sendStopMessage() {
            sendMessage(obtainMessage(STOP));
        }

        /**
         * Process incoming messages
         *
         * @param msg message to handle
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    LOG.d(TAG, "starting background processing of frames");
                    try {
                        frameDetector.start();
                        //noinspection deprecation
                        cameraHelper.acquire(Camera.CameraInfo.CAMERA_FACING_FRONT);
                        cameraHelper.start(surfaceTexture); // initiates previewing
                    } catch (IllegalStateException e) {
                        LOG.d(TAG, "couldn't open camera: " + e.getMessage());
                        return;
                    }
                    break;
                case STOP:
                    LOG.d(TAG, "stopping background processing of frames");
                    cameraHelper.stop(); // stops previewing
                    cameraHelper.release();
                    frameDetector.stop();

                    LOG.d(TAG, "quitting detection thread");
                    ((HandlerThread) getLooper().getThread()).quit();
                    break;

                default:
                    break;
            }
        }

        /**
         * A listener for CameraHelper callbacks
         */
        private class CameraHelperListener implements CameraHelper.Listener {
            private static final float TIMESTAMP_DELTA = .01f;
            private float lastTimestamp = -1f;

            @Override
            public void onFrameAvailable(byte[] frame, int width, int height, Frame.ROTATE rotation) {
                float timeStamp = (float) SystemClock.elapsedRealtime() / 1000f;
                if (timeStamp > (lastTimestamp + TIMESTAMP_DELTA)) {
                    lastTimestamp = timeStamp;
                    frameDetector.process(createFrameFromData(frame, width, height, rotation), timeStamp);
                }
            }

            @Override
            public void onFrameSizeSelected(int width, int height, Frame.ROTATE rotation) {
            }

            private Frame createFrameFromData(byte[] frameData, int width, int height, Frame.ROTATE rotation) {
                Frame.ByteArrayFrame frame = new Frame.ByteArrayFrame(frameData, width, height, Frame.COLOR_FORMAT.YUV_NV21);
                frame.setTargetRotation(rotation);
                return frame;
            }
        }

        /**
         * A listener for FrameDetector callbacks
         */
        private static class DetectorListener implements FrameDetector.ImageListener, FrameDetector.FaceListener {
            private Context context;

            private DetectorListener(Context context) {
                this.context = context;
            }

            @Override
            public void onFaceDetectionStarted() {
                if (mOnDetectionListener != null) {
                    mOnDetectionListener.onFaceDetected(true);
                }
            }

            @Override
            public void onFaceDetectionStopped() {
                if (mOnDetectionListener != null) {
                    mOnDetectionListener.onFaceDetected(false);
                }
            }

            @Override
            public void onImageResults(List<Face> faces, Frame frame, float v) {
                if (mOnDetectionListener != null) {
                    mOnDetectionListener.onImageResults(faces, frame, v);
                }
            }
        }
    }
}

