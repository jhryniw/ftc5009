

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import static java.lang.Thread.sleep;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class Hardware {

    //Motors
    DcMotor leftMotor;
    DcMotor rightMotor;
    DcMotor chickenMotor;
    DcMotor shooterMotorRight;
    DcMotor shooterMotorLeft;
    DcMotor liftMotor;

    private double baseSpeed = 0;
    private double angularSpeed = 0;

    //Servos
    Servo leftClaw;
    Servo rightClaw;
    Servo feeder;
    CRServo slider;

    //Color Sensor + LED
    DeviceInterfaceModule cdim;
    ColorSensor colorSensor;
    boolean bLedOn = true;
    final int LED_CHANNEL = 5; // we assume that the LED pin of the RGB sensor is connected to digital port 5 (zero indexed).
    TouchSensor limit;

    private HardwareMap hwMap;
    static double WHEEL_BASE = 16;
    static double WHEEL_DIAMETER = 4.0;
    static int ROUNDS_PER_MINUTE = 160;
    private static double TICKS_PER_MOTOR_REV = 1120;
    private static double DRIVE_GEAR_RATIO = 1.0;

    static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);
    static double SLIDER_TRACK_LENGTH = 10.5;
    static double SLIDER_MAX_SPEED = 2.5; //2 7/16"

    public Hardware(HardwareMap hwm) {
        hwMap = hwm;

        try {
            leftMotor = hwMap.dcMotor.get("drive_left");
            rightMotor = hwMap.dcMotor.get("drive_right");
            chickenMotor = hwMap.dcMotor.get("chicken_fingers");
            shooterMotorRight = hwMap.dcMotor.get("shooter_R");
            shooterMotorLeft = hwMap.dcMotor.get("shooter_L");
            liftMotor = hwMap.dcMotor.get("lift");
            limit = hwMap.touchSensor.get("limit");
            leftClaw = hwMap.servo.get("left_claw");
            rightClaw = hwMap.servo.get("right_claw");
            feeder = hwMap.servo.get("feeder");
            slider = hwMap.crservo.get("slider");
        }
        catch (NullPointerException e) {
            throw new NullPointerException("Error: a motor did not initialize properly. Check the configuration!");
            //WRONG: the only possible NullPointer here is hwMap/hwm;
        }

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        shooterMotorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);

        shooterMotorRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotorLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        leftMotor.setPower(0);
        rightMotor.setPower(0);
        chickenMotor.setPower(0);
        shooterMotorRight.setPower(0);
        shooterMotorLeft.setPower(0);

        leftClaw.scaleRange(0.05, 0.95);
        rightClaw.scaleRange(0.05, 0.95);
        feeder.scaleRange(0.05 , 0.95);

        leftClaw.setDirection(Servo.Direction.REVERSE);

        leftClaw.setPosition(0);
        rightClaw.setPosition(0);
        feeder.setPosition(1);
        slider.setPower(0.05);

        //ColorSensor setup
        /*cdim = hwMap.deviceInterfaceModule.get("dim");
        cdim.setDigitalChannelMode(LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);
        colorSensor = hwMap.colorSensor.get("color");*/
    }

    //Use baseSpeed and angular speed
    void setPower() {
        double lPower = baseSpeed + angularSpeed;
        double rPower = baseSpeed - angularSpeed;

        setPower(lPower, rPower);
    }
    void setPower(double lpower, double rpower) {

        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setPower(bound(lpower, -1, 1));
        rightMotor.setPower(bound(rpower, -1, 1));
    }

    void setBaseSpeed(double speed) {
        baseSpeed = speed;
    }

    void setAngularSpeed(double angular) {
        angularSpeed = angular;
    }

    void stop() {
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }

    private void setMotorMode(DcMotor.RunMode targetMode) {
        if(leftMotor.getMode() != targetMode)
            leftMotor.setMode(targetMode);

        if(rightMotor.getMode() != targetMode)
            rightMotor.setMode(targetMode);
    }

        /*
     * Slider Functionality
     */

    //Positive is left, negative is right
    void moveSlider(double distance) throws InterruptedException {
        long targetTime = (long) (distance / SLIDER_MAX_SPEED * 1000);

        moveSlider(Math.signum(distance), targetTime);
    }

    void moveSlider(double power, long msTime) throws InterruptedException {
        slider.setPower(power);
        Thread.sleep(msTime);
        stopSlider();
    }

    void resetSlider () throws InterruptedException {
        slider.setPower(-1);
        while (!limit.isPressed()) { Thread.yield(); }
        //moveSlider(1, (long) (SLIDER_TRACK_LENGTH / SLIDER_MAX_SPEED * 1000 / 2));
        stopSlider();
    }

    void stopSlider () throws InterruptedException {
        slider.setPower(0.05);
    }

    void ballgrabber ( double speed, long time ) throws InterruptedException {
        chickenMotor.setPower(speed);
        sleep(time);
    }

    void ballshooter ( double speed, long time ) throws InterruptedException {
        shooterMotorRight.setPower(speed);
        shooterMotorLeft.setPower(speed);
        sleep(time);
    }
    void feeder (float position, long time) throws InterruptedException {
        feeder.setPosition(position);
        sleep(time);
    }

    /*
     * Color Sensor Functionality
     */
    public float[] getRgb() {
        return new float[] {colorSensor.red(), colorSensor.green(), colorSensor.green()};
    }

    public float[] getHsv() {
        float[] hsvValues = {0f, 0f, 0f};
        Color.RGBToHSV((colorSensor.red() * 255) / 800, (colorSensor.green() * 255) / 800, (colorSensor.blue() * 255) / 800, hsvValues);
        return hsvValues;
    }

    /*
     * LED Functionality
     */
    void enableLed() {
        if(!bLedOn)
            toggleLed();
    }

    void disableLed() {
        if(bLedOn)
            toggleLed();
    }

    void toggleLed() {
        bLedOn = !bLedOn;
        cdim.setDigitalChannelState(LED_CHANNEL, bLedOn);
    }

    /*
     * Utility
     */
    static double bound(double value, double lower, double upper) {
        if(value < lower)
            return lower;
        else if (value > upper)
            return upper;
        else
            return value;
    }
}
