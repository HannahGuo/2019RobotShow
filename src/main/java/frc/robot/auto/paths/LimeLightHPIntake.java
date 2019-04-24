package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.auto.DriveStraight;
import frc.robot.auto.LimeLightAlignDrive;
import frc.robot.auto.LimeLightAlignNew;

public class LimeLightHPIntake extends CommandGroup {
  public LimeLightHPIntake() {
    addSequential(new WaitCommand(0.2));
    addSequential(new LimeLightAlignNew());
    addSequential(new LimeLightAlignDrive());
    addSequential(new LimeLightAlignNew());
    addSequential(new LimeLightAlignDrive());
    addSequential(new DriveStraight(4000));
  }
}
