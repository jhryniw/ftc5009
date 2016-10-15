package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;
import static org.firstinspires.ftc.teamcode.Hardware.WHEEL_BASE;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private Hardware hardware = new Hardware();
    private ElapsedTime runtime = new ElapsedTime();
    private Callable <Boolean> isActiveCallback;

    public static String name;

    public Robot(String robotName, Callable <Boolean> funcIsActive) {
        name = robotName;
        isActiveCallback = funcIsActive;
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

        while (opModeIsActive() && hardware.leftMotor.getCurrentPosition() < target && runtime.seconds() < timeoutS) {
            //TODO: add telemetry to track position
            sleep(5);
        }

        stop();
    }

    public void pivot (int deg, double power) throws InterruptedException {
        //convert degree into ticks
        int target = (int)(deg / 360 * Math.PI * WHEEL_BASE);

        //Reset the encoders
        hardware.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hardware.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // set the power on the motors in opposite directions
        if (deg < 0) {
            power = -power;
        }

        hardware.leftMotor.setPower(power);
        hardware.rightMotor.setPower(-power);

        //loop
        while (opModeIsActive() && hardware.leftMotor.getCurrentPosition() < target) {
            sleep(10);
        }

        // stop
        stop();

    }

    public void stop(){
        hardware.leftMotor.setPower(0);
        hardware.rightMotor.setPower(0);
    }

    private boolean opModeIsActive() {
        try {
            return isActiveCallback.call();
        }
        catch (Exception e) {
            return false;
        }
    }
}
