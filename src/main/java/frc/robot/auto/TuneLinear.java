package frc.robot.auto;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class TuneLinear extends Command {
	private Drive drive;
	private SynchronousPID vPID;
	private double distanceSetpoint = 0;
	
    public TuneLinear(double distance) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
      this.vPID.setOutputRange(-1.0, 1.0);
      distanceSetpoint = distance;
    	requires(this.drive);
    }

    protected void initialize() {
    	RobotMap.gyroSPI.reset();
    	this.vPID.reset();
      this.vPID.setPID(0.01, 0.0, 0);
    }
   
    protected void execute() {
      double error = distanceSetpoint - Drive.getAverageDrivePosition();
    	this.vPID.setSetpoint(error);
      double vel = vPID.calculate(Drive.getAverageDriveVelocity());
    
      drive.driveLR(vel, vel);
      System.out.print(error);
    }

    protected boolean isFinished() {
        return false;
    }

    protected void end() {
    	this.vPID.reset();
    	this.drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}