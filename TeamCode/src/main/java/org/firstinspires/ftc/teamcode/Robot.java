package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;
import android.view.SurfaceView;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Func;
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
    private LinearOpMode opMode;

    public BeaconClassifier beaconClassifier;

    public static String name;

    private static double MAX_POWER = 0.6;
    private static double P = 0.2;
    private static double D = 8;

    public Robot(String robotName, HardwareMap hwMap, LinearOpMode om) {
        name = robotName;
        opMode = om;

        hw.init(hwMap);
        //locator.init(hwMap.appContext);

        beaconClassifier = new BeaconClassifier((Activity) hwMap.appContext, 0);
        beaconClassifier.setPreviewVisibility(SurfaceView.VISIBLE);

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
        boolean accelerationEnabled = false;
        int target = (int)Math.abs(distance * Hardware.TICKS_PER_INCH);

        if(target > ACCELERATION_DISTANCE + DECELERATION_DISTANCE) {
            accelerationEnabled = true;
            target -= DECELERATION_DISTANCE;
        }

        //Reset the encoders
        resetEncoders();

        if(accelerationEnabled)
            acceleration (0.01, speed, 1000);

        hw.leftMotor.setPower(speed);
        hw.rightMotor.setPower(speed);

        int position = hw.leftMotor.getCurrentPosition();
        while (opMode.opModeIsActive() && Math.abs(position) < target) {
            position = hw.leftMotor.getCurrentPosition();

            opMode.telemetry.addData("EncoderTarget", "%d", target);
            opMode.telemetry.addData("EncoderPosition", "%d", position);
            opMode.telemetry.update();
            sleep(10);
        }

        if(accelerationEnabled)
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

        while (opMode.opModeIsActive() && Math.abs(position) < target) {
            //TODO: Take the average of both the left and right encoders
            position = hw.leftMotor.getCurrentPosition();

            opMode.telemetry.addData("EncoderTarget", "%d", target);
            opMode.telemetry.addData("EncoderPosition", "%d", position);
            opMode.telemetry.update();
            sleep(10);
        }

        // stop the motors
        stop();
        sleep(200);
    }

    public void testLoop() throws InterruptedException {
        while(opMode.opModeIsActive()) {
            opMode.telemetry.addData("Status", locator.isTracking() ? "Tracking" : "Not Tracking");
            opMode.telemetry.addData("Robot location", locator.getRobotLocation().toString());
            opMode.telemetry.addData("Rate", Integer.toString(locator.getFps()));

            opMode.telemetry.update();
            opMode.idle();
        }
    }

    public void launchLocator() {
        locator.launch();
    }

    public void haltLocator() {
        locator.halt();
    }

    public void moveToTargetEncoder(int x, int z,int o, double speed) throws InterruptedException {

        while(!locator.isTracking()) {
            locator.getRobotLocation();
            opMode.idle();
        }

        float[] start = locator.getRobotLocationXZ();
        float[] goal = {x, z};
        double dx = goal[0] - start[0];
        double dz = goal[1] - start[1];
        double d = Math.sqrt(dx * dx + dz * dz);
        int theta = (int) Math.toDegrees(Math.acos((dx * Math.cos(o) + dz * Math.sin(o)) / d));

        opMode.telemetry.addData("Distance", Double.toString(d));
        opMode.telemetry.addData("Theta", Double.toString(theta));
        opMode.telemetry.update();

        encoderDrive(speed, d);
        pivot(theta, speed);

        //Thread.sleep(3000);
        //pivot( (int) theta, -speed);
        //encoderDrive(-speed, d);

    }

    public void moveToTarget(int x, int z,  double speed) throws InterruptedException {

        float[] start = locator.getRobotLocationXZ();
        float[] goal = { x, z };

        double u = 0;
        double dx = goal[0] - start[0];
        double dz = goal[1] - start[1];
        double e = Math.sqrt(dx * dx + dz + dz);
        double last_error = 0;
        double error = 0;
        double diff_error = 0;

        while (e > 1 && opMode.opModeIsActive()) {
            float[] location = locator.getRobotLocationXZ();

            double rx = location[0] - start[0];
            double rz = location[1] - start[1];

            if(rx == 0) {
                rx = 0.01;
            }
            if(rz == 0) {
                rz = 0.01;
            }

            double ex = goal[0] - location[0];
            double ez = goal[1] - location[1];

            e = Math.sqrt(ex * ex + ez * ez);

            //u  = (rx * dx + rz * dz) / (dx * dx + dz * dz);
            error = (rz * dx - rx * dz) / Math.sqrt(dx * dx + dz * dz);
            //double blah = (rx * dx + rz * dz) / (Math.sqrt(rx * rx + rz * rz) * Math.sqrt(dx * dx + dz * dz));
            //error = Math.toDegrees(Math.acos(blah));

            diff_error = error - last_error;
            last_error = error;

            double steer = error * P - diff_error * D;

            setSpeed(-speed, steer);

            opMode.telemetry.addData("Status:", "%s", locator.isTracking() ? "Tracking" : "Not Tracking");
            opMode.telemetry.addData("Robot location", locator.getRobotLocation().toString());
            opMode.telemetry.addData("Errors", "{ %.2f, %.2f, %.2f }", e, error, diff_error);
            opMode.telemetry.addData("Position:", "{ %.2f, %.2f }", location[0], location[1]);
            opMode.telemetry.addData("Speed:", "{ %.2f, %.2f }", speed, steer);
            opMode.telemetry.addData("Goal:", "%.2f { %.2f, %.2f }", u, goal[0], goal[1]);
            opMode.telemetry.addData("Rate", Integer.toString(locator.getFps()));
            opMode.telemetry.update();

            opMode.idle();
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
    public void feeder (float position, long time) throws InterruptedException {
        hw.feeder.setPosition(position);
        sleep(time);
    }

    /**
     * @param linear - desired linear velocity in inches/s
     * @param angular - desired angular velocity in rad/s
     */
    private void setSpeed(double linear, double angular) {
        double left_speed = (linear - (Hardware.WHEEL_BASE / 2) * angular) / Hardware.WHEEL_DIAMETER; //rounds per second
        double right_speed = (linear + (Hardware.WHEEL_BASE / 2) * angular) / Hardware.WHEEL_DIAMETER; //rounds per second

        double left_power = left_speed * 60 / Hardware.ROUNDS_PER_MINUTE;
        double right_power = right_speed * 60 / Hardware.ROUNDS_PER_MINUTE;

        //Scale the power to our range if it is exceeded
        if (Math.abs(left_power) > MAX_POWER) {
            right_power = right_power / Math.abs(left_power) * MAX_POWER;
            left_power = Math.signum(left_power) * MAX_POWER;
        }

        if (Math.abs(right_power) > MAX_POWER) {
            left_power = left_power / Math.abs(right_power) * MAX_POWER;
            right_power = Math.signum(right_power) * MAX_POWER;
        }

        opMode.telemetry.addData("Power:", "{ %.2f, %.2f }", left_power, right_power);

        try {
            hw.leftMotor.setPower(left_power);
            hw.rightMotor.setPower(right_power);
        }
        catch (IllegalArgumentException e) {
            return;
        }
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
