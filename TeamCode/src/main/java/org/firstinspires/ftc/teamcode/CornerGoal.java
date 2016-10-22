package org.firstinspires.ftc.teamcode;

/**
 * Created by Vicki on 2016-10-22.
 */

public class CornerGoal extends PathBase {

    public CornerGoal(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc);
    }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(-0.5, 41); //off the wall
        robot.pivot(-51, 03); //first pivot
        robot.encoderDrive(0.5, 53); //up the corner
        robot.encoderDrive(-0.3, 2 ); //down the corner
        robot.ballgrabber(-0.5, 3000); //ungrabbing
        robot.ballgrabber(0, 0);
        robot.encoderDrive(0.6, 5); //stay on corner
        robot.encoderDrive(0.2, 5);
    }
}
