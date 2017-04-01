package org.firstinspires.ftc.teamcode;

import android.widget.Toast;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by James on 2017-03-18.
 */

@Autonomous(name="PIDTesting", group="test")
public class PIDTesting extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {

        Robot robot = new Robot("proto1", hardwareMap, this);

        DialogUtil.buildConfigDialog(hardwareMap.appContext);

        waitForStart();
        RobotLocator.start();

        robot.moveToTarget();

        RobotLocator.stop();
    }
}
