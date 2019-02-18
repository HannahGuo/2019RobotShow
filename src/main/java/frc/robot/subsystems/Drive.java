/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.OI;
import frc.robot.RobotMap;
import frc.robot.SynchronousPID;

public class Drive extends Subsystem {
  private static Drive instance;

  public static Drive getInstance() {
    return instance == null ? instance = new Drive() : instance;
  }

  public void initDefaultCommand() {
		setDefaultCommand(new Command() {
			private SynchronousPID hPID;
			private boolean stopGyro = false;

			{
				this.hPID = new SynchronousPID();
				this.hPID.setOutputRange(-1.0, 1.0);
				requires(getInstance());
			}

			protected void initialize() {
				System.out.println("Starting drive");
				this.hPID.reset();
				this.hPID.setPID(Constants.getAngPID());
				stopMoving();
			}

			protected void execute() {
				if(OI.getPrimaryStart())
					stopGyro = true;
				else if(OI.getPrimaryBack())
					stopGyro = false;
				if(!stopGyro) {
					double straight = OI.getStraight(), steering = Math.pow(OI.getSteering(), 3), head;
					this.hPID.setSetpoint(steering * 400);
          head = this.hPID.calculate(RobotMap.gyroSPI.getRate());
          driveLR(-straight + head, straight + head);
				} else {
					driveLR(0.0, 0.0);
				}
			}

			protected boolean isFinished() {
				return false;
			}

			protected void end() {
				System.out.println("Stopping drive");
				this.hPID.reset();
				stopMoving();
			}

			protected void interrupted() {
				end();
			}
		});
	}

  public static void driveLR(double left, double right) {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, left);
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, right);
  }
  
  public static void stopMoving() {
    RobotMap.driveLeftTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveLeftBot.set(ControlMode.Follower, RobotMap.driveLeftTop.getDeviceID());
    RobotMap.driveRightTop.set(ControlMode.PercentOutput, 0.0);
    RobotMap.driveRightBot.set(ControlMode.Follower, RobotMap.driveRightTop.getDeviceID());
  }
}