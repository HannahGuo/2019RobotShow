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
        SmartDashboard.putNumber("Vertical Offset", getVerticalOffset());
        SmartDashboard.putNumber("Skew", getSkew());
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

    public static void setCamMode(int camMode){
        // 0 = Vision Processing, 1 = Drive Camera
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("camMode").setNumber(camMode);
    }

    public static double getDistance(){
        double a1 = 0.0;
        double a2 = getVerticalOffset();
        double h1 = 41.5; //inches
        double h2 = 29.0; //inches
        
        return (h2 - h1) / Math.tan(a1 + a2);
    }

    public static void setBlink(int blink) {
        // 1 --> Force off, 2 --> Force Blink, 3 --> Force on
        NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(blink);
    }
}
