package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2017-04-03.
 */

final class TestPivot extends PathBase {
    TestPivot(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "TestPivot");
}
@Override
void run() throws InterruptedException {
    switch (alliance) {
        case BLUE:
            robot.encoderDrive(-0.5 , 14);
            robot.pivot(180, 0.8);
            robot.encoderDrive(-0.5, 20);
            break;
        case RED:
            break;
    }
}
}
