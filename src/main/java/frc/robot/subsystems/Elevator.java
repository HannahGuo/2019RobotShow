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

public class Elevator extends Subsystem {
  private static Elevator instance;
  private static int ABSOLUTE_STARTING_POSITION = 0;
  public static ElevatorState elevatorState = ElevatorState.ZERO;

  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    MANUAL(0.0, 0),
    ZERO(0.0, 0),
    INTAKE(0.0, 0),
    ROCKET1(0.0, 0),
    ROCKET2(0.0, 0),
    ROCKET3(0.0, 0);

    private final double height;
    private final int accel;

    ElevatorState(double height, int accel) {
      this.height = height;
      this.accel = accel;
    }

    private double getHeight() {
      return height;
    }

    private int getAccel() {
      return accel;
    }
  }

  private Elevator() {
    // int currentValue = RobotMap.elevatorTop.getSensorCollection().getPulseWidthPosition();
    // RobotMap.elevatorTop.getSensorCollection().setPulseWidthPosition(0, 10);
    // double dT = System.currentTimeMillis();
    // while (System.currentTimeMillis() - dT < 2056 && currentValue == RobotMap.elevatorTop.getSensorCollection().getPulseWidthPosition());
    // ABSOLUTE_STARTING_POSITION = RobotMap.elevatorTop.getSelectedSensorPosition(0);
    // System.out.println("ABSOLUTE STARTING POSITION" + ABSOLUTE_STARTING_POSITION);
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
        if (OI.getSecondaryA()) elevatorState = ElevatorState.INTAKE;
        else if (OI.getSecondaryB()) elevatorState = ElevatorState.ROCKET1;
        else if (OI.getSecondaryX()) elevatorState = ElevatorState.ROCKET2;
        else if (OI.getSecondaryY()) elevatorState = ElevatorState.ROCKET3;
        else if (OI.getSecondaryStart()) elevatorState = ElevatorState.MANUAL;
        else if (OI.getSecondaryBack()) elevatorState = ElevatorState.ZERO;

        RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);

        if (elevatorState != ElevatorState.MANUAL)
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getHeight());
        else
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, OI.getSecondaryA1());
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