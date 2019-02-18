package frc.robot;

/**
 * Constants
 */
public class Constants {
    private static final double linP = 0.0;
    private static final double linI = 0.0;
    private static final double linD = 0.0;

    private static final double angP = 0.0;
    private static final double angI = 0.0;
    private static final double angD = 0.0;

    public static double[] getLinPID(){
        return new double[] {linP, linI, linD};
    }

    public static double[] getAngPID() {
        return new double[] {angP, angI, angD};
    }
}