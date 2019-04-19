/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;

public class LimeLightDrive extends Command {
  private Drive drive;
  private double targetAngle;

  public LimeLightDrive() {
    this.drive = Drive.getInstance();
    requires(drive);
  }

  @Override
  protected void initialize() {
    drive = Drive.getInstance();
  }

  @Override
  protected void execute() {
    if(LimelightVision.isTargetVisible()) {
    }
    RobotMap.driveLeftTop.set(ControlMode.MotionMagic, 10000);    
    RobotMap.driveRightTop.set(ControlMode.MotionMagic, 10000);    
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
    drive.stopMoving();
  }

  @Override
  protected void interrupted() {
    System.out.println(this.getName() + " was interrupted.");
  }
}