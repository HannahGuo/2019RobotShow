package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.OI;
import frc.robot.ParadoxTimer;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;

public class Drive extends Subsystem {
  private static Drive instance;
  private ParadoxTimer visionToggle = new ParadoxTimer();
  private ParadoxTimer openLoopToggle = new ParadoxTimer();
  private static boolean openLoop = false;
  public static Drive getInstance() {
    return instance == null ? instance = new Drive() : instance;
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      private SynchronousPID hPID;

      {
        this.hPID = new SynchronousPID();
				this.hPID.setOutputRange(-1.0, 1.0);
        requires(getInstance());
      }

      protected void initialize() {
        this.hPID.reset();
        this.hPID.setPID(Constants.angPID);
        System.out.println("Starting " + this.getName());
      }

      protected void execute() {
        double straight = OI.getPrimaryLeftYAxis(), multiplier = OI.getPrimaryLB() ? 0.3 : 1.0, steering, left, right;
        
        if(OI.isPrimaryDPadPressed()) {
          openLoop = true;
        }

        if(OI.getPrimaryRB()) {
          LimelightVision.setVisionProcessingMode();
          if(!visionToggle.isEnabled()) {
            visionToggle.enableTimer(System.currentTimeMillis());
          }
        } else {
          LimelightVision.setVisionProcessingMode();
          visionToggle.disableTimer();
        }

        if(OI.getPrimaryRB() && LimelightVision.isTargetVisible() && LimelightVision.getHorizontalOffset() != 0.0 && visionToggle.hasTimeHasPassed(800, System.currentTimeMillis())){
          LimelightVision.updateVision();
          steering = LimelightVision.getHorizontalOffset() * Constants.limelightP;  
        } else if(openLoop) {
          steering = Math.pow(OI.getPrimaryRightXAxis(), 3);

          if(-0.1 < straight && 0.1 > straight) straight = 0.0;
          if(-0.1 < steering && 0.1 > steering) steering = 0.0;

          if(!OI.isPrimaryDPadPressed()) {
            if(!openLoopToggle.isEnabled()) openLoopToggle.enableTimer(System.currentTimeMillis());
          }

          if(OI.isPrimaryDPadPressed() && openLoopToggle.isEnabled() && openLoopToggle.hasTimeHasPassed(1000, System.currentTimeMillis())) {
            openLoop = false;
            openLoopToggle.disableTimer();
          }
        } else {
          this.hPID.setSetpoint(Math.pow(OI.getPrimaryRightXAxis(), 3) * 10);
          steering = this.hPID.calculate(RobotMap.gyroSPI.getRate());
          
          if(-0.1 < straight && 0.1 > straight) straight = 0.0;
          if(-0.1 < steering && 0.1 > steering) steering = 0.0;
        }
        
        left = multiplier * (-straight - steering);
        right = multiplier * (straight - steering);

        // RobotMap.printDriveEncoderPositions();
        // RobotMap.printDriveEncoderVelocitiess();
        
        SmartDashboard.putBoolean("IS OPEN LOOP?", openLoop);
        driveLR(left, right);

        if(OI.getPrimaryB()) setHighGear();
        else if(OI.getPrimaryA()) setLowGear();
      }

      protected void end(){
        System.out.println("Stopping " + this.getName());
        this.hPID.reset();
      }

      protected boolean isFinished() {
        return false;
      }

      protected void interrupted() {
        System.out.println("Sn4pplejacks, " + this.getName() + " stopped!");
        end();
      }
    });
  }

  public void driveLR(double left, double right) {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, left);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, right);
  }

  public static void resetDriveEncoders(){
    RobotMap.driveLeftTop.setSelectedSensorPosition(0);
    RobotMap.driveRightTop.setSelectedSensorPosition(0);
  }

  public static void setHighGear(){
    RobotMap.driveShifter.set(true);
  }

  public static void setLowGear(){
    RobotMap.driveShifter.set(false);
  }

  public static double getAverageDrivePosition(){
    return (RobotMap.driveLeftTop.getSelectedSensorPosition(0) + RobotMap.driveRightTop.getSelectedSensorPosition(0)) / 2;
  }

  public static double getAverageDriveVelocity(){
    return (RobotMap.driveLeftTop.getSelectedSensorVelocity(0) + RobotMap.driveRightTop.getSelectedSensorVelocity(0)) / 2;
  }

  public static void printDriveEncoderPositions() {
    System.out.println("Drive Encoder positions: " + RobotMap.driveLeftTop.getSelectedSensorPosition() + " " + RobotMap.driveRightTop.getSelectedSensorPosition());
  }

  public static void printDriveEncoderVelocitiess() {
    System.out.println("Drive Encoder velocities: " + RobotMap.driveLeftTop.getSelectedSensorVelocity() + " " + RobotMap.driveRightTop.getSelectedSensorVelocity());
  }

  public static void printDriveCurrents(){
    System.out.println("Drive currents: " + RobotMap.driveLeftTop.getOutputCurrent() + " " + RobotMap.driveRightTop.getOutputCurrent());
  }
  
  public static void stopMoving() {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.0);
    openLoop = false;
  }
}