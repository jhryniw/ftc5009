package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class is intended to return the state of the beacon (RED/BLUE, BLUE/RED, RED/RED, BLUE/BLUE)
 * Created by James on 2017-02-02.
 */

class BeaconClassifier implements CameraBridgeViewBase.CvCameraViewListener2 {

    private Activity mActivity;
    private JavaCameraView mCameraView;
    private BaseLoaderCallback mLoaderCallback;
    private SynchronousQueue<Mat> mFrameQueue = new SynchronousQueue<>();
    private BeaconMatcher mBeaconMatcher;

    private boolean mOpenCvInitialized = false, mCameraEnabled = false;

    static Scalar BEACON_RED = new Scalar(255, 110, 200);
    static Scalar BEACON_BLUE = new Scalar(50, 220, 255);
    private static double MINIMUM_VALUE = 0.95;

    static Alliance[] CLASSIFICATION_ERROR = new Alliance[] { Alliance.NA, Alliance.NA};

    BeaconClassifier(Activity activity, final int cameraId) {

        mActivity = activity;

        mLoaderCallback = new BaseLoaderCallback(mActivity) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i("OpenCV", "OpenCV loaded successfully");
                        mOpenCvInitialized = true;

                        mBeaconMatcher = new BeaconMatcher(mActivity, 240, 180);
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

        mActivity.runOnUiThread(new CameraViewInitializer(this, cameraId));

        Log.d("OpenCV", "Created Beacon Classifier");
    }

    private double[] scalarDiff(Scalar s1, Scalar s2) {
        //TODO: Assert sizes

        double[] val1 = s1.val;
        double[] val2 = s2.val;

        return new double[] { val1[0] - val2[0], val1[1] - val2[1], val1[2] - val2[2] };
    }

    private double scalarDistanceRed(Scalar s) {
        //Will prioritize red value
        double[] rgbDiff = scalarDiff(s, BEACON_RED);

        return rgbDiff[0] * rgbDiff[0] + Math.sqrt(rgbDiff[1] * rgbDiff[1] + rgbDiff[2] * rgbDiff[2]);
    }

    private double scalarDistanceBlue(Scalar s) {
        //Will prioritize blue value
        double[] rgbDiff = scalarDiff(s, BEACON_BLUE);

        return rgbDiff[2] * rgbDiff[2] + Math.sqrt(rgbDiff[0] * rgbDiff[0] + rgbDiff[1] * rgbDiff[1]);
    }

    Alliance[] classify() {

        if(!mOpenCvInitialized || mCameraView == null)
            return CLASSIFICATION_ERROR;

        Mat frame;
        Alliance lResult, rResult;
        try {
            enableCamera();
            frame = mFrameQueue.poll(3000, TimeUnit.MILLISECONDS);
            disableCamera();

            if(frame == null) {
                Log.e("OpenCV", "Unable to read frame from Camera");
                return CLASSIFICATION_ERROR;
            }

            if(frame.width() == 0 || frame.height() == 0) {
                Log.e("OpenCV", "Frame is of size 0!");
                return CLASSIFICATION_ERROR;
            }

            //Alliance[] result = filterMethod(frame);
            Alliance[] result = BeaconMatcher.beaconTypeToArray(mBeaconMatcher.searchForMatch(frame));

            lResult = result[0];
            rResult = result[1];

        }
        catch(InterruptedException e) {
            e.printStackTrace();
            close();
            return CLASSIFICATION_ERROR;
        }
        catch (Exception e) {
            close();
            return CLASSIFICATION_ERROR;
        }

        return new Alliance[] { lResult , rResult };
    }

    private Alliance[] filterMethod(Mat frame) {
        Alliance lResult, rResult;
        Mat valueMask = new Mat();
        Mat hsv = new Mat(), v = new Mat();

        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);
        Core.extractChannel(hsv, v, 2);

        Imgproc.threshold(v, valueMask, MINIMUM_VALUE, 1, Imgproc.THRESH_BINARY);

        Mat leftMask = new Mat(), rightMask = new Mat();

        Mat zeroHalf = Mat.zeros(frame.rows(), frame.cols() / 2, CvType.CV_8UC1);
        Mat oneHalf = Mat.ones(frame.rows(), frame.cols() - zeroHalf.cols(), CvType.CV_8UC1);

        ArrayList<Mat> lLeftHalf = new ArrayList<>();
        lLeftHalf.add(oneHalf);
        lLeftHalf.add(zeroHalf);

        ArrayList<Mat> lRightHalf = new ArrayList<>();
        lRightHalf.add(zeroHalf);
        lRightHalf.add(oneHalf);

        Core.hconcat(lLeftHalf, leftMask);
        Core.hconcat(lRightHalf, rightMask);
        leftMask = leftMask.mul(valueMask);
        rightMask = rightMask.mul(valueMask);

        //Log.d("OpenCV", "Frame: " + frame.cols() + "x" + frame.rows() + " LeftMask: " + leftMask.cols() + "x" + leftMask.rows() + " RightMask: " + rightMask.cols() + "x" + rightMask.rows());

        Scalar leftAvg = Core.mean(frame, leftMask);
        Scalar rightAvg = Core.mean(frame, rightMask);

        double lDistanceToRed = scalarDistanceRed(leftAvg);
        double lDistanceToBlue = scalarDistanceBlue(leftAvg);

        double rDistanceToRed = scalarDistanceRed(rightAvg);
        double rDistanceToBlue = scalarDistanceBlue(rightAvg);

        lResult = lDistanceToRed < lDistanceToBlue ? Alliance.RED : Alliance.BLUE;
        rResult = rDistanceToRed < rDistanceToBlue ? Alliance.RED : Alliance.BLUE;

        Log.d("OpenCVResult", "Left Average: " + leftAvg.toString() + " D2R: " + lDistanceToRed + " D2B: " + lDistanceToBlue + " Result: " + lResult.toString());
        Log.d("OpenCVResult", "Right Average: " + rightAvg.toString() + " D2R: " + rDistanceToRed + " D2B: " + rDistanceToBlue + " Result: " + rResult.toString());

        return new Alliance[] { lResult, rResult };
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

/**
 * Garbage
 */

/*Mat hsv = new Mat(), h = new Mat(), centers = new Mat(), labels = new Mat();

Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);
Core.extractChannel(hsv, h, 0);
h.convertTo(h, CvType.CV_32F, 1.0 / 255.0);

//Important: kmeans requires a Mat formatted as 32F!
TermCriteria criteria = new TermCriteria(TermCriteria.EPS + TermCriteria.MAX_ITER, 100, 1);
double compactness = Core.kmeans(h, 2, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

//Reshape labels to 2D mat of width and height + scale * 255.0

Log.d("OpenCV - Clustering", "Compactness: " + compactness);
Log.d("OpenCV - Clustering", "Labels - Width: "  + labels.cols() + " Height: " + labels.rows());*/


