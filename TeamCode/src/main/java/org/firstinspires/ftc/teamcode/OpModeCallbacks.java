package org.firstinspires.ftc.teamcode;

/**
 * An interface for OpMode functionality outside the OpMode class
 * Created by James on 2016-10-15.
 */

public interface OpModeCallbacks {
    void idle() throws InterruptedException;
    boolean opModeIsActive();
    void addData(String caption, String format, Object... args);
    void updateTelemetry();
}
