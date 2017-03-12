package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by szost on 2017-03-11.
 */

public class SlidingBeacon extends PathBase {

    SlidingBeacon(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "SlidingBeacon");
    }
    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.5, 10);
                robot.pivot(-45, 0.2);
                robot.encoderDrive(0.5, 20);
                break;

            case RED:
                robot.encoderDrive(0.5, 10);
                robot.pivot(-45, 0.2);
                robot.encoderDrive(0.5, 10);
                break;
        }
        robot.touchDrive(0.5, robot.touchSensors[0]);
    }
}
