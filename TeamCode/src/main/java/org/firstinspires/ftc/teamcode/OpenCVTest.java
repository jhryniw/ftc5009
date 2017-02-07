package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.util.Log;
import android.view.SurfaceView;

import com.qualcomm.robotcore.eventloop.opmode.*;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

/**
 * Created by James on 2017-02-04.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name="OpenCVTest")
public class OpenCVTest extends LinearOpMode {

    BeaconClassifier beaconClassifier;

    @Override
    public void runOpMode() throws InterruptedException {

        /*
         * Camera ID 0: Back Camera
         * Camera ID 1: Front Camera
         */
        beaconClassifier = new BeaconClassifier((Activity) hardwareMap.appContext, 0);

        waitForStart();

        beaconClassifier.setPreviewVisibility(SurfaceView.VISIBLE);
        Alliance[] result = beaconClassifier.classify();
        if(result == BeaconClassifier.CLASSIFICATION_ERROR) {
            telemetry.addData("OpenCV", "Error...");
        }
        else {
            telemetry.addData("OpenCV", "Classification succeeded!");
            telemetry.addData("OpenCV", "Result { %s, %s }", result[0].toString(), result[1].toString());
        }

        telemetry.update();

        sleep(5000);
        beaconClassifier.close();
    }
}
