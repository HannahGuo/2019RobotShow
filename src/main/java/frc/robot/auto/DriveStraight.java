package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class DriveStraight extends Command {
	private Drive drive;
	private SynchronousPID vPID, hPID;
	private double distance = 0;
	private boolean lowGear = false;
	private double multiplier = 1.0;
	
	public DriveStraight(double dist) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		distance = dist;
    	requires(this.drive);
	}

	public DriveStraight(double dist, double multiplier) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		this.multiplier = multiplier;
		distance = dist;
    	requires(this.drive);
	}
	
	public DriveStraight(double dist, boolean lowGear) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		distance = dist;
		this.lowGear = true;
		this.multiplier = 0.2;
		requires(this.drive);
		setTimeout(0.8);
    }

    protected void initialize() {
    	RobotMap.gyroSPI.reset();
		this.vPID.reset();
		
		if(lowGear) {
			this.vPID.setPID(Constants.linPIDLowGear);
			Drive.setLowGear();
		} else {
			this.vPID.setPID(Constants.linPIDHighGear);
			Drive.setHighGear();
		}

    	this.hPID.reset();
		this.hPID.setPID(Constants.angPID);
		Drive.resetDriveEncoders();
    }
   
    protected void execute() {
		// this.vPID.setSetpoint(OI.getPrimaryLeftXAxis() * 3000);
    	this.vPID.setSetpoint((Drive.getAverageDrivePosition() - distance) * 0.35);
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((0 - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
		double head = hPID.calculate(RobotMap.gyroSPI.getRate());

    	RobotMap.driveLeftTop.set(ControlMode.PercentOutput, this.multiplier * (-vel - head));
		RobotMap.driveRightTop.set(ControlMode.PercentOutput, this.multiplier * (vel - head));
		System.out.println("DRIVE COMMAND ERROR " + this.vPID.getError());
    }

    protected boolean isFinished() {
		if(this.lowGear) return isTimedOut();
		return (Constants.isWithinThreshold(this.vPID.getError(), -400, 400)) && Constants.isWithinThreshold(-Drive.getAverageDriveVelocity(), -1100, 1100);
	}

    protected void end() {
		System.out.println(this.getName() + " FINISHED");
		this.lowGear = false;
		Drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}