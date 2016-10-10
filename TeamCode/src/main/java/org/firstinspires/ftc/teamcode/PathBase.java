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

    private Alliance alliance;
    private Coordinate startLocation;

    public PathBase(Alliance a, Coordinate startLoc) {
        alliance = a;
        startLocation = startLoc;
    }

    abstract void run();
}
