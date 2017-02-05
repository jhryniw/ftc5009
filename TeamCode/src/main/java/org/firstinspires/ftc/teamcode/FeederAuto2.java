package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/**
 * Created by Vicki on 2017-02-04.
 */


@Autonomous (name = "FeederAuto2")
    public class FeederAuto2 extends PathBase {

    public FeederAuto2(Robot r, Coordinate startLoc) { super(r, startLoc, "Feeder Auto2"); }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(-0.5, 2);
        robot.pivot(15, 0.2);
        robot.ballshooter(1.0, 100);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0 ,1000); //down feed
        robot.feeder(1 , 500); //up feed
        robot.ballshooter(0, 0);

        robot.pivot(30, 0.3);
        robot.encoderDrive(-0.8, 70);
        robot.pivot(45, 0.3);
        robot.encoderDrive(0.5, 20);//paste
        robot.pivot(-45, 0.5);
        robot.pivot(45, 0.3);
        robot.encoderDrive(0.3, 4);

    }
}