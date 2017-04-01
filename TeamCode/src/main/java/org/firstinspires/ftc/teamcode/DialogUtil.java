package org.firstinspires.ftc.teamcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

/**
 * Created by James on 2017-03-19.
 */

class DialogUtil {

    static void buildConfigDialog(Context ctx) {

        final FtcRobotControllerActivity ftcActivity = (FtcRobotControllerActivity) ctx;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ftcActivity);

        Controller.readControllerParameters(ftcActivity);

        LayoutInflater inflater = ftcActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pid_configuration, null);

        builder.setTitle("Configuration")
                .setView(dialogView)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ftcActivity);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("PROPORTIONAL_A", (float) Controller.P_A);
                        editor.putFloat("PROPORTIONAL_Z", (float) Controller.P_Z);
                        editor.putInt("TARGET_Z", (int) Controller.TARGET_Z);
                        editor.apply();

                        dialog.dismiss();
                    }
                });

        final TextView txtPaBar = (TextView) dialogView.findViewById(R.id.txtPropA);
        txtPaBar.setText(String.format("P_A: %.3f", Controller.P_A));

        SeekBar paBar = (SeekBar) dialogView.findViewById(R.id.propA);
        paBar.setMax(40);
        paBar.setProgress((int) (Controller.P_A * 400));
        paBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Controller.setPA((float)i / 400);
                txtPaBar.setText(String.format("P_A: %.3f", (float)i / 400));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        final TextView txtPzBar = (TextView) dialogView.findViewById(R.id.txtPropZ);
        txtPzBar.setText(String.format("P_Z: %.3f", Controller.P_Z));

        SeekBar pzBar = (SeekBar) dialogView.findViewById(R.id.propZ);
        pzBar.setMax(40);
        pzBar.setProgress((int) (Controller.P_Z * 400));
        pzBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Controller.setPZ((float)i / 400);
                txtPzBar.setText(String.format("P_Z: %.3f", (float)i / 400));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        final TextView txtTzBar = (TextView) dialogView.findViewById(R.id.txtTargetZ);
        txtTzBar.setText("Target Z: " + Controller.TARGET_Z);

        SeekBar tzBar = (SeekBar) dialogView.findViewById(R.id.targetZ);
        tzBar.setMax(80);
        tzBar.setProgress((int) (Controller.TARGET_Z / 10));
        tzBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                Controller.setTZ(i * 10);
                txtTzBar.setText("Target Z: " + Controller.TARGET_Z);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        // 2. Chain together various setter methods to set the dialog characteristics
        ftcActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
