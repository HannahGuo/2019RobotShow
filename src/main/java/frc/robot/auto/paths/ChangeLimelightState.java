/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.LimelightVision;

public class ChangeLimelightState extends Command {
  private boolean driveMode = true;
  public ChangeLimelightState(boolean driveMode) {
    this.driveMode = driveMode;
  }

  @Override
  protected void initialize() {
    setTimeout(0.4);

    if(this.driveMode) LimelightVision.setDriveMode();
    else LimelightVision.setVisionProcessingMode();
  }

  @Override
  protected void execute() {
  }

  @Override
  protected boolean isFinished() {
    return isTimedOut();
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
