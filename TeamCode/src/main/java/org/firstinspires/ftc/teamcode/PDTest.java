package org.firstinspires.ftc.teamcode;

/**
 * Created by James on 2016-12-29.
 */

public class PDTest extends PathBase {

    public PDTest(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc, "Target Test");
    }

    @Override
    void run() throws InterruptedException {
        robot.moveToTargetEncoder(116, 60, 90, 0.5);

        robot.moveToTarget(116, 60, 0.5);
        //robot.testLoop();
    }
}
