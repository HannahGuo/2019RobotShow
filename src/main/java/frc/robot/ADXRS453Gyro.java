package frc.robot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.BitSet;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.Timer;

public class ADXRS453Gyro {
	private static final int DATA_SIZE = 4; // 4 bytes = 32 bits
	private static final byte PARITY_BIT = (byte) 0x01; // parity check on first bit
	private static final byte FIRST_BYTE_DATA_MASK = (byte) 0x03; //mask to find sensor data bits on first byte
	private static final byte THIRD_BYTE_DATA_MASK = (byte) 0xFC; //mask to find sensor data bits on third byte
	private static final byte READ_COMMAND = (byte) 0x20; //0010 0000

	//absoluteAngle integration
	private volatile double currentRate;
	private volatile double lastRate;
	private volatile double deltaTime;
	private volatile double currentTime;
	private volatile double lastTime;
	private volatile double absoluteAngle;
	private volatile double relativeAngle;
	private volatile double driftRate;
	private volatile double accumulatedRate;

	//calibration loop
	private volatile boolean calibrate;
	private volatile boolean stopCalibrating;
	private volatile boolean firstLoop;
	private volatile double timeElapsed;

	private SPI spi;
	private long period;

	ADXRS453Gyro() {
		//run at 333Hz loop
		this.period = (long) 3;

		spi = new SPI(Port.kOnboardCS0);
		spi.setClockRate(4000000); //4 MHz (rRIO max, gyro can go higher)
		spi.setClockActiveHigh();
		spi.setChipSelectActiveLow();
		spi.setMSBFirst();

		currentRate = 0.0;
		driftRate = 0.0;

		lastTime = 0;
		currentTime = 0;
		lastRate = 0;
		deltaTime = 0;
		accumulatedRate = 0;

		calibrate();
		reset();
		startThread();
	}

	private void startThread() {
		System.out.println("Gyro calibrating... Please wait");
		java.util.Timer executor = new java.util.Timer();
		executor.schedule(new GyroUpdateTask(this), 0L, this.period);
	}

	/**
	 * Called to begin the gyros calibration sequence.
	 * This should only be called during a time when the robot will be
	 * stationary for a duration of time (~10 sec). Robot motion during
	 * the calibration sequence will cause significant steady state drift.
	 */
	private void calibrate() {
		calibrate = true;
		firstLoop = true;
		stopCalibrating = false;
	}

	/**
	 * Zero the gyro heading.
	 */
	public void reset() {
		absoluteAngle = 0.0;
		relativeAngle = 0.0;
	}

	public void setAbsoluteAngleGyro(double angle){
		absoluteAngle = angle;
	}

	public void resetRelative() {
		relativeAngle = 0.0;
	}

	public double getRate() {
		return currentRate;
	}

	public double getAbsoluteAngle() {
		return absoluteAngle;
	}

	public double getRelativeAngle() {
		return relativeAngle;
	}

	////////// PRIVATE FUNCTIONS ////////////////

	private void checkParity(byte[] data) {
		if (BitSet.valueOf(data).cardinality() % 2 == 0)
			data[3] |= PARITY_BIT;
	}

	/**
	 * @return gyro rate in deg/s
	 */
	private double getSensorData() {
		byte[] command = new byte[DATA_SIZE];
		byte[] data = new byte[DATA_SIZE];
		command[0] = READ_COMMAND;
		command[1] = 0;
		command[2] = 0;
		command[3] = 0;
		data[0] = 0;
		data[1] = 0;
		data[2] = 0;
		data[3] = 0;

		checkParity(command);
		spi.write(command, DATA_SIZE);
		spi.read(false, data, DATA_SIZE);

		return sensorDataMask(data);
	}

	private double sensorDataMask(byte[] data) {
		//Pull out bytes 25-10 as data bytes for gyro rate
		byte[] rateByte = new byte[2];
		rateByte[0] = (byte)((byte)((data[1] >> 2) & 0x3F) | ((data[0] & FIRST_BYTE_DATA_MASK) << 6));
		rateByte[1] = (byte)((byte)((data[1] << 6) & 0xC0) | (data[2] & THIRD_BYTE_DATA_MASK) >> 2 & 0x3F);

		//convert to 2's compo
		short value = ByteBuffer.wrap(rateByte).order(ByteOrder.BIG_ENDIAN).getShort();

		//data has 80 LSB
		return value / 80.0;
	}

	/**
	 * Periodically executed to update the gyro state data.
	 */
	private void update() {
		if (lastTime == 0) lastTime = Timer.getFPGATimestamp();

		currentRate = getSensorData();
		currentTime = Timer.getFPGATimestamp();
		deltaTime = currentTime - lastTime;

		//TODO: see if we can fix low-pass filter to stop drift
		//low-pass filter
		//remove until it can be further tested. Yields incorrect results
		//if(Math.abs(currentRate) < 2)
		//	currentRate = 0;

		absoluteAngle += (currentRate - driftRate) * deltaTime;
		relativeAngle += (currentRate - driftRate) * deltaTime;

		/*
		 * Periodically update our drift rate by normalizing out drift
		 * while the robot is not moving.
		 * This code is re-entrant and can be stopped at any time
		 *   (e.g. if a match starts).
		 */
		if (calibrate) {
			if (firstLoop) {
				driftRate = 0.0;
				accumulatedRate = 0.0;
				timeElapsed = 0.0;
				firstLoop = false;
			}

			timeElapsed += deltaTime;
			accumulatedRate += currentRate * deltaTime;
			driftRate = accumulatedRate / timeElapsed; //absoluteAngle/S

			if (timeElapsed >= 10.0 || stopCalibrating) {
				//finish calibration sequence
				calibrate = false;
				reset();

				System.out.println("Accumulated Offset: " + driftRate + "\tDelta Time: " + timeElapsed);
				System.out.println("GYRO READY... YOU MAY BEGIN");
			}
		}

		lastRate = currentRate;
		lastTime = currentTime;
	}

	private class GyroUpdateTask extends TimerTask {
		private ADXRS453Gyro gyro;

		private GyroUpdateTask(ADXRS453Gyro gyro) {
			if (gyro == null) throw new NullPointerException("Gyro pointer null");
			this.gyro = gyro;
		}

		/**
		 * Called periodically in its own thread
		 */
		public void run() {
			gyro.update();
		}
	}
}