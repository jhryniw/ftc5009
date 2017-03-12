package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.CameraCalibration;
import com.vuforia.CameraDevice;
import com.vuforia.Frame;
import com.vuforia.HINT;
import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.nio.ByteBuffer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by James on 2016-12-10.
 */

class RobotLocator {

    static float MM_PER_INCH = 25.4f;
    private static int FPS = 30;
    private static int MS_CYCLE_TIME = (int) (1000 / FPS);

    private static VectorF robotLocation = new VectorF(0, 0, 0);
    private static BeaconTarget poseToTarget = new BeaconTarget();

    private static VuforiaTrackables beacons;

    private static boolean isTracking = false;

    //TODO: Check that axes match for all images!
    private static OpenGLMatrix wheelsPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 58.5f * MM_PER_INCH)
                                                      .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0)); //90 degrees in y

    private static OpenGLMatrix legoPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 108.5f * MM_PER_INCH)
                                                    .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0));

    private static OpenGLMatrix toolsPosition = OpenGLMatrix.translation(107 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private static OpenGLMatrix gearsPosition = OpenGLMatrix.translation(59 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private static OpenGLMatrix phoneOffset = OpenGLMatrix.translation(0 * MM_PER_INCH, 9.5f * MM_PER_INCH, 0 * MM_PER_INCH);

    public static void init() {

        beacons = VuforiaWrapper.Instance.loadTrackablesFromAsset("FTC_2016-17");

        beacons.get(0).setName("Wheels");
        beacons.get(0).setLocation(wheelsPosition);

        beacons.get(1).setName("Tools");
        beacons.get(1).setLocation(toolsPosition);

        beacons.get(2).setName("Lego");
        beacons.get(2).setLocation(legoPosition);

        beacons.get(3).setName("Gears");
        beacons.get(3).setLocation(gearsPosition);

        beacons = applyPhoneInformation(beacons);

        beacons.activate();
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        Log.d("Vuforia", "Vuforia Initialized");
    }

    static BeaconTarget getTarget() {
        return new BeaconTarget(poseToTarget);
    }

    static VectorF getPose() { return getPose(true); }
    static VectorF getPose(boolean runUpdate) {
        if(runUpdate)
            updateLocation();

        return poseToTarget.toVector();
    }

    static VectorF getRobotLocation() { return getRobotLocation(true); }
    static VectorF getRobotLocation(boolean runUpdate) {
        if(runUpdate)
            updateLocation();

        return new VectorF(robotLocation.getData());
    }

    static float[] getRobotLocationXZ() { return getRobotLocationXZ(true); }
    static float[] getRobotLocationXZ(boolean runUpdate) {
        if(runUpdate)
            updateLocation();

        return new float[] { robotLocation.get(0), robotLocation.get(2) };
    }

    static boolean isTracking() {
        return isTracking;
    }

    static VectorF getEuler(OpenGLMatrix pose) {
        double heading, pitch, roll;

        // Assuming the angles are in radians.
        if (pose.get(1, 0) > 0.998) { // singularity at north pole
            heading = Math.atan2(pose.get(0, 2), pose.get(2, 2));
            pitch = Math.PI/2;
            roll = 0;
        }
        else if (pose.get(1, 0) < -0.998) { // singularity at south pole
            heading = Math.atan2(pose.get(0, 2), pose.get(2, 2));
            pitch = -Math.PI/2;
            roll = 0;
        }
        else {
            heading = Math.atan2(pose.get(2, 0), pose.get(0, 0));
            pitch = Math.atan2(-pose.get(1, 2), pose.get(1, 1));
            roll = Math.asin(pose.get(1, 0));
        }

        return new VectorF((float) Math.toDegrees(roll), (float) Math.toDegrees(heading), (float) Math.toDegrees(pitch));
    }

    private static VuforiaTrackables applyPhoneInformation(VuforiaTrackables trackables) {
        for (VuforiaTrackable t : trackables) {
            VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) t.getListener();
            listener.setPhoneInformation(phoneOffset, VuforiaLocalizer.CameraDirection.BACK);
            t.setListener(listener);
        }

        return trackables;
    }

    static void updateLocation() {

        try {
            boolean tracking = false;

            for (VuforiaTrackable b : beacons) {
                VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener) b.getListener());
                OpenGLMatrix update = listener.getUpdatedRobotLocation();

                if (listener.getPose() != null) {
                    tracking = true;
                }

                if (update != null) {
                    robotLocation = update.getTranslation().multiplied(1f / MM_PER_INCH);
                    poseToTarget = new BeaconTarget(b.getName(), listener.getPose());
                    break;
                }
            }

            isTracking = tracking;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
