/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.OI;
import frc.robot.RobotMap;

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
        System.out.println("Starting " + this.getName());
      }

      protected void execute() {
        double straight = OI.getStraight(), steering = OI.getSteering();
        driveLR(-straight - steering, straight - steering);

        if(OI.getPrimaryRB()) RobotMap.driveShifter.set(false); // low gear
        else if(OI.getPrimaryLB()) RobotMap.driveShifter.set(true); // high gear

      }

      protected boolean isFinished() {
        return false;
      }

      protected void interrupted() {
        System.out.println("Sn4pplejacks, " + this.getName() + " stopped!");
        end();
      }
    });
  }

  public static void driveLR(double left, double right) {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, left);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, right);
  }
  
  public static void stopMoving() {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveLeftBot.set(ControlMode.Follower, RobotMap.driveLeftTop.getDeviceID());
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightBot.set(ControlMode.Follower, RobotMap.driveRightTop.getDeviceID());
  }
}