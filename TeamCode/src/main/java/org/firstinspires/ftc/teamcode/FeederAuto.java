package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import java.util.Vector;

/**
 * Created by Vicki on 2017-01-31.
 */

final class FeederAuto extends PathBase {

    FeederAuto(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Feeder Auto");
    }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.5, 8); //backward
                robot.ballshooter(1.0, 100);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.ballshooter(0, 0);

                robot.pivot(60, 0.2); //pivot
                robot.encoderDrive(-0.8, 50);
                robot.pivot(57, 0.2);
                break;

            case RED:
                robot.encoderDrive(-0.5, 8); //backward
                robot.ballshooter(1.0, 100);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.ballshooter(0, 0);

                robot.pivot(-60, 0.2); //pivot
                robot.encoderDrive(-0.8, 50);
                robot.pivot(-50, 0.2);
                robot.encoderDrive(0.5, 10);
                break;
        }


        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.5, 29);
                robot.pivot(-45, 0.5);
                robot.pivot(45, 0.3);
                robot.encoderDrive(0.3, 4);
                break;
            case RED:
                robot.encoderDrive(0.5, 29);
                robot.pivot(45, 0.5);
                robot.pivot(-45, 0.3);
                robot.encoderDrive(0.3, 4);


        }


    }


}

