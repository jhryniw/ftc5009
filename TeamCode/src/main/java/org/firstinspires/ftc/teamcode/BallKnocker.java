package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-15.
 */

final class BallKnocker extends PathBase {

    BallKnocker(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Ball Knocker");
    }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(0.5, 82);
    }
}
