package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class K9Hardware {

    public DcMotor chickenfingers;
    public DcMotor leftMotor;
    public DcMotor rightMotor;


    private HardwareMap hwMap;
    private static double TICKS_PER_MOTOR_REV = 1440;
    private static double DRIVE_GEAR_RATIO = 1.0;
    private static double WHEEL_DIAMETER = 4.0;

    public static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);

    public K9Hardware () {
    }

    public void init (HardwareMap hwm) {
        hwMap = hwm;

        leftMotor = hwMap.dcMotor.get("drive_left");
        rightMotor = hwMap.dcMotor.get("drive_right");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        chickenfingers = hwMap.dcMotor.get("chicken_fingers");

        chickenfingers.setPower(0);
        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}
