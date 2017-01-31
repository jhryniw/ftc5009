package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by James on 2016-09-26.
 * Basic TeleOp Program for K9
 */

@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group = "TeleOp")
public class TeleOp extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();
    public static final float POWER_THRESHOLD = 0.05f;
    public static final float CHICKEN_POWER = 0.5f;
    private int chicken_state = 0;
    public static final float SHOOTER_POWER = 1f;
    private  int shooter_state = 0;
    private boolean chicken_is_clicked = false;
    private boolean r2_is_clicked = false;
    private boolean servo_is_clicked = false;

    @Override
    public void runOpMode() throws InterruptedException {

        waitForStart();

        // Setup the motors using K9Hardware
        Hardware robot = new Hardware();
        robot.init(hardwareMap);

        runtime.reset();

        while (opModeIsActive()) {

            double ts_cycle = runtime.milliseconds();

            //Lift
            //TODO: Make servos go outwards when the lift is raised
            if(gamepad1.right_trigger > 0.5) {
                robot.liftMotor.setPower(1);
            }
            else if (gamepad1.left_trigger > 0.5) {
                robot.liftMotor.setPower(-1);
            }
            else
                robot.liftMotor.setPower(0);

            boolean chicken_power = gamepad1.a;
            float chicken_speed = 90;
            float shooter_power = gamepad1.right_trigger;

            if (gamepad2.a && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state != 1) {
                    robot.chickenMotor.setPower((double) CHICKEN_POWER);
                    chicken_state = 1;
                }
                else {
                    robot.chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (gamepad2.y && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state!= -1) {
                    robot.chickenMotor.setPower((double) -CHICKEN_POWER);
                    chicken_state = -1;
                }
                else {
                    robot.chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (!gamepad2.a &&!gamepad2.y &&chicken_is_clicked) {
                chicken_is_clicked = false;
            }

            /*
            boolean chicken_power = gamepad1.a;
            float chicken_speed = 90;
            float shooter_power = gamepad1.right_trigger;
*/

            //Shooter
            if (gamepad2.right_trigger > 0.5 && !r2_is_clicked) {
                r2_is_clicked = true;
                if(shooter_state !=1) {
                    robot.shooterMotor.setPower((double) SHOOTER_POWER);
                    shooter_state = 1;
                }
                else {
                    robot.shooterMotor.setPower(0);
                    shooter_state = 0;
                }
            } else if (gamepad2.right_trigger < 0.5 && r2_is_clicked){
               r2_is_clicked = false;
            }
            /*

            //handle chicken fingers
            if (gamepad1.a && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if(chicken_state != 1) {
                    robot.chickenMotor.setPower((double) CHICKEN_POWER);
                    chicken_state = 1;
                }
                else {
                    robot.chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (gamepad1.y && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state != -1) {
                    robot.chickenMotor.setPower((double) -CHICKEN_POWER);
                    chicken_state = -1;
                }
                else {
                    robot.chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (!gamepad1.a && !gamepad1.y && chicken_is_clicked) {
                chicken_is_clicked = false;
            }

           //handle servo bounce error
            if (gamepad1.x && servo_is_clicked == 0) {
                servo_is_clicked = 1;
                robot.crazy_servo.setPosition(0.9);
                //robot.crazy_servo.setPosition(robot.crazy_servo.getPosition() + 0.5);
                //robot.crazy_servo.setPosition(0.5);
            }
            else if (gamepad1.b && servo_is_clicked == 0) {
                servo_is_clicked = -1;
                robot.crazy_servo.setPosition(0.1);
                //robot.crazy_servo.setPosition(robot.crazy_servo.getPosition() - 0.5);
                //robot.crazy_servo.setPosition(0.5);
            }
            else if(!(gamepad1.b || gamepad1.x) ){
                servo_is_clicked = 0;
            }
            /*else if ((gamepad1.x && servo_is_clicked == 1) || (gamepad1.b && servo_is_clicked == -1)); {
                servo_is_clicked = 0;
                robot.crazy_servo.setPosition(0.5);

            }*/

            //Feeder
            if(gamepad2.dpad_up) {
                double feedPos = robot.feeder.getPosition() + 0.05;
                if (feedPos > 1) {
                    robot.feeder.setPosition(1);
                } else
                    robot.feeder.setPosition(feedPos);
            }
            if(gamepad2.dpad_down) {
                double feedPos = robot.feeder.getPosition() - 0.05;
                if (feedPos < 0) {
                    robot.feeder.setPosition(0);
                } else
                    robot.feeder.setPosition(feedPos);
            }


             if(gamepad1.dpad_up) {

                double leftPos = robot.leftClaw.getPosition() - 0.05;
                double rightPos = robot.rightClaw.getPosition() + 0.05;

                //outward
                if(leftPos < 0)
                    robot.leftClaw.setPosition(0);
                else
                    robot.leftClaw.setPosition(leftPos);

                //inward
                if(rightPos > 1)
                    robot.rightClaw.setPosition(1);
                else
                    robot.rightClaw.setPosition(rightPos);
            }
            else if (gamepad1.dpad_down) {
                double leftPos = robot.leftClaw.getPosition() + 0.05;
                double rightPos = robot.rightClaw.getPosition() - 0.05;

                //inward
                if(leftPos > 1)
                    robot.leftClaw.setPosition(1);
                else
                    robot.leftClaw.setPosition(leftPos);

                //outward
                if(rightPos < 0)
                    robot.rightClaw.setPosition(0);
                else
                    robot.rightClaw.setPosition(rightPos);
            }

            //Get joystick y values
            double l_power = -gamepad1.left_stick_y;
            double r_power = -gamepad1.right_stick_y;

            l_power = Math.signum(l_power) * l_power * l_power;
            r_power = Math.signum(r_power) * r_power * r_power;

            if (Math.abs(l_power) < POWER_THRESHOLD)
                l_power = 0;
            if (Math.abs(r_power) < POWER_THRESHOLD)
                r_power = 0;

            //apply values to motor speed
            robot.leftMotor.setPower(l_power);
            robot.rightMotor.setPower(r_power);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Claws", "Left: %.2f Right %.2f", robot.leftClaw.getPosition(), robot.rightClaw.getPosition());
            telemetry.addData("Left", "Bumper: %s Trigger: %.2f", String.valueOf(gamepad1.right_trigger), gamepad1.left_trigger);
            telemetry.update();

            idle();
        }
    }
}
