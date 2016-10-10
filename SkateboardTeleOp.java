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
        robotVelocity = gamepad1.left_trigger - gamepad1.right_trigger;
        robotVelocity = robot.clamp(robotVelocity);
        heading = joystickToRadians(gamepad1.left_stick_x,gamepad1.left_stick_y);
        robot.setMotorValues(robotVelocity, heading, gamepad1.right_stick_x);
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
        if (stickX == 0 && 0 <= stickY)
        {
            radians = (3*Math.PI)/2;
        }
        else if (stickX == 0 && stickY <= 0)
        {
            radians = (3*Math.PI)/2;
        }
        else
        {
            radians = Math.atan(stickY/stickX);
            if (stickX < 0) {
                radians += Math.PI;
            }
        }
        radians += Math.PI/2;
        radians %= 2*Math.PI;
        return radians;
    }
    @Override
    public void stop()
    {

    }
    public void print(String key, String text)
    {
        Log.d(key, text);
        telemetry.addData(key, text);
    }
    public void print(String key, double text)
    {
        print(key, String.valueOf(text));
    }
}