package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.vuforia.HINT;
import com.vuforia.Vuforia;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by James on 2016-12-10.
 */

@Autonomous (name="VuforiaTest", group="vuforia")
public class VuforiaTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AW5ADSz/////AAAAGZwQUUhq3k+GnK7vAfcImu4iQkhXG40fIHzKKwouEq4vAAtcpPlheUJOnrPCeHmsgF4SBZmierGuoSVWmgjQ/yCUKnJWTAt8CpVBSJWV3uP0FoI61JtCC/0JBK5ehCITvHlGzWtMQyyl4yb7qIXjAKZeiDI4ztBPwODBpJLOvASrNSYWD+Wo+UdILBdIfMmisTg3gKSFeGnV5YQmmZKIh8Ikzjh5/GrT4yWsmHZdIpZF2JFQA3V8wSqSCuKIi/CQGarB+k24MH8l/+dcXt8PxlW16cjUHjT86KlDhLfioUTcWUYIRR1CE/BtX8zUnV5FUimQXsiBRn3DZHGQQ+jJW/omsEhWe2ApwIrbsdp56KJh";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");

        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Lego");
        beacons.get(3).setName("Gears");

        waitForStart();

        beacons.activate();

        while(opModeIsActive()) {
            for (VuforiaTrackable b : beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) b.getListener()).getPose();

                if (pose != null) {
                    VectorF translation = pose.getTranslation();

                    telemetry.addData(b.getName(), translation);
                }
            }

            telemetry.update();
        }
    }
}
