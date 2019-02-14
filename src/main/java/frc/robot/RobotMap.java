package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class RobotMap {
    public static final TalonSRX driveLeftFront = new TalonSRX(0);
    public static final TalonSRX driveLeftRear = new TalonSRX(1);
    public static final TalonSRX driveRightFront = new TalonSRX(2);
    public static final TalonSRX driveRightRear = new TalonSRX(3);

    private static final PowerDistributionPanel pdp = new PowerDistributionPanel();

    public static final ADXRS453Gyro _gyroSPI = new ADXRS453Gyro(); //Counter-clockwise (Left) = negative, clockwise (Right) = positive

    private static int talonVersion = 0x04011;

    private static RobotMap instance;
    public static RobotMap getInstance() {
        return instance == null ? instance = new RobotMap() : instance;
    }

    private RobotMap() {
        driveLeftFront.set(ControlMode.PercentOutput, 0.0);
        driveRightFront.set(ControlMode.Follower, driveLeftFront.getDeviceID());
        driveRightFront.set(ControlMode.PercentOutput, 0.0);
        driveRightRear.set(ControlMode.Follower, driveRightFront.getDeviceID());
    }

    public static void checkTalonVersions() {
        if (driveLeftFront.getFirmwareVersion() == talonVersion) {
            if (driveLeftRear.getFirmwareVersion() == talonVersion) {
                if (driveRightFront.getFirmwareVersion() == talonVersion) {
                    if (driveRightRear.getFirmwareVersion() == talonVersion) {
                        System.out.println("All talons up to date.");
                    } else {
                        System.out.println(driveRightRear.getDeviceID() + " is not updated");
                    }
                } else {
                    System.out.println(driveRightFront.getDeviceID() + " is not updated");
                }
            } else {
                System.out.println(driveLeftRear.getDeviceID() + " is not updated");
            }
        } else {
            System.out.println(driveLeftFront.getDeviceID() + " is not updated.");
        }
    }
}