package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-17.
 */

public class RedBeacons extends PathBase {

        public RedBeacons(Robot r, Alliance a, Coordinate startLoc) { super(r, a, startLoc, "Red Beacons"); }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(-0.5, 10); //off the wall (backwards)
        robot.pivot(-40, 0.3); //first pivot
        robot.encoderDrive(-0.5, 56); //diagonal run
        robot.pivot(45, 0.3); //second pivot
        robot.encoderDrive(-0.5, 53); //last backwards run
    }
}



