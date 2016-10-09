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
    double backLeftPower;
    double backRightPower;
    double frontLeftPower;
    double frontRightPower;
    Servo servo;
    Servo left;
    Servo right;
    Servo pusher;
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
        pusher = hardwareMap.servo.get("pusher");
        backLeftMotor = hardwareMap.dcMotor.get("backl");
        backRightMotor = hardwareMap.dcMotor.get("backr");
        frontLeftMotor = hardwareMap.dcMotor.get("frontl");
        frontRightMotor = hardwareMap.dcMotor.get("frontr");
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setDirection(DcMotor.Direction.REVERSE);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        left.setPosition(0);
        right.setPosition(1);
        pusher.setPosition(0);
        I2cDevice range;
        range = hardwareMap.i2cDevice.get("range");
        rangeReader = new I2cDeviceReader(range, new I2cAddr(0x28), 0x04, 2);
        byte rangeReadings[];
    }
    @Override
    public void loop()
    {
        getGamepadValues();
        convertGamepadToMovement();
        runMotors();
    }

    public void getGamepadValues()
    {
        rightTrigger = gamepad1.right_trigger;
        speedMultiplier = rightTrigger / 2;
        print(speedMultiplier);
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
        frontLeftMotor.setPower(    frontLeftPower  );
        frontRightMotor.setPower(   frontRightPower );
        backLeftMotor.setPower(     backLeftPower   );
        backRightMotor.setPower(    backRightPower  );
    }

    @Override
    public void stop()
    {

    }
    public void print(String text)
    {
        Log.d("TeleOp", text);
        telemetry.addData("TeleOp", text);
    }
    public void print(double text)
    {
        print(String.valueOf(text));
    }
}
