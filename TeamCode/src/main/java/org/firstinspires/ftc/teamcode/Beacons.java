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
            robot.encoderDrive(0.5, 12);
            robot.pivot(45, 0.3);
            robot.encoderDrive(0.5, 30);
            robot.pivot(-45, 0.3);
            robot.encoderDrive(0.5, 67);
        }
}
