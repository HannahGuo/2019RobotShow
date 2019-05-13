/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.auto.DriveStraight;
import frc.robot.auto.LimeLightAlignDrive;
import frc.robot.auto.LimeLightAlignNew;
import frc.robot.auto.MoveElevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class LimeLightScore extends CommandGroup {
  public LimeLightScore(ElevatorState scorePosition) {
    addSequential(new ChangeLimelightState(false));
    addSequential(new MoveElevator(ElevatorState.HATCH1));
    addSequential(new WaitCommand(0.4));
    addSequential(new LimeLightAlignNew(-0.7));
    addSequential(new LimeLightAlignDrive());
    addSequential(new LimeLightAlignNew(-0.7));
    addSequential(new LimeLightAlignNew(-0.7));
    addSequential(new LimeLightAlignDrive());
    addSequential(new ChangeLimelightState(true));
    addSequential(new DriveStraight(40000, true));
    addParallel(new MoveElevator(ElevatorState.HATCH1, true));
  }
}