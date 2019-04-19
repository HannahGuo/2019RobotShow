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
	private double distance = 0, heading = 0;
	
    public DriveStraight(double dist, double head, double timeout) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		distance = dist;
		heading = head;
		setTimeout(timeout);
    	requires(this.drive);
    }

    protected void initialize() {
    	RobotMap.gyroSPI.reset();
    	this.vPID.reset();
    	this.vPID.setPID(0.0001, 0.0000055, 0.0);
    	this.hPID.reset();
		this.hPID.setPID(Constants.angP, 0.0, 0);
		this.drive.resetDriveEncoders();
    }
   
    protected void execute() {
		//(Drive.getAverageDrivePosition() - 20000)     	
		//OI.getPrimaryLeftXAxis() * 3000
    	this.vPID.setSetpoint((Drive.getAverageDrivePosition() - distance));
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((heading - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
        double head = hPID.calculate(RobotMap.gyroSPI.getRate());

    	RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.5 * (-vel - head));
		RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.5 * (vel - head));
		System.out.println("ERROR " + this.vPID.getError());
		System.out.println("Average Enc Position " + Drive.getAverageDrivePosition());
		RobotMap.printDriveEncoderPositions();
		RobotMap.printDriveEncoderVelocitiess();
		System.out.println("Average Enc Velocity " + Drive.getAverageDriveVelocity());
    }

    protected boolean isFinished() {
        return ((this.vPID.getError() >= 80 && this.vPID.getError() <= 80)) || isTimedOut();
    }

    protected void end() {
    	// this.vPID.reset();
		// this.hPID.reset();
		System.out.println("AUTO COMMAND FINISHED");
    	this.drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}