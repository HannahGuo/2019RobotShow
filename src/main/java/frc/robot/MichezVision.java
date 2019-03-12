package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * MichezVision
 */
public class MichezVision {
    private static final double FOCAL_LENGTH_MM = 2.9272781257541;
    private static final double PIXEL_SIZE_MM = 0.010477924935948;
    private static final double C_MM = 120;
    private static final double RETROTAPE_MM = 55;
    private static final double MIDDLE_WIDTH_PX = 480;
    private LimelightVision limelightVision = LimelightVision.getInstance();

    public static double getRektWidth(){
        return LimelightVision.table.getEntry("thor").getDouble(0.0);
    } 

    public static double getRektHeight(){
        return LimelightVision.table.getEntry("tvert").getDouble(0.0);
    }

    public void getTargets(){
        SmartDashboard.putNumber("X0", LimelightVision.table.getEntry("tx0").getDouble(0.0));
        SmartDashboard.putNumber("Y0", LimelightVision.table.getEntry("ty0").getDouble(0.0));
        SmartDashboard.putNumber("S0", LimelightVision.table.getEntry("ts0").getDouble(0.0));

        SmartDashboard.putNumber("X1", LimelightVision.table.getEntry("tx1").getDouble(0.0));
        SmartDashboard.putNumber("Y1", LimelightVision.table.getEntry("ty1").getDouble(0.0));
        SmartDashboard.putNumber("S1", LimelightVision.table.getEntry("ts1").getDouble(0.0));
    }

    public static void visionCalc(){
        double[] cornX = LimelightVision.table.getEntry("tcornx").getDoubleArray(new double[] {0.0});
        double[] cornY = LimelightVision.table.getEntry("tcorny").getDoubleArray(new double[] {0.0});
                
        if(cornX.length >= 4 && cornY.length >= 4) {
            ArrayList<double[]> coors = new ArrayList<double[]>();
            for(int i = 0; i < cornX.length; i++){
                coors.add(new double[] {cornX[i], cornY[i]});
                System.out.println(cornX[i] + " " + cornY[i]);
            }

            double horizontal = (MIDDLE_WIDTH_PX * (LimelightVision.getHorizontalOffset() / 27) + MIDDLE_WIDTH_PX);
            double vertical = (360 * (LimelightVision.getVerticalOffset() / 20.5) + 360);
            double horizontalThresh1 = horizontal - (3*getRektWidth() * 0.3413);
            double horizontalThresh2 = horizontal + (3*getRektWidth() * 0.3413);

            System.out.println("");
            System.out.println("Centre h,v >" + horizontal + "     " + vertical);
            System.out.println("Width h >" + (3*getRektWidth()*0.3413) );
            System.out.println("Horizontal Thresholds >" + horizontalThresh1 + "    " + horizontalThresh2);
            System.out.println("");

            double[] actualCoords = new double[4];

            for(int i = 0; i < coors.size(); i++) {
                if(coors.get(i)[1] > vertical && coors.get(i)[0] > horizontalThresh1){
                    actualCoords[0] = coors.get(i)[0];
                    break;
                }
            }

            for(int i = 0; i < coors.size(); i++) {
                if(coors.get(i)[1] > vertical && coors.get(i)[0] < horizontalThresh1){
                    actualCoords[1] = coors.get(i)[0];
                    break; 
                }
            }

            for(int i = 0; i < coors.size(); i++) {
                if(coors.get(i)[1] > vertical && coors.get(i)[0] > horizontalThresh2){
                    actualCoords[2] = coors.get(i)[0];
                    break;
                }
            }

            for(int i = 0; i < coors.size(); i++) {
                if(coors.get(i)[1] > vertical && coors.get(i)[0] < horizontalThresh2 && Arrays.asList(actualCoords).contains(actualCoords[0])){
                    actualCoords[3] = coors.get(i)[0];
                    break;   
                }
            }            

            System.out.println(Arrays.toString(actualCoords));

            // double PLL = (coors[0][0] - MIDDLE_WIDTH_PX) * PIXEL_SIZE_MM;
            // double PLR = (coors[1][0] - MIDDLE_WIDTH_PX) * PIXEL_SIZE_MM;
            // double PRL = (coors[2][0] - MIDDLE_WIDTH_PX) * PIXEL_SIZE_MM;
            // double PRR = (coors[3][0] - MIDDLE_WIDTH_PX) * PIXEL_SIZE_MM;

            // double averageX = (PLL + PLR + PRL + PRR) / 4.0;

            // double angleToTarget = Math.toDegrees(Math.atan2(averageX * PIXEL_SIZE_MM, FOCAL_LENGTH_MM));

            // double pStorm = PRR - PLR + PLL - PRL;

            // double TC = RETROTAPE_MM + C_MM;

            // double robotFaceToTarget = Math.toDegrees(Math.atan2((-RETROTAPE_MM * FOCAL_LENGTH_MM) * pStorm, -((((-PLR * C_MM) + (PLL * TC)) * pStorm) - (PLR - PLL) * (((PRR - PLL) * TC) + ((PLR - PRL) * C_MM)))));
            // double distance = -((((PLR * C_MM) - (PLL * TC)) * Math.sin(Math.toRadians(robotFaceToTarget))) - (FOCAL_LENGTH_MM * RETROTAPE_MM * Math.cos(Math.toRadians(robotFaceToTarget)))) / ((PLR - PLL) * Math.cos(Math.toRadians(angleToTarget)));
        }
        // System.out.println(angleToTarget + " " + robotFaceToTarget + " " + distance);
    }
}