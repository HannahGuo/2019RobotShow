    
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

public class LimeLightAlign extends Command {
  private Drive drive;
  private LimelightVision limelightVision;
  private static double angleError;

  public LimeLightAlign() {
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

    if(limelightVision.isTargetVisible()) {
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.5 * -limelightVision.getHorizontalOffset() * Constants.limelightP);
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.5 * -limelightVision.getHorizontalOffset() * Constants.limelightP);

      System.out.println("ALIGN " + limelightVision.getHorizontalOffset() + " " + (-limelightVision.getHorizontalOffset() * Constants.limelightP));
    }
  }

  @Override
  protected boolean isFinished() {
    return (limelightVision.isTargetVisible() && (limelightVision.getHorizontalOffset() <= 2.7 && limelightVision.getHorizontalOffset() >= -2.7)) || isTimedOut();
  }

  @Override
  protected void end() {
    System.out.println("LIMELIGHT VISION TURN FINISHED");
  }

  @Override
  protected void interrupted() {
  }
}