/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;

public class SimpleTurn extends Command {
  private Drive drive;
  private LimelightVision limelightVision;
  private double target;
  private static double angleError;

  public SimpleTurn() {
    this.drive = Drive.getInstance();
    this.limelightVision = LimelightVision.getInstance();
    requires(drive);
  }

  // public SimpleTurn(double target) {
  //   this.drive = Drive.getInstance();
  //   this.target = target;
  //   requires(drive);
  // }

  @Override
  protected void initialize() {
    RobotMap.resetSensors();
    System.out.println("HELLO WORLDDDD");
  }

  @Override
  protected void execute() {
    limelightVision.updateVision();
    angleError = RobotMap.gyroSPI.getAbsoluteAngle() - limelightVision.getHorizontalOffset();
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, angleError * Constants.getAngleP());
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, angleError * Constants.getAngleP());

    System.out.println(angleError + " " + angleError * Constants.getAngleP());
  }

  @Override
  protected boolean isFinished() {
    return angleError < 2.5 && angleError > -2.5;
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
