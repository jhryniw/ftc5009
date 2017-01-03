package org.firstinspires.ftc.teamcode;

/**
 * Created by James on 2016-12-29.
 */

public class PDTest extends PathBase {

    public PDTest(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc);
    }

    @Override
    void run() throws InterruptedException {
        robot.moveToTarget(116, 60, 0.5);
        //robot.testLoop();
    }
}
