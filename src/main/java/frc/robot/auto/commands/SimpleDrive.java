/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;

public class SimpleDrive extends Command {
  private Drive drive;
  private double targetDistance;
  private double targetAngle;

  public SimpleDrive(double targetDistance, double targetAngle) {
    this.drive = Drive.getInstance();
    requires(drive);
    this.targetDistance = targetDistance;
    this.targetAngle = targetAngle;
  }

  @Override
  protected void initialize() {
    drive = Drive.getInstance();
    setTimeout(10.0);
  }

  @Override
  protected void execute() {
    double angleError = targetAngle - RobotMap.gyroSPI.getAbsoluteAngle();
    
    drive.driveLR(angleError * Constants.angP, angleError * Constants.angP);
  }

  @Override
  protected boolean isFinished() {
    return this.isTimedOut();
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
