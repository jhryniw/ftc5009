

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class Hardware {

    //Motors
    static DcMotor leftMotor;
    static DcMotor rightMotor;
    static DcMotor chickenMotor;
    static DcMotor shooterMotorRight;
    static DcMotor shooterMotorLeft;
    static DcMotor liftMotor;

    private static double baseSpeed = 0;
    private static double angularSpeed = 0;

    //Servos
    static Servo leftClaw;
    static Servo rightClaw;
    static Servo feeder;
    static CRServo slider;


    //Color Sensor + LED
    static DeviceInterfaceModule cdim;
    static ColorSensor colorSensor;
    static boolean bLedOn = true;
    static final int LED_CHANNEL = 5; // we assume that the LED pin of the RGB sensor is connected to digital port 5 (zero indexed).
    static TouchSensor limit;

    private static HardwareMap hwMap;
    static double WHEEL_BASE = 16;
    static double WHEEL_DIAMETER = 4.0;
    static int ROUNDS_PER_MINUTE = 160;
    private static double TICKS_PER_MOTOR_REV = 1120;
    private static double DRIVE_GEAR_RATIO = 1.0;

    static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);
    static double SLIDER_TRACK_LENGTH = 10.5;
    static double SLIDER_MAX_SPEED = 2.5; //2 7/16"

    public Hardware() throws Exception {
        throw new Exception("do not call this constructor");
    }

    public static void init (HardwareMap hwm) {
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
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
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
    static void setPower() {
        double lPower = baseSpeed + angularSpeed;
        double rPower = baseSpeed - angularSpeed;

        setPower(lPower, rPower);
    }
    static void setPower(double lpower, double rpower) {

        setMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftMotor.setPower(bound(lpower, -1, 1));
        rightMotor.setPower(bound(rpower, -1, 1));
    }

    static void setBaseSpeed(double speed) {
        baseSpeed = speed;
    }

    static void setAngularSpeed(double angular) {
        angularSpeed = angular;
    }

    static void stop() {
        setMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private static void setMotorMode(DcMotor.RunMode targetMode) {
        if(leftMotor.getMode() != targetMode)
            leftMotor.setMode(targetMode);

        if(rightMotor.getMode() != targetMode)
            rightMotor.setMode(targetMode);
    }

    static double bound(double value, double lower, double upper) {
        if(value < lower)
            return lower;
        else if (value > upper)
            return upper;
        else
            return value;
    }
}
