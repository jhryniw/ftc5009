package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Created by Vicki on 2017-02-15.
 */

final class FarCorner extends PathBase {
    FarCorner(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Far Corner");
    }
    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.4, 3); //forward a bit
                robot.pivot(45, 0.3); //face the center vortex
                robot.encoderDrive(-0.6, 25); //get closer center vortex
                shoot();
                robot.encoderDrive(-0.6, 45); //knock capball
                robot.pivot(50, 0.4);
                break;
            case RED:
                robot.encoderDrive(-0.4, 3);
                robot.pivot(-45, 0.3);
                robot.encoderDrive(-0.6, 25);
                shoot();
                robot.encoderDrive(-0.6, 45);
                robot.pivot(-35, 0.4);
                break;
        }
    }

    private void shoot() throws InterruptedException{
        robot.ballshooter(0.95, 100);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0 ,200); //down feed
        robot.feeder(1 , 0); //up feed
        robot.ballshooter(0, 0);
    }
}
