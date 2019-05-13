/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.LimelightVision;

public class FlashLimelight extends Command {
  public FlashLimelight() {
  }

  @Override
  protected void initialize() {
    setTimeout(2.0);
  }

  @Override
  protected void execute() {
    LimelightVision.blink();
  }

  @Override
  protected boolean isFinished() {
    return isTimedOut();
  }

  @Override
  protected void end() {
    LimelightVision.turnOn();
  }

  @Override
  protected void interrupted() {
  }
}
