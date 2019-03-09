package frc.robot.vision.loops;

/**
 * Interface for loops, which are routine that run periodically in the robot
 * code (such as periodic gyroscope calibration, etc.)
 */
public interface Loop {
	void onStart();

	void onLoop();

	void onStop();
}