

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class Hardware {

    public DcMotor leftMotor;
    public DcMotor rightMotor;

    private HardwareMap hwMap;
    public static float WHEEL_BASE = 13.5f;
    private static double TICKS_PER_MOTOR_REV = 1440;
    private static double DRIVE_GEAR_RATIO = 1.0;
    private static double WHEEL_DIAMETER = 4.0;

    public static double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_RATIO) / (WHEEL_DIAMETER * Math.PI);

    public Hardware() {
    }

    public void init (HardwareMap hwm) {
        hwMap = hwm;

        leftMotor = hwMap.dcMotor.get("drive_left");
        rightMotor = hwMap.dcMotor.get("drive_right");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        leftMotor.setPower(0);
        rightMotor.setPower(0);
    }
}
