/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.OI;

public class Drive extends Subsystem {
  private static Drive instance;

  public static Drive getInstance() {
    return instance == null ? instance = new Drive() : instance;
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting drive");
      }

      protected void execute() {
        double straight = OI.getStraight(), steering = OI.getSteering();
        driveLR(-straight + steering, straight + steering);
      }

      protected boolean isFinished() {
        return false;
      }

      protected void end() {
        System.out.println("Stopping drive");
        stopMoving();
      }

      protected void interrupted() {
        System.out.println("Snapplejacks, " + this.getName() + " stopped!");
        end();
      }
    });
  }

  public static void driveLR(double left, double right) {
    RobotMap.driveLeftFront.set(ControlMode.PercentOutput, left);
    RobotMap.driveRightFront.set(ControlMode.PercentOutput, right);
  }
  
  public static void stopMoving() {
    RobotMap.driveLeftFront.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightFront.set(ControlMode.Follower, RobotMap.driveLeftFront.getDeviceID());
    RobotMap.driveRightFront.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightRear.set(ControlMode.Follower, RobotMap.driveRightFront.getDeviceID());
  }
}