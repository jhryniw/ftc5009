package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-15.
 */

public class Beacons extends PathBase {

        public Beacons(Robot r, Alliance a, Coordinate startLoc) {
            super(r, a, startLoc);
        }

        @Override
        void run() throws InterruptedException {
            robot.encoderDrive(-0.5, 18); //off the blue wall (backwards)
            robot.pivot(45, 0.2); //first pivot
            robot.encoderDrive(-0.5, 45); //diagonal run
            robot.pivot(-45, 0.2); //second pivot
            robot.encoderDrive(-0.5, 53); //last backwards run
        }
}
