

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
    public static DcMotor leftMotor;
    public static DcMotor rightMotor;
    public static DcMotor chickenMotor;
    public static DcMotor shooterMotorRight;
    public static DcMotor shooterMotorLeft;
    public static DcMotor liftMotor;

    //Servos
    public static Servo leftClaw;
    public static Servo rightClaw;
    public static Servo feeder;
    public static CRServo slider;

    //Color Sensor + LED
    public static DeviceInterfaceModule cdim;
    public static ColorSensor colorSensor;
    public static boolean bLedOn = true;
    public static final int LED_CHANNEL = 5; // we assume that the LED pin of the RGB sensor is connected to digital port 5 (zero indexed).
    public static TouchSensor limit;


    private static HardwareMap hwMap;
    public static double WHEEL_BASE = 16;
    public static double WHEEL_DIAMETER = 4.0;
    public static int ROUNDS_PER_MINUTE = 160;
    private static double TICKS_PER_MOTOR_REV = 1120;
    private static double DRIVE_GEAR_RATIO = 1.0;

    public static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);

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
}
