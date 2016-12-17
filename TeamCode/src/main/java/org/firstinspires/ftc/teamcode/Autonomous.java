package org.firstinspires.ftc.teamcode;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.SeekBar;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Main Autonomous Class
 * Created by James on 2016-10-09.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="Autonomous")
public class Autonomous extends LinearOpMode {

    private Robot robot;

    //TODO: Define Path List
    private HashMap<String, PathBase> pathList = new HashMap<>();
    private PathBase selectedPath;

    private int delay; //delay is in milliseconds

    @Override
    public void runOpMode() throws InterruptedException {
        buildConfigDialog();

        //Initialize robot/hardware
        robot = new Robot("proto1", hardwareMap, getCallbacks(this));

        //Register paths
        pathList.put("Ball Knocker", new BallKnocker(robot, Alliance.NA, new Coordinate(0, 0)));
        pathList.put("Beacons", new Beacons(robot, Alliance.NA, new Coordinate(0, 0)));
        pathList.put("Red Beacons", new RedBeacons(robot, Alliance.RED, new Coordinate(0, 0)));
        pathList.put("Corner Goal", new CornerGoal(robot, Alliance.BLUE, new Coordinate(0, 0)));
        pathList.put("Ball Shooter", new BallShooter (robot, Alliance.NA, new Coordinate(0, 0)));

        //Run configuration


        //Select Path
        Set<String> strPathList = pathList.keySet();
        selectedPath = pathList.get("Beacons");

        telemetry.addLine("Status");
        telemetry.addLine("EncoderTarget");
        telemetry.addLine("Encoder");
        updateTelemetry(telemetry);

        waitForStart();

        //Run selected path
        telemetry.addData("Status", "Running the path!");
        selectedPath.run();
        idle();
    }


    private OpModeCallbacks getCallbacks(final LinearOpMode opMode) {
        return new OpModeCallbacks() {
            @Override
            public void idle() throws InterruptedException {
                opMode.idle();
            }

            @Override
            public boolean opModeIsActive() {
                return opMode.opModeIsActive();
            }

            @Override
            public void addData(String caption, String format, Object... args) {
                telemetry.addData(caption, format, args);
            }

            @Override
            public void updateTelemetry() {
                opMode.updateTelemetry(telemetry);
            }
        };
    }

    private void buildConfigDialog() {

        FtcRobotControllerActivity ftcActivity = (FtcRobotControllerActivity) hardwareMap.appContext;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ftcActivity);

        SeekBar delayBar = new SeekBar(ftcActivity);
        delayBar.setMax(300);

        delayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                delay = i * 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // 2. Chain together various setter methods to set the dialog characteristics
        builder .setTitle("Configuration")
                .setMessage("Delay:")
                .setView(delayBar);

        ftcActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
