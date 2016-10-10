package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private Hardware hardware = new Hardware();
    private ElapsedTime runtime = new ElapsedTime();
    private Callable <Boolean> opModeIsActive;

    public static String name;

    public Robot(String robotName, Callable <Boolean> funcIsActive) {
        name = robotName;
        opModeIsActive = funcIsActive;
    }

    public void encoderDrive (double speed, double distance, double timeoutS ) throws InterruptedException{
        int target;

        runtime.reset();
        target = hardware.leftMotor.getCurrentPosition() + (int)(distance * hardware.TICKS_PER_INCH);

        //TODO: Check if RUN_USING_ENCODER is needed for getCurrentPosition() to work
        //Reset the encoders
        hardware.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        hardware.leftMotor.setPower(speed);
        hardware.rightMotor.setPower(speed);

        boolean isActive = true;

        while (isActive && hardware.leftMotor.getCurrentPosition() < target && runtime.seconds() < timeoutS) {
            //TODO: add telemetry to track position

            try {
                isActive = opModeIsActive.call();
            }
            catch (Exception e) {
                isActive = false;
            }

            sleep(5);
        }

        stop();
    }

    public void stop(){
        hardware.leftMotor.setPower(0);
        hardware.rightMotor.setPower(0);

    }
}
