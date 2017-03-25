package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.TermCriteria;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * This class is intended to return the state of the beacon (RED/BLUE, BLUE/RED, RED/RED, BLUE/BLUE)
 * Created by James on 2017-02-02.
 */

class BeaconClassifier {

    private Activity mActivity;
    private BaseLoaderCallback mLoaderCallback;
    private BeaconMatcher mBeaconMatcher;
    private RobotLocator robotLocator = new RobotLocator();

    private boolean mOpenCvInitialized = false;

    static Scalar BEACON_RED = new Scalar(255, 110, 200);
    static Scalar BEACON_BLUE = new Scalar(50, 220, 255);
    private static double MINIMUM_VALUE = 0.95;

    static Alliance[] CLASSIFICATION_ERROR = new Alliance[] { Alliance.NA, Alliance.NA};

    BeaconClassifier(Activity activity) {

        mActivity = activity;

        mLoaderCallback = new BaseLoaderCallback(mActivity) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i("OpenCV", "OpenCV loaded successfully");
                        mOpenCvInitialized = true;

                        //mBeaconMatcher = new BeaconMatcher(mActivity, 480, 360);
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, mActivity, mLoaderCallback);

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

        if(!mOpenCvInitialized)
            return CLASSIFICATION_ERROR;

        Mat frame = FrameExtractor.getFrame();

        Alliance lResult, rResult;

        //Get frame
        if(frame == null) {
            Log.e("OpenCV", "Unable to read frame from Camera");
            return CLASSIFICATION_ERROR;
        }

        if(frame.width() == 0 || frame.height() == 0) {
            Log.e("OpenCV", "Frame is of size 0!");
            return CLASSIFICATION_ERROR;
        }

        //FrameExtractor.saveFrame(frame, "frame.png");

        Alliance[] result = meanMethod(frame);
        //Alliance[] result = BeaconMatcher.beaconTypeToArray(mBeaconMatcher.searchForMatch(frame));

        lResult = result[0];
        rResult = result[1];

        return new Alliance[] { lResult , rResult };
    }

    private Alliance[] meanMethod(Mat frame) {
        Alliance lResult, rResult;

        BeaconTarget target = RobotLocator.getTarget();

        VectorF roiLeft = target.getRoi(false);
        Point lp1 = new Point(roiLeft.get(0), roiLeft.get(1));
        Point lp2 = new Point(roiLeft.get(2), roiLeft.get(3));

        VectorF roiRight = target.getRoi(true);
        Point rp1 = new Point(roiRight.get(0), roiRight.get(1));
        Point rp2 = new Point(roiRight.get(2), roiRight.get(3));

        Mat leftMask = Mat.zeros(frame.size(), CvType.CV_8U);
        Mat lROI = leftMask.submat((int)lp1.y, (int)lp2.y, (int)lp1.x, (int)lp2.x);
        lROI.setTo(Scalar.all(255));

        Mat rightMask = Mat.zeros(frame.size(), CvType.CV_8U);
        Mat rROI = rightMask.submat((int)rp1.y, (int)rp2.y, (int)rp1.x, (int)rp2.x);
        rROI.setTo(Scalar.all(255));

        /*Mat lMaskedImage = new Mat();
        frame.copyTo(lMaskedImage, leftMask);
        FrameExtractor.saveFrame(lMaskedImage, "leftMask.png");

        Mat rMaskedImage = new Mat();
        frame.copyTo(rMaskedImage, rightMask);
        FrameExtractor.saveFrame(rMaskedImage, "rightMask.png");*/

        double[] leftMean = Core.mean(frame, leftMask).val;
        double[] rightMean = Core.mean(frame, rightMask).val;

        //lResult = leftMean[0] > leftMean[2] ? Alliance.RED : Alliance.BLUE;
        //rResult = rightMean[0] > rightMean[2] ? Alliance.RED : Alliance.BLUE;

        lResult = leftMean[0] > rightMean[0] ? Alliance.RED : Alliance.BLUE;
        rResult = lResult == Alliance.RED ? Alliance.BLUE : Alliance.RED;

        return new Alliance[] { lResult, rResult };
    }

    private Alliance[] momentMethod(Mat frame) {
        //TODO: complete this method
        return new Alliance[] {Alliance.NA, Alliance.NA};
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
}


