package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by szost on 2017-02-10.
 */

final class Case1blueredbluered extends PathBase {

    Case1blueredbluered(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Case1 blue/red blue/red");
    }


    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.5, 5); //backward
                robot.ballshooter(0.95, 1);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 200); //down feed
                robot.feeder(1, 0); //up feed
                robot.ballshooter(0, 0); //stop shooter
                robot.pivot(56, 0.2); //pivot
                robot.encoderDrive(-0.9, 62.2); //gets to beacon
                robot.pivot(55, 0.2);
                robot.encoderDrive(-0.5, 1);
                robot.encoderDrive(-0.2, 3);
                robot.pivot(45, 0.2);
                robot.pivot(-45, 0.2);
                robot.encoderDrive(-0.5, 7);
                robot.encoderDrive(0.5, 7);
                robot.pivot(-92, 0.2);
                robot.encoderDrive(-0.7, 52);
                robot.pivot(92, 0.2);
                robot.encoderDrive(-0.2, 3);
                robot.pivot(45, 0.2);
                robot.pivot(-45, 0.2);
                robot.encoderDrive(-0.5, 12);
                robot.encoderDrive(0.5, 10);
                robot.pivot(45, -0.3);
                robot.encoderDrive(0.5, 50);
                break;

            case RED:
                robot.encoderDrive(-0.5, 5); //backward
                robot.ballshooter(0.95, 100);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.ballshooter(0, 0);
                robot.pivot(-56, 0.2); //pivot
                robot.encoderDrive(0.9, 62.2);
                robot.pivot(-55, 0.2);
                robot.encoderDrive(-0.5, 1);
                robot.encoderDrive(-0.2, 3);
                robot.pivot(-45, 0.2);
                robot.pivot(45, 0.2);
                robot.encoderDrive(-0.5, 7);
                robot.encoderDrive(0.5, 7);
                robot.pivot(92, 0.2);
                robot.encoderDrive(0.7, 52);
                robot.pivot(-92, 0.2);
                robot.encoderDrive(-0.2, 3);
                robot.pivot(-45, 0.2);
                robot.pivot(45, 0.2);
                robot.encoderDrive(-0.5, 12);
                robot.encoderDrive(0.5, 10);
                robot.pivot(45, -0.3);
                robot.encoderDrive(0.5, 50);
                break;
        }
    }
}