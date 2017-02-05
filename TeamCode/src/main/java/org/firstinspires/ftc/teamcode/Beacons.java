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
                robot.encoderDrive(-0.5, 8); //backward
                robot.ballshooter(1.0, 500);
                robot.feeder(0, 700); //down feed
                robot.feeder(1, 1000); //up feed
                robot.feeder(0, 1000); //down feed
                robot.feeder(1, 500); //up feed
                robot.feeder(0 ,1000); //down feed
                robot.feeder(1 , 500); //up feed
                robot.ballshooter(0, 0); //stop shooter

                robot.pivot(60, 0.2); //pivot
                robot.encoderDrive(-0.8 , 50); //gets to beacon
                robot.pivot(57, 0.2);
                robot.moveToTargetEncoder(10,10,0,-0.2);
                break;
            case RED:

                break;
        }

    }
}

