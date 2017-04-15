package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by szost on 2017-03-11.
 */

final class SlidingBeacon extends PathBase {

    SlidingBeacon(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "SlidingBeacon");
    }
    @Override
     protected void run() throws InterruptedException {
        robot.moveSlider(Hardware.SLIDER_TRACK_LENGTH / 2);
        robot.resetSlider(true);
        robot.encoderDrive(-0.3, 20);
    }
}
