package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-24.
 */

public class BallShooter extends PathBase {

    public BallShooter(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc, "Ball Shooter");
    }

    @Override
    void run() throws InterruptedException {
        robot.ballshooter(1.0, 10000);
        robot.ballshooter(0, 0);
    }
}
