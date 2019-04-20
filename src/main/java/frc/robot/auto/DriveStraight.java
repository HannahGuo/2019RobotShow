package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.ElevatorState;

public class DriveStraight extends Command {
	private Drive drive;
	private SynchronousPID vPID, hPID;
	private double distance = 0;
	private boolean scoreHatch = false;
	private ElevatorState eleState = ElevatorState.ZERO;
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
	
	public DriveStraight(double dist, ElevatorState eleState) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		distance = dist;
		this.eleState = eleState;
    	requires(this.drive);
	}

	// public DriveStraight(double dist, ElevatorState eleState, double slowMultplier) {
    // 	this.drive = Drive.getInstance();
    // 	this.vPID = new SynchronousPID();
    // 	this.vPID.setOutputRange(-1.0, 1.0);
    // 	this.hPID = new SynchronousPID();
	// 	this.hPID.setOutputRange(-1.0, 1.0);
	// 	distance = dist;
	// 	this.eleState = eleState;
	// 	this.multiplier = slowMultplier;
    // 	requires(this.drive);
	// }
	
	public DriveStraight(double dist, ElevatorState eleState, boolean scoreHatch) {
    	this.drive = Drive.getInstance();
    	this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		distance = dist;
		this.eleState = eleState;
		this.scoreHatch = true;
		requires(this.drive);
		setTimeout(1.0);
    }

    protected void initialize() {
    	RobotMap.gyroSPI.reset();
    	this.vPID.reset();
    	this.vPID.setPID(Constants.linPID);
    	this.hPID.reset();
		this.hPID.setPID(Constants.angP, 0.0, 0);
		this.drive.resetDriveEncoders();
		Elevator.elevatorState = eleState;
		LimelightVision.setCamMode(1);
    }
   
    protected void execute() {
		//Drive.getAverageDrivePosition() - distance
		// this.vPID.setSetpoint(OI.getPrimaryLeftXAxis() * 3000);
    	this.vPID.setSetpoint(Drive.getAverageDrivePosition() - distance);
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((0 - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
		double head = hPID.calculate(RobotMap.gyroSPI.getRate());
		
		if(scoreHatch) {
			LimelightVision.setCamMode(0);
			RobotMap.wristControl.configMotionCruiseVelocity(Elevator.CLAW_VEL + 400);
            RobotMap.wristControl.configMotionAcceleration(Elevator.CLAW_ACCEL);
			RobotMap.wristControl.set(ControlMode.MotionMagic, Elevator.elevatorState.getClawPosition() + 1500);
			Elevator.runHatchOuttake();
			RobotMap.driveLeftTop.set(ControlMode.PercentOutput, -0.1);
			RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.1);
		} else {
			RobotMap.driveLeftTop.set(ControlMode.PercentOutput, this.multiplier * (-vel - head));
			RobotMap.driveRightTop.set(ControlMode.PercentOutput, this.multiplier *(vel - head));
		}

		// System.out.println("ERROR " + this.vPID.getError() + " " + multiplier + " " + Elevator.lowerHatch);
		// System.out.println("Average Enc Position " + Drive.getAverageDrivePosition());
		// RobotMap.printDriveEncoderPositions();
		// RobotMap.printDriveEncoderVelocitiess();
		// System.out.println("Average Enc Velocity " + Drive.getAverageDriveVelocity());
    }

    protected boolean isFinished() {
		if(!scoreHatch) 
			return Constants.isWithinThreshold(this.vPID.getError(), -110, 110) && 
			(Constants.isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(0), Elevator.elevatorState.getClawPosition() - 100, Elevator.elevatorState.getClawPosition() + 100)) &&
			(Constants.isWithinThreshold(RobotMap.elevatorTop.getSelectedSensorPosition(0), Elevator.elevatorState.getElevatorHeight() - 200, Elevator.elevatorState.getElevatorHeight() + 200));
		else 
			return (isTimedOut() || (Constants.isWithinThreshold(RobotMap.wristControl.getSelectedSensorPosition(0), Elevator.elevatorState.getClawPosition() + 1400, Elevator.elevatorState.getClawPosition() + 1600)) && !Elevator.isHatchIn());
	}

    protected void end() {
    	// this.vPID.reset();
		// this.hPID.reset();
		System.out.println("DRIVE COMMAND FINISHED");
		Drive.stopMoving();
		Elevator.stopIntakeWheels();
		scoreHatch = false;
		this.multiplier = 1.0;
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}