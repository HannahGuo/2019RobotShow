package frc.robot;

/**
 * Constants
 */
public class Constants {
    private static final double angP = 0.0;

    public static final int oneEncoderRotationNativeUnits = 4096; 
    public static final int pidMax = 1023;

    public static final double carrageInchesPerEncoderRev = 2.699;

    public static double getAngleP(){
        return angP;
    }

    public static double degToTicks(double degrees) {
        return degrees * (4096 / 360);
    }

    public static double inchesToMeters(double inches){
        return inches / 0.0254;
    }
}