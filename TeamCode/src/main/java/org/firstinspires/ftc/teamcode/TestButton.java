package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.*;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * Created by Vicki on 2017-03-27.
 */

final class TestButton extends PathBase {
    TestButton(LinearOpMode opMode, Robot r, Coordinate startLoc) {
        super(opMode, r, startLoc, "TestButton");
    }

    @Override
    void run() throws InterruptedException {
        switch (alliance) {
            case BLUE:
                robot.moveSlider(-Hardware.SLIDER_TRACK_LENGTH / 4);
                robot.moveSlider(Hardware.SLIDER_TRACK_LENGTH / 4);
                break;
            case RED:

                break;
        }
    }
}

