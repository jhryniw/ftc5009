package org.firstinspires.ftc.teamcode;

import android.graphics.Path;
import android.os.AsyncTask;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.vuforia.HINT;
import com.vuforia.Vuforia;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.R;

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

/**
 * Created by James on 2016-12-10.
 */

@Autonomous (name="VuforiaTest", group="vuforia")
public class VuforiaTest extends LinearOpMode {

    private static float MM_PER_INCH = 25.4f;

    private VectorF robotLocation = new VectorF(0, 0, 0);
    private VuforiaTrackables beacons;
    private LocationFetcher fetcher = new LocationFetcher();

    private boolean isTracking = false;

    //TODO: Check that axes match for all images!
    private OpenGLMatrix wheelsPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 58.5f * MM_PER_INCH)
                                                      .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0)); //90 degrees in y

    private OpenGLMatrix legoPosition = OpenGLMatrix.translation(142 * MM_PER_INCH, 5 * MM_PER_INCH, 108.5f * MM_PER_INCH)
                                                    .multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.YXY, AngleUnit.DEGREES, -90, 0, 0));

    private OpenGLMatrix toolsPosition = OpenGLMatrix.translation(107 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private OpenGLMatrix gearsPosition = OpenGLMatrix.translation(59 * MM_PER_INCH, 5 * MM_PER_INCH, 142 * MM_PER_INCH);

    private OpenGLMatrix phoneOffset = OpenGLMatrix.translation(-5 * MM_PER_INCH, 9.5f * MM_PER_INCH, 2.25f * MM_PER_INCH);

    @Override
    public void runOpMode() throws InterruptedException {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = hardwareMap.appContext.getString(R.string.vuforia_license_key); //"AW5ADSz/////AAAAGZwQUUhq3k+GnK7vAfcImu4iQkhXG40fIHzKKwouEq4vAAtcpPlheUJOnrPCeHmsgF4SBZmierGuoSVWmgjQ/yCUKnJWTAt8CpVBSJWV3uP0FoI61JtCC/0JBK5ehCITvHlGzWtMQyyl4yb7qIXjAKZeiDI4ztBPwODBpJLOvASrNSYWD+Wo+UdILBdIfMmisTg3gKSFeGnV5YQmmZKIh8Ikzjh5/GrT4yWsmHZdIpZF2JFQA3V8wSqSCuKIi/CQGarB+k24MH8l/+dcXt8PxlW16cjUHjT86KlDhLfioUTcWUYIRR1CE/BtX8zUnV5FUimQXsiBRn3DZHGQQ+jJW/omsEhWe2ApwIrbsdp56KJh";
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

        waitForStart();

        fetcher.execute();

        while(opModeIsActive()) {
            telemetry.addData("Status:", isTracking ? "Tracking" : "Not Tracking");
            telemetry.addData("Robot location:", robotLocation.toString());

            telemetry.update();
            idle();
        }
    }

    private VuforiaTrackables applyPhoneInformation(VuforiaTrackables trackables) {
        for (VuforiaTrackable t : trackables) {
            VuforiaTrackableDefaultListener listener = (VuforiaTrackableDefaultListener) t.getListener();
            listener.setPhoneInformation(phoneOffset, VuforiaLocalizer.CameraDirection.BACK);
            t.setListener(listener);
        }

        return trackables;
    }

    private class LocationFetcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void[] params) {

            beacons.activate();

            while(opModeIsActive()) {
                boolean tracking = false;

                for (VuforiaTrackable b : beacons) {
                    VuforiaTrackableDefaultListener listener = ((VuforiaTrackableDefaultListener) b.getListener());
                    OpenGLMatrix update = listener.getUpdatedRobotLocation();

                    if(listener.getPose() != null) {
                        tracking = true;
                    }

                    if (update != null) {
                        robotLocation = update.getTranslation().multiplied(1f / MM_PER_INCH);
                        break;
                    }
                }

                isTracking = tracking;

                idle();
            }

            return null;
        }
    }
}
