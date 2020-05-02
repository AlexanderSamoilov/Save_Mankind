package com.company.gametools;

import com.company.gamemath.cortegemath.cortege.Cortege3D_Integer;
import com.company.gamemath.cortegemath.point.Point2D_Double;
import com.company.gamemath.cortegemath.point.Point2D_Integer;
import com.company.gamemath.cortegemath.point.Point3D_Integer;
import com.company.gamethread.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

import static com.company.gamecontent.Restrictions.Y_ORIENT;

public abstract class MathTools {

    private static Logger LOG = LogManager.getLogger(MathTools.class.getName());

    public static enum RoundingMode {
        TRUNC, // simply removes any fractional part from the number (round-toward-zero)
        CEIL,  // chooses the smallest (closest to negative infinity) integer that is not smaller than the original number
        FLOOR, // chooses the largest (closest to positive infinity) integer that is not greater than the original number
        ROUND  // chooses the integer closest to the original number (implementation dependent)
    };
    // Let use "round" mode
    public static RoundingMode getRoundingMode() {
        return RoundingMode.ROUND;
    }

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

    public static Integer round(Double x) {
        RoundingMode rm = getRoundingMode();
        switch (rm) {
            case TRUNC: // https://stackoverflow.com/questions/58220779/java-trunc-method-equivalent/58221383
                return x.intValue();
            case CEIL:
                return (int)Math.ceil(x);
            case FLOOR:
                return (int)Math.floor(x);
            case ROUND:
                return (int)Math.round(x);
            default: // https://stackoverflow.com/questions/58220779/java-trunc-method-equivalent/58221383
                return x.intValue();
        }

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
        double norm = MathBugfixes.sqrt(Cortege3D_Integer.distSqrVal(srcPoint, destPoint));
        LOG.trace("srcPoint=" + srcPoint + ", dstPoint=" + destPoint + ", norm=" + norm);

        // Avoid division by zero and endless wandering around the destination point
        if (norm <= step) {
            // One step to target
            // This may be not economically, but safe that nobody modify "destPoint" outside, so do clone()
            nextPoint = destPoint.clone();
        } else {
            // Many steps to target
            nextPoint = srcPoint.plusClone(destPoint.minusClone(srcPoint).mult(step).divInt(norm));
            /*
                srcPoint.x() + (int)((destPoint.x() - srcPoint.x()) * step / norm),
                srcPoint.y() + (int)((destPoint.y() - srcPoint.y()) * step / norm),
                srcPoint.z() + (int)((destPoint.z() - srcPoint.z()) * step / norm)
            */
        }

        return nextPoint;
    }

    /*
     This function calculates the rotation direction between two vectors.
     The first vector is defined by its ray angle rayAngle.
     The second vector is defined by its start and end points.

     Return value:
      1 - counter clockwise
      0 - 180 degrees (turn opposite)
     -1 - clockwise
     */
    public static int getRotationDirectionRay (double rayAngle, Point2D_Double pointFrom, Point2D_Integer pointTo) {

        if (pointFrom == null) throw new NullPointerException("getRotationDirectionRay: pointFrom is NULL!");
        if (pointTo == null) throw new NullPointerException("getRotationDirectionRay: pointTo is NULL!");

        /*
        Hayami uses the following formula to determine on which of two semi-planes (left or right)
        obtained by the division of the 2D-space by the given ray with center in O(x0, y0) and direction-vector (xv,yv)
        lays the given point P(xp, yp):

                                           invariant = xv * (yp - y0) - (xp - x0) * yv

        If invariant > 0 then the point (xp, yp) lays on the left semi-plane from the vector (xv,yv)
        If invariant < 0 then the point (xp, yp) lays on the right semi-plane from the vector (xv,yv)
        If invariant == 0 then the point (xp, yp) lays on the line given by the vector (xv,yv)

        The goal is to rotate the ray given by the vector (xv, yv) until it intersects the point (xp, yp),
        but in the direction of minimal angle (shorter angle). It is obvious that it is shorter to turn to
        that semi-plane where the point is located.

        Thus,
        if invatiant > 0 we should turn left (counter clockwise)
        if invariant < 0 we should turn right (clockwise)
        if invariant == 0 we should turn backwards (180°)

        */

        // Point "O" - center of the object
        double x0 =             pointFrom.x();
        double y0 = Y_ORIENT  * pointFrom.y();

        // Point "P"
        int xp = pointTo.x();
        int yp = Y_ORIENT  * pointTo.y();

        // The direction-vector of the ray has coordinates (cos fi, sin fi) where fi = this.currAngle, so ...
        double invariant = (yp - y0) * Math.cos(rayAngle) - (xp - x0) * Math.sin(rayAngle);
        //double angleLeft = Math.acos(((xp - x0) * Math.cos(this.currAngle) + (yp - y0) * Math.sin(this.currAngle))
        //        / MathBugfixes.sqrt(sqrVal(xp - x0) + sqrVal(yp - y0)));

        // counter clockwise (1), clockwise (-1) or just backwards (0)
        // NOTE: In the Cartesian coordinate system the angle is growing counter clockwise!
        return (int)Math.signum(invariant);

    }


    /*
        public int getRotationDirectionPolar () {
            double destAngle = ...;
            double diffAngles = destAngle - currAngle;

            if (diffAngles > 0 && diffAngles < Math.toRadians(180)) {
                return 1;
            }

            if (diffAngles > Math.toRadians(180)) {
                return -1;
            }

            if (diffAngles < 0 && diffAngles > -Math.toRadians(180)) {
                return -1;
            }

            if (diffAngles < -Math.toRadians(180)) {
                return 1;
            }

            return 0;
        }
    */

    /*
     Calculates the angle between two vectors and checks whether it exceeds the given value (dAngle).
     The first vector is defined by its ray angle (rayAngle)
     The second vector is defined by its start and end points.
     */
    public static boolean angleBetweenRayAndPointLessThan(double rayAngle, Point2D_Double pointFrom, Point2D_Integer pointTo, double dAngle) {

        if (pointFrom == null) throw new NullPointerException("angleBetweenRayAndPointLessThan: pointFrom is NULL!");
        if (pointTo == null) throw new NullPointerException("angleBetweenRayAndPointLessThan: pointTo is NULL!");

        // Point "O" - center of the object
        double x0 =             pointFrom.x();
        double y0 = Y_ORIENT  * pointFrom.y();

        // Point "P" - destination point of rotation
        int xp = pointTo.x();
        int yp = Y_ORIENT  * pointTo.y();

        // Distance to dest point less than 1 pixel
        // We don't check exactly == 0 because we use type double that can give small deviation
        if ((Math.abs(xp - x0) < 1) && (Math.abs(yp - y0) < 1)) {
            return true;
        }
        /* Here we use the formulae of the angle between two vectors:
                       cos (a1,b1) ^ (a2,b2) = (a1*a2 + b1*b2) / (|(a1,b1)| * |(a2,b2)|)
                       where |(ai,bi)| is then length of the i-vector, that is sqrt(ai*ai + bi*bi)

        We want to know when the angle between two vectors is less then the given value dAngle. Calculations:
                       (a1,b1) ^ (a2,b2) < dAngle

        f(x) = cos(x) is monotonically decreasing within [0; PI] and we consider only angles within [0; PI],
        because we don't care about orientation (we just need to get the angle between two rays given by two vectors).
        Taking into account the monotonic behaviour of f(x) = cos(x) in the interval [0; PI] we have this:
                       cos (a1,b1) ^ (a2,b2) > cos(dAngle)
                       (a1*a2 + b1*b2) / (|(a1,b1)| * |(a2,b2)|) > cos(dAngle)
                       a1*a2 + b1*b2 > |(a1,b1)| * |(a2,b2)| * cos(dAngle)
                       a1*a2 + b1*b2 > len(a1,b1) * len(a2,b2) * cos(dAngle)

        We are free to choose arbitrary the length of the vector which represents the ray of the objects' orientation,
        thus let us suppose that len(a1,b1) = 1, because a1 = cos(fi), b1 = sin(fi), therefore:
                       a2 * cos(fi) + b2 * sin(fi) > len(a2,b2) * cos(dAngle)

        Vector (a1,b1) represents the ray of the current objects' orientation (its length is 1)
        a1 = cos(rayAngle), b1 = sin(rayAngle)
        Vector (a2,b2) represents the ray from the center of the object towards destination rotation point "P"
        a2 = xp - x0, b2 = yp - y0

        */

        // len(a2,b2)
        double len = MathBugfixes.sqrt(sqrVal(xp - x0) + sqrVal(yp - y0));
/*
        LOG.debug("Len = " + len);
        LOG.debug(
            "Current angle between vectors: " +
            Math.acos(
                ((xp - x0) * Math.cos(rayAngle) + (yp - y0) * Math.sin(rayAngle)) / len
            )
        );
        LOG.debug("a1 = " + 100*Math.cos(rayAngle) + ", b1 = " + Math.sin(rayAngle));
        LOG.debug("a2 = " + (xp - x0) + ", b2=" + (yp - y0));
        LOG.debug("rayAngle = " + Math.toDegrees(rayAngle));
*/
        return  (xp - x0) * Math.cos(rayAngle) +
                (yp - y0) * Math.sin(rayAngle) > len * Math.cos(dAngle);
    }


    public static boolean angleBetweenRayAndPointSmallEnoughRegardingRotationSpeed(double rayAngle, Point2D_Double pointFrom, Point2D_Integer pointTo, double rotation_speed) {
        // Sometimes it is better to turn one more time (even if the angle different is already less than given delta)
        // For example if delta is 45°, we turned and now the angle between the target and our object is 40°.
        // In such case it is better to do one more turn step and the angle will 40° - 45° = -5° which is more precise.

        // Let imagine we are on the position where delta less then dFi (that is we are closer to the desired destAngle
        // less than the rotation step). Then we can turn once again and we cross the destination ray.
        // So, before we cross the destination ray the delta is fi1=abs(destAngle - currAngle)
        // After we cross the delta will be fi2=abs(destAngle - currAngle - step)
        // Both of them less than step, but we want to choose the one which absolute value is less.

        // It is obvious that: fi1 + fi2 = step
        // It means that fi1 <= step /2 or fi2 <= step /2
        // So we COULD set the stopping criterion: angleBetweenRayAndPointLessThan(rayAngle, pointFrom, pointTo, rotation_speed * 0.5)
        // However it is very risky since we are not in the perfect math world, but in computer
        // Thus we must take some value which is "a little more than" 0.5, but enough more to cover calc errors.

        // What is "close to, but not really more than" 0.5?
        // 2/3, 3/5, 4/7, 5/9, 6/10, ... - this sequence approximates to 0.5
        // the sequence formula: (n + 1) / 2n
        // let's take for example n = 5, then K = 0.6 which is a little more than 0.5, but safe.
        int N = 5;
        double K = (N + 1) / (2.0 * N);
        return angleBetweenRayAndPointLessThan(rayAngle, pointFrom, pointTo, K * rotation_speed);
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
            Main.terminateNoGiveUp(null,1000, "Wrong data: section [A; B] is a point.");
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
            Main.terminateNoGiveUp(null,1000, "Wrong data: section [1; 2] is a point!");
            // TODO: return anyway the result if the point belongs to another section
        }
        if ((p3.x() == p4.x()) && (p3.y() == p4.y())) {
            Main.terminateNoGiveUp(null,1000, "Wrong data: section [3; 4] is a point!");
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
