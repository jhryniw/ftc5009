package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private Hardware hw = new Hardware();
    private ElapsedTime runtime = new ElapsedTime();
    private OpModeCallbacks opModeCallbacks;

    public static String name;

    public Robot(String robotName, HardwareMap hwMap, OpModeCallbacks callbacks) {
        name = robotName;
        opModeCallbacks = callbacks;
        hw.init(hwMap);
    }

    public void encoderDrive (double speed, double distance) throws InterruptedException{

        int target = (int)Math.abs(distance * Hardware.TICKS_PER_INCH);

        //Reset the encoders
        hw.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        opModeCallbacks.idle();

        hw.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        hw.leftMotor.setPower(speed);
        hw.rightMotor.setPower(speed);

        int position = hw.leftMotor.getCurrentPosition();
        while (opModeCallbacks.opModeIsActive() && Math.abs(position) < target) {
            position = hw.leftMotor.getCurrentPosition();

            opModeCallbacks.addData("EncoderTarget", "%d", target);
            opModeCallbacks.addData("EncoderPosition", "%d", position);
            opModeCallbacks.updateTelemetry();
            sleep(10);
        }

        stop();
    }

    public void pivot (int deg, double power) throws InterruptedException {
        //convert degree into ticks
        int target = (int)((Math.abs(deg) / 360.0) * Math.PI * Hardware.WHEEL_BASE * Hardware.TICKS_PER_INCH);

        //Reset the encoders
        hw.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        opModeCallbacks.idle();

        hw.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // set the power on the motors in opposite directions
        if (deg < 0) {
            power = -power;
        }

        hw.leftMotor.setPower(power);
        hw.rightMotor.setPower(-power);

        //loop
        int position = hw.leftMotor.getCurrentPosition();
        opModeCallbacks.addData("EncoderTarget", "%d", target);
        opModeCallbacks.addData("EncoderPosition", "%d", position);
        opModeCallbacks.updateTelemetry();
        while (opModeCallbacks.opModeIsActive() && Math.abs(position) < target) {
            //TODO: Take the average of both the left and right encoders
            position = hw.leftMotor.getCurrentPosition();

            opModeCallbacks.addData("EncoderTarget", "%d", target);
            opModeCallbacks.addData("EncoderPosition", "%d", position);
            opModeCallbacks.updateTelemetry();
            sleep(10);
        }

        // stop the motors
        stop();
        sleep(1000);
    }

    public void ballgrabber ( double speed, long time ) throws InterruptedException {

        hw.chickenMotor.setPower(speed);
        sleep(time);
    }

    public void ballshooter ( double speed, long time ) throws InterruptedException {

        hw.shooterMotor.setPower(speed);
        sleep(time);

    }

    public void stop() {
        hw.leftMotor.setPower(0);
        hw.rightMotor.setPower(0);
    }
}
