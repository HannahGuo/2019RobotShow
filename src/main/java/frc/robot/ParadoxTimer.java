package frc.robot;

/**
 * A class for timers used in the robot code.
 */
public class ParadoxTimer {
    private static boolean isTiming = false;
    private static double startTime = 0.0;

    public void enableTimer(double startTime){
        isTiming = true;
        startTime = startTime;
    }

    public void disableTimer(){
        isTiming = false;
    }

    public boolean isEnabled(){
        return isTiming;
    }

    public boolean hasTimeHasPassed(double range, double currentTime){
        return currentTime - startTime >= range;
    } 
}