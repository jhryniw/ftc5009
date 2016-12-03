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

    private double DECELERATION_DISTANCE = 6 * Hardware.TICKS_PER_INCH;
    private double ACCELERATION_DISTANCE = 2 * Hardware.TICKS_PER_INCH;

    public void encoderDrive (double speed, double distance) throws InterruptedException{

        int target = (int)Math.abs(distance * Hardware.TICKS_PER_INCH);

        //Reset the encoders
        resetEncoders();

        acceleration (0.01, speed, 1000);

        hw.leftMotor.setPower(speed);
        hw.rightMotor.setPower(speed);

        int position = hw.leftMotor.getCurrentPosition();
        while (opModeCallbacks.opModeIsActive() && Math.abs(position) < target - DECELERATION_DISTANCE) {
            position = hw.leftMotor.getCurrentPosition();

            opModeCallbacks.addData("EncoderTarget", "%d", target);
            opModeCallbacks.addData("EncoderPosition", "%d", position);
            opModeCallbacks.updateTelemetry();
            sleep(10);
        }

        deceleration(0.01, speed, 500, target);

        stop();
    }

    public void pivot (int deg, double power) throws InterruptedException {
        //convert degree into ticks
        int target = (int)((Math.abs(deg) / 360.0) * Math.PI * Hardware.WHEEL_BASE * Hardware.TICKS_PER_INCH);

        resetEncoders();

        // set the power on the motors in opposite directions
        if (deg < 0) {
            power = -power;
        }

        hw.leftMotor.setPower(power);
        hw.rightMotor.setPower(-power);

        //loop
        int position = hw.leftMotor.getCurrentPosition();

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
        sleep(200);
    }

    public void ballgrabber ( double speed, long time ) throws InterruptedException {
        hw.chickenMotor.setPower(speed);
        sleep(time);
    }

    public void ballshooter ( double speed, long time ) throws InterruptedException {
        hw.shooterMotor.setPower(speed);
        sleep(time);
    }

    private double MIN_SPEED = 0.2;

    private int acceleration (double increment, double max_speed, int ms_time) throws InterruptedException {

        if (max_speed < MIN_SPEED) {
            return -1;
        }

        double dir = max_speed/Math.abs(max_speed);
        long increment_time = (long) (ms_time / ((Math.abs(max_speed) - MIN_SPEED)/increment));

        for (double i = MIN_SPEED; i <= Math.abs(max_speed); i += increment) {
            hw.leftMotor.setPower(i * dir);
            hw.rightMotor.setPower(i * dir);
            sleep(increment_time);
        }

        hw.leftMotor.setPower(max_speed);
        hw.rightMotor.setPower(max_speed);

        return hw.leftMotor.getCurrentPosition();
    }

    private void deceleration (double decrement, double cur_speed, int ms_time, int target) throws InterruptedException {

        double dir = cur_speed/Math.abs(cur_speed);
        long decrement_time = (long) (ms_time / ((Math.abs(cur_speed) - MIN_SPEED) / decrement));

        for (double i = Math.abs(cur_speed); i >= MIN_SPEED && Math.abs(hw.leftMotor.getCurrentPosition()) < target; i -= decrement) {
            hw.leftMotor.setPower(i * dir);
            hw.rightMotor.setPower(i * dir);
            sleep(decrement_time);
        }

        stop();
    }

    private void resetEncoders() {
        //Reset the encoders
        hw.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Return mode back to run with encoders
        hw.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        hw.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void stop() {
        hw.leftMotor.setPower(0);
        hw.rightMotor.setPower(0);
    }
}
