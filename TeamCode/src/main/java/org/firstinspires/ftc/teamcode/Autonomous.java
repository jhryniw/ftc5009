package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

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
        //Initialize robot/hardware
        robot = new Robot("proto1", hardwareMap, getCallbacks(this));

        //Register paths
        pathList.put("Ball Knocker", new BallKnocker(robot, Alliance.NA, new Coordinate(0, 0)));
        pathList.put("Beacons", new Beacons(robot, Alliance.NA, new Coordinate(0, 0)));

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
}
