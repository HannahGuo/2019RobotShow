package frc.robot.auto;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Constants;
import frc.robot.LimelightVision;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;
import frc.robot.subsystems.Drive;

public class LimeLightAlignNew extends Command {
	private Drive drive;
	private SynchronousPID vPID, hPID;
	private double heading = 0;
	private double offset = 0.0;
	
    public LimeLightAlignNew() {
		this.drive = Drive.getInstance();
		this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		requires(this.drive);
	}

	public LimeLightAlignNew(double offset) {
		this.drive = Drive.getInstance();
		this.vPID = new SynchronousPID();
    	this.vPID.setOutputRange(-1.0, 1.0);
    	this.hPID = new SynchronousPID();
		this.hPID.setOutputRange(-1.0, 1.0);
		this.offset = offset;
		requires(this.drive);
	}
	
    protected void initialize() {
		RobotMap.gyroSPI.reset();
		this.vPID.reset();
		this.vPID.setPID(Constants.linPIDLowGear);
		this.hPID.reset();
		this.hPID.setPID(Constants.angPID);
		Drive.resetDriveEncoders();
		this.heading = LimelightVision.getHorizontalOffset() + this.offset;
		setTimeout(0.5);
    }
   
    protected void execute() {
		//OI.getPrimaryLeftXAxis() * 3000
    	this.vPID.setSetpoint((Drive.getAverageDrivePosition()));
    	double vel = vPID.calculate(-Drive.getAverageDriveVelocity());
    	this.hPID.setSetpoint((this.heading - RobotMap.gyroSPI.getAbsoluteAngle()) * 10);
        double head = hPID.calculate(RobotMap.gyroSPI.getRate());

    	RobotMap.driveLeftTop.set(ControlMode.PercentOutput, -vel - head);
		RobotMap.driveRightTop.set(ControlMode.PercentOutput, vel - head);
		System.out.println("LIMELIGHT ANGLE ERROR" + this.hPID.getError());
    }

    protected boolean isFinished() {
        return Constants.isWithinThreshold(this.hPID.getError(), -2.5, 2.5) || isTimedOut();
    }

    protected void end() {
		System.out.println(this.getName() + " FINISHED");
		this.offset = 0.0;
		Drive.stopMoving();
    }

    protected void interrupted() {
    	System.out.println("Interrupted");
    	end();
    }
}