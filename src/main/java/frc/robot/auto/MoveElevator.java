/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class MoveElevator extends Command {
  ElevatorState elevatorState;
  private boolean score = false;
  public MoveElevator(ElevatorState eleState) {
    this.elevatorState = eleState;
  }

  public MoveElevator(ElevatorState eleState, boolean score) {
    this.elevatorState = eleState;
    this.score = true;
  }

  @Override
  protected void initialize() {
    Elevator.elevatorState = this.elevatorState;
    LimelightVision.setVisionProcessingMode();
    setTimeout(0.9);
  }

  @Override
  protected void execute() {
		if(score) {
			Elevator.lowerHatch = Elevator.HATCH_OUTTAKE_CONSTANT;
			Elevator.runHatchOuttake();
		}
  }

  @Override
  protected boolean isFinished() {
    if(score) return isTimedOut();
    return Constants.isWithinThreshold(RobotMap.elevatorTop.getSelectedSensorPosition(0), Elevator.elevatorState.getElevatorHeight() - 200, Elevator.elevatorState.getElevatorHeight() + 200) && 
           Constants.isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(0), Elevator.elevatorState.getClawPosition() - 100, Elevator.elevatorState.getClawPosition() + 100);
  }

  @Override
  protected void end() {
    Elevator.stopIntakeWheels();
    Elevator.lowerHatch = 0;
    this.score = false;
    System.out.println(this.getName() + " FINISHED");
  }

  @Override
  protected void interrupted() {
    end();
  }
}
