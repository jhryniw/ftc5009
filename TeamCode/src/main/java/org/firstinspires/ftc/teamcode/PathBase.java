package org.firstinspires.ftc.teamcode;

/**
 * Base Class for Path Implementation
 * Created by James on 2016-10-09.
 */

enum Alliance {
    RED,
    BLUE,
    NA
}

public abstract class PathBase {

    protected Alliance alliance;
    protected Coordinate startLocation;
    protected Robot robot;

    public PathBase(Robot r, Alliance a, Coordinate startLoc) {
        alliance = a;
        startLocation = startLoc;
        robot = r;
    }

    abstract void run() throws InterruptedException;
}
