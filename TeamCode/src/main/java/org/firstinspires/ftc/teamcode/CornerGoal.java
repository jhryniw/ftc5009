package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-22.
 */

final class CornerGoal extends PathBase {

    CornerGoal(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Corner Goal");
    }

    @Override
    void run() throws InterruptedException {
        //robot.pivot(-90,0.5);
        robot.encoderDrive(-0.5, 41); //off the wall
        robot.pivot(-51, 0.3); //first pivot
        robot.encoderDrive(0.5, 53); //up the corner
        robot.encoderDrive(-0.3, 2 ); //down the corner
        robot.ballgrabber(-0.5, 3000); //ungrabbing
        robot.ballgrabber(0, 0);
        robot.encoderDrive(0.6, 5); //stay on corner
        robot.encoderDrive(0.2, 5);
    }
}
