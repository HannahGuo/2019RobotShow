/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class CoachQadarGroupLeft extends CommandGroup {
  public CoachQadarGroupLeft() {
    addSequential(new DriveStraight(-138000, ElevatorState.ZERO));
    addSequential(new TurnToAngle(-30));
    // addSequential(new DriveStraight(-85000));
    // addSequential(new TurnToAngle(-40));
    addSequential(new DriveStraight(-205000));
    addSequential(new TurnToAngle(59, 1.2));
    addSequential(new DriveStraight(-11000));
    addSequential(new DriveStraight(0, ElevatorState.HATCH1));
    addSequential(new LimeLightAlign());
    addSequential(new LimeLightAlign());
    addSequential(new LimeLightAlign());
    addSequential(new LimeLightAlignDrive());
    addSequential(new DriveStraight(0, ElevatorState.HATCH1, true));
    Elevator.elevatorState = ElevatorState.HOLDDEF;
    addSequential(new WaitCommand(0.3));
    addSequential(new DriveStraight(-20000));
    addSequential(new TurnToAngle(-50));
    // addSequential(new Outtake());
    // Add Commands here:
    // e.g. addSequential(new Command1());
    // addSequential(new Command2());
    // these will run in order.

    // To run multiple commands at the same time,
    // use addParallel()
    // e.g. addParallel(new Command1());
    // addSequential(new Command2());
    // Command1 and Command2 will run in parallel.

    // A command group will require all of the subsystems that each member
    // would require.
    // e.g. if Command1 requires chassis, and Command2 requires arm,
    // a CommandGroup containing them would require both the chassis and the
    // arm.
  }
}
