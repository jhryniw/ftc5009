package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Vicki on 2016-10-24.
 */

public class BallShooter extends PathBase {

    public BallShooter(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Ball Shooter");
    }

    @Override
    void run() throws InterruptedException {
        robot.ballshooter(1.0, 10000);
        robot.ballshooter(0, 0);
    }
}
