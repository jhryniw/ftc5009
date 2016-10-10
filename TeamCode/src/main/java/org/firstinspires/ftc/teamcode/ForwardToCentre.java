package org.firstinspires.ftc.teamcode;

/**
 * Created by s on 09/10/2016.
 */

public class ForwardToCentre extends PathBase {

    ForwardToCentre(Robot r, Alliance a, Coordinate startLoc){
        super(r, a, startLoc);
    }

    @Override
    public void run() throws InterruptedException {
        robot.encoderDrive(0.9, 72, 9);
    }
}
