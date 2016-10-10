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

    private Robot robot = new Robot("proto1", new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
            return opModeIsActive();
        }
    });

    //TODO: Define Path List
    private HashMap<String, PathBase> pathList = new HashMap<>();
    private PathBase selectedPath;

    private int delay; //delay is in milliseconds

    @Override
    public void runOpMode() throws InterruptedException {
        //Run configuration

        //Select Path
        Set<String> strPathList = pathList.keySet();
        selectedPath = pathList.get("path");

        waitForStart();

        //Run selected path
        selectedPath.run();
    }
}
