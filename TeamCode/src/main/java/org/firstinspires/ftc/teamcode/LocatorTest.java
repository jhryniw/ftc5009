package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;

/**
 * Created by James on 2017-03-10.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(group = "test", name="LocatorTest")
public class LocatorTest extends LinearOpMode {

    @Override
    public void runOpMode() {
        VuforiaWrapper.init(hardwareMap.appContext);
        RobotLocator.init();

        waitForStart();

        RobotLocator.start();

        while(opModeIsActive()) {
            BeaconTarget target = RobotLocator.getTarget();

            telemetry.addData("Status", !target.isNone() ? "Tracking" : "Not Tracking");

            if(!target.isNone()) {
                telemetry.addData("Robot location", RobotLocator.getRobotLocation().toString());
                telemetry.addData("Pose", target.toVector().toString());
                //telemetry.addData("ROI Right", target.getRoi(true));
                //telemetry.addData("ROI Left", target.getRoi(false));
            }

            telemetry.update();
            idle();
        }

        RobotLocator.stop();
    }
}
