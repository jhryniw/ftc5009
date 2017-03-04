

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannelController;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class Hardware {

    //Motors
    public DcMotor leftMotor;
    public DcMotor rightMotor;
    public DcMotor chickenMotor;
    public DcMotor shooterMotor;
    public DcMotor shooterMotor2;
    public DcMotor liftMotor;

    //Servos
    public Servo leftClaw;
    public Servo rightClaw;
    public Servo feeder;

    //Color Sensor + LED
    public DeviceInterfaceModule cdim;
    public ColorSensor colorSensor;
    public boolean bLedOn = true;
    public static final int LED_CHANNEL = 5; // we assume that the LED pin of the RGB sensor is connected to digital port 5 (zero indexed).
    public TouchSensor limit;


    private HardwareMap hwMap;
    public static double WHEEL_BASE = 13.5;
    public static double WHEEL_DIAMETER = 4.0;
    public static int ROUNDS_PER_MINUTE = 160;
    private static double TICKS_PER_MOTOR_REV = 1120;
    private static double DRIVE_GEAR_RATIO = 1.0;


    public static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);

    public Hardware() {
    }

    public void init (HardwareMap hwm) {
        hwMap = hwm;

        try {
            leftMotor = hwMap.dcMotor.get("drive_left");
            rightMotor = hwMap.dcMotor.get("drive_right");
            chickenMotor = hwMap.dcMotor.get("chicken_fingers");
            shooterMotor = hwMap.dcMotor.get("shooter");
            shooterMotor2 = hwMap.dcMotor.get("shooter_2");
            liftMotor = hwMap.dcMotor.get("lift");
            limit = hwMap.touchSensor.get("limit");

            //we don't need it
            //armmotor = hwMap.dcMotor.get("rightdrive")
            //toplimit = hwMap.touchSensor.get("limit_1")
            //bottomlimit = hwMap.touchSensor.get("limit_2")

            leftClaw = hwMap.servo.get("left_claw");
            rightClaw = hwMap.servo.get("right_claw");
            feeder = hwMap.servo.get("feeder");
        }
        catch (NullPointerException e) {
            throw new NullPointerException("Error: a motor did not initialize properly. Check the configuration!");
        }

        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        leftMotor.setPower(0);
        rightMotor.setPower(0);
        chickenMotor.setPower(0);
        shooterMotor.setPower(0);
        shooterMotor2.setPower(0);

        leftClaw.scaleRange(0.05, 0.95);
        rightClaw.scaleRange(0.05, 0.95);
        feeder.scaleRange(0.05 , 0.95);

        //leftClaw.setDirection(Servo.Direction.REVERSE);

        leftClaw.setPosition(1);
        rightClaw.setPosition(0);
        feeder.setPosition(1);

        //ColorSensor setup
        /*cdim = hwMap.deviceInterfaceModule.get("dim");
        cdim.setDigitalChannelMode(LED_CHANNEL, DigitalChannelController.Mode.OUTPUT);
        colorSensor = hwMap.colorSensor.get("color");*/
    }
}
