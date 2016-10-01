package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.annotation.Target;

/**
 * Created by James on 2016-10-01.
 */
@Autonomous (name = "K9Autonomous")
public class K9Autonomous extends LinearOpMode {

    private K9Hardware robot = new K9Hardware();
    private ElapsedTime runtime = new ElapsedTime();

    static final double TICKS_PER_MOTOR_REV = 1440;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER = 4.0;
    static final double TICKS_PER_INCH = (TICKS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER * Math.PI);


    static final double DRIVE_SPEED = 0.6;




    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //Move foraward for one second
        waitForStart();

        /*robot.leftMotor.setPower(0.5);
        robot.rightMotor.setPower(0.5);

        //Pause for 1 second
        sleep(1000);

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);*/

        encoderDrive(0.9, 72, 9);
    }

    public void encoderDrive (double speed, double distance, double timeouts ) throws InterruptedException{
        int target;

        runtime.reset();

    //Ensure that OpMode is still active

        target = robot.leftMotor.getCurrentPosition() + (int)(distance*TICKS_PER_INCH);

        robot.leftMotor.setPower(speed);
        robot.rightMotor.setPower(speed);
        while (robot.leftMotor.getCurrentPosition()<target && runtime.seconds()<timeouts){sleep(5);}

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
    }
}