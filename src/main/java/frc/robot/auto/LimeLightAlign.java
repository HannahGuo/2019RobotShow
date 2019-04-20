    
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

  public LimeLightAlign() {
    this.drive = Drive.getInstance();
    requires(drive);
    setTimeout(0.4);
  }

  @Override
  protected void initialize() {
  }

  @Override
  protected void execute() {
    LimelightVision.updateVision();
    LimelightVision.setCamMode(0);

    if(LimelightVision.isTargetVisible()) {
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.4 * -LimelightVision.getHorizontalOffset() * Constants.limelightP);
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.4 * -LimelightVision.getHorizontalOffset() * Constants.limelightP);

      System.out.println("ALIGN " + LimelightVision.getHorizontalOffset() + " " + (-LimelightVision.getHorizontalOffset() * Constants.limelightP));
    }
  }

  @Override
  protected boolean isFinished() {
    System.out.println("LIMELIGHT OFFSET = " + LimelightVision.getHorizontalOffset());
    return (LimelightVision.isTargetVisible() && (LimelightVision.getHorizontalOffset() <= 1.0 && LimelightVision.getHorizontalOffset() >= -1.0)) || isTimedOut();
  }

  @Override
  protected void end() {
    System.out.println("LIMELIGHT VISION TURN FINISHED");
  }

  @Override
  protected void interrupted() {
  }
}