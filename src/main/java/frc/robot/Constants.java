package frc.robot;

/**
 * Constants
 */
public class Constants {
    public static final double angP = 0.09;

    public static final int oneEncoderRotationNativeUnits = 4096; 
    public static final int pidMax = 1023;

    public static final double carrageInchesPerEncoderRev = 2.699;

    public static final int maxFtPerSecond = 5;
    public static final double maxMPerSecond = feetToMeters(maxFtPerSecond);

    public static final double trackWidthInches = 26.26;
    public static final double trackWidthM = inchesToMeters(trackWidthInches);
    
    public static final double wheelDiameterIn = 6.2;
    public static final double wheelDiameterM = inchesToMeters(wheelDiameterIn);

    public static boolean testingMode = true;
    public static double getAngleP(){
        return angP;
    }

    public static double degToTicks(double degrees) {
        return degrees * (4096 / 360);
    }

    public static double inchesToMeters(double inches){
        return inches / 0.0254;
    }

    public static double feetToMeters(double feet){
        return feet / 3.281;
    }
}