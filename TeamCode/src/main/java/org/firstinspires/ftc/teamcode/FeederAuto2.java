package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2017-02-04.
 */

final class FeederAuto2 extends PathBase {

    FeederAuto2(LinearOpMode opMode, Robot r, Coordinate startLoc) { super(opMode, r, startLoc, "Feeder Auto2"); }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
            robot.encoderDrive(-0.5, 2);
            robot.pivot(15, 0.2);
            robot.ballshooter(0.95, 100);
            robot.feeder(0, 700); //down feed
            robot.feeder(1, 1000); //up feed
            robot.feeder(0, 1000); //down feed
            robot.feeder(1, 1000); //up feed
            robot.feeder(0 ,200); //down feed
            robot.feeder(1 , 0); //up feed
            robot.ballshooter(0, 0);

            robot.pivot(40, 0.4);
            robot.encoderDrive(1, 70);
            robot.pivot(45, 0.3);
                break;


            case RED:
                robot.encoderDrive(-0.5, 2);
                robot.pivot(-15, 0.2);
                robot.ballshooter(0.95, 100);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0 ,200); //down feed
                robot.feeder(1 , 0); //up feed
                robot.ballshooter(0, 0);

                robot.pivot(-40, 0.4);
                robot.encoderDrive(1, 70);
                robot.pivot(-45, 0.3);
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

        if (is_left){
            robot.encoderDrive(-0.2, 3);
            robot.pivot(45, 0.2);
            robot.pivot(-45, 0.2);
        }
        else if (is_right) {
            robot.encoderDrive(-0.2, 3);
            robot.pivot(-45, 0.2);
            robot.pivot(45, 0.2);
        }
        robot.encoderDrive(-0.5, 7);
        robot.encoderDrive(0.5, 7);

        //going parking
        robot.encoderDrive(-0.5, 12);
        robot.encoderDrive(0.5, 10);
        robot.pivot(45, -0.3);
        robot.encoderDrive(0.5, 50);

}
}