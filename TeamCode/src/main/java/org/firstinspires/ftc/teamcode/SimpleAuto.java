package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2017-02-10.
 */

final class SimpleAuto extends PathBase {
    SimpleAuto(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Simple Auto"); }

        @Override
        void run() throws InterruptedException {
            switch (alliance) {
                case BLUE:
                    robot.encoderDrive(-0.4, 2); //forward a bit
                    robot.pivot(45, 0.3); //face the center vortex
                    robot.encoderDrive(-0.9, 14); //get closer center vortex
                    robot.ballshooter(0.95, 100);
                    robot.feeder(0, 700); //down feed
                    robot.feeder(1, 1000); //up feed
                    robot.feeder(0, 1000); //down feed
                    robot.feeder(1, 1000); //up feed
                    robot.feeder(0 ,200); //down feed
                    robot.feeder(1 , 0); //up feed
                    robot.ballshooter(0, 0);
                    robot.encoderDrive(-1, 25); //knock capball
                    break;
                case RED:
                    robot.encoderDrive(-0.4, 2);
                    robot.pivot(-45, 0.3);
                    robot.encoderDrive(-0.9, 14);
                    robot.ballshooter(0.95, 100);
                    robot.feeder(0, 700); //down feed
                    robot.feeder(1, 1000); //up feed
                    robot.feeder(0, 1000); //down feed
                    robot.feeder(1, 1000); //up feed
                    robot.feeder(0 ,200); //down feed
                    robot.feeder(1 , 0); //up feed
                    robot.ballshooter(0, 0);
                    robot.encoderDrive(-1, 25);
                    break;
    }
}
}
