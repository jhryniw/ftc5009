package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.util.Log;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Scalar;

/**
 * Created by s on 22/02/2017. inits OpenCV in a static function
 */

public class OpenCVInitializer {

    public static void initOpenCV(Activity activity){

        BaseLoaderCallback loaderCallback = new BaseLoaderCallback(activity) {
            @Override
            public void onManagerConnected(int status) {
                Log.i("OpenCV", "OpenCV loading ...");
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS:
                    {
                        Log.i("OpenCV", "OpenCV loaded successfully");
                    } break;
                    default:
                    {
                        super.onManagerConnected(status);
                    } break;
                }
            }
        };

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, activity, loaderCallback);

        Log.d("OpenCV", "initOpenCV");

    }

}
