    
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;

public class LimeLightAlignDrive extends Command {
  private Drive drive;

  public LimeLightAlignDrive() {
    this.drive = Drive.getInstance();
    requires(drive);
  }

  @Override
  protected void initialize() {
    RobotMap.gyroSPI.reset();
  }

  @Override
  protected void execute() {
    LimelightVision.updateVision();
    LimelightVision.setCamMode(0);
    if(LimelightVision.isTargetVisible()) {
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.2);
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, -0.2);
    }
  }

  @Override
  protected boolean isFinished() {
    return LimelightVision.getTargetArea() > 3.65;
  }

  @Override
  protected void end() {
    System.out.println("LIMELIGHT VISION FINISHED");
    Drive.stopMoving();
  }

  @Override
  protected void interrupted() {
  }
}