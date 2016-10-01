package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * Created by James on 2016-09-26.
 * Stores Basic K9 Hardware Mapping
 */

public class K9Hardware {

    public DcMotor leftMotor;
    public DcMotor rightMotor;

    private HardwareMap hwMap;

    public K9Hardware (){
    }

    public void init (HardwareMap hwm) {
        hwMap = hwm;

        leftMotor = hwMap.dcMotor.get("drive_left");
        rightMotor = hwMap.dcMotor.get("drive_right");
        leftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        leftMotor.setPower(0);
        rightMotor.setPower(0);

        leftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }
}
