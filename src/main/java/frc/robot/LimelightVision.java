/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LimelightVision {
    public static NetworkTable table;
    private static LimelightVision instance;

    public static LimelightVision getInstance() {
        return instance == null ? instance = new LimelightVision() : instance;
    }
    
    public LimelightVision(){
        table = NetworkTableInstance.getDefault().getTable("limelight");
    } 

    public static void updateVision(){
        table = NetworkTableInstance.getDefault().getTable("limelight");
        SmartDashboard.putNumber("Horizontal Offset", getHorizontalOffset());
        // SmartDashboard.putNumber("Vertical Offset", getVerticalOffset());
        // SmartDashboard.putNumber("Skew", getSkew());
        SmartDashboard.putNumber("Target Area", getTargetArea());
        SmartDashboard.putBoolean("Has targets?", isTargetVisible());
    }

    public static boolean isTargetVisible(){
        return table.getEntry("tv").getDouble(0.0) == 1.0;
    }

    public static double getHorizontalOffset(){
        // Degrees, -27 to 27
        return table.getEntry("tx").getDouble(0.0);
    } 

    public static double getVerticalOffset(){
        // Degrees, -20.5 to 20.5
        return table.getEntry("ty").getDouble(0.0);
    } 

    public static double getSkew(){
        return table.getEntry("ts").getDouble(0.0);
    } 

    public static double getTargetArea(){
        return table.getEntry("ta").getDouble(0.0);
    }  

    public static void setDriveMode(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(1);
    }

    public static void setVisionProcessingMode(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(0);
    }

    public static void turnOff(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
    }

    public static void blink(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(2);
    }

    public static void turnOn(){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(3);
    }
}
