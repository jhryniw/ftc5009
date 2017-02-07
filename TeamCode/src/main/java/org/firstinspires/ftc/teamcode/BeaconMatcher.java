package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by James on 2017-02-06.
 */


public class BeaconMatcher {

    public enum BeaconType { RED_BLUE, BLUE_RED, RED_RED, BLUE_BLUE };

    private HashMap<BeaconType, Mat> templates = new HashMap<>();

    BeaconMatcher(Context ctx, double width, double height) {

        try {
            templates.put(BeaconType.RED_BLUE, Utils.loadResource(ctx, R.drawable.red_blue));
            templates.put(BeaconType.BLUE_RED, Utils.loadResource(ctx, R.drawable.blue_red));
            //templates.put(BeaconType.RED_RED, Utils.loadResource(ctx, R.drawable.red_red));
            //templates.put(BeaconType.BLUE_BLUE, Utils.loadResource(ctx, R.drawable.blue_blue));
        }
        catch (IOException e) {
            Log.e("OpenCV", "Error Loading Beacon Image!");
        }

        Size tSize = new Size(width, height);

        //Resize the images to desired size
        for(BeaconType t : templates.keySet()) {
            Imgproc.resize(templates.get(t), templates.get(t), tSize);
            Log.d("BeaconMatcher", "Template Size: " + templates.get(t).size());
        }
    }

    private double findMatch(Mat frame, Mat template) {
        Mat output = new Mat();
        Core.MinMaxLocResult result;

        Imgproc.matchTemplate(frame, template, output, Imgproc.TM_CCOEFF);
        result = Core.minMaxLoc(output);

        Log.d("BeaconMatcher", "Match found at x: " + result.maxLoc.x + " y: " + result.maxLoc.y);

        return result.maxVal;
    }

    public BeaconType searchForMatch(Mat temp) {
        //search all channels for match
        Mat frame = new Mat();
        BeaconType bestMatch = BeaconType.BLUE_BLUE;
        double bestResult = 0;

        Imgproc.cvtColor(temp, frame, Imgproc.COLOR_RGBA2RGB);

        for(BeaconType t : templates.keySet()) {
            double result = findMatch(frame, templates.get(t));

            Log.d("BeaconMatcher", t.name() + " result: " + result);

            if(result > bestResult) {
                bestMatch = t;
                bestResult = result;
            }
        }

        Log.d("BeaconMatcher", "Result: " + bestMatch.name());

        return bestMatch;
    }

    static Alliance[] beaconTypeToArray(BeaconType t) {
        switch (t) {
            case BLUE_RED:
                return new Alliance[] { Alliance.BLUE, Alliance.RED };
            case RED_BLUE:
                return new Alliance[] { Alliance.RED, Alliance.BLUE };
            case RED_RED:
                return new Alliance[] { Alliance.RED, Alliance.RED };
            case BLUE_BLUE:
                return new Alliance[] { Alliance.BLUE, Alliance.BLUE };
            default:
                return new Alliance[] {Alliance.NA, Alliance.NA};
        }
    }
}
