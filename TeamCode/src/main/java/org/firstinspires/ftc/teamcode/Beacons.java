package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-15.
 */

final class Beacons extends PathBase {

    static double BEACON_X_DIFF = 52.0;
    double xOffset = 0;
    int classificationFailures = 0;

    Beacons(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Beacons");
    }

    // - counterclockwise, + clockwise

    @Override
    public void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:

                robot.encoderDrive(-0.3, 5); //backward
                shoot();
                robot.pivot(43, 0.3); //pivot
                robot.encoderDrive(-0.9 , 64); //diagonal beacon
                robot.pivot(48, 0.3); // pivot to face beacon
                robot.encoderDrive(-0.5 , 5); //inch closer to beacon
                break;

            case RED:
                robot.encoderDrive(-0.3, 5); //backward
                shoot();
                robot.pivot(-30, 0.3); //pivot
                robot.encoderDrive(-0.9, 60);
                robot.pivot(-45, 0.3);
                robot.encoderDrive(-0.5, 5);
                break;
        }

        RobotLocator.start();
        Thread.sleep(1000);

        pushBeacon();

        RobotLocator.stop();

        robot.resetSlider(true);

        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.5, 5);
                robot.pivot(-94, 0.2);
                robot.encoderDrive(-0.8, BEACON_X_DIFF + xOffset);
                robot.pivot(90, 0.2);
                break;
            case RED:
                robot.encoderDrive(0.5, 5);
                robot.pivot(90, 0.2);
                robot.encoderDrive(-0.8, BEACON_X_DIFF + xOffset);
                robot.pivot(-90, 0.2);
                break;
        }

        RobotLocator.start();
        Thread.sleep(1000);

        pushBeacon();

        switch (alliance) {
            case BLUE:
                robot.pivot(-40, 0.2);
                robot.encoderDrive(0.9, 35);
                //robot.pivot(-45, 0.2);
                robot.encoderDrive(0, 0);
                break;
            case RED:
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
        xOffset = target.getX();

        if (is_left)
            xDist -= BeaconTarget.BUTTON_X_OFFSET;
        else if (is_right)
            xDist += BeaconTarget.BUTTON_X_OFFSET;

        if(xDist > (Hardware.SLIDER_TRACK_LENGTH + Hardware.BUTTON_PUSHER_WIDTH / 2) || xDist < -(Hardware.BUTTON_PUSHER_WIDTH / 2)) {
            return;
        }

        xDist = Hardware.bound(xDist, 0, Hardware.SLIDER_TRACK_LENGTH);
        robot.moveSlider(xDist);

        double zDist = target.getZ() + BeaconTarget.BUTTON_Z_OFFSET;

        //opMode.telemetry.addData("ButtonPusher", "Z Distance: %.2f", zDist);
        //opMode.telemetry.update();
        //Thread.sleep(1000);

        //Push button
        robot.resetEncoders();
        int target1 = (int)Math.abs(zDist * Hardware.TICKS_PER_INCH);
        int target2 = (int)Math.abs(5 * Hardware.TICKS_PER_INCH);
        robot.goToEncoderTarget(target1, -0.5, -0.5);
        robot.goToEncoderTarget(target1 + target2, -0.3, -0.3);
        robot.stop();

        //Back up
        robot.encoderDrive(0.9, 11);
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