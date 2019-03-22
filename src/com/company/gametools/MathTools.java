package com.company.gametools;

import java.util.Random;

public class MathTools {
    public static int randomSign() {
        Random random = new Random();
        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    public static int sqrVal(int value) {
        return value * value;
    }

    public static double sqrVal(double value) {
        return value * value;
    }

    public static boolean withinRadius(Integer [] A, Integer [] B, int radius) {
        return sqrVal(radius) >= sqrVal(A[0] - B[0]) + sqrVal(A[1] - B[1]);
    }

    public static boolean in_range(int left, int val, int right, boolean strict) {
        if (strict) {
            return (left < val && val < right);
        }

        return (left <= val && val <= right);
    }

    public static Integer[] getNextPointOnRay(Integer srcPoint[], Integer[] destPoint, int step) {

        Integer nextPoint[] = new Integer[3];

        // Count the distance between current point and next point
        double norm = Math.sqrt(
                sqrVal(destPoint[0] - srcPoint[0]) + sqrVal(destPoint[1] - srcPoint[1])
        );
        //Main.printMsg("norm=" + norm + ", speed=" + speed);

        // Avoid division by zero and endless wandering around the destination point
        if (norm <= step) {
            // One step to target
            nextPoint[0] = destPoint[0];
            nextPoint[1] = destPoint[1];
            nextPoint[2] = 0; // destPoint[2]; - we don't support 3D so far
        } else {
            // Many steps to target
            nextPoint[0] = srcPoint[0] + (int)((destPoint[0] - srcPoint[0]) * step / norm);
            nextPoint[1] = srcPoint[1] + (int)((destPoint[1] - srcPoint[1]) * step / norm);
            nextPoint[2] = 0; // srcPoint[2] + (int)((destPoint[2] - srcPoint[2]) * step / norm); - we don't support 3D so far
        }

        return nextPoint;
    }
}
