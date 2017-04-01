package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by James on 2017-03-18.
 */

public class Controller {

    static double P_A = 0.25;
    static double P_Z = 0.20;
    static double TARGET_Z = 310; //mm

    static void setPA(double newValue) {
        P_A = newValue;
    }
    static void setPZ(double newValue) {
        P_Z = newValue;
    }
    static void setTZ(double newValue) {
        TARGET_Z = newValue;
    }

    static void readControllerParameters(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

        Controller.setPA(prefs.getFloat("PROPORTIONAL_A", (float) Controller.P_A));
        Controller.setPZ(prefs.getFloat("PROPORTIONAL_Z", (float) Controller.P_Z));
        Controller.setTZ(prefs.getInt("TARGET_Z", (int) Controller.TARGET_Z));
    }
}
