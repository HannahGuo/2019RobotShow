package frc.robot.vision.loops;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List
 * object. They are started when the robot powers up and stopped after the
 * match.
 */
public class Looper {
	private final Notifier notifier_;
	private final List<Loop> loops_;
	private final Object taskRunningLock_ = new Object();
	private boolean running_;
	private double timestamp_ = 0;
	private double dt_ = 0;

	public Looper() {
		Runnable runnable_ = new Runnable() {
			@Override
			public void run() {
				synchronized(taskRunningLock_) {
					if(running_) {
						double now = Timer.getFPGATimestamp();
						loops_.forEach(Loop::onLoop);
						dt_ = now - timestamp_;
						timestamp_ = now;
					}
				}
			}
		};
		notifier_ = new Notifier(runnable_);
		running_ = false;
		loops_ = new ArrayList<>();
	}

	public synchronized void register(Loop loop) {
		synchronized(taskRunningLock_) {
			loops_.add(loop);
		}
	}

	public synchronized void start() {
		if(!running_) {
			System.out.println("Starting loops");
			synchronized(taskRunningLock_) {
				timestamp_ = Timer.getFPGATimestamp();
				loops_.forEach(Loop::onStart);
				running_ = true;
			}
			notifier_.startPeriodic(0.01);
		}
	}

	public synchronized void stop() {
		if(running_) {
			System.out.println("Stopping loops");
			notifier_.stop();
			synchronized(taskRunningLock_) {
				running_ = false;
				loops_.forEach(loop -> {
					System.out.println("Stopping " + loop);
					loop.onStop();
				});
			}
		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", dt_);
	}
}