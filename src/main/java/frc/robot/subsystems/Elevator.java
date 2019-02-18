/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
public class Elevator extends Subsystem {
  private static Elevator instance;
  private static int ABSOLUTE_STARTING_POSITION = 0;

  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    ZERO, INTAKE, ROCKET1, ROCKET2, ROCKET3
  }

  private Elevator(){
    int currentValue = RobotMap.elevatorTop.getSensorCollection().getPulseWidthPosition();
    RobotMap.elevatorTop.getSensorCollection().setPulseWidthPosition(0, 10);
    double dT = System.currentTimeMillis();
    while(System.currentTimeMillis() - dT < 2056 && currentValue == RobotMap.elevatorTop.getSensorCollection().getPulseWidthPosition());
    ABSOLUTE_STARTING_POSITION = RobotMap.elevatorTop.getSelectedSensorPosition(0);
    System.out.println("ABSOLUTE STARTING POSITION" + ABSOLUTE_STARTING_POSITION);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting " + this.getSubsystem());
      }

      protected void execute() {
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