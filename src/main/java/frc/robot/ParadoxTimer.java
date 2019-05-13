package frc.robot;

/**
 * A class for timers used in the robot code.
 * @author Hannah
 */
public class ParadoxTimer {
    private boolean isTiming = false;
    private double startTime = 0.0;

    public void enableTimer(double startTime){
        isTiming = true;
        this.startTime = startTime;
    }

    public void disableTimer(){
        isTiming = false;
    }

    public boolean isEnabled(){
        return isTiming;
    }

    public boolean hasTimeHasPassed(double range, double currentTime){
        return currentTime - this.startTime >= range;
    } 

    public double getTimePassed(double currentTime){
        return currentTime - this.startTime;
    }
}