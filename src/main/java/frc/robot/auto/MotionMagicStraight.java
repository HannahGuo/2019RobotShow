/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class MotionMagicStraight extends Command {
  private SynchronousPID vPID, hPID;
	private double distance = 0, heading = 0;

  Drive drive;

  public MotionMagicStraight(double dist, double head, double time) {
    this.drive = Drive.getInstance();
    requires(this.drive);

    this.hPID = new SynchronousPID();
    this.hPID.setPID(10.0, 0.0, 0.001);
    this.hPID.setOutputRange(-200000, 200000);
  }

  @Override
  protected void initialize() {
    RobotMap.gyroSPI.reset();
    RobotMap.driveLeftTop.setSelectedSensorPosition(0);
    RobotMap.driveRightTop.setSelectedSensorPosition(0);

    RobotMap.driveLeftTop.configMotionCruiseVelocity(1000, 10);
    RobotMap.driveLeftTop.configMotionAcceleration(10000, 10);

    RobotMap.driveRightTop.configMotionCruiseVelocity(1000, 10);
    RobotMap.driveRightTop.configMotionAcceleration(10000, 10);
  }

  @Override
  protected void execute() {
    this.hPID.setSetpoint(RobotMap.gyroSPI.getAbsoluteAngle() * 800);
    double steering = this.hPID.calculate(RobotMap.gyroSPI.getRate());
    RobotMap.driveLeftTop.set(ControlMode.MotionMagic, 200000 - steering);
    RobotMap.driveRightTop.set(ControlMode.MotionMagic, -200000 + steering);
    RobotMap.printDriveEncoderPositions();
  }

  @Override
  protected boolean isFinished() {
    return false;
  }

  @Override
  protected void end() {
  }

  @Override
  protected void interrupted() {
  }
}
