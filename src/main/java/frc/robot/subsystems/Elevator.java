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
  private static boolean holdGroundMode = false;
  private static boolean holdWristUpMode = false;

  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    // No, I don't know why the positions have to be negative. 
    // Yes, I have tried reversing stuff. 
    // No, don't make the velocities and accelerations negative.
    // Yes, that messes everything up.

    // Elevator Height, Vel, Accel, Claw, Angle
    // All of these units are in native encoder units (4096units/1rev)
    MANUAL(0, 0, 0, 0),
    ZERO(-100, 6000, 8000, 4115),
    HOLD(-3500, 6000, 8000, 920),
    INTAKEBALL(-6025, 6000, 8000, 5240),
    INTAKEHATCH(-6025, 6000, 8000, 5240),
    HATCH1(-12000, 6000, 8000, 4115),
    HATCH2(-46450, 0, 0, 0),
    HATCH3(-85850, 0, 0, 0),
    BALL1(-22500, 10000, 20000, 0),
    BALL2(-65200, 20000, 25000, 0),
    BALL3(-92600, 20000, 10000, 0),
    TESTING(0, 0, 0, 0);

    private final int elevatorHeight;
    private final int vel;
    private final int accel;
    private final int clawPosition;

    ElevatorState(int elevatorHeight, int vel, int accel, int clawPosition) {
      this.elevatorHeight = elevatorHeight;
      this.vel = vel;
      this.accel = accel;
      this.clawPosition = clawPosition;
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

    private int getClawPosition(){
      return clawPosition;
    }
  }

  @Override
  public void initDefaultCommand() {
    setDefaultCommand(new Command() {
      {
        requires(getInstance());
      }

      protected void initialize() {
        System.out.println("Starting " + this.getName());

        if(isElevatorButtonPressed()) {
          RobotMap.elevatorTop.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Elevator has been zeroed!");
          elevatorZeroed = true;
        } else {
          System.out.println("Please zero the elevator!!");
        }

        if(isWristButtonPressed()) {
          RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Wrist has been zeroed!");
          wristZeroed = true;
        } else {
          System.out.println("Please zero the wrist!!");
        }
      }

      protected void execute() {
        if (OI.getPrimaryA()) elevatorState = ElevatorState.HATCH1;
        else if(OI.getPrimaryB()) elevatorState = ElevatorState.ZERO;
        else if(OI.getPrimaryX()) elevatorState = ElevatorState.BALL3;
        else if(OI.getPrimaryStart()) elevatorState = ElevatorState.HOLD;
        else if(OI.getPrimaryY()) elevatorState = ElevatorState.INTAKEHATCH;

        if(!elevatorZeroed) {
          if(isElevatorButtonPressed()) {
            RobotMap.elevatorTop.getSensorCollection().setQuadraturePosition(0, 10);
            elevatorZeroed = true;
          } else {
            System.out.println("Please zero the elevator!");
          }
        }

        if(!wristZeroed) {
          if(isWristButtonPressed()) {
            RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
            wristZeroed = true;
          } else {
            System.out.println("Please zero the wrist!");
          }
        }
        
        if(elevatorState == ElevatorState.INTAKEHATCH) {
          RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight()); 
          
          if(elevatorState.getClawPosition() - 100 <= RobotMap.wristControl.getSelectedSensorPosition() && 
              RobotMap.wristControl.getSelectedSensorPosition() <= elevatorState.getClawPosition() + 100)
                holdGroundMode = true;

          if(!holdGroundMode) {
            RobotMap.wristControl.configMotionCruiseVelocity(2000);
            RobotMap.wristControl.configMotionAcceleration(200000);
            RobotMap.wristControl.set(ControlMode.MotionMagic, elevatorState.getClawPosition());
          } else {
            RobotMap.wristControl.set(ControlMode.Current, 0.5);
          }

          RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.5);
        } else if(elevatorState == ElevatorState.MANUAL) {
          // RobotMap.elevatorTop.set(ControlMode.PercentOutput, OI.getSecondaryA1());

          if(isWristButtonPressed() && OI.getPrimaryRightYAxis() <= 0) RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
          else RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getPrimaryRightYAxis() * 0.3);
        
        } else {
          if(lastState != elevatorState) {
            if(lastState.clawPosition <= elevatorState.clawPosition) RobotMap.wristControl.selectProfileSlot(0, 0);
            else RobotMap.wristControl.selectProfileSlot(1, 0);
          }
          
          if((elevatorState == ElevatorState.HOLD  || elevatorState == ElevatorState.HATCH1) && (elevatorState.getClawPosition() - 100 <= RobotMap.wristControl.getSelectedSensorPosition() && 
          RobotMap.wristControl.getSelectedSensorPosition() <= elevatorState.getClawPosition() + 100)) {
            holdWristUpMode = true;
            RobotMap.wristControl.selectProfileSlot(2, 0);
            RobotMap.wristControl.set(ControlMode.Position, elevatorState.getClawPosition());
          } else {
            RobotMap.wristControl.configMotionCruiseVelocity(2000);
            RobotMap.wristControl.configMotionAcceleration(200000);
            RobotMap.wristControl.set(ControlMode.MotionMagic, elevatorState.getClawPosition());
            RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.0);
          }
            
          RobotMap.traumatizedGhosts.set(holdWristUpMode);

          RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight());    
        }  
        lastState = elevatorState;
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
    RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.traumatizedGhosts.set(false);
    elevatorState = ElevatorState.MANUAL;
    elevatorZeroed = false;
    wristZeroed = false;
    holdGroundMode = false;
    holdWristUpMode = false;
  }

  private static boolean isElevatorButtonPressed() {
    return !RobotMap.zeroThyElevator.get();
  }

  private static boolean isWristButtonPressed(){
    return !RobotMap.zeroThyWrist.get();
  }
}