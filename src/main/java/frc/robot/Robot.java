/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;

public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private Drive drive = Drive.getInstance();
  private RobotMap robotMap = RobotMap.getInstance();
  private Elevator elevator = Elevator.getInstance();
  private OI oi = OI.getInstance();
  private Compressor c = new Compressor(0);

  @Override
  public void robotInit() {
    // robotMap.checkTalonVersions();
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit(){
    drive.stopMoving();
    robotMap.elevatorTop.set(ControlMode.PercentOutput, 0);
  }

  @Override
  public void teleopInit(){
    c.setClosedLoopControl(true);
    Scheduler.getInstance().removeAll();
    robotMap.resetSensors();
    SmartDashboard.putNumber("EUP", 0.0);
    SmartDashboard.putNumber("EUI", 0.0);
    SmartDashboard.putNumber("EUD", 0.0);

    SmartDashboard.putNumber("EDP", 0.0);
    SmartDashboard.putNumber("EDI", 0.0);
    SmartDashboard.putNumber("EDD", 0.0);
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void testPeriodic() {
  }
}
