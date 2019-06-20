package com.company.gametools;

import com.company.gamegeom.vectormath.point.Point2D_Integer;
import com.company.gamegeom.vectormath.point.Point3D_Integer;
import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

public abstract class MathTools {

    private static Logger LOG = LogManager.getLogger(MathTools.class.getName());

    public static int randomSign() {
        Random random = new Random();
        int result = random.nextInt(2) - 1;

        return (result == 0) ? 1 : result;
    }

    public static Long sqrVal(Integer value) {
        return new Long(value * value);
    }
    public static Double sqrVal(Double value) {
        return new Double(value * value);
    }

    public static boolean in_range(int left, int val, int right, boolean strict) {
        if (strict) {
            return (left < val && val < right);
        }

        return (left <= val && val <= right);
    }

    public static Point3D_Integer getNextPointOnRay(Point3D_Integer srcPoint, Point3D_Integer destPoint, int step) {

        Point3D_Integer nextPoint;

        // Count the distance between current point and next point
        double norm = MathBugfixes.sqrt(Point3D_Integer.distSqrVal(srcPoint, destPoint));
        LOG.trace("srcPoint=" + srcPoint + ", dstPoint=" + destPoint + ", norm=" + norm);

        // Avoid division by zero and endless wandering around the destination point
        if (norm <= step) {
            // One step to target
            // This may be not economically, but safe that nobody modify "destPoint" outside, so do clone()
            nextPoint = destPoint.clone();
        } else {
            // Many steps to target
            nextPoint = srcPoint.plus1(destPoint.minus1(srcPoint).mult(step).divInt(norm));
            /*
                srcPoint.x() + (int)((destPoint.x() - srcPoint.x()) * step / norm),
                srcPoint.y() + (int)((destPoint.y() - srcPoint.y()) * step / norm),
                srcPoint.z() + (int)((destPoint.z() - srcPoint.z()) * step / norm)
            */
        }

        return nextPoint;
    }

    /* Tests if a given point belongs to the given section [A; B].
        Return value:
        1 - belongs to the section interior
        0 - lays on the end of the section
       -1 - does not belong
     */
    public static int sectionContains(Point2D_Integer A, Point2D_Integer p, Point2D_Integer B) {

        // Validation. We are not supposed that the section turns to a point.
        // Thus we don't just return here smth, but exit the program with a fatal error.
        if ((A.x() == B.x()) && (A.y() == B.y())) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [A; B] is a point.");
        }

        if ((A.x() == p.x()) && (A.y() == p.y())) return 0; // belongs to the end A
        if ((B.x() == p.x()) && (B.y() == p.y())) return 0; // belongs to the end B

        int minX = Math.min(A.x(), B.x());
        int maxX = Math.max(A.x(), B.x());
        int minY = Math.min(A.y(), B.y());
        int maxY = Math.max(A.y(), B.y());

        if (
               ((p.x() - A.x()) * (B.y() - A.y()) - (B.x() - A.x()) * (p.y() - A.y()) == 0) // lays on the line
            && (minX <= p.x()) && (p.x() <= maxX) && (minY <= p.y()) && (p.y() <= maxY) // lays in the coordinates intervals
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
    public static int twoSectionsIntersect(Point2D_Integer p1, Point2D_Integer p2, Point2D_Integer p3, Point2D_Integer p4) {

        // Validation. We are not supposed that the section turns to a point.
        // We call this function to check intersection of a line with the edge of another shape.
        // This edge must never turn to a point. Thus we don't just return here smth, but exit the program with a fatal error.
        if ((p1.x() == p2.x()) && (p1.y() == p2.y())) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [1; 2] is a point!");
            // TODO: return anyway the result if the point belongs to another section
        }
        if ((p3.x() == p4.x()) && (p3.y() == p4.y())) {
            Main.terminateNoGiveUp(1000, "Wrong data: section [3; 4] is a point!");
            // TODO: return anyway the result if the point belongs to another section
        }

        double Det23 = (p2.x() - p1.x()) * (p3.y() - p1.y()) - (p2.y() - p1.y()) * (p3.x() - p1.x());
        double Det24 = (p2.x() - p1.x()) * (p4.y() - p1.y()) - (p2.y() - p1.y()) * (p4.x() - p1.x());
        double Det43 = (p4.x() - p3.x()) * (p3.y() - p1.y()) - (p4.y() - p3.y()) * (p3.x() - p1.x());
        double Det42 = (p4.x() - p3.x()) * (p3.y() - p2.y()) - (p4.y() - p3.y()) * (p3.x() - p2.x());

        // intersection
        if ((Math.signum(Det23) * Math.signum(Det24) < 0) && (Math.signum(Det43) * Math.signum(Det42) < 0)) return 1;

        if (
                (p3.x() <= p1.x()) && (p1.x() <= p4.x()) && (p3.y() <= p1.y()) && (p1.y() <= p4.y()) // 1 between [3; 4]
             && (p3.x() - p1.x()) * (p4.y() - p3.y()) - (p3.y() - p1.y()) * (p4.x() - p3.x()) == 0 // 1 lays on [3; 4]
            ||  (p3.x() <= p2.x()) && (p2.x() <= p4.x()) && (p3.y() <= p2.y()) && (p2.y() <= p4.y()) // 2 between [3; 4]
             && (p3.x() - p2.x()) * (p4.y() - p3.y()) - (p3.y() - p2.y()) * (p4.x() - p3.x()) == 0 // 2 lays on [3; 4]
            ||  (p1.x() <= p3.x()) && (p3.x() <= p2.x()) && (p1.y() <= p3.y()) && (p3.y() <= p2.y()) // 3 between [1; 2]
             && (p3.x() - p1.x()) * (p2.y() - p1.y()) - (p3.y() - p1.y()) * (p2.x() - p1.x()) == 0 // 3 lays on [1; 2]
            ||  (p1.x() <= p4.x()) && (p4.x() <= p2.x()) && (p1.y() <= p4.y()) && (p4.y() <= p2.y()) // 4 between [1; 2]
             && (p4.x() - p1.x()) * (p2.y() - p1.y()) - (p4.y() - p1.y()) * (p2.x() - p1.x()) == 0 // 4 lays on [1; 2]
        ) return 0; // overlapping or touching (we don't distinguish these cases for our tasks)

        return -1; // don't intersect, don't overlap and don't even touch
    }
}
