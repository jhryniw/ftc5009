package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-15.
 */

public class BallKnocker extends PathBase {


    public BallKnocker(Robot r, Coordinate startLoc) {
        super(r, startLoc, "Ball Knocker");
    }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(0.5, 82);
    }
}
