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
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.CoachQadarGroup;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class Robot extends TimedRobot {
  private static Drive drive = Drive.getInstance();
  private RobotMap robotMap = RobotMap.getInstance();
  private Elevator elevator = Elevator.getInstance();
  private OI oi = OI.getInstance();
  private Compressor c = new Compressor(0);
  private LimelightVision limelightVision = LimelightVision.getInstance();
  private static boolean isCompMode = false;
  Command autonomousCommand;

  @Override
  public void robotInit() {
    // CameraServer.getInstance().startAutomaticCapture();
  }

  @Override
  public void robotPeriodic() {
    limelightVision.updateVision();
    SmartDashboard.putNumber("GYRO ANGLE", RobotMap.gyroSPI.getAbsoluteAngle());
    SmartDashboard.putNumber("GYRO RATE", RobotMap.gyroSPI.getRate());
  }

  @Override
  public void autonomousInit() {
    if(isCompMode) {
      Scheduler.getInstance().removeAll(); 
      robotMap.resetSensors();
    }
    autonomousCommand = new CoachQadarGroup();
    if(autonomousCommand != null) autonomousCommand.start();
  }

  @Override
  public void autonomousPeriodic() {
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit(){
    drive.stopMoving();
    elevator.stopMoving();
    LimelightVision.setBlink(1);
    Scheduler.getInstance().removeAll();

    if(!isCompMode) robotMap.resetSensors();
  }

  @Override
  public void teleopInit(){
    c.setClosedLoopControl(true);
    LimelightVision.setBlink(0);

    if(isCompMode) Elevator.elevatorState = ElevatorState.MANUAL;
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
    // limelightVision.visionCalc();
  }
  
  @Override
  public void testPeriodic() {
  }
}
