package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-15.
 */

public class Beacons extends PathBase {

    private OpModeCallbacks opModeCallbacks;
    public Beacons(Robot r, Coordinate startLoc) {
        super(r, startLoc, "Beacons");
    }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.5, 5); //backward
                robot.ballshooter(1.0, 500);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.feeder(0 ,1000); //down feed
                robot.feeder(1 , 500); //up feed
                robot.ballshooter(0, 0); //stop shooter
                robot.pivot(60, 0.2); //pivot
                robot.encoderDrive(-0.8 , 60); //gets to beacon
                robot.pivot(50, 0.2);
                robot.encoderDrive(-0.5 , 5);
                break;

            case RED:
                robot.encoderDrive(-0.5, 5); //backward
                robot.ballshooter(1.0, 100);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.ballshooter(0, 0);
                robot.pivot(-60, 0.2); //pivot
                robot.encoderDrive(-0.8, 60);
                robot.pivot(-50, 0.2);
                robot.encoderDrive(-0.5 , 5);
                break;
        }
        //Alliance[] results = robot.beaconClassifier.classify();
        Alliance[] results = { Alliance.RED, Alliance.BLUE };
        boolean is_left = (alliance == results[0]);
        boolean is_right = (alliance == results[1]);

       /* try  {
            opModeCallbacks.addData("OpenCV", "Classification succeeded!");
            opModeCallbacks.addData("OpenCV", "Result { %s, %s }", results[0].toString(), results[1].toString());
            opModeCallbacks.updateTelemetry();
        }
        catch (NullPointerException except) {
            opModeCallbacks.addData("OpenCV", "Classification NA!");
        }*/

        if (is_left) {
            robot.pivot(-45, 0.2);
            robot.pivot(45, 0.2);
        }
        else if (is_right) {
            robot.pivot(45, 0.2);
            robot.pivot(-45, 0.2);
        }

        robot.encoderDrive(-0.5, 7);
        robot.encoderDrive(0.5, 7);

        switch (alliance) {
            case BLUE:
                robot.pivot(-92, 0.2);
                robot.encoderDrive(-0.7, 52);
                robot.pivot(92, 0.2);
                break;
            case RED:
                robot.pivot(92, 0.2);
                robot.encoderDrive(0.7, 52);
                robot.pivot(-92, 0.2);
                break;
        }

        //Alliance[] results2 = robot.beaconClassifier.classify();
        Alliance[] results2 = { Alliance.BLUE, Alliance.RED };
        is_left = (alliance == results2[0]);
        is_right = (alliance == results2[1]);

        if (is_left) {
            robot.pivot(-45, 0.2);
            robot.pivot(45, 0.2);
        }
        else if (is_right) {
            robot.pivot(45, 0.2);
            robot.pivot(-45, 0.2);
        }
    }
}
