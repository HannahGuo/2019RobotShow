package frc.robot;

/**
 * Constants
 */
public class Constants {
    public static final double angP = 0.0115;
    public static final double maxAngVel = 400; // 400 deg/sec cuz why not
    public static final double limelightP = 0.08;

    public static final double[] angPID = new double[]{0.009, 0, 0};
    public static final double[] linPID = new double[] {0.0001, 0.0000055, 0.0};
    // public static final double[] linPID = new double[] {0.00004, 0.0000066, 0.0};

    public static final int oneEncoderRotationNativeUnits = 4096; 
    public static final int pidMax = 1023;

    public static final double carrageInchesPerEncoderRev = 2.699;

    public static final int maxFtPerSecond = 5;
    public static final double maxMPerSecond = feetToMeters(maxFtPerSecond);

    public static final double trackWidthInches = 26.26;
    public static final double trackWidthFeet = inchesToFeet(trackWidthInches);
    public static final double trackWidthM = inchesToMeters(trackWidthInches);
    
    public static final double wheelDiameterIn = 6.2;
    public static final double feetPerEncoderRev = 0.0;
    public static final double wheelDiameterM = inchesToMeters(wheelDiameterIn);

    public static double degToTicks(double degrees) {
        return degrees * (4096 / 360);
    }

    public static double inchesToMeters(double inches){
        return inches / 0.0254;
    }

    public static double feetToMeters(double feet){
        return feet / 3.281;
    }

    public static double inchesToFeet(double inches){
        return inches / 12.0;
    }

    public static boolean isWithinThreshold(double val, double minThresh, double maxThresh) {
        return minThresh <= val && val <= maxThresh;
    }
}