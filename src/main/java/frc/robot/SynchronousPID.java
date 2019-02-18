package frc.robot;

import edu.wpi.first.hal.util.BoundaryException;

/**
 * @author team254
 * This class implements a PID Control Loop.
 * <p>
 * Does all computation synchronously (i.e. the calculate() function must be
 * called by the user from his own thread)
 */
public class SynchronousPID {
	private double m_P; // factor for "proportional" control
	private double m_I; // factor for "integral" control
	private double m_D; // factor for "derivative" control
	private double m_maximumOutput = 1.0; // |maximum output|
	private double m_minimumOutput = -1.0; // |minimum output|
	private double m_prevError = 0.0; // the prior sensor input (used to compute velocity)
	private double m_totalError = 0.0; // the sum of the errors for use in the integral calc
	private double m_setpoint = 0.0;
	private double m_error = 0.0;
	private double m_result = 0.0;
	private double m_last_input = Double.NaN;
	private double m_PTermVal; // The value of the P calc
	private double m_ITermVal; // The value of the I calc
	private double m_DTermVal; // The value of the D calc

	public SynchronousPID() {
	}

	/**
	 * Read the input, calculate the output accordingly, and write to the
	 * output. This should be called at a constant rate by the user (ex. in a
	 * timed thread)
	 *
	 * @param input the input
	 */
	public double calculate(double input) {
		m_last_input = input;
		m_error = m_setpoint - input;

		if((m_error * m_P < m_maximumOutput) && (m_error * m_P > m_minimumOutput)) m_totalError += m_error;
		else m_totalError = 0;

		m_PTermVal = m_P * m_error;
		m_ITermVal = m_I * m_totalError;
		m_DTermVal = m_D * (m_error - m_prevError);

		m_result = (m_PTermVal + m_ITermVal + m_DTermVal);
		m_prevError = m_error;

		if(m_result > m_maximumOutput) m_result = m_maximumOutput;
		else if(m_result < m_minimumOutput) m_result = m_minimumOutput;

		return m_result;
	}

	public double getError() {
		return m_error;
	}

	/**
	 * Set the PID controller gain parameters. Set the proportional, integral,
	 * and differential coefficients.
	 *
	 * @param p Proportional coefficient
	 * @param i Integral coefficient
	 * @param d Differential coefficient
	 */
	public void setPID(double p, double i, double d) {
		m_P = p;
		m_I = i;
		m_D = d;
	}

	public void setPID(double[] pid) {
		m_P = pid[0];
		m_I = pid[1];
		m_D = pid[2];
	}

	/**
	 * Return the current PID result This is always centered on zero and
	 * constrained the the max and min outs
	 *
	 * @return the latest calculated output
	 */
	public double get() {
		return m_result;
	}

	/**
	 * Sets the minimum and maximum values to write.
	 *
	 * @param minimumOutput the minimum value to write to the output
	 * @param maximumOutput the maximum value to write to the output
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		if(minimumOutput > maximumOutput) {
			throw new BoundaryException("Lower bound is greater than upper bound");
		}
		m_minimumOutput = minimumOutput;
		m_maximumOutput = maximumOutput;
	}

	/**
	 * Returns the current setpoint of the PID controller
	 *
	 * @return the current setpoint
	 */
	public double getSetpoint() {
		return m_setpoint;
	}

	/**
	 * Set the setpoint for the PID controller
	 *
	 * @param setpoint the desired setpoint
	 */
	public void setSetpoint(double setpoint) {
		m_setpoint = setpoint;
	}

	/**
	 * Reset all internal terms.
	 */
	public void reset() {
		m_last_input = Double.NaN;
		m_prevError = 0;
		m_totalError = 0;
		m_result = 0;
		m_setpoint = 0;
	}

	public String getValues() {
		return "P Val: " + m_PTermVal + "I Val: " + m_ITermVal + " D Val: " + m_DTermVal + " Err: " + m_error + " TotErr: " + m_totalError + " Out: " + m_result;
	}
}