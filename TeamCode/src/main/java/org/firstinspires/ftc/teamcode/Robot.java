package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import static java.lang.Thread.sleep;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private ElapsedTime runtime = new ElapsedTime();
    public LinearOpMode opMode;

    public BeaconClassifier beaconClassifier;

    public static String name;

    private static double MAX_POWER = 0.6;
    private static double P = 0.2;
    private static double D = 8;
    TouchSensor[] touchSensors = { Hardware.limit };

    public Robot(String robotName, HardwareMap hwMap, LinearOpMode om) {
        name = robotName;
        opMode = om;

        Hardware.init(hwMap);
        VuforiaWrapper.init(hwMap.appContext);
        beaconClassifier = new BeaconClassifier((Activity) hwMap.appContext);
        RobotLocator.init();

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
        long wait_time = 200;
        try {
            //Reset the encoders
            resetEncoders();

            if (accelerationEnabled)
                acceleration(0.01, speed, 1000);

            Hardware.leftMotor.setPower(speed);
            Hardware.rightMotor.setPower(speed);

            int position = Hardware.leftMotor.getCurrentPosition();
            while (opMode.opModeIsActive() && Math.abs(position) < target) {

                position = Hardware.leftMotor.getCurrentPosition();

                opMode.telemetry.addData("EncoderTarget", "%d", target);
                opMode.telemetry.addData("EncoderPosition", "%d", position);
                opMode.telemetry.update();
                sleep(10);
            }
        }
        catch (NullPointerException e) {
            wait_time = 4000;
            if(Hardware.rightMotor == null) {
                opMode.telemetry.addData("RightMotor Null", "%d", Hardware.rightMotor.getCurrentPosition());
            }
            else if(Hardware.leftMotor == null) {
                opMode.telemetry.addData("LeftMotor Null", "%d", Hardware.leftMotor.getCurrentPosition());
            }
            else {
                opMode.telemetry.addData("Encoder Uncaught Exception", "%d", 0);
            }
        }
        finally {
            if(accelerationEnabled)
                deceleration(0.01, speed, 500, target);

            stop();
            opMode.telemetry.update();
            sleep(wait_time);

        }
    }

    public void pivot (int deg, double power) throws InterruptedException {
        //convert degree into ticks
        int target = (int)((Math.abs(deg) / 360.0) * Math.PI * Hardware.WHEEL_BASE * Hardware.TICKS_PER_INCH);
        long wait_time = 200;
        try {
            resetEncoders();

            // set the power on the motors in opposite directions
            if (deg < 0) {
                power = -power;
            }

            Hardware.leftMotor.setPower(power);
            Hardware.rightMotor.setPower(-power);

            //loop
            int position = Hardware.leftMotor.getCurrentPosition();

            while (opMode.opModeIsActive() && Math.abs(position) < target) {
                //TODO: Take the average of both the left and right encoders
                position = Hardware.leftMotor.getCurrentPosition();

                opMode.telemetry.addData("EncoderTarget", "%d", target);
                opMode.telemetry.addData("EncoderPosition", "%d", position);
                opMode.telemetry.update();
                sleep(10);
            }
        } catch (NullPointerException e) {
            wait_time = 4000;
            if (Hardware.rightMotor == null) {
                opMode.telemetry.addData("Pivot RightMotor Null", "%d", Hardware.rightMotor.getCurrentPosition());
            } else if (Hardware.leftMotor == null) {
                opMode.telemetry.addData("Pivot LeftMotor Null", "%d", Hardware.leftMotor.getCurrentPosition());
            } else {
                opMode.telemetry.addData("Pivot Uncaught Exception", "%d", 0);
            }
        }
        finally {
            stop();
            opMode.telemetry.update();
            sleep(wait_time);
        }
            // stop the motors
        //stop();
        //sleep(200);
    }


    public void touchDrive(double power, TouchSensor touch) throws InterruptedException {
        resetEncoders();

        Hardware.leftMotor.setPower(power);
        Hardware.rightMotor.setPower(power);

        //touch sensors
        while (!touch.isPressed()) {sleep(10);}

        stop();
        sleep(200);
    }

    public void moveToTargetEncoder(int x, int z,int o, double speed) throws InterruptedException {

        /*while(!locator.isTracking()) {
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
        */
    }

    public void moveToTarget(int x, int z, double speed) throws InterruptedException {

        /*float[] start = locator.getRobotLocationXZ();
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
        }*/

        stop();
    }

    public void ballgrabber ( double speed, long time ) throws InterruptedException {
        Hardware.chickenMotor.setPower(speed);
        sleep(time);
    }

    public void ballshooter ( double speed, long time ) throws InterruptedException {
        Hardware.shooterMotorRight.setPower(speed);
        Hardware.shooterMotorLeft.setPower(speed);
        sleep(time);
    }
    public void feeder (float position, long time) throws InterruptedException {
        Hardware.feeder.setPosition(position);
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
            Hardware.leftMotor.setPower(left_power);
            Hardware.rightMotor.setPower(right_power);
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
            Hardware.leftMotor.setPower(i * dir);
            Hardware.rightMotor.setPower(i * dir);
            sleep(increment_time);
        }

        Hardware.leftMotor.setPower(max_speed);
        Hardware.rightMotor.setPower(max_speed);

        return Hardware.leftMotor.getCurrentPosition();
    }

    private void deceleration (double decrement, double cur_speed, int ms_time, int target) throws InterruptedException {

        double dir = cur_speed/Math.abs(cur_speed);
        long decrement_time = (long) (ms_time / ((Math.abs(cur_speed) - MIN_SPEED) / decrement));

        for (double i = Math.abs(cur_speed); i >= MIN_SPEED && Math.abs(Hardware.leftMotor.getCurrentPosition()) < target; i -= decrement) {
            Hardware.leftMotor.setPower(i * dir);
            Hardware.rightMotor.setPower(i * dir);
            sleep(decrement_time);
        }

        stop();
    }

    private void resetEncoders() {
        //Reset the encoders
        Hardware.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Hardware.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //Return mode back to run with encoders
        Hardware.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Hardware.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    private void stop() {
        Hardware.leftMotor.setPower(0);
        Hardware.rightMotor.setPower(0);
    }

    /*
     * Slider Functionality
     */

    //Positive is left, negative is right
    void moveSlider(double distance) throws InterruptedException {
        ElapsedTime timer = new ElapsedTime();
        timer.reset();

        Hardware.slider.setPower(Math.signum(distance));

        double targetTime = (distance / Hardware.SLIDER_MAX_SPEED * 1000);

        while(!Hardware.limit.isPressed() && timer.milliseconds() < targetTime) {
            Thread.yield();
        }

        stopSlider();
    }

    void moveSlider(double power, long msTime) throws InterruptedException {
        Hardware.slider.setPower(power);
        sleep(msTime);
        stopSlider();
    }

    void resetSlider () throws InterruptedException {
        Hardware.slider.setPower(-1);
        while (!Hardware.limit.isPressed()) { Thread.yield(); }
        //moveSlider(1, (long) (Hardware.SLIDER_TRACK_LENGTH / Hardware.SLIDER_MAX_SPEED * 1000 / 2));
        stopSlider();
    }

    void stopSlider () throws InterruptedException {
        Hardware.slider.setPower(0.05);
    }

    /*
     * Color Sensor Functionality
     */
    public float[] getRgb() {
        ColorSensor colorSensor = Hardware.colorSensor;
        float[] rgb = {colorSensor.red(), colorSensor.green(), colorSensor.green()};
        return rgb;
    }

    public float[] getHsv() {
        ColorSensor colorSensor = Hardware.colorSensor;
        float[] hsvValues = {0f, 0f, 0f};
        Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
        return hsvValues;
    }

    /*
     * LED Functionality
     */
    public void enableLed() {
        if(!Hardware.bLedOn)
            toggleLed();
    }

    public void disableLed() {
        if(Hardware.bLedOn)
            toggleLed();
    }

    public void toggleLed() {
        Hardware.bLedOn = !Hardware.bLedOn;
        Hardware.cdim.setDigitalChannelState(Hardware.LED_CHANNEL, Hardware.bLedOn);
    }
}
