package org.firstinspires.ftc.teamcode;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

@TeleOp(name = "SkateboardTeleOp", group = "TeleOp")
public class SkateboardTeleOp extends OpMode
{
    SteelSerpentsRobot robot;
    @Override
    public void init()
    {
        robot = new SteelSerpentsRobot(hardwareMap);
    }
    @Override
    public void loop()
    {
        gamePad1Control();
    }

    /**
     * Processes the inputs from gamepad1 and converts them into controls to drive the robot.
     */
    public void gamePad1Control()
    {
        double robotVelocity;
        double heading;
        // The robot should go forward if the right trigger is pulled, and backward if the left trigger is pulled
        robotVelocity = gamepad1.left_trigger - gamepad1.right_trigger;
        // Ensure that the speed is in the correct range [-1,1]
        robotVelocity = robot.clamp(robotVelocity);
        // Set the direction of travel to be the same as the left joystick
        heading = joystickToRadians(gamepad1.left_stick_x,gamepad1.left_stick_y);
        // Set the appropriate values for each motor
        robot.setMotorValues(robotVelocity, heading, gamepad1.right_stick_x);
        // Drive the robot
        robot.drive();
    }

    /**
     * This function converts a joystick's direction into a heading in radians. TODO: Test this, determine what direction is zero, and which direction is positive.
     * @param stickX The joystick's x value.
     * @param stickY The joystick's y value.
     * @return The joystick's heading in radians.
     */
    public double joystickToRadians(float stickX, float stickY)
    {
        double radians = 0;
        // If stick is centered
        if (stickX == 0 && 0 <= stickY)
        {
            // Set direction to be straight forward
            radians = (3*Math.PI)/2;
        }
        // If stick is pushing straight forward
        else if (stickX == 0 && stickY <= 0)
        {
            // Set the direction to be straight forward
            radians = (3*Math.PI)/2;
        }
        else
        {
            radians = Math.atan(stickY/stickX);
            if (stickX < 0) {
                radians += Math.PI;
            }
        }
        // Rotate the axis by 90 degrees
        radians += Math.PI/2;
        // Ensure that the result is in the range [0,2PI]
        radians %= 2*Math.PI;
        return radians;
    }
    @Override
    public void stop()
    {

    }

    /**
     * Print to the Logcat debug console as well as to the FTC Telemetry.
     * @param key A text key that we can use to filter our debug output.
     * @param text The information to be monitored when debugging.
     */
    public void print(String key, String text)
    {
        Log.d(key, text);
        telemetry.addData(key, text);
    }

    /**
     * Print to the Logcat debug console as well as to the FTC Telemetry.
     * @param key A text key that we can use to filter our debug output.
     * @param text The information to be monitored when debugging.
     */
    public void print(String key, double text)
    {
        print(key, String.valueOf(text));
    }
}