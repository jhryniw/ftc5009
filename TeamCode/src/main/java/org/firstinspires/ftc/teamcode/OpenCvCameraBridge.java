package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.LinearLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by James on 2017-02-22.
 */

public class OpenCvCameraBridge implements CameraBridgeViewBase.CvCameraViewListener2 {
    private Activity mActivity;
    private JavaCameraView mCameraView;
    private BaseLoaderCallback mLoaderCallback;
    private SynchronousQueue<Mat> mFrameQueue = new SynchronousQueue<>();

    private boolean mOpenCvInitialized = false, mCameraEnabled = false;

    OpenCvCameraBridge(Activity activity, final int cameraId) {

        mActivity = activity;

        mLoaderCallback = new BaseLoaderCallback(mActivity) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i("OpenCV", "OpenCV loaded successfully");
                        mOpenCvInitialized = true;

                        mCameraView.connectCamera(480, 640);
                        mCameraView.enableView();
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        mActivity.runOnUiThread(new OpenCvCameraBridge.CameraViewInitializer(this, cameraId));

        Log.d("OpenCV", "Created Beacon Classifier");
    }

    Mat getFrame() {
        Mat frame;

        try {
            enableCamera();
            frame = mFrameQueue.poll(3000, TimeUnit.MILLISECONDS);
            disableCamera();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
            close();
            return null;
        }

        return frame;
    }

    boolean isInitialized() {
        return mOpenCvInitialized;
    }

    /**
     * Camera Callbacks
     */

    private class CameraViewInitializer implements Runnable {
        private CameraBridgeViewBase.CvCameraViewListener2 listener;
        private int mCameraId;
        private CameraViewInitializer(CameraBridgeViewBase.CvCameraViewListener2 l, int cameraId) { listener = l; mCameraId = cameraId; }

        public void run() {
            LinearLayout cameraPreviewLayout = (LinearLayout) mActivity.findViewById(R.id.cameraMonitorViewId);

            mCameraView = new JavaCameraView(mActivity, mCameraId);

            cameraPreviewLayout.addView(mCameraView);

            //mCameraView.setVisibility(SurfaceView.GONE);
            mCameraView.setCvCameraViewListener(listener);
            mCameraView.enableFpsMeter();
            mCameraView.setMaxFrameSize(960, 720);

            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, mActivity, mLoaderCallback);
        }
    }

    private Runnable hEnableCamera = new Runnable() {
        @Override
        public void run() {
            mCameraView.setVisibility(SurfaceView.VISIBLE);
            mCameraView.enableView();
        }
    };

    private Runnable hDisableCamera = new Runnable() {
        @Override
        public void run() {
            mCameraView.disableView();
            mCameraView.setVisibility(SurfaceView.GONE);
        }
    };

    private void enableCamera() {
        mActivity.runOnUiThread(hEnableCamera);
        mCameraEnabled = true;
    }

    private void disableCamera() {
        mActivity.runOnUiThread(hDisableCamera);
        mCameraEnabled = false;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        Log.d("OpenCV", "Frame callback received");

        mFrameQueue.offer(inputFrame.rgba());

        return inputFrame.rgba();
    }

    /**
     * Sets the visibility of the JavaCameraView
     * @param visibility - should be of enum SurfaceView => VISIBLE / GONE
     */
    void setPreviewVisibility(final int visibility) {
        //This prevents the visibility from changing which the SurfaceView is locked, crashing the thread
        if(!mCameraEnabled)
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCameraView.setVisibility(visibility);
                }
            });
    }

    void close() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                disableCamera();
                mCameraView.disconnectCamera();
                mCameraView.setVisibility(SurfaceView.GONE);
            }
        });

        mCameraEnabled = false;
    }
}
