package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.HINT;
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

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by James on 2016-12-10.
 */

public class RobotLocator extends AsyncTask<Void, Void, Void> {

    private static float MM_PER_INCH = 25.4f;
    private static int FPS = 30;
    private static int MS_CYCLE_TIME = (int) (1000 / FPS);

    private VectorF robotLocation = new VectorF(0, 0, 0);
    private VuforiaTrackables beacons;

    private long last_cycle;
    private boolean isTracking = false;
    private int fps = 0;

    //TODO: Check that axes match for all images!
    private OpenGLMatrix wheelsPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 58.5f * MM_PER_INCH)
                                                      .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0)); //90 degrees in y

    private OpenGLMatrix legoPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 108.5f * MM_PER_INCH)
                                                    .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0));

    private OpenGLMatrix toolsPosition = OpenGLMatrix.translation(107 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private OpenGLMatrix gearsPosition = OpenGLMatrix.translation(59 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private OpenGLMatrix phoneOffset = OpenGLMatrix.translation(-5 * MM_PER_INCH, 9.5f * MM_PER_INCH, 2.25f * MM_PER_INCH);

    public void init(Context ctx) {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = ctx.getString(R.string.vuforia_license_key); //"AW5ADSz/////AAAAGZwQUUhq3k+GnK7vAfcImu4iQkhXG40fIHzKKwouEq4vAAtcpPlheUJOnrPCeHmsgF4SBZmierGuoSVWmgjQ/yCUKnJWTAt8CpVBSJWV3uP0FoI61JtCC/0JBK5ehCITvHlGzWtMQyyl4yb7qIXjAKZeiDI4ztBPwODBpJLOvASrNSYWD+Wo+UdILBdIfMmisTg3gKSFeGnV5YQmmZKIh8Ikzjh5/GrT4yWsmHZdIpZF2JFQA3V8wSqSCuKIi/CQGarB+k24MH8l/+dcXt8PxlW16cjUHjT86KlDhLfioUTcWUYIRR1CE/BtX8zUnV5FUimQXsiBRn3DZHGQQ+jJW/omsEhWe2ApwIrbsdp56KJh";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");

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
    }

    public VectorF getRobotLocation() {
        updateLocation();
        return robotLocation;
    }

    public float[] getRobotLocationXZ() {
        updateLocation();
        return new float[] { robotLocation.get(0), robotLocation.get(2) };
    }

    public boolean isTracking() {
        return this.isTracking;
    }

    public int getFps() { return fps; }

    public void launch() {
        //this.execute();
    }

    public void halt() {
        //this.cancel(true);
        //isTracking = false;S
    }

    private VuforiaTrackables applyPhoneInformation(VuforiaTrackables trackables) {
        for (VuforiaTrackable t : trackables) {
            VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) t.getListener();
            listener.setPhoneInformation(phoneOffset, VuforiaLocalizer.CameraDirection.BACK);
            t.setListener(listener);
        }

        return trackables;
    }

    private void updateLocation() {

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
                    break;
                }
            }

            isTracking = tracking;
        }
        catch (Exception e) {

        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Debug.waitForDebugger();
        last_cycle = System.currentTimeMillis();

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
                    break;
                }
            }

            isTracking = tracking;

            long duration = System.currentTimeMillis() - last_cycle;

            if (duration < MS_CYCLE_TIME) {
                fps = FPS;
                Thread.sleep(MS_CYCLE_TIME - duration);
            }
            else{
                fps = (int) (1000 / duration);
            }

            last_cycle = System.currentTimeMillis();
        }
        catch(InterruptedException e) {
            halt();
        }
        catch (Exception e) {
            halt();
            launch();
        }

        return null;
    }
}
