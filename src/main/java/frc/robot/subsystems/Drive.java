/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

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
  private LimelightVision limelightVision = LimelightVision.getInstance();
  private ParadoxTimer visionToggle = new ParadoxTimer();
  private ParadoxTimer openLoopToggle = new ParadoxTimer();
  private boolean openLoop = false;
  private boolean fnatic = false; // open loop toggle thing
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
				this.hPID.setPID(0.01, 0.0002, 0);
        System.out.println("Starting " + this.getName());
      }

      protected void execute() {
        double straight = OI.getPrimaryLeftYAxis(), multiplier = OI.getPrimaryLB() ? 0.3 : 1.0, steering, left, right;
        
        if(OI.isPrimaryDPadPressed()) {
          openLoop = true;
        }

        if(OI.getPrimaryRB()) {
          limelightVision.setCamMode(0);
          if(!visionToggle.isEnabled()) {
            visionToggle.enableTimer(System.currentTimeMillis());
          }
        } else {
          limelightVision.setCamMode(1);
          visionToggle.disableTimer();
        }

        if(OI.getPrimaryRB() && limelightVision.isTargetVisible() && limelightVision.getHorizontalOffset() != 0.0 && visionToggle.hasTimeHasPassed(800, System.currentTimeMillis())){
          limelightVision.updateVision();
          steering = limelightVision.getHorizontalOffset() * Constants.limelightP;  
          left = multiplier * (-straight - steering);
          right = multiplier * (straight - steering);
        } else if(openLoop) {
          steering = Math.pow(OI.getPrimaryRightXAxis(), 3);

          if(-0.1 < straight && 0.1 > straight) straight = 0.0;
          if(-0.1 < steering && 0.1 > steering) steering = 0.0;

          left = multiplier * (-straight - steering);
          right = multiplier * (straight - steering);

          if(!OI.isPrimaryDPadPressed()) {
            if(!openLoopToggle.isEnabled()) openLoopToggle.enableTimer(System.currentTimeMillis());
          }

          if(OI.isPrimaryDPadPressed() && openLoopToggle.isEnabled() && openLoopToggle.hasTimeHasPassed(800, System.currentTimeMillis())) {
            openLoop = false;
            openLoopToggle.disableTimer();
          }
        } else {
          this.hPID.setSetpoint(Math.pow(OI.getPrimaryRightXAxis(), 3) * 400);
          steering = this.hPID.calculate(RobotMap.gyroSPI.getRate());
          
          if(-0.1 < straight && 0.1 > straight) straight = 0.0;
          if(-0.1 < steering && 0.1 > steering) steering = 0.0;

          left = multiplier * (-straight - steering);
          right = multiplier * (straight - steering);
        }
        
        SmartDashboard.putNumber("STEERING", steering);
        SmartDashboard.putNumber("GYRO RATE", RobotMap.gyroSPI.getRate());
        SmartDashboard.putBoolean("IS OPEN LOOP?", openLoop);
        driveLR(left, right);

        if(OI.getPrimaryB()) RobotMap.driveShifter.set(true); // low gear
        else if(OI.getPrimaryA()) RobotMap.driveShifter.set(false); // high gear
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

  public void resetDriveEncoders(){
    RobotMap.driveLeftTop.setSelectedSensorPosition(0);
    RobotMap.driveRightTop.setSelectedSensorPosition(0);
  }
  
  public static void stopMoving() {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.0);
  }
}