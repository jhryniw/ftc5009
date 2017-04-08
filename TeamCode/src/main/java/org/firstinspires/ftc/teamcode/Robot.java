package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.matrices.VectorF;

import static java.lang.Thread.sleep;

/**
 * Created by s on 09/10/2016.
 */

public class Robot {

    private ElapsedTime runtime = new ElapsedTime();
    public LinearOpMode opMode;

    BeaconClassifier beaconClassifier;

    public static String name;

    private static double MAX_POWER = 0.6;
    private static double P = 0.2;
    private static double D = 8;
    TouchSensor[] touchSensors = { Hardware.limit };

    Robot(String robotName, HardwareMap hwMap, LinearOpMode om) {
        name = robotName;
        opMode = om;

        Hardware.init(hwMap);
        VuforiaWrapper.init(hwMap.appContext);
        beaconClassifier = new BeaconClassifier((Activity) hwMap.appContext);

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

        //reset Encoders
        resetEncoders();

        if(accelerationEnabled)
            acceleration (0.01, speed, 1000);

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

        if(accelerationEnabled)
            deceleration(0.01, speed, 500, target);

        stop();
    }


    void pivot (int deg, double power) throws InterruptedException {
        //convert degree into ticks
        int target = (int)((Math.abs(deg) / 360.0) * Math.PI * Hardware.WHEEL_BASE * Hardware.TICKS_PER_INCH);

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

        // stop the motors
        stop();
        sleep(200);
    }



    void touchDrive(double power, TouchSensor touch) throws InterruptedException {
        resetEncoders();

        Hardware.leftMotor.setPower(power);
        Hardware.rightMotor.setPower(power);

        //touch sensors
        while (!touch.isPressed()) {sleep(10);}

        stop();
        sleep(200);
    }

    void moveToTargetEncoder(int x, int z,int o, double speed) throws InterruptedException {

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

    //Uses PID Controller
    void moveToTarget() throws InterruptedException {
        //TODO: Add timeout
        VectorF pose = null;

        while(opMode.opModeIsActive() && (pose == null || pose.get(2) > Controller.TARGET_Z)) {
            pose = RobotLocator.getPose();

            if(pose == null) {
                Hardware.setPower(0, 0);
                opMode.telemetry.addData("Status", "Not tracking");
                opMode.telemetry.update();
                continue;
            }

            setError(pose);

            opMode.telemetry.addData("Pose", pose.toString());
            opMode.telemetry.addData("Motor Power", "Left: %d%% Right: %d%%", (int) (Hardware.leftMotor.getPower() * 100), (int) (Hardware.rightMotor.getPower() * 100));
            opMode.telemetry.update();

            Thread.yield();
        }

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

    void setError(VectorF error) {
        double[] speeds = poseToSpeed(error);

        Hardware.setBaseSpeed(speeds[0]);
        Hardware.setAngularSpeed(speeds[1]);
        Hardware.setPower();
    }

    double[] poseToSpeed(VectorF pose) {
        //Set angular to correct x
        float x = pose.get(0);
        float z = pose.get(2);

        double angular = Math.signum(x) * (Math.abs(x) / Math.abs(z) * Controller.P_A);
        angular = Hardware.bound(angular, -0.2, 0.2);

        //Set basespeed to correct z
        double base = (Math.abs(z / Controller.TARGET_Z) - 1) * Controller.P_Z;
        base = Hardware.bound(base, -0.2, 0.4);

        return new double[] { base, angular };
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
        long targetTime = (long) (distance / Hardware.SLIDER_MAX_SPEED * 1000);

        moveSlider(Math.signum(distance), targetTime);
    }

    void moveSlider(double power, long msTime) throws InterruptedException {
        Hardware.slider.setPower(power);
        Thread.sleep(msTime);
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
