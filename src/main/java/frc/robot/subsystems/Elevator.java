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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.OI;
import frc.robot.RobotMap;

public class Elevator extends Subsystem {
  private static Elevator instance;
  private static int ABSOLUTE_STARTING_POSITION = 0;
  public static ElevatorState elevatorState = ElevatorState.MANUAL;

  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    MANUAL(0, 0, 0, 0),
    ZERO(0, 0, 0, 0),
    HOLD(0, 0, 0, 0),
    INTAKEBALL(0, 0, 0, 0),
    INTAKEHATCH(0, 0, 0, 0),
    HATCH1(0, 0, 0, 0),
    HATCH2(0, 0, 0, 0),
    HATCH3(0, 0, 0, 0),
    BALL1(0, 0, 0, 0),
    BALL2(0, 0, 0, 0),
    BALL3(0, 0, 0, 0);

    private final int elevatorHeight;
    private final int vel;
    private final int accel;
    private final int clawAngle;

    ElevatorState(int elevatorHeight, int vel, int accel, int clawAngle) {
      this.elevatorHeight = elevatorHeight;
      this.vel = vel;
      this.accel = accel;

      this.clawAngle = clawAngle;
    }

    private int getElevatorHeight() {
      return elevatorHeight;
    }

    private int getVel(){
      return vel;
    }

    private int getAccel() {
      return accel;
    }

    private int getClawAngle(){
      return clawAngle;
    }
  }

  private Elevator() {
    // int currentValue = RobotMap.elevatorTop.getSensorCollection().getPulseWidthPosition();
    RobotMap.elevatorBot.getSensorCollection().setPulseWidthPosition(0, 10);
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
        System.out.println("Starting " + this.getName());
        // RobotMap.elevatorBot.getSensorCollection().setPulseWidthPosition(0, 10);
      }

      protected void execute() {
        // if (OI.getSecondaryA()) elevatorState = ElevatorState.INTAKE;
        // else if (OI.getSecondaryB()) elevatorState = ElevatorState.ROCKET1;
        // else if (OI.getSecondaryX()) elevatorState = ElevatorState.ROCKET2;
        // else if (OI.getSecondaryY()) elevatorState = ElevatorState.ROCKET3;
        // else if (OI.getSecondaryStart()) elevatorState = ElevatorState.MANUAL;
        // else if (OI.getSecondaryBack()) elevatorState = ElevatorState.ZERO;
        if(OI.getPrimaryA()){
          RobotMap.elevatorBot.config_kP(0, 0.2, 10);
		      RobotMap.elevatorBot.config_kI(0, 0.0, 10);
		      RobotMap.elevatorBot.config_kD(0, 0.0, 10);
          RobotMap.elevatorBot.config_kF(0, 0.2, 10);
          RobotMap.elevatorBot.selectProfileSlot(0, 0);

          RobotMap.elevatorBot.setSelectedSensorPosition(0);
          RobotMap.elevatorBot.getSensorCollection().setPulseWidthPosition(0, 10);

          elevatorState = ElevatorState.ZERO;
          System.out.println("This ran!");
        } 
        
        // Elevator Heights
        if (elevatorState != ElevatorState.MANUAL) {
          // RobotMap.elevatorBot.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          // RobotMap.elevatorBot.configMotionAcceleration(elevatorState.getAccel(), 10);
          // RobotMap.elevatorBot.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight());

          RobotMap.elevatorBot.configMotionCruiseVelocity(10000, 10);
          RobotMap.elevatorBot.configMotionAcceleration(20000, 10);
          RobotMap.elevatorBot.set(ControlMode.MotionMagic, 20000);
          System.out.println("V" + RobotMap.elevatorBot.getMotorOutputVoltage());
          System.out.println("Velo" + RobotMap.elevatorBot.getSelectedSensorVelocity());
          
        } else {
          RobotMap.elevatorBot.set(ControlMode.PercentOutput, OI.getSecondaryA1());
          RobotMap.elevatorTop.follow(RobotMap.elevatorTop);

          RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getSecondaryA3());
        }
        RobotMap.getElevatorOutputs();         
      }

      protected boolean isFinished() {
        return false;
      }

      protected void end() {
        System.out.println("Stopping " + this.getName());
      }

      protected void interrupted() {
        System.out.println("Sn4pplejacks, " + this.getName() + " stopped!");
        end();
      }
    });
  }

  public static void stopMoving() {
    RobotMap.elevatorBot.set(ControlMode.PercentOutput, 0.0);
  }
}