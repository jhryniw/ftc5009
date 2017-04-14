package org.firstinspires.ftc.teamcode;

import com.vuforia.Vec2F;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import static android.R.attr.max;
import static android.R.attr.translationZ;

/**
 * Created by James on 2017-03-11.
 */

class BeaconTarget {

    static float BEACON_COLORED_WIDTH = 3f;
    static float BEACON_COLORED_HEIGHT = 5f;
    static float BUTTON_OFFSET = 2.625f;
    static float IMAGE_WIDTH = 10f;

    private static VectorF R_RIGHT = new VectorF(-10.5f, -4f, 0);
    private static VectorF R_LEFT = new VectorF(-10.5f, 1.125f, 0);

    private String type;
    private VectorF translation;
    private float angle;

    BeaconTarget() {
        type = "null";
        translation = new VectorF(0, 0, 0);
        angle = 0;
    }

    BeaconTarget(String type, OpenGLMatrix pose) {
        this.type = type;

        translation = pose.getTranslation().multiplied(1f / RobotLocator.MM_PER_INCH);
        translation.put(2, -translation.get(2)); //flip z

        //angle = RobotLocator.getEuler(pose).get(1); //heading
    }

    BeaconTarget(BeaconTarget target) {
        this.type = target.type;
        this.translation = target.translation;
        this.angle = target.angle;
    }

    boolean isNone() {
        return translation.get(0) == 0 && translation.get(1) == 0 && translation.get(2) == 0;
    }

    VectorF toVector() {
        return new VectorF(translation.get(0), translation.get(1), translation.get(2), angle);
    }

    float getX() { return translation.get(0); }
    float getY() { return translation.get(1); }
    float getZ() { return translation.get(2); }

    VectorF getRoi(boolean right) {
        float x1, y1, x2, y2;

        float[] size = VuforiaWrapper.Instance.getCameraCalibration().getSize().getData();
        float[] focalLength = VuforiaWrapper.Instance.getCameraCalibration().getFocalLength().getData();
        float[] principalPoint = VuforiaWrapper.Instance.getCameraCalibration().getPrincipalPoint().getData();

        float pixelsPerInchX = focalLength[0] / translation.get(2);
        float pixelsPerInchY = focalLength[1] / translation.get(2);

        if(right) {
            x1 = principalPoint[0] + (-translation.get(1) + R_RIGHT.get(0)) * pixelsPerInchX;
            y1 = principalPoint[1] + (-translation.get(0) + R_RIGHT.get(1)) * pixelsPerInchY;
        }
        else {
            x1 = principalPoint[0] + (-translation.get(1) + R_LEFT.get(0)) * pixelsPerInchX;
            y1 = principalPoint[1] + (-translation.get(0) + R_LEFT.get(1)) * pixelsPerInchY;
        }

        x2 = x1 + BEACON_COLORED_HEIGHT * pixelsPerInchX;
        y2 = y1 + BEACON_COLORED_WIDTH * pixelsPerInchY;

        x1 = forceInBound(x1, size[0]);
        y1 = forceInBound(y1, size[1]);
        x2 = forceInBound(x2, size[0]);
        y2 = forceInBound(y2, size[1]);

        return new VectorF(x1, y1, x2, y2);
    }

    private float forceInBound(float value, float max) {
        if(value < 0)
            return 0;
        else if (value > max)
            return max;
        else
            return value;
    }
}
