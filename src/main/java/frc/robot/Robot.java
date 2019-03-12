/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
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

  private static Drive drive = Drive.getInstance();
  private RobotMap robotMap = RobotMap.getInstance();
  private Elevator elevator = Elevator.getInstance();
  private OI oi = OI.getInstance();
  private Compressor c = new Compressor(0);
  private LimelightVision limelightVision = LimelightVision.getInstance();

  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
    limelightVision.updateVision();
    limelightVision.getTargets();
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
    elevator.stopMoving();
  }

  @Override
  public void teleopInit(){
    c.setClosedLoopControl(true);
    Scheduler.getInstance().removeAll();
    robotMap.resetSensors();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    SmartDashboard.putNumber("GYRO ANGLE", RobotMap.gyroSPI.getAbsoluteAngle());
    // limelightVision.visionCalc();
  }

  @Override
  public void testPeriodic() {
  }
}
