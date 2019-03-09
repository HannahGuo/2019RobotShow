package frc.robot;

/**
 * A class for timers used in the robot code.
 */
public class Timer {
    private static boolean isTiming = false;
    private static double startTime = 0.0;

    public static void enableTimer(double start){
        isTiming = true;
        startTime = start;
    }

    public static void disableTimer(){
        isTiming = false;
    }

    public static boolean hasTimeHasPassed(double range){
        return System.currentTimeMillis() - startTime >= range;
    } 
}