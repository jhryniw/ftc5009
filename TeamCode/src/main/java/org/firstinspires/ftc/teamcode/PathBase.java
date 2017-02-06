package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Base Class for Path Implementation
 * Created by James on 2016-10-09.
 */

//TODO: get LinearOpMode functionality by extending LinearOpMode here
public abstract class PathBase {

    public String name;
    protected Alliance alliance;
    protected Coordinate startLocation;
    protected Robot robot;

    public PathBase(Robot r, Coordinate startLoc, String name) {
        alliance = Alliance.NA;
        startLocation = startLoc;
        robot = r;
        this.name = name;
    }

    public void setAlliance(Alliance a) {
        alliance = a;
    }

    abstract void run() throws InterruptedException;
}
