package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.lang.annotation.Target;

import static org.firstinspires.ftc.teamcode.K9Hardware.TICKS_PER_INCH;

/**
 * Created by James on 2016-10-01.
 */
@Autonomous (name = "K9Autonomous")
public class K9Autonomous extends LinearOpMode {

    private K9Hardware robot = new K9Hardware();
    private ElapsedTime runtime = new ElapsedTime();

    static final double DRIVE_SPEED = 0.6;

    @Override
    public void runOpMode() throws InterruptedException {
        robot.init(hardwareMap);

        waitForStart();

        encoderDrive(0.9, 72, 9);
    }

    public void encoderDrive (double speed, double distance, double timeoutS ) throws InterruptedException{
        int target;

        runtime.reset();
        target = robot.leftMotor.getCurrentPosition() + (int)(distance * TICKS_PER_INCH);

        //TODO: Check if RUN_ENCODER is needed for getCurrentPosition to work
        robot.rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        robot.leftMotor.setPower(speed);
        robot.rightMotor.setPower(speed);

        while (opModeIsActive() && robot.leftMotor.getCurrentPosition() < target && runtime.seconds() < timeoutS) {
            //TODO: add telemetry to track position
            sleep(5);
        }

        robot.leftMotor.setPower(0);
        robot.rightMotor.setPower(0);
    }
}