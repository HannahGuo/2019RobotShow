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

public class Drive extends Subsystem {
  private static Drive instance;
  private LimelightVision limelightVision = LimelightVision.getInstance();
  private ParadoxTimer visionToggle = new ParadoxTimer();
  public static Drive getInstance() {
    return instance == null ? instance = new Drive() : instance;
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting " + this.getName());
      }

      protected void execute() {
        double straight = OI.getPrimaryLeftYAxis(), steering = Math.pow(OI.getPrimaryRightXAxis(), 3), 
               multiplier = OI.getPrimaryLB() ? 0.3 : 1.0, left, right;
        
        if(OI.getPrimaryRB()) {
          limelightVision.setCamMode(0);
          visionToggle.enableTimer(System.currentTimeMillis());
        } else {
          limelightVision.setCamMode(1);
        }

        if(OI.getPrimaryRB() && limelightVision.isTargetVisible() && limelightVision.getHorizontalOffset() != 0 && visionToggle.hasTimeHasPassed(400, System.currentTimeMillis())){
          limelightVision.updateVision();
          steering = limelightVision.getHorizontalOffset() * Constants.angP;  
          left = multiplier * (-straight + steering);
          right = multiplier * (straight + steering);
          SmartDashboard.putNumber("STEERING", steering);
        } else {
          if(-0.1 < straight && 0.1 > straight) straight = 0.0;
          if(-0.1 < steering && 0.1 > steering) steering = 0.0;
          left = multiplier * (-straight - steering);
          right = multiplier * (straight - steering);
          visionToggle.disableTimer();
        }
        left = multiplier * (-straight - steering);
        right = multiplier * (straight - steering);

        driveLR(left, right);

        if(OI.getPrimaryA()) RobotMap.driveShifter.set(false); // low gear
        else if(OI.getPrimaryY()) RobotMap.driveShifter.set(true); // high gear
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