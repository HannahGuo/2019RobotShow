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
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class PathfinderDrive extends Command {
  private Drive drive;
  private static EncoderFollower left;
  private static EncoderFollower right;
  public PathfinderDrive() {
    this.drive = Drive.getInstance();
    requires(drive);
  }

  @Override
  protected void initialize() {
    RobotMap.resetSensors();

    Waypoint[] points = new Waypoint[] {
      new Waypoint(0, 0, 0),                           // Waypoint @ x=0, y=0,   exit angle=0 radians
      new Waypoint(0.5, 0.5, Pathfinder.d2r(-45)),      // Waypoint @ x=-4, y=-1, exit angle=-45 degrees
    };
  
    Trajectory.Config config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, 0.05, 1.7, 2.0, 60.0);
    Trajectory trajectory = Pathfinder.generate(points, config);
    TankModifier modifier = new TankModifier(trajectory).modify(Constants.trackWidthInches);

    left = new EncoderFollower(modifier.getLeftTrajectory());
    right = new EncoderFollower(modifier.getRightTrajectory());

    left.configureEncoder(RobotMap.driveLeftTop.getSelectedSensorPosition(0), 1000, Constants.wheelDiameterM);
    left.configurePIDVA(0.8, 0.0, 0.0, 1 / Constants.maxMPerSecond, 0);

    right.configureEncoder(RobotMap.driveLeftTop.getSelectedSensorPosition(0), 1000, Constants.wheelDiameterM);
    right.configurePIDVA(0.8, 0.0, 0.0, 1 / Constants.maxMPerSecond, 0);
  }

  @Override
  protected void execute() {
    double leftStraight = left.calculate(RobotMap.driveLeftTop.getSelectedSensorPosition(0));
    double rightStraight = right.calculate(RobotMap.driveRightTop.getSelectedSensorPosition(0));

    double gyroHeading = RobotMap.gyroSPI.getAbsoluteAngle();
    double desiredHeading = -45;
    
    double angleError = gyroHeading - desiredHeading;

    double steering = Constants.angP * angleError;
    drive.driveLR(-leftStraight + steering, rightStraight + steering);

    System.out.println(leftStraight + " " + rightStraight + " " + steering + " " + angleError);
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
