package frc.robot;

/**
 * Constants
 */
public class Constants {
    public static final double limelightP = 0.08;

    public static final double[] angPID = new double[] {0.0115, 0.0, 0.0};

    public static final double[] linPIDLowGear = new double[] {0.0001, 0.0000068, 0.0};
    public static final double[] linPIDHighGear = new double[] {0.00025, 0.0000025, 0.0};

    public static final double carrageInchesPerEncoderRev = 2.699;

    public static final double driveEncoderUnitsPerInch = 1000;
    public static final int maxFtPerSecond = 5;
    public static final double maxMPerSecond = feetToMeters(maxFtPerSecond);

    public static final double trackWidthInches = 26.26;
    public static final double trackWidthFeet = inchesToFeet(trackWidthInches);
    public static final double trackWidthM = inchesToMeters(trackWidthInches);
    
    public static final double wheelDiameterIn = 6.2;
    public static final double feetPerEncoderRev = 1.0 / (driveEncoderUnitsPerInch * 12.0 / 4096.0);
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

    public static double inchesToEncoderUnits(double inches){
        return inches * driveEncoderUnitsPerInch;
    }

    public static boolean isWithinThreshold(double val, double minThresh, double maxThresh) {
        return minThresh <= val && val <= maxThresh;
    }
}