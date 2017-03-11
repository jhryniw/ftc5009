package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.vuforia.Image;
import com.vuforia.PIXEL_FORMAT;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by James on 2017-03-09.
 */

class FrameExtractor {
    private static BlockingQueue<VuforiaLocalizer.CloseableFrame> frameQueue;

    static void init() {
        frameQueue = VuforiaWrapper.Instance.getFrameQueue();
    }

    static Mat getFrame() {
        try {
            VuforiaLocalizer.CloseableFrame vuforiaFrame = frameQueue.poll(3000, TimeUnit.MILLISECONDS);

            if(vuforiaFrame == null) {
                throw new NullPointerException("Unable to get frame from Vuforia");
            }

            Image rgbImage = null;
            Image grayImage = null;

            for (int i = 0; i < vuforiaFrame.getNumImages(); i++) {
                Image img = vuforiaFrame.getImage(i);
                if (img.getFormat() == PIXEL_FORMAT.RGB888) {
                    rgbImage = img;
                    break;
                }
                else if (grayImage == null && img.getFormat() == PIXEL_FORMAT.GRAYSCALE) {
                    grayImage = img;
                }
            }

            if(rgbImage == null && grayImage == null)
                throw new NullPointerException("Unable to find image with RGB or grayscale format in vuforia frame");

            if(rgbImage == null) { //Use grayImage...

                Mat frame = new Mat();
                Mat grayFrame = new Mat(grayImage.getHeight(), grayImage.getWidth(), CvType.CV_8UC1);

                byte[] grayBuffer = new byte[grayImage.getHeight() * grayImage.getWidth()];
                grayImage.getPixels().get(grayBuffer);

                grayFrame.put(0, 0, grayBuffer);

                //Convert to RGB
                Imgproc.cvtColor(grayFrame, frame, Imgproc.COLOR_GRAY2RGB);
                Log.d("Mean", Core.mean(grayFrame).toString() + " Color " + Core.mean(frame).toString());
                return frame;
            }

            Mat frame = new Mat(rgbImage.getHeight(), rgbImage.getWidth(), CvType.CV_8UC3);

            byte[] buffer = new byte[rgbImage.getHeight() * rgbImage.getWidth()];
            rgbImage.getPixels().get(buffer);

            frame.put(0, 0, buffer);

            vuforiaFrame.close();

            Log.d("Vuforia Frame", "Extracted size: " + frame.size());
            return frame;
        }
        catch (NullPointerException|InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
