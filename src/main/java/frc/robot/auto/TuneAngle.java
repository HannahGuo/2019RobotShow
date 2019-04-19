package frc.robot.auto;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class TuneAngle extends Command {
	private Drive drive;
	private SynchronousPID hPID;
	private double angleSetpoint = 0;
	
    public TuneAngle(double deg) {
    	this.drive = Drive.getInstance();
      this.hPID = new SynchronousPID();
      this.hPID.setOutputRange(-1.0, 1.0);
      this.angleSetpoint = deg;
      requires(this.drive);
      setTimeout(4.0);
    }

    protected void initialize() {
      RobotMap.gyroSPI.reset();
      drive.resetDriveEncoders();
      this.hPID.setPID(Constants.angP, 0.0, 0);
      this.hPID.reset();
      this.hPID.setSetpoint(this.angleSetpoint);
    }
   
    protected void execute() {    
      drive.driveLR(-this.hPID.calculate(RobotMap.gyroSPI.getAbsoluteAngle()), -this.hPID.calculate(RobotMap.gyroSPI.getAbsoluteAngle()));
      System.out.println(this.hPID.calculate(RobotMap.gyroSPI.getAbsoluteAngle()) + " " + RobotMap.gyroSPI.getAbsoluteAngle() + " " + this.hPID.getSetpoint());
    }

    protected boolean isFinished() {
        return (RobotMap.gyroSPI.getAbsoluteAngle() - this.angleSetpoint <= 2 && RobotMap.gyroSPI.getAbsoluteAngle() - this.angleSetpoint >= -2) || isTimedOut();
    }

    protected void end() {
      this.drive.stopMoving();
      System.out.println("Angle Achieved " + RobotMap.gyroSPI.getAbsoluteAngle());
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}