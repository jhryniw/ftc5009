package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
/**
 * Created by szost on 2017-02-13.
 */

final class CloseCorner extends PathBase{
    CloseCorner(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Simple Auto 2");
    }
    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.4, 6);
                robot.ballshooter(0.95, 0);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0 ,200); //down feed
                robot.feeder(1 , 0); //up feed
                robot.ballshooter(0, 0);
                robot.encoderDrive(-0.5, 40);
                robot.pivot(90, 1);
                robot.feeder(1, 2000);
                robot.pivot(-90, 0.4);
                robot.encoderDrive(-0.3, 25);
                break;


            case RED:
                robot.encoderDrive(-0.4, 6);
                robot.ballshooter(0.95, 0);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0 ,1000); //down feed
                robot.feeder(1 , 0); //up feed
                robot.ballshooter(0, 0);
                robot.encoderDrive(-0.5, 40);
                robot.pivot(-90, 1);
                robot.feeder(1, 2000);
                robot.pivot(90, 0.4);
                robot.encoderDrive(-0.3, 25);
                robot.pivot(-60, 0.8);
                break;

        }
    }
}
