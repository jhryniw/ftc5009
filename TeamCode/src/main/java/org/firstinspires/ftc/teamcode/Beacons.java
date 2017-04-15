package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-15.
 */

final class Beacons extends PathBase {

    int classificationFailures = 0;

    Beacons(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Beacons");
    }


    // - counterclockwise, + clockwise

    @Override
    protected void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:

                robot.encoderDrive(-0.3, 5); //backward
                shoot();
                robot.pivot(45, 0.3); //pivot
                robot.encoderDrive(-0.9 , 66); //diagonal beacon
                robot.pivot(45, 0.3); // pivot to face beacon
                //robot.encoderDrive(-0.5 , 6); //inch closer to beacon
                break;

            case RED:
                robot.encoderDrive(-0.3, 5); //backward
                shoot();
                robot.pivot(45, 0.3); //pivot
                robot.encoderDrive(-0.9, 66);
                robot.pivot(45, 0.3);
                //robot.encoderDrive(-0.5 , 6);
                break;
        }

        RobotLocator.start();
        Thread.sleep(1000);

        pushBeacon();

        RobotLocator.stop();

        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.9, 11);
                robot.resetSlider(true);
                robot.pivot(-90, 0.2);
                robot.encoderDrive(-0.8, 59);
                robot.pivot(90, 0.2);
                break;
            case RED:
                robot.encoderDrive(0.9, 11);
                robot.resetSlider(true);
                robot.pivot(90, 0.2);
                robot.encoderDrive(0.8, 59);
                robot.pivot(-90, 0.2);
                //robot.encoderDrive(-0.8, 8);
                break;
        }

        RobotLocator.start();
        Thread.sleep(1000);

        pushBeacon();

        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.6, 10);
                robot.pivot(-40, 0.2);
                robot.encoderDrive(0.9, 35);
                //robot.pivot(-45, 0.2);
                robot.encoderDrive(0, 0);
                break;
            case RED:
                robot.encoderDrive(0.6, 10);
                robot.pivot(40, 0.2);
                robot.encoderDrive(0.9, 35);
                //robot.pivot(45, 0.2);
                //robot.encoderDrive(-0.5, 10);

                break;
        }
    }

    private void pushBeacon() throws InterruptedException {
        //Alliance[] result = { Alliance.RED, Alliance.BLUE };
        BeaconTarget target = RobotLocator.getTarget();

        if(target.isNone()) {
            classificationFailures++;
            classificationErrorProtocol();
        }

        Alliance[] result = robot.beaconClassifier.classify();

        if(result == BeaconClassifier.CLASSIFICATION_ERROR) {
            opMode.telemetry.addData("OpenCV", "Classification Error...");

            classificationFailures++;

            //Failure Protocol should end in moveSliderToBeacon
            classificationErrorProtocol();
            return;
        }
        else {
            opMode.telemetry.addData("OpenCV", "Classification succeeded!");
            opMode.telemetry.addData("OpenCV", "Result { %s, %s }", result[0].toString(), result[1].toString());
            opMode.telemetry.update();
            classificationFailures = 0;
        }

        boolean is_left = (alliance == result[0]);
        boolean is_right = (alliance == result[1]);
        double xDist = target.getX() + Hardware.SLIDER_TRACK_LENGTH / 2;

        if (is_left)
            xDist -= BeaconTarget.BUTTON_X_OFFSET;
        else if (is_right)
            xDist += BeaconTarget.BUTTON_X_OFFSET;

        xDist = Hardware.bound(xDist, 0, Hardware.SLIDER_TRACK_LENGTH);
        robot.moveSlider(xDist);

        double zDist = target.getZ() + BeaconTarget.BUTTON_Z_OFFSET;
        //opMode.telemetry.addData("ButtonPusher", "Z Distance: %.2f", zDist);
        //opMode.telemetry.update();
        //Thread.sleep(1000);
        robot.encoderDrive(-0.5, zDist);
        robot.encoderDrive(-0.3, 5);
    }

    private void classificationErrorProtocol() throws InterruptedException {
        switch(classificationFailures) {
            case 1:
                //Simply trying again
                opMode.telemetry.addData("BeaconClassifier", "Recovery Mode %d: Trying Again", classificationFailures);
                pushBeacon();
                break;
            case 2:
                //Try restarting the locator
                opMode.telemetry.addData("BeaconClassifier", "Recovery Mode %d: Restarting RobotLocator", classificationFailures);
                RobotLocator.start();
                Thread.sleep(500);
                pushBeacon();
                break;
            case 3:
                //Move backward 5 inches and try again
                opMode.telemetry.addData("BeaconClassifier", "Recovery Mode %d: Attempting to close on beacon", classificationFailures);
                robot.encoderDrive(-0.3, 6);
                pushBeacon();
                break;
            default:
                opMode.telemetry.addData("BeaconClassifier", "Failure recovery modes exhausted, exiting...");
                break;
        }

        opMode.telemetry.update();
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