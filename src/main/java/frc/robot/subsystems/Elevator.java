/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import static org.junit.Assert.assertEquals;

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
  private static boolean extendGhosts = false;
  private static boolean hasOrange = false;
  private static boolean hasHatch = false;
  private static boolean outtakeOrange = false;
  private static final int CLAW_VEL = 700;
  private static final int CLAW_ACCEL = 900;
  private static int lowerHatch = 0;
  private static int HATCH_OUTTAKE_CONSTANT = -10000;
  private static double timerBallTime = System.currentTimeMillis();
  private static boolean hasSeenOrange = false;
  private static double toHoldFromIntakeTimer = System.currentTimeMillis();
  private static boolean activateHoldTimer = false;
  private static double spinHatchIntakeTimer = System.currentTimeMillis();
  private static boolean activateIntakeTimer = false;

  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    // No, I don't know why the positions have to be negative. 
    // Yes, I have tried reversing stuff. 
    // No, don't make the velocities and accelerations negative.
    // Yes, that messes everything up.

    // Elevator Height, Vel, Accel, Claw Position (All of these units are in native encoder units (4096units/1rev)
    // Intake Wheels Top, Intake Wheels Bot
    MANUAL(0, 0, 0, 0),
    ZERO(0, 0, 0, 0),
    HOLD(-3500, 6000, 3000, 920),
    INTAKE(-5875, 6000, 3000, 5240),
    INTAKEBALL(-5875, 6000, 3000, 5240),
    INTAKEHATCH(-5875, 6000, 3000, 5240),
    INTAKEHUMANHATCH(-6025, 3000, 8000, 5240),
    INTAKEHUMANBALL(-6025, 3000, 8000, 5240),
    HATCH1(-11850, 6000, 8000, 4115),
    HATCH2(-52000, 6000, 8000, 4115),
    HATCH3(-94830, 6000, 8000, 4115),
    BALL1(-21983, 10000, 20000, 4115),
    BALL2(-66900, 20000, 25000, 4115),
    BALL3(-97195, 20000, 10000, 3610),
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
         // SECONDARY CONTROLS
        // A = GROUND
        // B = LEVEL 1
        // X = LEVEL 2
        // Y = LEVEL 3
        // A + RIGHT TRIGGER = GROUND HATCH INTAKE
        // B + RIGHT TRIGGER =  HUMAN PLAYER STATION INTAKE
        // B + RIGHT BUMPER/LEFT BUMPER  = OUTTAKE LEVEL 1
        // X + RIGHT BUMPER/LEFT BUMPER = OUTTAKE LEVEL 2
        // Y + RIGHT BUMPER/LEFT BUMPER = OUTTAKE LEVEL 3
        // START = MANUAL
        // MANUAL + RIGHT TRIGGER = WHEELS SPIN FOR HATCH
        // MANUAL + LEFT TRIGGER = WHEELS SPIN FOR BALL
        // A + LEFT TRIGGER = GROUND BALL INTAKE
        // B + LEFT TRIGGER = HUMAN PLAYER STATION INTAKE
        // BACK = ZERO ELEVATOR

        if (OI.getSecondaryA()) {
          if(lastState != ElevatorState.INTAKEBALL && lastState != ElevatorState.INTAKEHATCH) elevatorState = ElevatorState.INTAKE;
        } else if(OI.getSecondaryB()) {
          if(OI.getSecondaryRT()) elevatorState = ElevatorState.INTAKEHUMANHATCH;
          if(hasHatch) elevatorState = ElevatorState.HATCH1;
          else if(hasOrange) elevatorState = ElevatorState.BALL1;
        } else if(OI.getSecondaryX()) {
          if(hasHatch) elevatorState = ElevatorState.HATCH2;
          else if(hasOrange) elevatorState = ElevatorState.BALL2;
        } else if(OI.getSecondaryY()) {
          if(hasHatch) elevatorState = ElevatorState.HATCH3;
          else if(hasOrange) elevatorState = ElevatorState.BALL3;
        } else if(OI.getSecondaryStart()) {
          elevatorState = ElevatorState.MANUAL;
        } else if(OI.getSecondaryBack()){
          elevatorState = ElevatorState.ZERO;
        } else if(isIntakeMode()) {
          if(OI.getSecondaryRT()) elevatorState = ElevatorState.INTAKEHATCH;
          else if(OI.getSecondaryLT()) elevatorState = ElevatorState.INTAKEBALL;
          else elevatorState = ElevatorState.INTAKE;
        } else if(OI.getSecondaryDPad() != -1) {
          elevatorState = ElevatorState.HOLD;
        }
        else if(isHatchHeightMode() || isOrangeHeightMode()){
          if(OI.getSecondaryLB() || OI.getSecondaryRB()) {
            if(hasHatch) {
              lowerHatch = HATCH_OUTTAKE_CONSTANT;
              runHatchOuttake();
            } else if(hasOrange) {
              outtakeOrange = true;
            }
          } else {
            lowerHatch = 0;
            toHoldFromIntakeTimer = System.currentTimeMillis();
          }
        } 

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

        if((!elevatorZeroed || !wristZeroed) && elevatorState != ElevatorState.ZERO) elevatorState = ElevatorState.MANUAL;
        
        if(elevatorState == ElevatorState.MANUAL) {
          if(isElevatorButtonPressed() && OI.getSecondaryLeftYAxis() >= 0) RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
          else RobotMap.elevatorTop.set(ControlMode.PercentOutput, OI.getSecondaryLeftYAxis());
          
          if(isWristButtonPressed() && OI.getSecondaryRightYAxis() <= 0) RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
          else RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getSecondaryRightYAxis() * 0.3);
          
          if(OI.getSecondaryLT()) runBallIntake();
          else if(OI.getSecondaryRT()) runHatchIntake();
        } else if(elevatorState == ElevatorState.ZERO) {
          if(isElevatorButtonPressed()){
            elevatorState = ElevatorState.MANUAL;
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
            RobotMap.elevatorTop.getSensorCollection().setQuadraturePosition(0, 10);
            System.out.println("Elevator has been rezeroed!");
          } else {
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.2);
          }
        } else {
          if(lastState != ElevatorState.HOLD && ((!isForbiddenOrangeIn() && isHatchIn() && (lastState == ElevatorState.INTAKEHATCH))) ||
            (isForbiddenOrangeIn() && !isHatchIn() && ((lastState == ElevatorState.INTAKEBALL))))
            elevatorState = ElevatorState.HOLD;
  
          if(isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(), elevatorState.getClawPosition() - 100, 
             elevatorState.getClawPosition() + 100) && (elevatorState == ElevatorState.HOLD  || isHatchHeightMode()) && !isForbiddenOrangeIn()) {
            extendGhosts = true;
          } else {
            extendGhosts = false;
          }

          if(isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(), elevatorState.getClawPosition() - 100, elevatorState.getClawPosition() + 100) && isIntakeMode()) {
            holdGroundMode = true;
          } else {
            holdGroundMode = false;
          }

          if(isForbiddenOrangeIn() && !hasSeenOrange){
            timerBallTime = System.currentTimeMillis();
            hasSeenOrange = true;
            hasOrange = true;
          }

          if(isHatchIn() && !activateHoldTimer && elevatorState == ElevatorState.INTAKEHATCH) {
            activateIntakeTimer = true;
            spinHatchIntakeTimer = System.currentTimeMillis();
          }

          if((isHatchIn() && !isForbiddenOrangeIn()) && System.currentTimeMillis() - spinHatchIntakeTimer >= 500) {
            hasHatch = true;
            hasOrange = false;
            hasSeenOrange = false;
            activateHoldTimer = false;
          }

          if((!isHatchIn()) || (isHatchIn() && lastState != elevatorState)) lowerHatch = 0;

          if(elevatorState == ElevatorState.INTAKEBALL) {
            if(hasSeenOrange && elevatorState == ElevatorState.INTAKEBALL && System.currentTimeMillis() - timerBallTime >= 300) {
              // Ball is fully in
              hasOrange = false;
              hasSeenOrange = false;
              stopIntakeWheels();
            } else {
              runBallIntake();
            }
          } else if(elevatorState == ElevatorState.INTAKEHATCH) {
            runHatchIntake();
          } else if(outtakeOrange) {
            runBallOuttake();
          } else {
            stopIntakeWheels();
          }

          if(hasSeenOrange && elevatorState != ElevatorState.INTAKEBALL) {
            hasSeenOrange = false;
            timerBallTime = System.currentTimeMillis();
          }

          RobotMap.traumatizedGhosts.set(extendGhosts);
          
          RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight() - lowerHatch); 

          if(lastState != elevatorState) {
            if(lastState.clawPosition <= elevatorState.clawPosition) RobotMap.wristControl.selectProfileSlot(0, 0);
            else RobotMap.wristControl.selectProfileSlot(1, 0);
          }
          
          if(!holdGroundMode) {
            RobotMap.wristControl.configMotionCruiseVelocity(CLAW_VEL);
            RobotMap.wristControl.configMotionAcceleration(CLAW_ACCEL);
            RobotMap.wristControl.set(ControlMode.MotionMagic, elevatorState.getClawPosition());
          } else {
            RobotMap.wristControl.set(ControlMode.Current, 0.5);
          }
        }  
        lastState = elevatorState;
        System.out.println(elevatorState.name());
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
    RobotMap.wristControl.set(ControlMode.PercentOutput, 0.0);
    RobotMap.traumatizedGhosts.set(false);
    stopIntakeWheels();
    elevatorState = ElevatorState.MANUAL;
    lastState = ElevatorState.MANUAL;
    elevatorZeroed = false;
    wristZeroed = false;
    holdGroundMode = false;
    extendGhosts = false;
    hasSeenOrange = false;
    hasOrange = false;
    hasHatch = false; 
    outtakeOrange = false;
  }

  private static boolean isElevatorButtonPressed() {
    return !RobotMap.zeroThyElevator.get();
  }

  private static boolean isWristButtonPressed(){
    return !RobotMap.zeroThyWrist.get();
  }

  private static boolean isForbiddenOrangeIn(){
    return !RobotMap.forbiddenOrange.get();
  }

  private static boolean isHatchIn(){
    return !RobotMap.hatchDetector.get();
  }

  private static boolean isWithinThreshold(double val, int minThresh, int maxThresh) {
    return minThresh <= val && val <= maxThresh;
  }

  private static void runHatchIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.5);
  }

  private static void runBallIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, -0.4);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -0.4);
  }

  private static void runBallOuttake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 1.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 1.0);
  }

  private static void stopIntakeWheels(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.0);
  }

  private static void runHatchOuttake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -1.0);
  }

  private static boolean isIntakeMode(){
    return elevatorState == ElevatorState.INTAKE || elevatorState == ElevatorState.INTAKEBALL || elevatorState == ElevatorState.INTAKEHATCH; 
  }

  private static boolean isHatchHeightMode(){
    return elevatorState == ElevatorState.HATCH1 || elevatorState == ElevatorState.HATCH2 || elevatorState == ElevatorState.HATCH3;
  }

  private static boolean isOrangeHeightMode(){
    return elevatorState == ElevatorState.BALL1 || elevatorState == ElevatorState.BALL2 || elevatorState == ElevatorState.BALL3;
  }
}