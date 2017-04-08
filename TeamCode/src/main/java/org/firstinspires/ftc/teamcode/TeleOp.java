package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.teamcode.Hardware.chickenMotor;
import static org.firstinspires.ftc.teamcode.Hardware.feeder;
import static org.firstinspires.ftc.teamcode.Hardware.liftMotor;
import static org.firstinspires.ftc.teamcode.Hardware.leftMotor;
import static org.firstinspires.ftc.teamcode.Hardware.limit;
import static org.firstinspires.ftc.teamcode.Hardware.rightMotor;
import static org.firstinspires.ftc.teamcode.Hardware.leftClaw;
import static org.firstinspires.ftc.teamcode.Hardware.rightClaw;
import static org.firstinspires.ftc.teamcode.Hardware.shooterMotorRight;
import static org.firstinspires.ftc.teamcode.Hardware.shooterMotorLeft;
import static org.firstinspires.ftc.teamcode.Hardware.slider;

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

    @Override
    public void runOpMode() throws InterruptedException {
        // Setup the motors using K9Hardware
        Hardware.init(hardwareMap);

        waitForStart();

        runtime.reset();

        while (opModeIsActive()) {

            double ts_cycle = runtime.milliseconds();

            //Lift

            if (gamepad1.right_trigger > 0.5 || gamepad2.right_trigger > 0.5) {
                openClaws();
                liftMotor.setPower(1);
            } else if (gamepad1.left_trigger > 0.5 || gamepad2.left_trigger > 0.5) {
                openClaws();
                liftMotor.setPower(-1);
            } else
                liftMotor.setPower(0);

            if (gamepad2.a && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state != 1) {
                    chickenMotor.setPower((double) CHICKEN_POWER);
                    chicken_state = 1;
                }
                else {
                    chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            }
            else if (gamepad2.y && !chicken_is_clicked) {
                chicken_is_clicked = true;
                if (chicken_state!= -1) {
                    chickenMotor.setPower((double) -CHICKEN_POWER);
                    chicken_state = -1;
                }
                else {
                    chickenMotor.setPower(0);
                    chicken_state = 0;
                }
            } else if (!gamepad2.a && !gamepad2.y && chicken_is_clicked) {
                chicken_is_clicked = false;
            }

            //Shooter
            if (gamepad2.b && !r2_is_clicked) {
                r2_is_clicked = true;
                if (shooter_state != 1) {
                    shooterMotorRight.setPower((double) SHOOTER_POWER);
                    shooterMotorLeft.setPower((double) SHOOTER_POWER);
                    shooter_state = 1;
                } else {
                    shooterMotorRight.setPower(0);
                    shooterMotorLeft.setPower(0);
                    shooter_state = 0;
                }
            } else if (!gamepad2.b && r2_is_clicked) {
                r2_is_clicked = false;
            }

            //Feeder
            if (gamepad1.a && shooterMotorLeft.getPower() > 0) {
                double feedPos = feeder.getPosition();
                while (feedPos > 0) {
                    feedPos = feeder.getPosition() - 0.05;
                    sleep(10);
                    feeder.setPosition(feedPos);
                }

                sleep(500);

                feedPos = feeder.getPosition();

                while (feedPos < 1) {
                    feedPos = feeder.getPosition() + 0.05;
                    sleep(10);
                    feeder.setPosition(feedPos);
                }
                sleep(500);
            }

            if(gamepad2.dpad_up) {
                openClaws();
            }
            else if (gamepad2.dpad_down) {
                closeClaws();
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


            //slow mode
            if (gamepad1.dpad_up) {
                l_power = 0.2;
                r_power = 0.2;
            } else if (gamepad1.dpad_down) {
                l_power = -0.2;
                r_power = -0.2;
            } else if (gamepad1.dpad_right) {
                l_power = 0.1;
                r_power = -0.1;
            } else if (gamepad1.dpad_left) {
                l_power = -0.1;
                r_power = 0.1;
            }

            if (gamepad1.left_bumper || gamepad2.left_bumper){
                slideLeft();
            } else if (gamepad1.right_bumper || gamepad2.right_bumper) {
                slideRight();
            } else {
                slider.setPower(0.05);
            }


                //apply values to motor speed
            leftMotor.setPower(l_power);
            rightMotor.setPower(r_power);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Claws", "Left: %.2f Right %.2f", leftClaw.getPosition(), rightClaw.getPosition());
            telemetry.addData("Left", "Bumper: %s Trigger: %.2f", String.valueOf(gamepad1.right_trigger), gamepad1.left_trigger);
            telemetry.update();

            idle();
        }
    }

    private void openClaws() {
        leftClaw.setPosition(1);
        rightClaw.setPosition(1);
    }

    private void closeClaws() {
        Servo[] claws = {leftClaw,rightClaw};
        for(Servo claw:claws) {
            double pos = claw.getPosition() - 0.05;
            if(pos > 0) claw.setPosition(pos);
            else claw.setPosition(0);
        }
    }
    private void slideLeft() {
        slider.setPower(-1);
    }
    private void slideRight() {
        slider.setPower(1);
    }
}
