    
/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class Outtake extends Command {
  private Elevator elevator;

  public Outtake() {
    this.elevator = Elevator.getInstance();
    requires(this.elevator);
    setTimeout(1.0);
  }

  @Override
  protected void initialize() {
    
  }

  @Override
  protected void execute() {
    RobotMap.elevatorTop.configMotionCruiseVelocity(ElevatorState.HATCH3.getVel(), 10);
    RobotMap.elevatorTop.configMotionAcceleration(ElevatorState.HATCH3.getAccel(), 10);
    RobotMap.elevatorTop.set(ControlMode.MotionMagic, ElevatorState.HATCH3.getElevatorHeight()); 
    RobotMap.wristControl.configMotionCruiseVelocity(Elevator.CLAW_VEL);
    RobotMap.wristControl.configMotionAcceleration(Elevator.CLAW_ACCEL);
    RobotMap.wristControl.set(ControlMode.MotionMagic, Elevator.elevatorState.getClawPosition() + 1500);
    Elevator.runHatchOuttake();
  }

  @Override
  protected boolean isFinished() {
    return isTimedOut();
  }

  @Override
  protected void end() {
    System.out.println("OUTTOOK FINISHED");
  }

  @Override
  protected void interrupted() {
  }
}