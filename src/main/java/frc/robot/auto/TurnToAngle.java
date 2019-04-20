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
	
    public TurnToAngle(double head) {
		this.drive = Drive.getInstance();
		this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		heading = head;
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
		//OI.getPrimaryLeftXAxis() * 3000
    	this.vPID.setSetpoint((Drive.getAverageDrivePosition()));
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((heading - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
        double head = hPID.calculate(RobotMap.gyroSPI.getRate());

    	RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.8 *  (-vel - head));
		RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.8 * (vel - head));
		// System.out.println("GYRO ANGLE " + RobotMap.gyroSPI.getAbsoluteAngle());
		System.out.println("ERROR ANGLE" + this.hPID.getError());
		// System.out.println("Average Enc Position " + Drive.getAverageDrivePosition());
		RobotMap.printDriveEncoderPositions();
		// RobotMap.printDriveEncoderVelocitiess();
		// System.out.println("Average Enc Velocity " + Drive.getAverageDriveVelocity());
    }

    protected boolean isFinished() {
        return (this.hPID.getError() >= -1.2 && this.hPID.getError() <= 1.2);
    }

    protected void end() {
    	// this.vPID.reset();
		// this.hPID.reset();
		System.out.println("TURN COMMAND FINISHED");
    	this.drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}