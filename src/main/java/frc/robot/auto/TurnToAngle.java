package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class TurnToAngle extends Command {
	private Drive drive;
	private SynchronousPID vPID, hPID;
	private double heading = 0;
	private boolean timeOut = false;
	
    public TurnToAngle(double head) {
		this.timeOut = false;
		this.drive = Drive.getInstance();
		this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		heading = head;
		requires(this.drive);
	}

	
	public TurnToAngle(double head, boolean timeOut) {
		this.drive = Drive.getInstance();
		this.vPID = new SynchronousPID();
		this.vPID.setOutputRange(-1.0, 1.0);
		this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		this.timeOut = timeOut;
		heading = head;
		requires(this.drive);
	}
	
    protected void initialize() {
		RobotMap.gyroSPI.reset();
		this.vPID.reset();
    	this.vPID.setPID(Constants.linPIDLowGear);
    	this.hPID.reset();
		this.hPID.setPID(Constants.angPID);
		Drive.resetDriveEncoders();
		Drive.setLowGear();
		if(this.timeOut) setTimeout(0.5);
    }
   
    protected void execute() {
		//OI.getPrimaryLeftXAxis() * 3000
    	this.vPID.setSetpoint((Drive.getAverageDrivePosition()));
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((this.heading - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
		double head = hPID.calculate(RobotMap.gyroSPI.getRate());

    	RobotMap.driveLeftTop.set(ControlMode.PercentOutput, -vel - head);
		RobotMap.driveRightTop.set(ControlMode.PercentOutput, vel - head);
		System.out.println("ERROR ANGLE" + this.hPID.getError());
    }

    protected boolean isFinished() {
			if(this.timeOut) {
				return isTimedOut();
			}
			return Constants.isWithinThreshold(this.hPID.getError(), -0.5, 0.5);
    }

    protected void end() {
		System.out.println(this.getName() + " FINISHED");
		Drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}