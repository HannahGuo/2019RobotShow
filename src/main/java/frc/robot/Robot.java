package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.auto.paths.LeftFarRocket;
import frc.robot.auto.paths.RightFarRocket;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class Robot extends TimedRobot {
  private static Drive drive = Drive.getInstance();
  private RobotMap robotMap = RobotMap.getInstance();
  private Elevator elevator = Elevator.getInstance();
  private OI oi = OI.getInstance();
  private LimelightVision limelightVision = LimelightVision.getInstance();
  private Compressor c = new Compressor(0);
  Command[] autonomousCommandsOptionRocket = new Command[] {new LeftFarRocket(), new RightFarRocket()};
  Command autoCommand;
  public static boolean runAuto = false;
  SendableChooser<Integer> autoChooser;

  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    autoChooser = new SendableChooser<Integer>();
    autoChooser.setDefaultOption("MANUAL", 2);
    autoChooser.addOption("LEFT FAR ROCKET", 0);
    autoChooser.addOption("RIGHT FAR ROCKET", 1);
    SmartDashboard.putData("Auto Mode Chooser", autoChooser);
    runAuto = false;
  }

  @Override
  public void robotPeriodic() {
    LimelightVision.updateVision();
    SmartDashboard.putNumber("GYRO ANGLE", RobotMap.gyroSPI.getAbsoluteAngle());
    SmartDashboard.putNumber("GYRO RATE", RobotMap.gyroSPI.getRate());
    SmartDashboard.putString("DRIVE GEAR", RobotMap.driveShifter.get() ? "HIGH" : "LOW");
  }

  @Override
  public void autonomousInit() {
    LimelightVision.turnOn();
    RobotMap.makePDPWork();
    Elevator.elevatorState = ElevatorState.ZERO;
    if(autoChooser.getSelected() != 2) runAuto = true;
    if(runAuto) {
      autoCommand = autonomousCommandsOptionRocket[autoChooser.getSelected()];
      if(autoCommand != null) autoCommand.start();
    }
  }

  @Override
  public void autonomousPeriodic() {
    if(!Robot.runAuto) Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit(){
    Scheduler.getInstance().removeAll();

    Drive.stopMoving();
    Elevator.stopMoving();
    LimelightVision.turnOff();
    RobotMap.makePDPWork();
  }

  @Override
  public void teleopInit(){
    if(runAuto) Scheduler.getInstance().removeAll();
    else Elevator.elevatorState = ElevatorState.MANUAL;

    c.setClosedLoopControl(true);
    LimelightVision.turnOn();
    Drive.stopMoving();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();
  }
  
  @Override
  public void testPeriodic() {
  }
}
