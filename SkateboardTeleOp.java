package org.firstinspires.ftc.teamcode;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceReader;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "SkateboardTeleOp", group = "TeleOp")
public class SkateboardTeleOp extends OpMode
{
    float DEAD_ZONE = 0.15f;
    DcMotor backLeftMotor;
    DcMotor backRightMotor;
    DcMotor frontLeftMotor;
    DcMotor frontRightMotor;
    double backLeftPower   = 0;
    double backRightPower  = 0;
    double frontLeftPower  = 0;
    double frontRightPower = 0;
    Servo servo;
    Servo left;
    Servo right;
    Servo pusherr;
    I2cDevice range;
    I2cDeviceReader rangeReader;
    byte rangeReadings[];
    boolean tog2 = false;
    boolean xPressed = false;
    boolean xPrevious = false;
    boolean bPressed = false;
    boolean bPrevious = false;
    boolean aPressed = false;
    boolean aPrevious = false;

    double speedMultiplier;
    float rightTrigger;


    @Override
    public void init()
    {
        servo = hardwareMap.servo.get("servo");
        left = hardwareMap.servo.get("left");
        right = hardwareMap.servo.get("right");
        pusherr = hardwareMap.servo.get("pusherr");
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
    @Override
    public void loop()
    {
//        getGamepadValues();
//        convertGamepadToMovement();
        getMotorValues();
        runMotors();
    }

    public void getGamepadValues()
    {
        rightTrigger = gamepad1.right_trigger;
        speedMultiplier = rightTrigger / 2;
        print("SpeedMult", speedMultiplier);
    }

    public void getMotorValues()
    {
        double robotVelocity = 0;
        double heading = 0;
        robotVelocity = gamepad1.left_trigger - gamepad1.right_trigger;
        if (1 < robotVelocity)
        {
            robotVelocity = 1;
        }
        else if (robotVelocity < -1)
        {
            robotVelocity = -1;
        }
        double leftStickY = gamepad1.left_stick_y;
        double leftStickX = gamepad1.left_stick_x;
        if (leftStickX == 0 && 0 <= leftStickY)
        {
            heading = (3*Math.PI)/2;
        }
        else if (leftStickX == 0 && leftStickY <= 0)
        {
            heading = (3*Math.PI)/2;
        }
        else
        {
            heading = Math.atan(leftStickY/leftStickX);
            if (leftStickX < 0) {
                heading += Math.PI;
            }
        }
        heading += Math.PI/2;
        heading %= 2*Math.PI;
        frontLeftPower  = getFrontLeftMecanumVelocity(  robotVelocity, -heading, -gamepad1.right_stick_x);
        frontRightPower = getFrontRightMecanumVelocity( robotVelocity, -heading, -gamepad1.right_stick_x);
        backLeftPower   = getBackLeftMecanumVelocity(   robotVelocity, -heading, -gamepad1.right_stick_x);
        backRightPower  = getBackRightMecanumVelocity(  robotVelocity, -heading, -gamepad1.right_stick_x);
    }
    public void convertGamepadToMovement()
    {
        // numOfMovements is a counter that counts how many directions that the rover is being pushed in.
        // We use it to enable us to compound rover controls (move forward while turning, etc)
        double numOfMovements = 0;
        // Initialize drive power, this will be modified
        frontLeftPower  = 0.0;
        frontRightPower = 0.0;
        backLeftPower   = 0.0;
        backRightPower  = 0.0;
        // If the left stick is pushed forward or back more than the DEAD_ZONE
        if (DEAD_ZONE < Math.abs(gamepad1.left_stick_y))
        {
            // Drive Forward or Backward
            frontLeftPower  += gamepad1.left_stick_y;
            frontRightPower += gamepad1.left_stick_y;
            backLeftPower   += gamepad1.left_stick_y;
            backRightPower  += gamepad1.left_stick_y;
            numOfMovements++;
        }
        // If the left stick is pushed left or right more than the DEAD_ZONE
        if (DEAD_ZONE < Math.abs(gamepad1.left_stick_x))
        {
            // Strafe Left or Right
            frontLeftPower  += gamepad1.left_stick_x;
            frontRightPower += -gamepad1.left_stick_x;
            backLeftPower   += -gamepad1.left_stick_x;
            backRightPower  += gamepad1.left_stick_x;
            numOfMovements++;
        }
        // If the left stick is in the center
        if (Math.abs(gamepad1.left_stick_x) < DEAD_ZONE && Math.abs(gamepad1.left_stick_y) < DEAD_ZONE)
        {
            // Stop
            frontLeftPower  = 0.0;
            frontRightPower = 0.0;
            backLeftPower   = 0.0;
            backRightPower  = 0.0;
        }


        if (numOfMovements != 0) {
            frontLeftPower  /= numOfMovements;
            frontRightPower /= numOfMovements;
            backLeftPower   /= numOfMovements;
            backRightPower  /= numOfMovements;
        }

        frontLeftPower  *= speedMultiplier;
        frontRightPower *= speedMultiplier;
        backLeftPower   *= speedMultiplier;
        backRightPower  *= speedMultiplier;
    }


    /**
     * Gets the speed for the front left wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    public double getFrontLeftMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.sin(robotHeading + (Math.PI / 4)) + robotAngularVelocity;
    }
    /**
     * Gets the speed for the front right wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    public double getFrontRightMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.cos(robotHeading + (Math.PI / 4)) - robotAngularVelocity;
    }
    /**
     * Gets the speed for the back left wheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    public double getBackLeftMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.cos(robotHeading + (Math.PI / 4)) + robotAngularVelocity;
    }
    /**
     * Gets the speed for the back rightwheel.
     * Based on this paper <a href="http://thinktank.wpi.edu/resources/346/ControllingMecanumDrive.pdf">Controlling Mecanum Drive</a>
     */
    public double getBackRightMecanumVelocity(double robotVelocity, double robotHeading, double robotAngularVelocity)
    {
        return robotVelocity * Math.sin(robotHeading + (Math.PI / 4)) - robotAngularVelocity;
    }

    public void runMotors()
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
    public double clamp(double value)
    {
        if (value < -1)
        {
            print("Clamp", value);
            value = -1;
        }
        else if (1 < value)
        {
            print("Clamp", value);
            value = 1;
        }
        return value;
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
