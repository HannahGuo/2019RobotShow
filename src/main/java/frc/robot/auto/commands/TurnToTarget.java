/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;

public class TurnToTarget extends Command {
  private Drive drive;
  private LimelightVision limelightVision;

  public TurnToTarget() {
    this.drive = Drive.getInstance();
    requires(drive);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.5);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.5);
  }

  @Override
  protected boolean isFinished() {
    return limelightVision.isTargetVisible();
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
