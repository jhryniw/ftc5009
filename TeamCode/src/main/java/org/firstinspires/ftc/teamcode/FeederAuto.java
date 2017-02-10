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
                robot.pivot(50, 0.2);
                robot.encoderDrive(-0.5, 5);
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
                robot.encoderDrive(-0.5, 5);
                break;
        }
        Alliance[] result = robot.beaconClassifier.classify();
        //Alliance[] result = { Alliance.RED, Alliance.BLUE };

        if(result == BeaconClassifier.CLASSIFICATION_ERROR) {
            opMode.telemetry.addData("OpenCV", "Error...");
            return;
        }
        else {
            opMode.telemetry.addData("OpenCV", "Classification succeeded!");
            opMode.telemetry.addData("OpenCV", "Result { %s, %s }", result[0].toString(), result[1].toString());
        }

        opMode.telemetry.update();

        boolean is_left = (alliance == result[0]);
        boolean is_right = (alliance == result[1]);

        if (is_left) {
            robot.pivot(-45, 0.2);
            robot.pivot(45, 0.2);
        }
        else if (is_right) {
            robot.pivot(45, 0.2);
            robot.pivot(-45, 0.2);
        }
            robot.encoderDrive(-0.5, 7);
            robot.encoderDrive(1, 57);




        }


    }




