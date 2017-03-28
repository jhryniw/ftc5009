package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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
                robot.resetSlider();
                Thread.sleep(5000);

                Hardware.slider.setPower(-1);
                while (!Hardware.limit.isPressed()) { Thread.yield(); }
                robot.moveSlider(1, 1000);

                break;
            case RED:

                break;
        }
    }
}

