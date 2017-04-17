package org.firstinspires.ftc.teamcode;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Main Autonomous Class
 * Created by James on 2016-10-09.
 */

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous")
public class Autonomous extends LinearOpMode {

    private Robot robot;

    private HashMap<String, PathBase> pathList = new HashMap<>();
    private List<String> pathNames;
    private PathBase selectedPath;
    private SharedPreferences prefs;

    private int delay; //delay is in milliseconds
    private Alliance alliance = Alliance.BLUE;
    private boolean configured = false;

    @Override
    public void runOpMode() throws InterruptedException {

        //Initialize robot/hardware
        robot = new Robot("proto1", hardwareMap, this);

        //Register paths
        pathList.put("Beacons", new Beacons(this, robot, new Coordinate(0, 0)));
        pathList.put("Close Corner", new CloseCorner(this, robot, new Coordinate(0, 0)));
        pathList.put("Far Corner", new FarCorner(this, robot, new Coordinate(0, 0)));
        //pathList.put("Test Button", new TestButton(this, robot, new Coordinate(0, 0)));
        //pathList.put("TestPivot", new TestPivot(this, robot, new Coordinate(0, 0)));
        //pathList.put("Sliding Beacon", new SlidingBeacon(this, robot, new Coordinate(0, 0)));

        pathNames = new ArrayList<String>(pathList.keySet());

        //Initialize config parameters
        prefs = PreferenceManager.getDefaultSharedPreferences(hardwareMap.appContext);
        delay = prefs.getInt("DELAY_KEY", 0);
        alliance = prefs.getString("ALLIANCE_KEY", "BLUE").equals("BLUE") ? Alliance.BLUE : Alliance.RED;
        selectedPath = pathList.get(prefs.getString("PATH_KEY", pathList.keySet().toArray()[0].toString()));

        if(pathList.isEmpty())
            throw new NullPointerException("There are no registered paths!");

        if (selectedPath == null) {
            selectedPath = pathList.get(pathList.keySet().iterator().next());
        }

        //Run configuration
        buildConfigDialog();

        while (!configured) {
            idle();
        }

        selectedPath.setAlliance(alliance);
        robot.resetSlider();

        waitForStart();
        sleep(delay);

        //RobotLocator.start();

        //Run selected path
        selectedPath.run();
    }

    private int getPathPosition(String key) {
        if (pathList.containsKey(key)) {
            for (int i = 0; i < pathNames.size(); i++) {
                if (pathNames.get(i).equals(key))
                    return i;
            }
        }

        return -1;
    }

    private void buildConfigDialog() {

        final FtcRobotControllerActivity ftcActivity = (FtcRobotControllerActivity) hardwareMap.appContext;
        final AlertDialog.Builder builder = new AlertDialog.Builder(ftcActivity);

        LayoutInflater inflater = ftcActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_configuration, null);

        builder.setTitle("Configuration")
                .setView(dialogView)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        configured = true;

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putInt("DELAY_KEY", delay);
                        editor.putString("ALLIANCE_KEY", alliance == Alliance.BLUE ? "BLUE" : "RED");
                        editor.putString("PATH_KEY", selectedPath.name);
                        editor.apply();

                        dialog.dismiss();
                    }
                });

        final TextView txtDelayBar = (TextView) dialogView.findViewById(R.id.txtDelayBar);
        txtDelayBar.setText("Delay: " + delay + "ms");


        SeekBar delayBar = (SeekBar) dialogView.findViewById(R.id.delayBar);
        delayBar.setMax(300);
        delayBar.setProgress(delay / 100);
        delayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                delay = i * 100;
                txtDelayBar.setText("Delay: " + delay + "ms");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        ToggleButton btnAlliance = (ToggleButton) dialogView.findViewById(R.id.btnAlliance);
        btnAlliance.setChecked(alliance == Alliance.BLUE);
        btnAlliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton btn = (ToggleButton) v;
                if (btn.isChecked()) {
                    alliance = Alliance.BLUE;
                } else {
                    alliance = Alliance.RED;
                }
            }
        });

        ArrayAdapter<String> pathAdapter = new ArrayAdapter<String>(dialogView.getContext(), R.layout.path_spinner, pathNames);

        Spinner list = (Spinner) dialogView.findViewById(R.id.pathList);
        list.setAdapter(pathAdapter);
        list.setSelection(getPathPosition(selectedPath.name));

        list.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPath = pathList.get(pathNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // 2. Chain together various setter methods to set the dialog characteristics
        ftcActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //DO NOT MOVE THIS LINE, EVEN THOUGH I KNOW YOU WANT TO
                AlertDialog configurationDialog = builder.create();
                configurationDialog.show();
            }
        });
    }
}
