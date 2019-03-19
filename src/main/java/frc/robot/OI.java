package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class OI {
    public static final Joystick primary = new Joystick(0); //Logitech Dual Action
    public static final Joystick secondary = new Joystick(1); //Logitech Dual Action
    private static OI instance;

    public static OI getInstance() {
        return instance == null ? instance = new OI() : instance;
    }

    // PRIMARY
    public static double getPrimaryLeftXAxis(){
        return primary.getRawAxis(0);
    }

    public static double getPrimaryLeftYAxis() {
        return primary.getRawAxis(1);
    }

    public static double getPrimaryRightXAxis() {
        return primary.getRawAxis(2);
    }

    public static double getPrimaryRightYAxis(){
        return primary.getRawAxis(3);
    }

    static boolean getPrimaryAPressed() {
        return primary.getRawButtonPressed(2);
    }

    public static boolean getPrimaryA() {
        return primary.getRawButton(2);
    }

    public static boolean getPrimaryB() {
        return primary.getRawButton(3);
    }

    public static boolean getPrimaryX() {
        return primary.getRawButton(1);
    }

    public static boolean getPrimaryY() {
        return primary.getRawButton(4);
    }

    public static boolean getPrimaryLB() {
        return primary.getRawButton(5);
    }

    public static boolean getPrimaryRB() {
        return primary.getRawButton(6);
    }

    public static boolean getPrimaryLT() {
        return primary.getRawButton(7);
    }

    public static boolean getPrimaryRT() {
        return primary.getRawButton(8);
    }

    public static boolean getPrimaryBack() {
        return primary.getRawButton(9);
    }

    public static boolean getPrimaryStart() {
        return primary.getRawButton(10);
    }

    public static boolean getPrimaryStartPressed() {
        return primary.getRawButtonPressed(10);
    }

    public static boolean getPrimaryStartReleased() {
        return primary.getRawButtonReleased(10);
    }

    public static boolean isPrimaryDPadPressed() {
        return primary.getPOV(0) != -1;
    }

    // SECONDARY
    public static double getSecondaryLeftXAxis(){
        return secondary.getRawAxis(0);
    }

    public static double getSecondaryLeftYAxis() {
        return secondary.getRawAxis(1);
    }

    public static double getSecondaryRightXAxis() {
        return secondary.getRawAxis(2);
    }

    public static double getSecondaryRightYAxis(){
        return secondary.getRawAxis(3);
    }

    public static boolean getSecondaryA() {
        return secondary.getRawButton(2);
    }

    public static boolean getSecondaryB() {
        return secondary.getRawButton(3);
    }

    public static boolean getSecondaryX() {
        return secondary.getRawButton(1);
    }

    public static boolean getSecondaryY() {
        return secondary.getRawButton(4);
    }

    public static boolean getSecondaryLB() {
        return secondary.getRawButton(5);
    }

    public static boolean getSecondaryRB() {
        return secondary.getRawButton(6);
    }

    public static boolean getSecondaryLT() {
        return secondary.getRawButton(7);
    }

    public static boolean getSecondaryRT() {
        return secondary.getRawButton(8);
    }

    public static boolean getSecondaryBack() {
        return secondary.getRawButton(9);
    }

    public static boolean getSecondaryStart() {
        return secondary.getRawButton(10);
    }

    public static int getSecondaryDPad() {
        return secondary.getPOV(0);
    }

    // Button testingButton = new JoystickButton(primary, 3);
    // public OI(){
    //     testingButton.whenActive(new PathfinderDrive());
    // }
}