package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
/**
 * Created by Vicki on 2017-01-31.
 */

@Autonomous (name = "FeederAuto")
public class FeederAuto extends PathBase {

    public FeederAuto(Robot r, Alliance a, Coordinate startLoc) {
        super(r, a, startLoc, "Feeder Auto");
    }

    @Override
    void run() throws InterruptedException {
        robot.encoderDrive(-0.5, 8); //backward
        robot.ballshooter(1.0, 500);
        robot.feeder(0, 700); //down feed
        robot.feeder(1, 1000); //up feed
        robot.feeder(0, 1000); //down feed
        robot.feeder(1, 500); //up feed
        robot.feeder(0 ,1000); //down feed
        robot.feeder(1 , 500); //up feed
        robot.ballshooter(0, 0);

        robot.pivot(60, 0.2); //pivot
        robot.encoderDrive(-0.8 , 50);
        robot.pivot(57, 0.2);
        robot.encoderDrive(0.5, 20);
        robot.pivot(-45, 0.5);
        robot.pivot(45, 0.3);
        robot.encoderDrive(0.3, 4);
    }
}
