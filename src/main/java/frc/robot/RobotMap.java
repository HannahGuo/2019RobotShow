package frc.robot;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
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

    public static final Solenoid driveShifter = new Solenoid(0);
    // public static final Solenoid traumatizedGhosts = new Solenoid(1); // Frogs to the non-believers

    public static final DigitalInput buttonTest = new DigitalInput(0);
    public static final PowerDistributionPanel pdp = new PowerDistributionPanel();
    
    public static final ADXRS453Gyro gyroSPI = new ADXRS453Gyro(); //Counter-clockwise (Left) = negative, clockwise (Right) = positive

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
        driveLeftTop.configPeakCurrentLimit(0, 10);
        driveLeftTop.configPeakCurrentDuration(0);
        driveLeftTop.configContinuousCurrentLimit(35, 10);
        driveLeftTop.enableCurrentLimit(true);
        driveLeftTop.setInverted(true);

        driveLeftBot.follow(driveLeftTop);
        driveLeftBot.setNeutralMode(NeutralMode.Brake);
        driveLeftBot.configVoltageCompSaturation(12.0, 10);
        driveLeftBot.enableVoltageCompensation(true);
        driveLeftBot.configPeakCurrentLimit(0, 10);
        driveLeftBot.configPeakCurrentDuration(0);
        driveLeftBot.configContinuousCurrentLimit(35, 10);
        driveLeftBot.enableCurrentLimit(true);
        driveLeftBot.setInverted(true);

        driveRightTop.set(ControlMode.PercentOutput, 0.0);
        driveRightTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveRightTop.setSensorPhase(false);
        driveRightTop.setNeutralMode(NeutralMode.Brake);
        driveRightTop.configVoltageCompSaturation(12.0, 10);
        driveRightTop.enableVoltageCompensation(true);
        driveRightTop.configPeakCurrentLimit(0, 10);
        driveRightTop.configPeakCurrentDuration(0);
        driveRightTop.configContinuousCurrentLimit(35, 10);
        driveRightTop.setInverted(true);

        driveRightBot.follow(driveRightTop);
        driveRightBot.setNeutralMode(NeutralMode.Brake);
        driveRightBot.configVoltageCompSaturation(12.0, 10);
        driveRightBot.enableVoltageCompensation(true);
        driveRightBot.configPeakCurrentLimit(0, 10);
        driveRightBot.configPeakCurrentDuration(0);
        driveRightBot.configContinuousCurrentLimit(35, 10);
        driveRightBot.enableCurrentLimit(true);
        driveRightBot.setInverted(true);
        
        elevatorBot.set(ControlMode.MotionMagic, 0.0);
        elevatorBot.setNeutralMode(NeutralMode.Brake);
        elevatorBot.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        elevatorBot.setInverted(false);
        elevatorBot.setSensorPhase(true);

        elevatorTop.follow(elevatorBot);
        elevatorTop.setNeutralMode(NeutralMode.Brake);

        intakeTop.set(ControlMode.PercentOutput, 0.0);

        intakeBot.follow(intakeTop);

        wristControl.set(ControlMode.Position, 0.0);
        wristControl.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        wristControl.setNeutralMode(NeutralMode.Brake);
    }

    public static void resetSensors() {
        // gyroSPI.resetRelative();
        driveLeftTop.setSelectedSensorPosition(0);
        driveRightTop.setSelectedSensorPosition(0);
        wristControl.setSelectedSensorPosition(0);
        elevatorBot.setSelectedSensorPosition(0);
        pdp.clearStickyFaults();
    }

    public static void getElevatorOutputs() {
        System.out.println("Elevator Outputs:" + elevatorBot.getSelectedSensorPosition());
    }

    public static void getEncoderOutputs() {
        System.out.println("Encoder outputs: " + driveLeftTop.getSelectedSensorPosition() + " " + driveRightTop.getSelectedSensorPosition());
    }

    public static void getDriveCurrents(){
        System.out.println("Drive currents: " + driveLeftTop.getOutputCurrent() + " " + driveRightTop.getOutputCurrent());
    }

    public static boolean getEncoderStatus(TalonSRX talonToCheck){
        ErrorCode checkSensor = talonToCheck.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        
        if(checkSensor != ErrorCode.OK) {
            System.out.println(talonToCheck.getDeviceID() + " has an unplugged encoder!");
            return false;
        } 
        
        return true;
    }
}