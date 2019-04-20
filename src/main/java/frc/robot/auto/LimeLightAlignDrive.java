    
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
  private LimelightVision limelightVision;
  private double target;
  private static double angleError;

  public LimeLightAlignDrive() {
    this.drive = Drive.getInstance();
    this.limelightVision = LimelightVision.getInstance();
    requires(drive);
  }

  @Override
  protected void initialize() {
    RobotMap.gyroSPI.reset();
  }

  @Override
  protected void execute() {
    limelightVision.updateVision();
    LimelightVision.setCamMode(0);
    if(limelightVision.isTargetVisible()) {
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.2);
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, -0.2);
    }
  }

  @Override
  protected boolean isFinished() {
    return limelightVision.getTargetArea() > 4.7;
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