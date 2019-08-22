package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import frc.robot.auto.TurnToAngle;

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

    public static boolean getSecondaryRightAxisButton() {
        return secondary.getRawButton(12);
    }

    public static boolean getSecondaryRightAxisButtonPressed() {
        return secondary.getRawButtonPressed(12);
    }

    public static int getSecondaryDPad() {
        return secondary.getPOV(0);
    }

    // JoystickButton primaryX = new JoystickButton(primary, 1);
    // JoystickButton primaryY = new JoystickButton(primary, 4);
    // JoystickButton primaryLB = new JoystickButton(primary, 5);
    // JoystickButton testingButtonBack = new JoystickButton(primary, 9);
    // JoystickButton testingButtonStart = new JoystickButton(primary, 10);
    public OI(){
        // primaryRB.toggleWhenActive(new FullLimeLightAlign());
        // primaryLB.toggleWhenActive(new TurnToAngle(180));
        // primaryRB.toggleWhenActive(new TurnToAngle(90));
        // testingButtonBack.toggleWhenActive(new DriveStraight(0, 45, 2));
        // testingButtonStart.toggleWhenActive(new DriveStraight(-85000, 0, 3));
        // testingButtonX.toggleWhenActive(new MoveElevator(Elevator.ElevatorState.HATCH1, true));
    }
}