package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Paint;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.opencv.android.Utils.loadResource;

/**
 * Created by James on 2017-02-06.
 */

class BeaconMatcher {

    enum BeaconType { RED_BLUE, BLUE_RED, RED_RED, BLUE_BLUE }

    private class BeaconTemplate {

        private Size MIN_SIZE = new Size(60, 40);
        private Mat oTemplate;
        ArrayList<Mat> pyramid = new ArrayList<>();
        BeaconType type;

        BeaconTemplate(Context ctx, int resid, BeaconType type, Size max_size, int max_levels) {
            this.type = type;

            try {
                oTemplate = Utils.loadResource(ctx, resid);
            }
            catch (IOException e) {
                Log.e("OpenCV", "Error Loading Beacon Image!");
            }

            Imgproc.resize(oTemplate, oTemplate, max_size);

            pyramid.add(0, oTemplate);
            Mat temp = oTemplate;

            for(int level = 1; level < max_levels && temp.width() > MIN_SIZE.width && temp.height() > MIN_SIZE.height; level++) {
                Mat newTemp = new Mat();
                Size nextSize = scale(max_size, Math.pow(0.75, level));
                Imgproc.pyrDown(temp, newTemp);
                pyramid.add(level, newTemp);
                temp = newTemp.clone();
                Log.d("BeaconMatcher", "Level: " + level + " Template Size: " + temp.size());
            }
        }

        private Size scale(Size s, double factor) {
            return new Size((int) s.width * factor, (int) s.height * factor);
        }
    }

    private ArrayList<BeaconTemplate> templates = new ArrayList<>();

    BeaconMatcher(Context ctx, double width, double height) {

        Size tSize = new Size(width, height);

        templates.add(new BeaconTemplate(ctx, R.drawable.med_red_blue, BeaconType.RED_BLUE, tSize, 3));
        templates.add(new BeaconTemplate(ctx, R.drawable.med_blue_red, BeaconType.BLUE_RED, tSize, 3));
    }

    private double findMatch(Mat frame, Mat template) {
        Mat output = new Mat();
        Core.MinMaxLocResult result;

        Imgproc.matchTemplate(frame, template, output, Imgproc.TM_CCOEFF_NORMED);
        result = Core.minMaxLoc(output);

        Log.d("BeaconMatcher", "Match found at x: " + result.maxLoc.x + " y: " + result.maxLoc.y);

        return result.maxVal;
    }

    public BeaconType searchForMatch(Mat frame) {
        //search all channels for match
        BeaconType bestMatch = BeaconType.BLUE_BLUE;
        double bestResult = 0;

        for( BeaconTemplate template : templates ) {
            for( int level = 0; level < template.pyramid.size(); level++ ) {
                Mat t = template.pyramid.get(level);

                double result = findMatch(frame, t);

                Log.d("BeaconMatcher", template.type.name() + " level: " + level + " size: " + t.size() +  " result: " + result);

                if (result > bestResult) {
                    bestMatch = template.type;
                    bestResult = result;
                }
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
