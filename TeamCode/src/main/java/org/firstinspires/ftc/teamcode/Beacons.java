package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-15.
 */

public class Beacons extends PathBase {

    public Beacons(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc, "Beacons");
    }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(-0.5, 8); //backward
        robot.ballshooter(1.0, 100);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 500); //up feed
        robot.feeder(0 ,1000); //down feed
        robot.feeder(1 , 500); //up feed
        robot.ballshooter(0, 0); //stop shooter

        robot.pivot(60, 0.2); //pivot
        robot.encoderDrive(-0.8 , 50); //backward
        robot.pivot(57, 0.2); // last pivot
        robot.encoderDrive(0.5, 20); //gets near cap ball
    }
}
