package org.firstinspires.ftc.teamcode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.*;

/**
 * Created by Vicki on 2017-02-15.
 */

final class CloseCorner extends PathBase {
    CloseCorner(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "Close Corner");
    }
    @Override
    protected void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.encoderDrive(-0.4, 6);
                shoot();
                robot.encoderDrive(-0.5, 40);
                robot.pivot(90, 1);
                robot.feeder(1, 2000);
                robot.pivot(-90, 0.4);
                robot.encoderDrive(-0.3, 25);
                break;


            case RED:
                robot.encoderDrive(-0.4, 6);
                shoot();
                robot.encoderDrive(-0.5, 40);
                robot.pivot(-90, 1);
                robot.feeder(1, 2000);
                robot.pivot(90, 0.4);
                robot.encoderDrive(-0.3, 25);
                robot.pivot(-60, 0.8);
                break;
        }
    }

    private void shoot() throws InterruptedException{
        robot.ballshooter(0.95, 0);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0 ,200); //down feed
        robot.feeder(1 , 0); //up feed
        robot.ballshooter(0, 0);

    }
}
