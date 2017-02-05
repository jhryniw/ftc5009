package org.firstinspires.ftc.teamcode;

/**
 * Autonomous for PID Controller Testing
 * Created by James on 2016-12-29.
 */

public class PDTest extends PathBase {

    public PDTest(Robot r, Coordinate startLoc) {
        super(r, startLoc, "Target Test");
    }

    @Override
    void run() throws InterruptedException {
        robot.moveToTargetEncoder(116, 60, 90, 0.5);

        robot.moveToTarget(116, 60, 0.5);
        //robot.testLoop();
    }
}
