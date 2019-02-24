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
  public static ElevatorState elevatorState = ElevatorState.MANUAL;
  private static ElevatorState lastState = ElevatorState.MANUAL;
  private static boolean elevatorZeroed = false;
  private static boolean wristZeroed = false;

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
    BALL1(-22500, 10000, 20000, 0),
    BALL2(-65200, 20000, 25000, 0),
    BALL3(-92600, 20000, 10000, 0),
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
    RobotMap.elevatorTop.getSensorCollection().setPulseWidthPosition(0, 10);
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting " + this.getName());
        if(!RobotMap.zeroThyElevator.get()) {
          RobotMap.elevatorBot.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Elevator has been zeroed!");
          elevatorZeroed = true;
        } else {
          System.out.println("Please zero the elevator!!");
        }

        if(!RobotMap.zeroThyWrist.get()) {
          RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Wrist has been zeroed!");
          wristZeroed = true;
        } else {
          System.out.println("Please zero the wrist!!");
        }

        RobotMap.elevatorTop.config_kP(0, 1.1, 10);
        RobotMap.elevatorTop.config_kI(0, 0.00025, 10);
        RobotMap.elevatorTop.config_kD(0, 0.0, 10);
        RobotMap.elevatorTop.config_kF(0, 0.25, 10);
        RobotMap.elevatorTop.selectProfileSlot(0, 0);
        RobotMap.elevatorTop.config_IntegralZone(0, 250, 10);

        RobotMap.wristControl.config_kP(0, 0.1, 10);
        RobotMap.wristControl.config_kI(0, 0.00002, 10);
        RobotMap.wristControl.config_kD(0, 0.0, 10);
        RobotMap.wristControl.config_kF(0, 0.0, 10);
        RobotMap.wristControl.selectProfileSlot(0, 0);
      }

      protected void execute() {
        // if (OI.getPrimaryA()) elevatorState = ElevatorState.BALL1;
        // else if(OI.getPrimaryB()) elevatorState = ElevatorState.BALL2;
        // else if(OI.getPrimaryX()) elevatorState = ElevatorState.BALL3;
        // else if(OI.getPrimaryStart()) elevatorState = ElevatorState.MANUAL;
        // else if (OI.getPrimaryX()) {
        //   RobotMap.elevatorBot.config_kP(0, SmartDashboard.getNumber("EUP", 0.8), 10);
        //   RobotMap.elevatorBot.config_kI(0, SmartDashboard.getNumber("EUI", 0.0), 10);
        //   RobotMap.elevatorBot.config_kD(0, SmartDashboard.getNumber("EUP", 0.0), 10);
        //   RobotMap.elevatorBot.config_kF(0, SmartDashboard.getNumber("EUF", 0.02), 10);
        //   RobotMap.elevatorBot.selectProfileSlot(0, 0);
        // }
        if(OI.getPrimaryY()) {
          // RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("WRIST CONTROL " + RobotMap.wristControl.getSelectedSensorPosition());
          elevatorState = ElevatorState.TESTING;
        }

        if(elevatorState == ElevatorState.TESTING) {
          RobotMap.wristControl.configMotionCruiseVelocity(500);
          RobotMap.wristControl.configMotionAcceleration(1500);
          RobotMap.wristControl.set(ControlMode.MotionMagic, 5200);
          // if(!RobotMap.zeroThyWrist.get()) {
          //   RobotMap.wristControl.set(ControlMode.PercentOutput, 0.0);
          // }
        } else if(elevatorState == ElevatorState.MANUAL) {
          RobotMap.elevatorBot.set(ControlMode.PercentOutput, OI.getSecondaryA1());
          RobotMap.elevatorTop.follow(RobotMap.elevatorBot);

          if(!RobotMap.zeroThyWrist.get() && OI.getPrimaryA3() <= 0) RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
          else RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getPrimaryA3());
        
        } else {
          RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight());    
          RobotMap.elevatorBot.follow(RobotMap.elevatorTop);  
        }  
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
    RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.elevatorBot.set(ControlMode.PercentOutput, 0.0);
    elevatorZeroed = false;
    wristZeroed = false;
  }
}