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
  public static SendableChooser<Elevator.ElevatorState> elevatorScorePos;
  private boolean hadItem = false;
  private boolean disableAutos = false;
  public static boolean finalClimbMode = false;

  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
    autoChooser = new SendableChooser<Integer>();
    autoChooser.setDefaultOption("MANUAL", 2);
    autoChooser.addOption("LEFT FAR ROCKET", 0);
    autoChooser.addOption("RIGHT FAR ROCKET", 1);
    SmartDashboard.putData("Auto Mode Chooser", autoChooser);


    elevatorScorePos = new SendableChooser<Elevator.ElevatorState>();
    elevatorScorePos.setDefaultOption("LEVEL 1", ElevatorState.HATCH1);
    elevatorScorePos.addOption("LEVEL 2", ElevatorState.HATCH2);
    elevatorScorePos.addOption("LEVEL 3", ElevatorState.HATCH3);
    SmartDashboard.putData("Elevator Auto Score Height", elevatorScorePos);
    runAuto = false;
    Scheduler.getInstance().removeAll();
  }

  @Override
  public void robotPeriodic() {
    LimelightVision.updateVision();
    SmartDashboard.putNumber("GYRO ANGLE", RobotMap.gyroSPI.getAbsoluteAngle());
    SmartDashboard.putNumber("GYRO RATE", RobotMap.gyroSPI.getRate());
    SmartDashboard.putString("DRIVE GEAR", !RobotMap.driveShifter.get() ? "HIGH" : "LOW"); //comp
  }

  @Override
  public void autonomousInit() {
    LimelightVision.turnOn();
    RobotMap.makePDPWork();
    Elevator.elevatorState = ElevatorState.ZERO;
    System.out.println(autoChooser.getSelected());
    if(autoChooser.getSelected() != 2 && autoChooser.getSelected() != null) {
      autoCommand = autonomousCommandsOptionRocket[autoChooser.getSelected()];
      if(autoCommand != null) autoCommand.start();
    }
  }

  @Override
  public void autonomousPeriodic() {
    if(OI.getPrimaryX()) Scheduler.getInstance().removeAll();
    Scheduler.getInstance().run();
  }

  @Override
  public void disabledInit(){
    Scheduler.getInstance().removeAll();

    Drive.stopMoving();
    Elevator.stopMoving();
    LimelightVision.turnOff();
    RobotMap.makePDPWork();
    disableAutos = false;
    finalClimbMode = false;
  }

  @Override
  public void teleopInit(){
    // if(this.runAuto) Scheduler.getInstance().removeAll();
    Scheduler.getInstance().removeAll();
    // Elevator.elevatorState = ElevatorState.MANUAL;

    c.setClosedLoopControl(true);
    LimelightVision.turnOn();
    LimelightVision.setDriveMode();
    Drive.stopMoving();
  }

  @Override
  public void teleopPeriodic() {
    Scheduler.getInstance().run();

    if(Elevator.elevatorState == ElevatorState.HOLDHATCH1 && Elevator.isHatchIn()) LimelightVision.blink();
    else if(Elevator.elevatorState == ElevatorState.HOLDHATCH2) LimelightVision.turnOn();
  }
  
  @Override
  public void testPeriodic() {
  }
}