package org.firstinspires.ftc.teamcode;
import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * <h1>SteelSerpentsRobot</h1>
 * The robot class used by the SteelSerpents FTC team. This class contains all of the robot hardware,
 * drive instructions, and other details specifically relating to controlling the robot.
 */
public class SteelSerpentsRobot
{

    public float DEAD_ZONE = 0.15f;
    private HardwareMap hardwareMap;
    private DcMotor backLeftMotor;
    private DcMotor backRightMotor;
    private DcMotor frontLeftMotor;
    private DcMotor frontRightMotor;
    private double backLeftPower   = 0;
    private double backRightPower  = 0;
    private double frontLeftPower  = 0;
    private double frontRightPower = 0;
    private Servo servo;
    private Servo left;
    private Servo right;
    private Servo pusherr;
    private Servo pusherl;
    private I2cDevice range;
    private I2cDeviceReader rangeReader;
    private byte rangeReadings[];

    /**
     * Sets up the SteelSerpents robot by initializing its hardware.
     * @param newHardwareMap The robot HardwareMap that is provided by our robot's OpMode.
     */
    SteelSerpentsRobot(HardwareMap newHardwareMap)
    {
        this.hardwareMap = newHardwareMap;
        servo = hardwareMap.servo.get("servo");
        left = hardwareMap.servo.get("left");
        right = hardwareMap.servo.get("right");
        pusherr = hardwareMap.servo.get("pusherr");
        pusherl = hardwareMap.servo.get("pusherl");
        backLeftMotor = hardwareMap.dcMotor.get("backl");
        backRightMotor = hardwareMap.dcMotor.get("backr");
        frontLeftMotor = hardwareMap.dcMotor.get("frontl");
        frontRightMotor = hardwareMap.dcMotor.get("frontr");
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        left.setPosition(1);
        right.setPosition(0);
        pusherr.setPosition(0);
        I2cDevice range;
        range = hardwareMap.i2cDevice.get("range");
        rangeReader = new I2cDeviceReader(range, new I2cAddr(0x28), 0x04, 2);
        byte rangeReadings[];
    }
    /**
     * Gets the speed for the front left wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    private double getFrontLeftMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.sin(robotHeading + (Math.PI / 4)) + robotAngularVelocity;
    }
    /**
     * Gets the speed for the front right wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    private double getFrontRightMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.cos(robotHeading + (Math.PI / 4)) - robotAngularVelocity;
    }
    /**
     * Gets the speed for the back left wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    private double getBackLeftMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.cos(robotHeading + (Math.PI / 4)) + robotAngularVelocity;
    }
    /**
     * Gets the speed for the back rightwheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    private double getBackRightMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.sin(robotHeading + (Math.PI / 4)) - robotAngularVelocity;
    }

    /**
     * Sets the motor speeds to the appropriate values for a mecanum-wheeled robot. Must use the drive() command to send the drive instruction to the robot.
     * @param robotVelocity The speed at which you want the robot to translate over the gamefield. Range: [-1,1]
     * @param directionOfMovement The heading in radians in which you want the robot to translate. Range: [0,2PI)
     * @param rateOfRotation The rate of rotation and direction of rotation you wish to move in. Range: [-1,1]
     */
    public void setMotorValues(double robotVelocity, double directionOfMovement, double rateOfRotation)
    {
        frontLeftPower  = getFrontLeftMecanumVelocity(  robotVelocity, -directionOfMovement, -rateOfRotation);
        frontRightPower = getFrontRightMecanumVelocity( robotVelocity, -directionOfMovement, -rateOfRotation);
        backLeftPower   = getBackLeftMecanumVelocity(   robotVelocity, -directionOfMovement, -rateOfRotation);
        backRightPower  = getBackRightMecanumVelocity(  robotVelocity, -directionOfMovement, -rateOfRotation);
    }

    /**
     * Sends the actual drive command to the robot.
     */
    public void drive()
    {
        frontLeftPower  = clamp(frontLeftPower  );
        frontRightPower = clamp(frontRightPower );
        backLeftPower   = clamp(backLeftPower   );
        backRightPower  = clamp(backRightPower  );
        frontLeftMotor.setPower(    frontLeftPower  );
        frontRightMotor.setPower(   frontRightPower );
        backLeftMotor.setPower(     backLeftPower   );
        backRightMotor.setPower(    backRightPower  );
    }
    public double clamp(double value, int min, int max)
    {
        if (value < min)
        {
            print("Clamp", value);
            value = min;
        }
        else if (max < value)
        {
            print("Clamp", value);
            value = max;
        }
        return value;
    }
    public double clamp (double value)
    {
        return clamp(value, -1, 1);
    }
    public void print(String key, String text)
    {
        Log.d(key, text);
    }
    public void print(String key, double text)
    {
        print(key, String.valueOf(text));
    }
}