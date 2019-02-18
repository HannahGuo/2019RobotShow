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
import frc.robot.RobotMap;

public class Wrist extends Subsystem {
  private static Wrist instance;
  public static Wrist getInstance() {
    return instance == null ? instance = new Wrist() : instance;
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
        int absolutePosition = RobotMap.wristControl.getSensorCollection().getPulseWidthPosition();
        RobotMap.wristControl.setSelectedSensorPosition(absolutePosition, 0, 10);
      }

      protected void initialize() {
        System.out.println("Starting " + this.getSubsystem());
      }

      protected void execute() {
        // RobotMap.wristControl.set(ControlMode.Position, 0.0);
      }

      protected boolean isFinished() {
        return false;
      }

      protected void end() {
        System.out.println("Stopping " + this.getSubsystem());
      }

      protected void interrupted() {
        System.out.println("Sn4pplejacks, " + this.getSubsystem() + " stopped!");
        end();
      }
    });
  }
}
