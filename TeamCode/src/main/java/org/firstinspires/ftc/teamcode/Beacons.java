package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-15.
 */

final class Beacons extends PathBase {

    Beacons(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Beacons");
    }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.4, 3); //backward
                shoot();
                robot.pivot(-45, 0.2); //pivot
                robot.encoderDrive(-0.9 , 60.2); //diagnol beacon
                robot.pivot(-45, 0.2); // pivot to face beacon
                robot.encoderDrive(-0.5 , 5); //inch closer to beacon
                break;

            case RED:
                robot.encoderDrive(-0.4, 5); //backward
                shoot();
                robot.pivot(45, 0.2); //pivot
                robot.encoderDrive(-0.9, 58.2);
                robot.pivot(45, 0.2);
                robot.encoderDrive(-0.5 , 5);
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
            robot.moveSlider(Hardware.SLIDER_TRACK_LENGTH / 4);
        }
        else if (is_right) {
            robot.moveSlider(Hardware.SLIDER_TRACK_LENGTH / 4 * 3);
        }

        robot.encoderDrive(0.5, 20);

        /*if (is_left){
            //robot.encoderDrive(-0.3, 5);
            robot.pivot(45, 0.2);
            robot.encoderDrive(-0.5, 5);
            robot.pivot(-45, 0.2);
            robot.encoderDrive(-0.5, 12);
        }
        else if (is_right) {
            //robot.encoderDrive(-0.3, 5);
            robot.pivot(-45, 0.2);
            robot.encoderDrive(-0.5, 5);
            robot.pivot(45, 0.2);
            robot.encoderDrive(-0.5, 10);
        }

        robot.encoderDrive(0.5, 20);

        switch (alliance) {
            case BLUE:
                robot.pivot(-90, 0.2);
                robot.encoderDrive(-0.8, 52);
                robot.pivot(90, 0.2);
                robot.encoderDrive(-0.8, 15);
                break;
            case RED:
                robot.pivot(90, 0.2);
                robot.encoderDrive(0.8, 52);
                robot.pivot(-90, 0.2);
                robot.encoderDrive(-0.8, 8);
                break;
        }

        //Alliance[] result2 = robot.beaconClassifier.classify();
        Alliance[] result2 = { Alliance.RED, Alliance.BLUE };

        if(result2 == BeaconClassifier.CLASSIFICATION_ERROR) {
            robot.opMode.telemetry.addData("OpenCV", "Error...");
            return;
        }
        else {
            robot.opMode.telemetry.addData("OpenCV", "Classification succeeded!");
            robot.opMode.telemetry.addData("OpenCV", "Result { %s, %s }", result2[0].toString(), result2[1].toString());
        }

        robot.opMode.telemetry.update();

        boolean is_left2 = (alliance == result2[0]);
        boolean is_right2 = (alliance == result2[1]);

        if (is_left2) {
            robot.encoderDrive(-0.2, 20);
            robot.pivot(45, 0.2);
            robot.encoderDrive(-0.5, 7);
            robot.pivot(-45, 0.2);
            robot.encoderDrive(0.5, 10);
        }
        else if (is_right2) {
            robot.encoderDrive(-0.2, 20);
            robot.pivot(-45, 0.2);
            robot.encoderDrive(-0.5, 7);
            robot.pivot(45, 0.2);
            robot.encoderDrive(-0.5, 10);
        }

        robot.encoderDrive(0.5, 10);
        robot.pivot(45, -0.3);
        robot.encoderDrive(0.5, 50);*/
    }

    private void shoot() throws InterruptedException {
        robot.ballshooter(0.95, 1);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0 ,200); //down feed
        robot.feeder(1 , 0); //up feed
        robot.ballshooter(0, 0); //stop shooter
    }
}