package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by James on 2016-09-26.
 * Basic TeleOp Program for K9
 */

@TeleOp(name="K9TeleOp", group = "TeleOp")
public class K9TeleOp extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    public static final float POWER_THRESHOLD = 0.05f;
    public static final float CHICKEN_POWER = 0.5f;
    private int chicken_state = 0;
    public static final float SHOOTER_POWER = 0.9f;
    private  int shooter_state = 0;
    private boolean chicken_is_clicked = false;
    private boolean r2_is_clicked = false;
    private double pos = 0;

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        // Setup the motors using K9Hardware
        K9Hardware robot = new K9Hardware();
        robot.init(hardwareMap);

        runtime.reset();

        while (opModeIsActive()) {

            //Get joystick y values
            float l_power = -gamepad1.left_stick_y;
            float r_power = -gamepad1.right_stick_y;
            boolean chicken_power = gamepad1.a;
            float chicken_speed = 90;
            float shooter_power = gamepad1.right_trigger;


            //fix the debound error
           if (gamepad1.right_trigger > 0.5 && !r2_is_clicked) {
               r2_is_clicked = true;
                if(shooter_state !=1) {
                    robot.shooter.setPower((double) SHOOTER_POWER);
                    shooter_state = 1;
                }
                else {
                    robot.shooter.setPower(0);
                    shooter_state = 0;
                }
            } else if (gamepad1.right_trigger < 0.5 && r2_is_clicked){
               r2_is_clicked = false;
            }

            //handle chicken fingers
            if (gamepad1.a && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if(chicken_state != 1) {
                    robot.chickenfingers.setPower((double) CHICKEN_POWER);
                    chicken_state = 1;
                }
                else {
                    robot.chickenfingers.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (gamepad1.y && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state != -1) {
                    robot.chickenfingers.setPower((double) -CHICKEN_POWER);
                    chicken_state = -1;
                }
                else {
                    robot.chickenfingers.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (!gamepad1.a && !gamepad1.y && chicken_is_clicked) {
                chicken_is_clicked = false;
            }

           //handle servo
            if (gamepad1.x) {
                pos += 0.5;
                robot.crazy_servo.setPosition(pos);
            }
            else if (gamepad1.b) {
                pos -= 0.5;
                robot.crazy_servo.setPosition(pos);
            }
            else {
                robot.crazy_servo.setPosition(0);
            }

            if (l_power > 0)
                l_power = (float)Math.pow(l_power,2);
            else if (l_power < 0)
                l_power = -(float)Math.pow(l_power,2);

            if (r_power > 0)
                r_power = (float)Math.pow(r_power,2);
            else if (r_power < 0)
                r_power = -(float)Math.pow(r_power,2);

            if (Math.abs(l_power) < POWER_THRESHOLD)
                l_power = 0;
            if (Math.abs(r_power) < POWER_THRESHOLD)
                r_power = 0;

            //apply values to motor speed
            robot.leftMotor.setPower((double)l_power);
            robot.rightMotor.setPower((double)r_power);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.update();

            idle();

        }
    }
}
