/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
// START AT LOWEST POINT W/ GHOSTS OUT, DRIVE ELE UP TO ROCKET 1 HATCH HEIGHT, DRIVEN AWAY THINGY
package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.OI;
import frc.robot.RobotMap;

public class Elevator extends Subsystem {
  private static Elevator instance;
  public static ElevatorState elevatorState = ElevatorState.ZERO;
  private static ElevatorState lastState = ElevatorState.ZERO;
  private static boolean elevatorZeroed = false;
  private static boolean wristZeroed = false; 
  private static boolean holdGroundMode = false;
  private static boolean extendGhosts = false;
  private static boolean hasOrange = false;
  private static boolean hasHatch = false;
  private static boolean outtakeOrange = false;
  
  private static boolean humanHatchMode = false;
  private static boolean hasHatchFromHuman = false;
  private static final int CLAW_VEL = 700;
  private static final int CLAW_ACCEL = 900;
  private static int lowerHatch = 0;
  private static int HATCH_OUTTAKE_CONSTANT = -1500;

  private static boolean hasIntookHatch = false;

  private static boolean hasSeenOrange = false;
  private static double timerBallTime = System.currentTimeMillis();

  private static double timerHatchTime = System.currentTimeMillis();
  
  private static boolean returnToHold = false;
  private static double timerHoldTime = System.currentTimeMillis();

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
    HOLD(-3500, 6000, 6000, 920),
    INTAKE(-5875, 6000, 6000, 5240),
    INTAKEBALL(-5875, 6000, 6000, 5240),
    INTAKEHATCH(-5875, 6000, 6000, 5240),
    INTAKEHATCH1(-11850, 6000, 8000, 4115),
    INTAKEHUMANHATCH(-2425, 3000, 6000, 4115),
    INTAKEHUMANBALL(-10025, 5000, 9000, 5240),
    HATCH1(-11850, 10000, 10000, 4115),
    HATCH2(-52000, 10000, 12000, 4115),
    HATCH3(-94830, 10000, 15000, 4115),
    BALL1(-21983, 10000, 10000, 4115),
    BALL2(-66900, 10000, 12000, 4115),
    BALL3(-97195, 10000, 15000, 3310),
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
          elevatorZeroed = false;
        }

        if(isWristButtonPressed()) {
          RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
          System.out.println("Wrist has been zeroed!");
          wristZeroed = true;
        } else {
          System.out.println("Please zero the wrist!!");
          wristZeroed = false;
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
        // LEFT BUMPER = BALL OUTTAKE ^^ 
        // RIGHT BUMPER = HATCH OUTTAKE ^^
        // START = MANUAL
        // MANUAL + RIGHT TRIGGER = WHEELS SPIN FOR HATCH
        // MANUAL + LEFT TRIGGER = WHEELS SPIN FOR BALL
        // A + LEFT TRIGGER = GROUND BALL INTAKE
        // B + LEFT TRIGGER = HUMAN PLAYER STATION INTAKE
        // BACK = ZERO ELEVATOR

        if (OI.getSecondaryA()) {
          if(lastState != ElevatorState.INTAKEBALL && lastState != ElevatorState.INTAKEHATCH) elevatorState = ElevatorState.INTAKE;
        } else if(OI.getSecondaryB()) {
          if(OI.getSecondaryRT()) {
            elevatorState = ElevatorState.INTAKEHUMANHATCH;
            humanHatchMode = true;
          } else {
            if(!humanHatchMode){
              if(hasHatch) elevatorState = ElevatorState.HATCH1;
              else if(hasOrange && isForbiddenOrangeIn()) elevatorState = ElevatorState.BALL1; 
              else elevatorState = ElevatorState.HATCH1;
            } else {
              elevatorState = ElevatorState.INTAKEHATCH1;
              humanHatchMode = true;
            }
          }
        } else if(OI.getSecondaryX()) {
          if(hasHatch) elevatorState = ElevatorState.HATCH2;
          else if(hasOrange && isForbiddenOrangeIn()) elevatorState = ElevatorState.BALL2; 
          else elevatorState = ElevatorState.HATCH2;
        } else if(OI.getSecondaryY()) {
          if(hasHatch) elevatorState = ElevatorState.HATCH3;
          else if(hasOrange && isForbiddenOrangeIn()) elevatorState = ElevatorState.BALL3; 
          else elevatorState = ElevatorState.HATCH3;
        } else if(OI.getSecondaryStart()) {
          elevatorState = ElevatorState.MANUAL;
        } else if(OI.getSecondaryBack()){
          elevatorState = ElevatorState.ZERO;
        } if(isGroundIntakeMode()) {
          if(OI.getSecondaryRT()) elevatorState = ElevatorState.INTAKEHATCH;
          else if(OI.getSecondaryLT()) elevatorState = ElevatorState.INTAKEBALL;
          else elevatorState = ElevatorState.INTAKE;
        } else if(OI.getSecondaryDPad() != -1) {
          elevatorState = ElevatorState.HOLD;
        } else if(OI.getPrimaryRT()){          
          lowerHatch = HATCH_OUTTAKE_CONSTANT;
          runHatchOuttake();
          hasHatchFromHuman = false;
          hasHatch = false;
          System.out.println("Outtaking!!");
        } 

        if(!OI.getPrimaryRT()){
          lowerHatch = 0;
          stopIntakeWheels();
        }

        outtakeOrange = OI.getPrimaryLT();

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
          else if(OI.getSecondaryRT()) runHumanHatchIntake();
        } else if(elevatorState == ElevatorState.ZERO) {
          if(isElevatorButtonPressed()){
            elevatorZeroed = true;
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
            RobotMap.elevatorTop.getSensorCollection().setQuadraturePosition(0, 10);
            System.out.println("Elevator has been rezeroed!");
            if(isWristButtonPressed()){
              wristZeroed = true;
              RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
              RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
              System.out.println("Wrist has been rezeroed!");
              elevatorState = ElevatorState.HOLD;
            } else {
              RobotMap.wristControl.set(ControlMode.PercentOutput, -0.3);
            }
          } else {
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.3);
          }
        } else {
          if((isHatchHeightMode() || isOrangeHeightMode()) && (hasNothing() || hasHatchFromHuman) && !returnToHold) {
            returnToHold = true;
            timerHoldTime = System.currentTimeMillis();
          } else if(returnToHold && OI.getPrimaryLeftYAxis() < 0) {
            returnToHold = false;
            lowerHatch = 0;
            extendGhosts = false;
          }

          if(returnToHold && System.currentTimeMillis() - timerHoldTime >= 250) {
            elevatorState = ElevatorState.HOLD;
            returnToHold = false;
          }

          if((lastState == ElevatorState.INTAKEHATCH1 && hasHatchNotOrange()) || (hasHatchNotOrange() && lastState == ElevatorState.INTAKEHATCH && hasIntookHatch) || (hasOrangeNotHatch() && lastState == ElevatorState.INTAKEBALL) 
             && !(isHatchHeightMode() || isOrangeHeightMode())) {
            elevatorState = ElevatorState.HOLD;
            hasIntookHatch = false;
            humanHatchMode = false;
          }
            
          if((isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(), elevatorState.getClawPosition() - 100, 
             elevatorState.getClawPosition() + 100) && !hasOrangeNotHatch() && !hasNothing() && elevatorState == ElevatorState.HOLD) || isHatchHeightMode() || elevatorState == ElevatorState.INTAKEHATCH1) {
            extendGhosts = true;
          } else if(lowerHatch != 0 || isGroundIntakeMode() || hasNothing()){
            extendGhosts = false;
          }

          if(isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(), elevatorState.getClawPosition() - 200, elevatorState.getClawPosition() + 200) && isGroundIntakeMode()) {
            holdGroundMode = true;
          } else {
            holdGroundMode = false;
          }

          if(hasOrangeNotHatch() && !hasSeenOrange){
            timerBallTime = System.currentTimeMillis();
            hasSeenOrange = true;
            hasOrange = true;
          }

          if(hasHatchNotOrange() && !hasHatch){
            timerHatchTime = System.currentTimeMillis();
            hasHatch = true;
            System.out.println("Broxah");
          }

          if(elevatorState == ElevatorState.INTAKEBALL) {
            if(hasSeenOrange && System.currentTimeMillis() - timerBallTime >= 300) {
              // Ball is fully in
              hasOrange = true;
              hasSeenOrange = false;
              stopIntakeWheels();
            } else {
              runBallIntake();
            }
          } else if(elevatorState == ElevatorState.INTAKEHATCH) {
            if(hasHatch && System.currentTimeMillis() - timerHatchTime >= 400) {
              // Hatch is fully in
              stopIntakeWheels();
              hasIntookHatch = true;
              System.out.println("THIS RAN");
            } else {
              runGroundHatchIntake();
            }
          } else if(elevatorState == ElevatorState.INTAKEHATCH1) {
            runHumanHatchIntake();
          } else if(outtakeOrange) {
            runBallOuttake();
            hasOrange = false;
          } else if(lowerHatch == 0 || elevatorState == ElevatorState.HOLD) {
            stopIntakeWheels();
          }

          if(hasSeenOrange && elevatorState != ElevatorState.INTAKEBALL) hasSeenOrange = false;

          RobotMap.traumatizedGhosts.set(extendGhosts);
          
          RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
          RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
          RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight()); 

          if(lastState != elevatorState) {
            if(lastState.clawPosition <= elevatorState.clawPosition) RobotMap.wristControl.selectProfileSlot(0, 0);
            else RobotMap.wristControl.selectProfileSlot(1, 0);
          }
          
          if(!holdGroundMode) { 
            RobotMap.wristControl.configMotionCruiseVelocity(CLAW_VEL);
            RobotMap.wristControl.configMotionAcceleration(CLAW_ACCEL);
            RobotMap.wristControl.set(ControlMode.MotionMagic, elevatorState.getClawPosition() - lowerHatch);
          } else {
            RobotMap.wristControl.set(ControlMode.Current, 0.5);
          }
        }  
        lastState = elevatorState;
        System.out.println(elevatorState.name() + " " + isHatchIn());
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
    elevatorState = ElevatorState.ZERO;
    lastState = ElevatorState.ZERO;
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

  private static void runGroundHatchIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.5);
  }

  private static void runBallIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, -1.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -1.0);
  }

  private static void runBallOuttake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0.8);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.8);
  }

  private static void stopIntakeWheels(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.0);
  }

  private static void runHatchOuttake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 1.0);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -1.0);
  }

  private static void runHumanHatchIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, -0.5);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.5);
  }

  private static boolean isGroundIntakeMode(){
    return elevatorState == ElevatorState.INTAKE || elevatorState == ElevatorState.INTAKEBALL || elevatorState == ElevatorState.INTAKEHATCH; 
  }

  private static boolean isHatchHeightMode(){
    return elevatorState == ElevatorState.HATCH1 || elevatorState == ElevatorState.HATCH2 || elevatorState == ElevatorState.HATCH3;
  }

  private static boolean isOrangeHeightMode(){
    return elevatorState == ElevatorState.BALL1 || elevatorState == ElevatorState.BALL2 || elevatorState == ElevatorState.BALL3;
  }

  private static boolean hasHatchNotOrange(){
    return !isForbiddenOrangeIn() && isHatchIn();
  }

  private static boolean hasOrangeNotHatch(){
    return isForbiddenOrangeIn() && !isHatchIn();
  }

  private static boolean hasNothing(){
    return !isForbiddenOrangeIn() && !isHatchIn();
  }
}