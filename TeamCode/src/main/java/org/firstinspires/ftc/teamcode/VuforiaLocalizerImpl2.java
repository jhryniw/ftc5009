package org.firstinspires.ftc.teamcode;

import android.view.View;

import com.vuforia.CameraDevice;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

/**
 * Created by James on 2017-03-09.
 */

final class VuforiaLocalizerImpl2 extends VuforiaLocalizerImpl {

    VuforiaLocalizerImpl2(VuforiaLocalizer.Parameters params) {
        super(params);
    }

    @Override
    protected void startAR()
    {
        synchronized (startStopLock)
        {
            showLoadingIndicator(View.VISIBLE);

            updateActivityOrientation();

            this.vuforiaFlags = Vuforia.GL_20;

            Vuforia.setInitParameters(activity, vuforiaFlags, parameters.vuforiaLicenseKey);
            int initProgress = -1;
            do  {
                initProgress = Vuforia.init();
            }
            while (initProgress >= 0 && initProgress < 100);

            if (initProgress < 0)
                throwFailure("Vuforia initialization failed: %s", getInitializationErrorString(initProgress));

            initTracker();
            Vuforia.registerCallback(VuforiaLocalizerImpl2.this.vuforiaCallback);

            makeGlSurface();

            this.wantCamera = true;
            startCamera(parameters.cameraDirection.direction);

            // Try to turn on auto-focus; ignore if not supported
            CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
            Vuforia.setFrameFormat(PIXEL_FORMAT.RGB888, true);

            this.rendererIsActive = true;

            showLoadingIndicator(View.INVISIBLE);
        }
    }
}
