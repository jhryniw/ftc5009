package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-15.
 */

public class Beacons extends PathBase {

    public Beacons(Robot r, Coordinate startLoc) {
        super(r, startLoc, "Beacons");
    }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.5, 6); //backward
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
                robot.encoderDrive(-0.5,23);
                robot.encoderDrive(0.5,5);
                break;

            case RED:
                robot.encoderDrive(-0.5, 6); //backward
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
                robot.encoderDrive(-0.5,23);
                robot.encoderDrive(0.5,5);
                break;
        }
        robot.feeder(1,5500);
        Alliance[] results = robot.beaconClassifier.classify();
        boolean isleft = (alliance == results[0]);
        boolean isright = (alliance == results[1]);

        if (!(isleft && isright)) {
            robot.encoderDrive(-0.6, 15);
            robot.encoderDrive(0.6, 5);
        }

        switch (alliance) {
            case BLUE:
                robot.encoderDrive(0.5, 30);
                robot.pivot(-95, 0.5);
                robot.pivot(95, 0.5);
                robot.encoderDrive(0.5, 5);
                break;
            case RED:
                robot.encoderDrive(0.5, 30);
                robot.pivot(95, 0.5);
                robot.pivot(-95, 0.5);
                robot.encoderDrive(0.5, 5);
                break;
        }
    }
}

