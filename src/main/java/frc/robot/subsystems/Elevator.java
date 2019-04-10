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
import frc.robot.LimelightVision;
import frc.robot.OI;
import frc.robot.ParadoxTimer;
import frc.robot.RobotMap;

public class Elevator extends Subsystem {
  private static Elevator instance;

  private static final int CLAW_VEL = 900;
  private static final int CLAW_ACCEL = 2200;

  public static ElevatorState elevatorState = ElevatorState.ZERO;
  private static ElevatorState lastState = ElevatorState.ZERO;
  private static String lastIntakeItem = "HATCH";
  
  private static boolean elevatorZeroed = false;
  private static boolean wristZeroed = false; 
  private static boolean holdGroundMode = false;
  private static boolean humanHatchMode = false;
  private static boolean lockWristToggle = false;
  private static boolean lockWristMode = false;
  private static double holdWristClimb = 0.0;

  private static boolean beastEle = false;
  private static boolean beastWrist = false;

  private static int lowerHatch = 0;
  private static int HATCH_OUTTAKE_CONSTANT = -1500;

  private static ParadoxTimer ballIntakeTimer = new ParadoxTimer();
  private static ParadoxTimer hatchTimer = new ParadoxTimer();
  private static ParadoxTimer humanHatchIntakeTimer = new ParadoxTimer();

  private static boolean beastToggle = false;
  public static Elevator getInstance() {
    return instance == null ? instance = new Elevator() : instance;
  }

  public enum ElevatorState {
    // No, I don't know why the positions have to be negative. 
    // Yes, I have tried reversing stuff. 
    // No, don't make the velocities and accelerations negative.
    // Yes, that messes everything up.

    // Elevator Height, Vel, Accel, Claw Position (All of these units are in native encoder units)
    // Ghost state 
    
    MANUAL(0, 0, 0, 0, false),
    ZERO(0, 0, 0, 0, false),
    BEAST(100, 10000, 16000, 100, false),
    HOLDDEF(-3500, 10000, 17000, 920, false),
    HOLDHATCH1(-3500, 10000, 17000, 200, true),
    HOLDHATCH2(-3500, 10000, 17000, 920, true),

    INTAKE(-6200, 10000, 22000, 5240, false),
    INTAKEBALLGROUND(-6200, 10000, 22000, 5240, false),
    INTAKEBALLUP(-10075, 10000, 22000, 5240, false),
    INTAKEHATCHGROUND(-6200, 10000, 22000, 5240, false),

    INTAKEHUMANHATCH1(-5425, 10000, 15000, 4185, false),
    INTAKEHUMANHATCH2(-10850, 10000, 15000, 4185, true),
    // INTAKEHUMANBALL(-10025, 5000, 9000, 5240),

    CARGOBALL(-52983, 10000, 20000, 4800, false),
    HATCH1(-9200, 10000, 39000, 4195, true),
    HATCH2(-55800, 10000, 40000, 4195, true),
    HATCH3(-95930, 10000, 41000, 4195, true),
    BALL1(-21983, 10000, 39000, 4115, false),
    BALL2(-64900, 10000, 40000, 4115, false),
    BALL3(-97195, 10000, 41000, 3510, false);

    private final int elevatorHeight;
    private final int vel;
    private final int accel;
    private final int clawPosition;
    private final boolean extendGhosts;

    ElevatorState(int elevatorHeight, int vel, int accel, int clawPosition, boolean extendGhosts) {
      this.elevatorHeight = elevatorHeight;
      this.vel = vel;
      this.accel = accel;
      this.clawPosition = clawPosition;
      this.extendGhosts = extendGhosts;
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

    private boolean getExtendGhosts(){
      return extendGhosts;
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
      }

      protected void execute() {
         // SECONDARY CONTROLS
        // A = GROUND (NO MOVING WHEELS)
        // B = LEVEL 1 (HATCH/BALL)
        // X = LEVEL 2 (HATCH/BALL)
        // Y = LEVEL 3 (HATCH/BALL)
        // A + RIGHT TRIGGER = GROUND HATCH INTAKE
        // A + LEFT TRIGGER = GROUND BALL INTAKE
        // A + LEFT BUMPER = ELEVATED BALL GROUND
        // B + RIGHT TRIGGER =  HUMAN PLAYER STATION INTAKE (RELEASE TRIGGER FOR STAGE 2)
        // B + RIGHT BUMPER = CARGO BALL
        // START = MANUAL
        // MANUAL + RIGHT TRIGGER = WHEELS SPIN FOR HATCH
        // MANUAL + LEFT TRIGGER = WHEELS SPIN FOR BALL
        // BACK = ZERO ELEVATOR
        // DPAD = HOLD MODE
        // 
        // PRIMARY CONTROLS
        // LEFT TRIGGER = BALL OUTTAKE ^^ 
        // RIGHT TRIGGER = HATCH OUTTAKE ^^
        // RIGHT BUMPER = VISION MODE FOR DRIVE
        // LEFT BUMPER = SLOW DRIVE
        // PRIMARY A + Y = DRIVE SHIFTERS
        // DPAD? --> BEAST MODE
        
        if(OI.getPrimaryStartPressed()) beastToggle = !beastToggle;

        if(beastToggle) elevatorState = ElevatorState.BEAST;
        else if(elevatorState == ElevatorState.BEAST && !beastToggle) elevatorState = ElevatorState.MANUAL;

        if(elevatorState != ElevatorState.MANUAL) lockWristMode = false;

        if(elevatorState != ElevatorState.BEAST) {
          if (OI.getSecondaryA()) {
            if(elevatorState != ElevatorState.INTAKEBALLGROUND && elevatorState != ElevatorState.INTAKEBALLUP && elevatorState != ElevatorState.INTAKEHATCHGROUND) elevatorState = ElevatorState.INTAKE;
          } else if(OI.getSecondaryB()) {
            if(OI.getSecondaryRT()) {
              elevatorState = ElevatorState.INTAKEHUMANHATCH1;
              humanHatchMode = true;
            } else {
              if(!humanHatchMode){
                if(isHatchIn() || (lastIntakeItem == "HATCH" && !isForbiddenOrangeIn())) elevatorState = ElevatorState.HATCH1;
                else if(isForbiddenOrangeIn() || lastIntakeItem == "BALL") elevatorState = ElevatorState.BALL1; 
              } else {
                elevatorState = ElevatorState.INTAKEHUMANHATCH2;
                humanHatchMode = true;
              }
            }
          } else if(OI.getSecondaryX()) {
            if(isHatchIn() || (lastIntakeItem == "HATCH" && !isForbiddenOrangeIn())) elevatorState = ElevatorState.HATCH2;
            else if(isForbiddenOrangeIn() || lastIntakeItem == "BALL") elevatorState = ElevatorState.BALL2; 
          } else if(OI.getSecondaryY()) {
            if(isHatchIn() || (lastIntakeItem == "HATCH" && !isForbiddenOrangeIn())) elevatorState = ElevatorState.HATCH3;
            else if(isForbiddenOrangeIn() || lastIntakeItem == "BALL") elevatorState = ElevatorState.BALL3; 
          } else if(OI.getSecondaryStart()) {
            if(lockWristMode && elevatorState == ElevatorState.MANUAL) { 
              lockWristMode = false;
              lockWristToggle = false;
            }
            elevatorState = ElevatorState.MANUAL;
          } else if(OI.getSecondaryBack()){
            elevatorState = ElevatorState.ZERO;
            elevatorZeroed = false;
            wristZeroed = false;
          } else if(OI.getSecondaryDPad() != -1) {
            if(isHatchIn() || (lastIntakeItem == "HATCH" && !isHatchHeightMode())) elevatorState = ElevatorState.HOLDHATCH1;
            else elevatorState = ElevatorState.HOLDDEF;
          } else if(OI.getSecondaryRB()) {
            elevatorState = ElevatorState.CARGOBALL;
          } 

          if(!OI.getPrimaryRT()){
            lowerHatch = 0;
            stopIntakeWheels();
          }
          
          if(!OI.getPrimaryLT() && (isOrangeHeightMode() || elevatorState == ElevatorState.CARGOBALL)) stopIntakeWheels();
          
          // Intakes and Outtakes
          if(OI.getPrimaryRT()){
            lowerHatch = HATCH_OUTTAKE_CONSTANT;
            runHatchOuttake();
          } else if(OI.getPrimaryLT()) {
            runBallOuttake();
          } else if(isGroundIntakeMode()) {
            if(OI.getSecondaryRT()) {
              elevatorState = ElevatorState.INTAKEHATCHGROUND;
            } else if(OI.getSecondaryLT()) { 
              elevatorState = ElevatorState.INTAKEBALLGROUND;
            } else if(OI.getSecondaryLB()) {
              elevatorState = ElevatorState.INTAKEBALLUP;
            } else elevatorState = ElevatorState.INTAKE;
          } 
        } // beast mode disable effect ends here

        if(elevatorState == ElevatorState.BEAST) LimelightVision.setBlink(2);
        else LimelightVision.setBlink(0);
        
        if(elevatorState == ElevatorState.MANUAL) {
          if(isElevatorButtonPressed() && OI.getSecondaryLeftYAxis() >= 0) RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
          else RobotMap.elevatorTop.set(ControlMode.PercentOutput, OI.getSecondaryLeftYAxis());
          
          if(OI.getSecondaryRightAxisButtonPressed()) {
            lockWristToggle = !lockWristToggle;
            holdWristClimb = RobotMap.wristControl.getSelectedSensorPosition(0);
          }
          
          if(lockWristToggle) lockWristMode = true;
          else if(lockWristMode && !lockWristToggle) lockWristMode = false; 

          if(lockWristMode && holdWristClimb != 0.0){
            RobotMap.wristControl.set(ControlMode.Position, holdWristClimb);
          } else {
            if(isWristButtonPressed() && OI.getSecondaryRightYAxis() <= 0) RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
            else RobotMap.wristControl.set(ControlMode.PercentOutput, OI.getSecondaryRightYAxis() * 0.3);  
          }
          
          if(OI.getPrimaryLT()) runBallOuttake();
          else if(OI.getPrimaryRT()) {
            runHatchOuttake();
            RobotMap.traumatizedGhosts.set(true);
          }
          else if(OI.getSecondaryLT()) runBallIntake();
          else if(OI.getSecondaryRT()) runHumanHatchIntake();
          else RobotMap.traumatizedGhosts.set(false);
        } else if(elevatorState == ElevatorState.ZERO) {
          if(isElevatorButtonPressed() && !elevatorZeroed){
            elevatorZeroed = true;
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
            RobotMap.elevatorTop.getSensorCollection().setQuadraturePosition(0, 10);
            System.out.println("Elevator has been rezeroed!");
          } else {
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.5);
          }

          if(isWristButtonPressed() && elevatorZeroed && !wristZeroed){
            wristZeroed = true;
            RobotMap.wristControl.set(ControlMode.PercentOutput, 0);
            RobotMap.wristControl.getSensorCollection().setQuadraturePosition(0, 10);
            System.out.println("Wrist has been rezeroed!");
          } else {
            RobotMap.wristControl.set(ControlMode.PercentOutput, -0.5);
          }

          if(elevatorZeroed && wristZeroed){
            if(isHatchIn()) {
              elevatorState = ElevatorState.HOLDHATCH2;
            } else {
              elevatorState = ElevatorState.HOLDDEF;
            }
          }
        } else if(elevatorState == ElevatorState.BEAST) {
          if(isElevatorButtonPressed() || beastEle) {
            beastEle = true;
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.0);
          } else {
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.5);
          }

          if(isWristButtonPressed() || beastWrist) {
            beastWrist = true;
            RobotMap.elevatorTop.set(ControlMode.PercentOutput, 0.0);
          } else {
            RobotMap.wristControl.set(ControlMode.PercentOutput, -0.5);
          }

        } else {
          updateLastIntakeItem();

          if(!(isHatchHeightMode() || isOrangeHeightMode()) && !isHoldMode()){
            if((isHatchIn() && elevatorState == ElevatorState.INTAKEHATCHGROUND) || (isHatchIn() && elevatorState == ElevatorState.INTAKEHUMANHATCH2)) {
              elevatorState = ElevatorState.HOLDHATCH1;
            } else if((isForbiddenOrangeIn() && (elevatorState == ElevatorState.INTAKEBALLGROUND || elevatorState == ElevatorState.INTAKEBALLUP))){
              elevatorState = ElevatorState.HOLDDEF;
            }
          }

          holdGroundMode = isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(), elevatorState.getClawPosition() - 250, elevatorState.getClawPosition() + 250) && (elevatorState == ElevatorState.INTAKEHATCHGROUND || elevatorState == ElevatorState.INTAKEBALLGROUND);
          
          if(isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(0), elevatorState.getClawPosition() - 200, elevatorState.getClawPosition() + 200) && elevatorState == ElevatorState.HOLDHATCH1) elevatorState = ElevatorState.HOLDHATCH2;

          if(isForbiddenOrangeIn() && !ballIntakeTimer.isEnabled() && isIntakingOrange()) ballIntakeTimer.enableTimer(System.currentTimeMillis());

          if(isHatchIn() && !hatchTimer.isEnabled() && elevatorState == ElevatorState.INTAKEHATCHGROUND) hatchTimer.enableTimer(System.currentTimeMillis());

          if(elevatorState == ElevatorState.INTAKEBALLUP || elevatorState == ElevatorState.INTAKEBALLGROUND) {
            if(ballIntakeTimer.isEnabled() && ballIntakeTimer.hasTimeHasPassed(300, System.currentTimeMillis())) {
              stopIntakeWheels(); // Ball is fully in
            } else {
              runBallIntake();
            }
          } else if(elevatorState == ElevatorState.INTAKEHATCHGROUND) {
            if(hatchTimer.isEnabled() && hatchTimer.hasTimeHasPassed(400, System.currentTimeMillis())) {
              stopIntakeWheels(); // Hatch is fully in
            } else {
              runGroundHatchIntake();
            }
          } else if(elevatorState == ElevatorState.INTAKEHUMANHATCH2) {
            runHumanHatchIntake();
            if(humanHatchIntakeTimer.isEnabled()) humanHatchIntakeTimer.enableTimer(System.currentTimeMillis());
          } else if((isOrangeHeightMode() || elevatorState == ElevatorState.HOLDDEF || elevatorState == ElevatorState.CARGOBALL) && OI.getSecondaryLT()) {
            runBallIntake();
          } else if((!isHatchHeightMode() && !isOrangeHeightMode() && elevatorState != ElevatorState.CARGOBALL) || isHoldMode()) {
            stopIntakeWheels();
            humanHatchIntakeTimer.disableTimer();
          } else {
            ballIntakeTimer.disableTimer();
            hatchTimer.disableTimer();
          }

          if(elevatorState != ElevatorState.INTAKEHUMANHATCH2 && elevatorState != ElevatorState.INTAKEHUMANHATCH1) humanHatchMode = false;
          
          RobotMap.traumatizedGhosts.set(elevatorState.getExtendGhosts());

          if(elevatorState != ElevatorState.INTAKEHUMANHATCH2 || (humanHatchIntakeTimer.hasTimeHasPassed(800, System.currentTimeMillis()) && elevatorState == ElevatorState.INTAKEHUMANHATCH2)) {
            RobotMap.elevatorTop.configMotionCruiseVelocity(elevatorState.getVel(), 10);
            RobotMap.elevatorTop.configMotionAcceleration(elevatorState.getAccel(), 10);
            RobotMap.elevatorTop.set(ControlMode.MotionMagic, elevatorState.getElevatorHeight()); 
          }

          if(lastState != elevatorState) {
            if(lastState.clawPosition <= elevatorState.clawPosition) RobotMap.wristControl.selectProfileSlot(0, 0);
            else RobotMap.wristControl.selectProfileSlot(1, 0);
          }

          if(!holdGroundMode) { 
            RobotMap.wristControl.configMotionCruiseVelocity(CLAW_VEL);
            RobotMap.wristControl.configMotionAcceleration(CLAW_ACCEL);
            RobotMap.wristControl.set(ControlMode.MotionMagic, elevatorState.getClawPosition() - lowerHatch);
          } else {
            RobotMap.wristControl.set(ControlMode.Current, 0.55);
          }
        }  

        lastState = elevatorState;
        // System.out.println(elevatorState.name() + " WRIST ENC " + RobotMap.wristControl.getSelectedSensorPosition(0) + " " + RobotMap.elevatorTop.getSelectedSensorPosition(0) + " " + elevatorState.getClawPosition() + " " + wristZeroed + " " + elevatorZeroed + " " + OI.getSecondaryBack());
        // System.out.println("WRIST BUTTON " + RobotMap.zeroThyWrist.get());

        SmartDashboard.putNumber("Elevator Height", RobotMap.elevatorTop.getSelectedSensorPosition());
        SmartDashboard.putString("Elevator State", elevatorState.name());
        SmartDashboard.putBoolean("HATCH IN? ", isHatchIn());
        SmartDashboard.putBoolean("BALL IN? ", isForbiddenOrangeIn());
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
    humanHatchMode = false;
    beastEle = false;
    beastWrist = false;
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
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.64);
  }

  private static void runBallIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, -0.5);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -1.0);
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
    RobotMap.intakeTop.set(ControlMode.PercentOutput, 0.75);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, -0.75);
  }

  private static void runHumanHatchIntake(){
    RobotMap.intakeTop.set(ControlMode.PercentOutput, -0.65);
    RobotMap.intakeBot.set(ControlMode.PercentOutput, 0.65);
  }

  private static boolean isIntakingOrange(){
    return elevatorState == ElevatorState.INTAKEBALLGROUND || elevatorState == ElevatorState.INTAKEBALLUP;
  }

  private static boolean isGroundIntakeMode(){
    return elevatorState == ElevatorState.INTAKE || elevatorState == ElevatorState.INTAKEBALLUP || elevatorState == ElevatorState.INTAKEBALLGROUND || elevatorState == ElevatorState.INTAKEHATCHGROUND; 
  }

  private static boolean isIntaking(){
    return elevatorState == ElevatorState.INTAKEHATCHGROUND || elevatorState == ElevatorState.INTAKEHUMANHATCH2 || elevatorState == ElevatorState.INTAKEBALLGROUND || elevatorState == ElevatorState.INTAKEBALLUP;
  }

  private static boolean isHatchHeightMode(){
    return elevatorState == ElevatorState.HATCH1 || elevatorState == ElevatorState.HATCH2 || elevatorState == ElevatorState.HATCH3;
  }

  private static boolean isOrangeHeightMode(){
    return elevatorState == ElevatorState.BALL1 || elevatorState == ElevatorState.BALL2 || elevatorState == ElevatorState.BALL3;
  }

  private static boolean isHoldMode(){
    return elevatorState == ElevatorState.HOLDDEF || elevatorState == ElevatorState.HOLDHATCH1 || elevatorState == ElevatorState.HOLDHATCH2;
  }

  private static void updateLastIntakeItem(){
    if(isIntaking()) {
      lastIntakeItem = isIntakingOrange() ? "BALL" : "HATCH";
    } 
  }
}