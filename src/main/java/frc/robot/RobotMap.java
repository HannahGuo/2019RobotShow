package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;

public class RobotMap {
    public static final TalonSRX driveLeftTop = new TalonSRX(14);
    public static final TalonSRX driveLeftBot = new TalonSRX(13);
    
    public static final TalonSRX driveRightTop = new TalonSRX(0);
    public static final TalonSRX driveRightBot = new TalonSRX(15);
    
    public static final TalonSRX elevatorTop = new TalonSRX(2);
    public static final TalonSRX elevatorBot = new TalonSRX(3);

    public static final TalonSRX intakeTop = new TalonSRX(8);
    public static final TalonSRX intakeBot = new TalonSRX(11);
    
    public static final TalonSRX wristControl = new TalonSRX(1);

    public static final Solenoid leftDriveShifter = new Solenoid(0);
    public static final Solenoid rightDriveShifter = new Solenoid(1);
    public static final Solenoid frogLeftShifter = new Solenoid(2);
    public static final Solenoid frogRightShifter = new Solenoid(3);

    private static final PowerDistributionPanel pdp = new PowerDistributionPanel();

    public static final ADXRS453Gyro _gyroSPI = new ADXRS453Gyro(); //Counter-clockwise (Left) = negative, clockwise (Right) = positive

    private static int talonVersion = 0x04017;

    private static RobotMap instance;
    public static RobotMap getInstance() {
        return instance == null ? instance = new RobotMap() : instance;
    }

    private RobotMap() {
        driveLeftTop.set(ControlMode.PercentOutput, 0.0);
        driveLeftTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveLeftTop.setSensorPhase(false);
        driveLeftTop.setNeutralMode(NeutralMode.Brake);
        driveLeftTop.configVoltageCompSaturation(12.0, 10);
        driveLeftTop.enableVoltageCompensation(true);
        driveLeftTop.configPeakCurrentLimit(40, 10);
        driveLeftTop.configPeakCurrentDuration(0);
        driveLeftTop.configContinuousCurrentLimit(35, 10);
        driveLeftTop.enableCurrentLimit(true);

        driveLeftBot.follow(driveLeftTop);
        driveLeftBot.setNeutralMode(NeutralMode.Brake);
        driveLeftBot.configVoltageCompSaturation(12.0, 10);
        driveLeftBot.enableVoltageCompensation(true);
        driveLeftBot.configPeakCurrentLimit(40, 10);
        driveLeftBot.configPeakCurrentDuration(0);
        driveLeftBot.configContinuousCurrentLimit(35, 10);
        driveLeftBot.enableCurrentLimit(true);

        driveRightTop.set(ControlMode.PercentOutput, 0.0);
        driveRightTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveRightTop.setSensorPhase(false);
        driveRightTop.setNeutralMode(NeutralMode.Brake);
        driveRightTop.configVoltageCompSaturation(12.0, 10);
        driveRightTop.enableVoltageCompensation(true);

        driveRightBot.follow(driveRightTop);
        driveRightBot.setNeutralMode(NeutralMode.Brake);
        driveRightBot.configVoltageCompSaturation(12.0, 10);
        driveRightBot.enableVoltageCompensation(true);
        driveRightBot.configPeakCurrentLimit(40, 10);
        driveRightBot.configPeakCurrentDuration(0);
        driveRightBot.configContinuousCurrentLimit(35, 10);
        driveRightBot.enableCurrentLimit(true);

        elevatorTop.set(ControlMode.MotionMagic, 0.0);
        elevatorTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        elevatorTop.setNeutralMode(NeutralMode.Brake);
        
        elevatorBot.follow(elevatorTop);
        elevatorBot.setNeutralMode(NeutralMode.Brake);

        intakeTop.set(ControlMode.PercentOutput, 0.0);

        intakeBot.follow(intakeTop);

        wristControl.set(ControlMode.Position, 0.0);
        wristControl.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
    }

    public static void resetSensors() {
        _gyroSPI.resetRelative();
        driveLeftTop.setSelectedSensorPosition(0);
        driveRightTop.setSelectedSensorPosition(0);
        pdp.clearStickyFaults();
    }

    public static void getEncoderOutputs() {
        System.out.println("Encoder outputs: " + driveLeftTop.getSelectedSensorPosition() + " " + driveRightTop.getSelectedSensorPosition());
    }

    public static void getDriveCurrents(){
        System.out.println("Drive currents: " + driveLeftTop.getOutputCurrent() + " " + driveRightTop.getOutputCurrent());
    }

    public static void checkTalonVersions() {
        if (driveLeftTop.getFirmwareVersion() == talonVersion) {
            if (driveLeftBot.getFirmwareVersion() == talonVersion) {
                if (driveRightTop.getFirmwareVersion() == talonVersion) {
                    if (driveRightBot.getFirmwareVersion() == talonVersion) {
                        if(wristControl.getFirmwareVersion() == talonVersion){
                            if(elevatorTop.getFirmwareVersion() == talonVersion){
                                if(elevatorBot.getFirmwareVersion() == talonVersion){
                                    if(intakeTop.getFirmwareVersion() == talonVersion){
                                        if(intakeBot.getFirmwareVersion() == talonVersion) {
                                            System.out.println("All talons up to date.");
                                        } else {
                                            System.out.println(intakeBot.getDeviceID() + " is not updated");
                                        }
                                    } else {
                                        System.out.println(intakeTop.getDeviceID() + " is not updated");
                                    }
                                } else {
                                    System.out.print(elevatorBot.getDeviceID() + " is not updated");
                                }
                            } else {
                                System.out.println(elevatorTop.getDeviceID() + " is not updated");
                            }
                        } else {
                            System.out.println(wristControl.getDeviceID() + " is not updated");
                        }
                    } else {
                        System.out.println(driveRightBot.getDeviceID() + " is not updated");
                    }
                } else {
                    System.out.println(driveRightTop.getDeviceID() + " is not updated");
                }
            } else {
                System.out.println(driveLeftBot.getDeviceID() + " is not updated");
            }
        } else {
            System.out.println(driveLeftTop.getDeviceID() + " is not updated");
        }
    }
}