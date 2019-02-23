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
    //No, I don't know why these have to be negative. 
    //Yes, I have tried reversing stuff. 

    MANUAL(0, 0, 0, 0),
    ZERO(0, 0, 0, 0),
    HOLD(0, 0, 0, -592),
    INTAKEBALL(0, 0, 0, 0),
    INTAKEHATCH(0, 0, 0, 0),
    HATCH1(-6766, 0, 0, 0),
    HATCH2(-46450, 0, 0, 0),
    HATCH3(-85850, 0, 0, 0),
    BALL1(-22500, -205000, -950000, 0),
    BALL2(-65200, -205000, -950000, 0),
    BALL3(-92600, 0, 0, 0),
    TESTING(0, 0, 0, 0);

    //Elevator Height, Vel, Accel, Claw, Angle

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
    RobotMap.elevatorBot.getSensorCollection().setPulseWidthPosition(0, 10);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting " + this.getName());
        if(!RobotMap.zeroMe.get()) {
          RobotMap.elevatorBot.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Elevator has been zeroed!");
        } else {
          System.out.println("Please zero the elevator!!!");
        }
        RobotMap.elevatorBot.config_kP(0, 0.8, 10);
        RobotMap.elevatorBot.config_kI(0, 0.0, 10);
        RobotMap.elevatorBot.config_kD(0, 0.0, 10);
        RobotMap.elevatorBot.config_kF(0, 0.5, 10);
        RobotMap.elevatorBot.selectProfileSlot(0, 0);

        RobotMap.wristControl.config_kP(0, 0.01, 10);
        RobotMap.wristControl.config_kI(0, 0.0, 10);
        RobotMap.wristControl.config_kD(0, 0.0, 10);
        RobotMap.wristControl.config_kF(0, 0.02, 10);
        RobotMap.wristControl.selectProfileSlot(0, 0);
      }

      protected void execute() {
        if (OI.getPrimaryA()) elevatorState = ElevatorState.BALL1;
        else if(OI.getPrimaryB()) elevatorState = ElevatorState.BALL2;
        else if (OI.getPrimaryX()) elevatorState = ElevatorState.TESTING;
        else if(OI.getPrimaryY()) {
          RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("WRIST CONTROL " + RobotMap.wristControl.getSelectedSensorPosition());
        }
        // else if (OI.getSecondaryX()) elevatorState = ElevatorState.ROCKET2;
        // else if (OI.getSecondaryY()) elevatorState = ElevatorState.ROCKET3;
        // else if (OI.getSecondaryStart()) elevatorState = ElevatorState.MANUAL;
        // else if (OI.getSecondaryBack()) elevatorState = ElevatorState.ZERO;
        
        if(elevatorState == ElevatorState.TESTING) {
          RobotMap.wristControl.configMotionCruiseVelocity(8000);
          RobotMap.wristControl.configMotionAcceleration(10000);
          RobotMap.wristControl.set(ControlMode.MotionMagic, 1000);
          // RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getPrimaryA3());
        }

        // Elevator Heights
        // if (elevatorState != ElevatorState.MANUAL) {
        //   RobotMap.elevatorBot.configMotionCruiseVelocity(elevatorState.getVel(), 10);
        //   RobotMap.elevatorBot.configMotionAcceleration(elevatorState.getAccel(), 10);
        //   RobotMap.elevatorBot.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight());      
        // } else {
        //   RobotMap.elevatorBot.set(ControlMode.PercentOutput, OI.getSecondaryA1());
        //   RobotMap.elevatorTop.follow(RobotMap.elevatorBot);

        //   RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getSecondaryA3());
        // }
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
    elevatorState = ElevatorState.MANUAL;
    RobotMap.elevatorBot.set(ControlMode.PercentOutput, 0.0);
  }
}