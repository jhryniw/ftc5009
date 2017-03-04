package org.firstinspires.ftc.teamcode;

import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

/**
 * Created by James on 2017-03-03.
 */

public final class VuforiaLocalizerImpl2 extends VuforiaLocalizerImpl {

    public VuforiaLocalizerImpl2(VuforiaLocalizer.Parameters parameters) {
        super(parameters);
    }

    @Override
    protected void startAR() {

        synchronized (startStopLock) {
            Vuforia.setFrameFormat(PIXEL_FORMAT.RGB888, true);
            super.startAR();
        }

    }
}
