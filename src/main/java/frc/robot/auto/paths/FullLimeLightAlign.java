/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.auto.paths;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.auto.LimeLightAlignNew;

public class FullLimeLightAlign extends CommandGroup {
  public FullLimeLightAlign() {
    addSequential(new ChangeLimelightState(false));
    addSequential(new LimeLightAlignNew(-0.7));
    // addSequential(new LimeLightAlignNew(-0.7));
    addSequential(new ChangeLimelightState(true));
    addParallel(new FlashLimelight());
  }
}
