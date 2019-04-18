package com.company.gametools;

import com.company.gamethread.Main;

import java.util.Random;

public class MathTools {
    public static int randomSign() {
        Random random = new Random();
        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    public static long sqrVal(int value) {
        return value * value;
    }

    public static double sqrVal(double value) {
        return value * value;
    }

    public static boolean withinRadius(Integer [] A, Integer [] B, int radius) {
        return distSqrVal(A, B) <= sqrVal(radius);
    }

    public static long distSqrVal(Integer [] A, Integer [] B) {
        return sqrVal(A[0] - B[0]) + sqrVal(A[1] - B[1]);
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
        //LOG.debug("norm=" + norm + ", speed=" + speed);

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

    /* Tests if a given point belongs to the given section [A; B].
        Return value:
        1 - belongs to the section interior
        0 - lays on the end of the section
       -1 - does not belong
     */
    public static int sectionContains(Integer [] A, Integer [] p, Integer [] B) {

        // Validation. We are not supposed that the section turns to a point.
        // Thus we don't just return here smth, but exit the program with a fatal error.
        if ((A[0] == B[0]) && (A[1] == B[1])) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [A; B] is a point.");
        }

        if ((A[0] == p[0]) && (A[1] == p[1])) return 0; // belongs to the end A
        if ((B[0] == p[0]) && (B[1] == p[1])) return 0; // belongs to the end B

        int minX = Math.min(A[0], B[0]);
        int maxX = Math.max(A[0], B[0]);
        int minY = Math.min(A[1], B[1]);
        int maxY = Math.max(A[1], B[1]);

        if (
               ((p[0] - A[0]) * (B[1] - A[1]) - (B[0] - A[0]) * (p[1] - A[1]) == 0) // lays on the line
            && (minX <= p[0]) && (p[0] <= maxX) && (minY <= p[1]) && (p[1] <= maxY) // lays in the coordinates intervals
        ) return 1; // belongs to the section [A; B]

        // otherwise outside
        return -1;
    }

    /* Tests if two sections [p1, p2] and [p3, p4] intersect.
       Return value:
       1 - intersect
       0 - overlap (lay on the same line and have a common section) or touch (have one and only one common point)
      -1 - don't even touch
     */
    public static int twoSectionsIntersect(Integer [] p1, Integer [] p2, Integer [] p3, Integer [] p4) {

        // Validation. We are not supposed that the section turns to a point.
        // We call this function to check intersection of a line with the edge of another shape.
        // This edge must never turn to a point. Thus we don't just return here smth, but exit the program with a fatal error.
        if ((p1[0] == p2[0]) && (p1[1] == p2[1])) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [1; 2] is a point!");
            // TODO: return anyway the result if the point belongs to another section
        }
        if ((p3[0] == p4[0]) && (p3[1] == p4[1])) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [3; 4] is a point!");
            // TODO: return anyway the result if the point belongs to another section
        }

        double Det23 = (p2[0] - p1[0]) * (p3[1] - p1[1]) - (p2[1] - p1[1]) * (p3[0] - p1[0]);
        double Det24 = (p2[0] - p1[0]) * (p4[1] - p1[1]) - (p2[1] - p1[1]) * (p4[0] - p1[0]);
        double Det43 = (p4[0] - p3[0]) * (p3[1] - p1[1]) - (p4[1] - p3[1]) * (p3[0] - p1[0]);
        double Det42 = (p4[0] - p3[0]) * (p3[1] - p2[1]) - (p4[1] - p3[1]) * (p3[0] - p2[0]);

        // intersection
        if ((Math.signum(Det23) * Math.signum(Det24) < 0) && (Math.signum(Det43) * Math.signum(Det42) < 0)) return 1;

        if (
                (p3[0] <= p1[0]) && (p1[0] <= p4[0]) && (p3[1] <= p1[1]) && (p1[1] <= p4[1]) // 1 between [3; 4]
                        && (p3[0] - p1[0]) * (p4[1] - p3[1]) - (p3[1] - p1[1]) * (p4[0] - p3[0]) == 0 // 1 lays on [3; 4]
                        ||  (p3[0] <= p2[0]) && (p2[0] <= p4[0]) && (p3[1] <= p2[1]) && (p2[1] <= p4[1]) // 2 between [3; 4]
                        && (p3[0] - p2[0]) * (p4[1] - p3[1]) - (p3[1] - p2[1]) * (p4[0] - p3[0]) == 0 // 2 lays on [3; 4]
                        ||  (p1[0] <= p3[0]) && (p3[0] <= p2[0]) && (p1[1] <= p3[1]) && (p3[1] <= p2[1]) // 3 between [1; 2]
                        && (p3[0] - p1[0]) * (p2[1] - p1[1]) - (p3[1] - p1[1]) * (p2[0] - p1[0]) == 0 // 3 lays on [1; 2]
                        ||  (p1[0] <= p4[0]) && (p4[0] <= p2[0]) && (p1[1] <= p4[1]) && (p4[1] <= p2[1]) // 4 between [1; 2]
                        && (p4[0] - p1[0]) * (p2[1] - p1[1]) - (p4[1] - p1[1]) * (p2[0] - p1[0]) == 0 // 4 lays on [1; 2]
        ) return 0; // overlapping or touching (we don't distinguish these cases for our tasks)

        return -1; // don't intersect, don't overlap and don't even touch
    }
}
