package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.util.Log;

import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

/**
 * Created by James on 2017-03-09.
 */

class VuforiaWrapper {

    static VuforiaLocalizer Instance;

    private static boolean isInitialized = false;

    public static void init(Context ctx) {

        if (isInitialized)
            return;

        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = ctx.getString(R.string.vuforia_license_key);
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        Instance = new VuforiaLocalizerImpl2(params);
        Instance.setFrameQueueCapacity(1);

        FrameExtractor.init();
        RobotLocator.init();

        Log.d("Vuforia", "Vuforia Initialized");
        isInitialized = true;
    }
}
