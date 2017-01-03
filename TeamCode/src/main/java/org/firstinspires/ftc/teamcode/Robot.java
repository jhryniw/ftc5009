package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import java.util.concurrent.Callable;

import static java.lang.Thread.sleep;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private Hardware hw = new Hardware();
    private RobotLocator locator = new RobotLocator();
    private ElapsedTime runtime = new ElapsedTime();
    private OpModeCallbacks opModeCallbacks;

    public static String name;

    private static double P = 2;
    private static double D = 50;

    public Robot(String robotName, HardwareMap hwMap, OpModeCallbacks callbacks) {
        name = robotName;
        opModeCallbacks = callbacks;
        hw.init(hwMap);
        locator.init(hwMap.appContext);

        // turn the LED on in the beginning, just so user will know that the sensor is active.
        enableLed();
    }

    /*
     * Robot Driving Functionality
     */

    private double DECELERATION_DISTANCE = 6 * Hardware.TICKS_PER_INCH;
    private double ACCELERATION_DISTANCE = 2 * Hardware.TICKS_PER_INCH;
    private double MIN_SPEED = 0.2;

    public void encoderDrive (double speed, double distance) throws InterruptedException {

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

    public void testLoop() throws InterruptedException {
        while(opModeCallbacks.opModeIsActive()) {
            opModeCallbacks.addData("Status", locator.isTracking() ? "Tracking" : "Not Tracking");
            opModeCallbacks.addData("Robot location", locator.getRobotLocation().toString());

            opModeCallbacks.updateTelemetry();
            opModeCallbacks.idle();
        }
    }

    public void launchLocator() {
        locator.launch();
    }

    public void haltLocator() {
        locator.halt();
    }

    public void moveToTarget(int x, int z, double speed) throws InterruptedException {

        float[] start = locator.getRobotLocationXZ();
        float[] goal = { x, z };

        double u = 0;
        double dx = goal[0] - start[0];
        double dz = goal[1] - start[1];
        double last_error = 0;
        double error = 0;
        double diff_error = 0;

        while (u < 1) {
            float[] location = locator.getRobotLocationXZ();

            double rx = location[0] - start[0];
            double rz = location[1] - start[1];

            u  = (rx * dx + rz * dz) / (dx * dx + dz * dz);
            error = (rz * dx - rx * dz) / Math.sqrt(dx * dx + dz * dz);

            diff_error = error - last_error;
            last_error = error;

            double steer = error * P - diff_error * D;

            setSpeed(speed, steer);

            opModeCallbacks.addData("Status:", "%s", locator.isTracking() ? "Tracking" : "Not Tracking");
            opModeCallbacks.addData("Position:", "{ %.2f, %.2f }", location[0], location[1]);
            opModeCallbacks.addData("Speed:", "{ %.2f, %.2f }", speed, steer);
            opModeCallbacks.addData("Goal:", "{ %.2f, %.2f }", goal[0], goal[1]);
            opModeCallbacks.updateTelemetry();
            opModeCallbacks.idle();
        }

        stop();
    }

    public void ballgrabber ( double speed, long time ) throws InterruptedException {
        hw.chickenMotor.setPower(speed);
        sleep(time);
    }

    public void ballshooter ( double speed, long time ) throws InterruptedException {
        hw.shooterMotor.setPower(speed);
        sleep(time);
    }

    /**
     * @param linear - desired linear velocity in inches/s
     * @param angular - desired angular velocity in rad/s
     */
    private void setSpeed(double linear, double angular) {
        double left_speed = (linear - (Hardware.WHEEL_BASE / 2) * angular) / Hardware.WHEEL_DIAMETER; //rounds per second
        double right_speed = (linear + (Hardware.WHEEL_BASE / 2) * angular) / Hardware.WHEEL_DIAMETER; //rounds per second

        double left_power = Hardware.ROUNDS_PER_MINUTE / 60 / left_speed;
        double right_power = Hardware.ROUNDS_PER_MINUTE / 60 / right_speed;

        //Scale the power to our range if it is exceeded
        if (left_power > 1 || left_power < -1) {
            right_power = right_power / Math.abs(left_power);
            left_power = Math.signum(left_power);
        }

        if (right_power > 1 || right_power < -1) {
            left_power = left_power / Math.abs(left_power);
            right_power = Math.signum(right_power);
        }

        hw.leftMotor.setPower(left_power);
        hw.rightMotor.setPower(right_power);
    }

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

    /*
     * Color Sensor Functionality
     */
    public float[] getRgb() {
        float[] rgb = {hw.colorSensor.red(), hw.colorSensor.green(), hw.colorSensor.green()};
        return rgb;
    }

    public float[] getHsv() {
        float[] hsvValues = {0f, 0f, 0f};
        Color.RGBToHSV((hw.colorSensor.red() * 255) / 800, (hw.colorSensor.green() * 255) / 800, (hw.colorSensor.blue() * 255) / 800, hsvValues);
        return hsvValues;
    }

    /*
     * LED Functionality
     */
    public void enableLed() {
        if(!hw.bLedOn)
            toggleLed();
    }

    public void disableLed() {
        if(hw.bLedOn)
            toggleLed();
    }

    public void toggleLed() {
        hw.bLedOn = !hw.bLedOn;
        hw.cdim.setDigitalChannelState(Hardware.LED_CHANNEL, hw.bLedOn);
    }

}
