    
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
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class LimeLightAlignDrive extends Command {
  private Drive drive;
  private double distance;
  private SynchronousPID vPID, hPID;

  public LimeLightAlignDrive() {
    this.drive = Drive.getInstance();
    this.vPID = new SynchronousPID();
    this.vPID.setOutputRange(-1.0, 1.0);
    this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
    requires(drive);
  }

  @Override
  protected void initialize() {
    RobotMap.gyroSPI.reset();
    this.vPID.reset();
    this.vPID.setPID(Constants.linPIDLowGear);
    this.hPID.reset();
    this.hPID.setPID(Constants.angPID);
    Drive.setLowGear();
    Drive.resetDriveEncoders();
    LimelightVision.updateVision();
    LimelightVision.setVisionProcessingMode();
    this.distance = ((2.8499 * Math.pow(LimelightVision.getTargetArea(), 2)) - (26.662 * LimelightVision.getTargetArea()) + 61.871) * Constants.driveEncoderUnitsPerInch;
  }

  @Override
  protected void execute() {
    LimelightVision.updateVision();
    LimelightVision.setVisionProcessingMode();
    if(LimelightVision.isTargetVisible()) {
      this.vPID.setSetpoint(Drive.getAverageDrivePosition() - this.distance);
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((0 - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
      double head = hPID.calculate(RobotMap.gyroSPI.getRate());
      System.out.println("LIMELIGHT DRIVE " + (distance / Constants.driveEncoderUnitsPerInch) + " " + vel + " " + head);
      RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.9 * (-vel - head));
      RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.9 * (vel - head));
    } else {
      Drive.stopMoving();
    }
  }

  @Override
  protected boolean isFinished() {
    return Constants.isWithinThreshold(this.vPID.getError(), -200, 200);
  }

  @Override
  protected void end() {
		System.out.println(this.getName() + " FINISHED");
    Drive.stopMoving();
  }

  @Override
  protected void interrupted() {
  }
}