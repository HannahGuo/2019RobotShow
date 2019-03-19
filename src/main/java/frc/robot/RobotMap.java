package frc.robot;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
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
    
    public static final TalonSRX elevatorTop = new TalonSRX(3);
    public static final TalonSRX elevatorBot = new TalonSRX(2);

    public static final TalonSRX intakeTop = new TalonSRX(8);
    public static final TalonSRX intakeBot = new TalonSRX(11);
    
    public static final TalonSRX wristControl = new TalonSRX(1);

    public static final Solenoid driveShifter = new Solenoid(0);
    public static final Solenoid traumatizedGhosts = new Solenoid(1); // Frogs to the non-believers

    public static final DigitalInput zeroThyElevator = new DigitalInput(0); // False = Pressed
    public static final DigitalInput zeroThyWrist = new DigitalInput(1); // False = Pressed
    public static final DigitalInput hatchDetector = new DigitalInput(5); // False = Thing detected
    public static final DigitalInput forbiddenOrange = new DigitalInput(9); // False = Thing detected
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
        driveLeftTop.configContinuousCurrentLimit(26, 10);
        driveLeftTop.configPeakCurrentLimit(26, 10);
        driveLeftTop.configPeakCurrentDuration(0, 10);
        driveLeftTop.enableCurrentLimit(true);
        driveLeftTop.setInverted(true);

        driveLeftBot.follow(driveLeftTop);
        driveLeftBot.setNeutralMode(NeutralMode.Brake);
        driveLeftBot.configVoltageCompSaturation(12.0, 10);
        driveLeftBot.enableVoltageCompensation(true);
        driveLeftBot.configContinuousCurrentLimit(26, 10);
        driveLeftBot.configPeakCurrentLimit(26, 10);
        driveLeftBot.configPeakCurrentDuration(0, 10);
        driveLeftBot.enableCurrentLimit(true);
        driveLeftBot.setInverted(true);

        driveRightTop.set(ControlMode.PercentOutput, 0.0);
        driveRightTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveRightTop.setSensorPhase(false);
        driveRightTop.setNeutralMode(NeutralMode.Brake);
        driveRightTop.configVoltageCompSaturation(12.0, 10);
        driveRightTop.enableVoltageCompensation(true);
        driveRightTop.configContinuousCurrentLimit(26, 10);
        driveRightTop.configPeakCurrentLimit(26, 10);
        driveRightTop.configPeakCurrentDuration(0, 10);
        driveRightTop.enableCurrentLimit(true);
        driveRightTop.setInverted(true);

        driveRightBot.follow(driveRightTop);
        driveRightBot.setNeutralMode(NeutralMode.Brake);
        driveRightBot.configVoltageCompSaturation(12.0, 10);
        driveRightBot.enableVoltageCompensation(true);
        driveRightBot.configContinuousCurrentLimit(26, 10);
        driveRightBot.configPeakCurrentLimit(26, 10);
        driveRightBot.configPeakCurrentDuration(0, 10);
        driveRightBot.enableCurrentLimit(true);
        driveRightBot.setInverted(true);
        
        elevatorTop.set(ControlMode.MotionMagic, 0.0);
        elevatorTop.setNeutralMode(NeutralMode.Brake);
        elevatorTop.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        elevatorTop.setSensorPhase(false);
        elevatorTop.setInverted(false);

        // Elevator going up
        elevatorTop.config_kP(0, 0.65, 10);
        elevatorTop.config_kI(0, 0.00025, 10);
        elevatorTop.config_kD(0, 4.0, 10);
        elevatorTop.config_kF(0, 0.25, 10);
        elevatorTop.selectProfileSlot(0, 0);
        elevatorTop.config_IntegralZone(0, 250, 10);

        // Elevator going down
        // elevatorTop.config_kP(1, 0.65, 10);
        // elevatorTop.config_kI(1, 0.00025, 10);
        // elevatorTop.config_kD(1, 0.0, 10);
        // elevatorTop.config_kF(1, 0.3, 10);
        // elevatorTop.config_IntegralZone(1, 250, 10);

        elevatorBot.follow(elevatorTop);
        elevatorBot.setNeutralMode(NeutralMode.Brake);
        elevatorBot.setInverted(InvertType.FollowMaster);

        intakeTop.set(ControlMode.PercentOutput, 0.0);

        intakeBot.set(ControlMode.PercentOutput, 0.0);

        wristControl.set(ControlMode.MotionMagic, 0.0);
        wristControl.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        wristControl.setNeutralMode(NeutralMode.Brake);
        wristControl.setSensorPhase(true);
        wristControl.setInverted(false);
        
        // Wrist going down
        wristControl.config_kP(0, 1.6, 10);
        wristControl.config_kI(0, 0.00016, 10);
        wristControl.config_kD(0, 0.0, 10);
        wristControl.config_kF(0, 0.2, 10);
        wristControl.selectProfileSlot(0, 0);

        // Wrist going up
        wristControl.config_kP(1, 1.6, 10);
        wristControl.config_kI(1, 0.00016, 10);
        wristControl.config_kD(1, 0.0, 10);
        wristControl.config_kF(1, 0.2, 10);
    }

    public static void resetSensors() {
        gyroSPI.reset();
        driveLeftTop.setSelectedSensorPosition(0);
        driveRightTop.setSelectedSensorPosition(0);
        wristControl.setSelectedSensorPosition(0);
        elevatorTop.setSelectedSensorPosition(0);
        pdp.clearStickyFaults();
    }

    public static void getElevatorOutputs() {
        System.out.println("Elevator Outputs:" + elevatorTop.getSelectedSensorPosition());
    }

    public static void getEncoderOutputs() {
        System.out.println("Encoder outputs: " + driveLeftTop.getSelectedSensorPosition() + " " + driveRightTop.getSelectedSensorPosition());
    }

    public static void getDriveCurrents(){
        System.out.println("Drive currents: " + driveLeftTop.getOutputCurrent() + " " + driveRightTop.getOutputCurrent());
    }

    public static boolean isEncoderConnected(TalonSRX talonToCheck){
        ErrorCode checkSensor = talonToCheck.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

        if(checkSensor != ErrorCode.OK) { 
            System.out.println(talonToCheck.getDeviceID() + " has an unplugged encoder!");
            return false;
        } 
        
        return true;
    }
}