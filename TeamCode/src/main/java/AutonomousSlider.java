import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.TouchSensor;
import static org.firstinspires.ftc.teamcode.Hardware.slider;
import org.firstinspires.ftc.teamcode.Coordinate;
import org.firstinspires.ftc.teamcode.PathBase;
import org.firstinspires.ftc.teamcode.Robot;


/**
 * Created by team5009 on 2017-03-27.
 */

    final class AutonomousSlider extends PathBase{

        AutonomousSlider(LinearOpMode opMode, Robot r, Coordinate startLoc) {
            super(opMode, r, startLoc, "AutonomousSlider");
        }
    void run() throws InterruptedException {

        if (TouchSensor.isPressed()){
            slider
        }


    }
}