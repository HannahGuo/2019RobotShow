    
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

public class LimeLightFwoosh extends Command {
  private Drive drive;

  public LimeLightFwoosh() {
    this.drive = Drive.getInstance();
    requires(drive);
    setTimeout(0.2);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    LimelightVision.updateVision();
    LimelightVision.setCamMode(0);

    if(LimelightVision.isTargetVisible()) {
      double head = LimelightVision.getHorizontalOffset() * Constants.limelightP;
      double vel = -0.2;
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, -vel - head);
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, vel - head);

      System.out.println("ALIGN " + LimelightVision.getHorizontalOffset() + " " + (-LimelightVision.getHorizontalOffset() * Constants.limelightP));
    }
  }

  @Override
  protected boolean isFinished() {
    return LimelightVision.isTargetVisible() && LimelightVision.getTargetArea() > 3.65;
  }

  @Override
  protected void end() {
    System.out.println("LIMELIGHT VISION FWWOOSH FINISHED");
  }

  @Override
  protected void interrupted() {
  }
}