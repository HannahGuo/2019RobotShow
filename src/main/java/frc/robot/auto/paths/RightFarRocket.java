package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.*;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class RightFarRocket extends CommandGroup {
  public RightFarRocket() {
    Elevator.elevatorState = ElevatorState.ZERO;
    addSequential(new DriveStraight(-125000));
    addSequential(new TurnToAngle(30));
    addSequential(new DriveStraight(-205000));
    addSequential(new TurnToAngle(-55));
    addSequential(new DriveStraight(-12000));
    addSequential(new MoveElevator(ElevatorState.HATCH1));
    addSequential(new LimeLightScore(ElevatorState.HATCH1));
    addSequential(new DriveStraight(-10000));
    addSequential(new MoveElevator(ElevatorState.HOLDDEF));
    addSequential(new TurnToAngle(50));
    addSequential(new DriveStraight(120000));
    addSequential(new TurnToAngle(-30, true));
    addSequential(new DriveStraight(50000));
  }
}
